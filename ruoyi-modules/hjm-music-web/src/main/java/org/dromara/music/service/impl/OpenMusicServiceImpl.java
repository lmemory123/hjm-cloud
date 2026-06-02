package org.dromara.music.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.Music;
import org.dromara.music.domain.MusicChartSnapshot;
import org.dromara.music.domain.Tag;
import org.dromara.music.domain.vo.*;
import org.dromara.music.mapper.*;
import org.dromara.music.service.IOpenMusicService;
import org.dromara.music.service.MusicInteractionService;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.dromara.music.domain.table.MusicChartItemTableDef.MUSIC_CHART_ITEM;
import static org.dromara.music.domain.table.MusicChartSnapshotTableDef.MUSIC_CHART_SNAPSHOT;
import static org.dromara.music.domain.table.MusicOriginalTableDef.MUSIC_ORIGINAL;
import static org.dromara.music.domain.table.MusicResourceTableDef.MUSIC_RESOURCE;
import static org.dromara.music.domain.table.MusicTableDef.MUSIC;
import static org.dromara.music.domain.table.MusicTagRelTableDef.MUSIC_TAG_REL;
import static org.dromara.music.domain.table.TagTableDef.TAG;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenMusicServiceImpl implements IOpenMusicService {

    private static final String AUDIT_APPROVED = "1";
    private static final String PUBLIC_VISIBLE = "1";
    private static final String CHART_ACTIVE = "published";
    private static final String DEFAULT_SORT = "publish_time_desc";
    private static final int DEFAULT_RANDOM_LIMIT = 12;
    private static final int MAX_RANDOM_LIMIT = 24;
    private static final int DEFAULT_CHART_LIMIT = 20;
    private static final int DEFAULT_SUGGEST_LIMIT = 8;
    private static final int MAX_SUGGEST_LIMIT = 12;
    private static final int DEFAULT_HOT_KEYWORD_LIMIT = 10;
    private static final int MAX_HOT_KEYWORD_LIMIT = 20;
    private static final int DEFAULT_HOT_KEYWORD_DAYS = 7;
    private static final int MAX_HOT_KEYWORD_DAYS = 30;
    private static final int HOT_KEYWORD_RETENTION_DAYS = 45;
    private static final int DEFAULT_PANEL_LIMIT = 10;
    private static final int MAX_PANEL_LIMIT = 20;
    private static final int DEFAULT_RELEVANCE_FETCH_LIMIT = 200;
    private static final int MAX_RELEVANCE_FETCH_LIMIT = 400;
    private static final String HOT_SEARCH_KEY = "music:search:hot_keywords";
    private static final String HOT_SEARCH_DAILY_KEY_PREFIX = "music:search:hot_keywords:";
    private static final Set<String> SEARCH_STOP_WORDS = Set.of(
        "的", "了", "和", "与", "并", "或", "啊", "呀", "呢", "吧", "吗",
        "the", "a", "an", "and", "or", "to", "of", "in", "on", "for"
    );
    private static final Map<String, List<String>> BUILTIN_SYNONYM_GROUPS = Map.of(
        "vocaloid", List.of("术力口", "v家", "vocal"),
        "术力口", List.of("vocaloid", "v家"),
        "电音", List.of("电子", "edm", "electronic"),
        "古风", List.of("国风", "中国风"),
        "二次元", List.of("acg", "动漫")
    );

    private final MusicMapper musicMapper;
    private final MusicChartSnapshotMapper musicChartSnapshotMapper;
    private final MusicChartItemMapper musicChartItemMapper;
    private final MusicOriginalMapper originalMapper;
    private final MusicResourceMapper resourceMapper;
    private final MusicTagRelMapper tagRelMapper;
    private final TagMapper tagMapper;
    private final MusicInteractionService musicInteractionService;

    @Value("${music.search.synonyms:}")
    private String customSynonymsConfig;

    @Override
    public TableDataInfo<MusicVo> searchPublic(String keyword, String tag, String tags, String style, String sort, String isOriginal, String isAi, String resourceStatus, String startDate, String endDate, Long playCountMin, Long playCountMax, PageQuery pageQuery) {
        recordSearchKeyword(keyword);
        return searchFromDatabase(keyword, tag, tags, style, sort, isOriginal, isAi, resourceStatus, startDate, endDate, playCountMin, playCountMax, pageQuery);
    }

    @Override
    public MusicDetailVo queryPublicDetail(Long id) {
        Music music = musicMapper.selectOneById(id);
        if (music == null || !AUDIT_APPROVED.equals(music.getAuditStatus()) || !PUBLIC_VISIBLE.equals(music.getIsPublic())) {
            return null;
        }
        MusicVo musicVo = musicMapper.selectVoById(id);
        MusicDetailVo detail = new MusicDetailVo();
        BeanUtils.copyProperties(musicVo, detail);
        List<MusicOriginalVo> originals = originalMapper.selectVoList(QueryWrapper.create().where(MUSIC_ORIGINAL.MUSIC_ID.eq(id)));
        List<MusicResourceVo> resources = resourceMapper.selectVoList(QueryWrapper.create().where(MUSIC_RESOURCE.MUSIC_ID.eq(id)));
        List<MusicTagRelVo> rels = tagRelMapper.selectVoList(QueryWrapper.create().where(MUSIC_TAG_REL.MUSIC_ID.eq(id)));
        List<Long> tagIds = rels.stream().map(MusicTagRelVo::getTagId).filter(Objects::nonNull).toList();
        List<TagVo> tagList = tagIds.isEmpty() ? Collections.emptyList() : tagMapper.selectVoList(QueryWrapper.create().where(TAG.ID.in(tagIds)));
        detail.setOriginals(originals);
        detail.setResources(resources);
        detail.setTags(tagList);
        detail.setAuditLogs(Collections.emptyList());
        musicInteractionService.fillDynamicStats(detail);
        return detail;
    }

    @Override
    public OpenUserProfileVo queryPublicUserProfile(String uid) {
        if (StringUtils.isBlank(uid)) return null;
        QueryWrapper wrapper = QueryWrapper.create().where(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED).and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE)));
        Long creatorId = parseCreatorId(uid);
        if (creatorId != null) wrapper.and(MUSIC.CREATOR_ID.eq(creatorId));
        else wrapper.and(MUSIC.CREATOR_NAME.eq(uid));
        wrapper.orderBy(MUSIC.PUBLISH_TIME.desc(), MUSIC.ID.desc());
        List<MusicVo> rows = musicMapper.selectVoList(wrapper);
        if (rows == null || rows.isEmpty()) return createEmptyUserProfile(uid, creatorId);
        musicInteractionService.fillDynamicStats(rows);
        MusicVo first = rows.get(0);
        OpenUserProfileVo profile = new OpenUserProfileVo();
        profile.setUid(creatorId == null ? uid : String.valueOf(creatorId));
        profile.setDisplayName(firstNonBlank(first.getCreatorName(), uid));
        profile.setCreatorLink(first.getCreatorLink());
        profile.setSongCount((long) rows.size());
        profile.setTotalPlayCount(rows.stream().map(MusicVo::getPlayCount).mapToLong(this::safeLong).sum());
        profile.setTotalLikeCount(rows.stream().map(MusicVo::getLikeCount).mapToLong(this::safeLong).sum());
        profile.setTotalCollectCount(rows.stream().map(MusicVo::getCollectCount).mapToLong(this::safeLong).sum());
        profile.setTotalCommentCount(rows.stream().map(MusicVo::getCommentCount).mapToLong(this::safeLong).sum());
        profile.setSongs(rows.size() > 24 ? new ArrayList<>(rows.subList(0, 24)) : rows);
        return profile;
    }

    @Override
    public List<MusicVo> queryRandomPublic(Integer limit) {
        int size = normalizeLimit(limit, DEFAULT_RANDOM_LIMIT, MAX_RANDOM_LIMIT);
        QueryWrapper wrapper = QueryWrapper.create().where(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED).and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE))).orderBy("random()", true);
        Page<MusicVo> result = musicMapper.selectVoPage(new Page<>(1, size), wrapper);
        List<MusicVo> rows = result.getRecords() == null ? Collections.emptyList() : result.getRecords();
        musicInteractionService.fillDynamicStats(rows);
        return rows;
    }

    @Override
    public List<OpenSearchSuggestVo> querySearchSuggestions(String keyword, Integer limit) {
        int size = normalizeLimit(limit, DEFAULT_SUGGEST_LIMIT, MAX_SUGGEST_LIMIT);
        String normalizedKeyword = normalizeKeyword(keyword);
        List<String> expandedTerms = buildExpandedSearchTerms(keyword);
        if (StringUtils.isBlank(normalizedKeyword)) {
            return queryHotKeywords(size, DEFAULT_HOT_KEYWORD_DAYS).stream().map(item -> {
                OpenSearchSuggestVo vo = new OpenSearchSuggestVo();
                vo.setKeyword(item);
                vo.setTitle(item);
                vo.setSuggestType("hot_keyword");
                vo.setMatchType("hot_keyword");
                return vo;
            }).toList();
        }
        QueryWrapper wrapper = QueryWrapper.create().where(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED).and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE)).and(buildKeywordCondition(expandedTerms))).orderBy(MUSIC.PLAY_COUNT.desc(), MUSIC.PUBLISH_TIME.desc());
        Page<MusicVo> page = musicMapper.selectVoPage(new Page<>(1, size), wrapper);
        List<MusicVo> rows = page.getRecords() == null ? Collections.emptyList() : page.getRecords();
        List<OpenSearchSuggestVo> suggestions = new ArrayList<>();
        suggestions.addAll(rows.stream().map(item -> toSongSuggestVo(item, normalizedKeyword)).toList());
        suggestions.addAll(buildCreatorSuggestions(rows, normalizedKeyword, size));
        suggestions.addAll(buildTagSuggestions(normalizedKeyword, size));
        return suggestions.stream().limit(size).toList();
    }

    @Override
    public List<String> queryHotKeywords(Integer limit, Integer days) {
        int size = normalizeLimit(limit, DEFAULT_HOT_KEYWORD_LIMIT, MAX_HOT_KEYWORD_LIMIT);
        int dayWindow = normalizeLimit(days, DEFAULT_HOT_KEYWORD_DAYS, MAX_HOT_KEYWORD_DAYS);
        try {
            if (dayWindow <= 1) return loadKeywordRange(currentDailyHotKey(), size);
            Map<String, Double> scores = new LinkedHashMap<>();
            for (int index = 0; index < dayWindow; index++) {
                for (ScoredEntry<Object> entry : RedisUtils.getClient().getScoredSortedSet(dailyHotKey(index)).entryRangeReversed(0, size * 3)) {
                    String keyword = String.valueOf(entry.getValue());
                    if (StringUtils.isBlank(keyword)) continue;
                    scores.merge(keyword, entry.getScore(), Double::sum);
                }
            }
            if (scores.isEmpty()) return loadKeywordRange(HOT_SEARCH_KEY, size);
            return scores.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(size).map(Map.Entry::getKey).toList();
        } catch (Exception ex) {
            log.warn("读取热门搜索词失败", ex);
            return Collections.emptyList();
        }
    }

    @Override
    public OpenSearchPanelVo querySearchPanel(String keyword, String tag, String tags, String style, String sort, String isOriginal, String isAi, String resourceStatus, String startDate, String endDate, Long playCountMin, Long playCountMax, Integer limit) {
        int facetLimit = normalizeLimit(limit, DEFAULT_PANEL_LIMIT, MAX_PANEL_LIMIT);
        QueryWrapper wrapper = buildPublicSearchWrapper(keyword, tag, tags, style, isOriginal, isAi, resourceStatus, startDate, endDate, playCountMin, playCountMax);
        List<MusicVo> rows = musicMapper.selectVoList(wrapper);
        List<String> selectedTags = parseTagFilters(tag, tags, style);
        List<String> synonymKeywords = resolveSynonymKeywords(keyword);
        String correctedKeyword = resolveCorrectedKeyword(keyword, synonymKeywords);
        String currentSort = normalizePanelSort(sort, keyword);
        String recommendedSort = resolveRecommendedSort(keyword);
        OpenSearchPanelVo panel = new OpenSearchPanelVo();
        panel.setKeyword(normalizeKeyword(keyword));
        panel.setCurrentSort(currentSort);
        panel.setRecommendedSort(recommendedSort);
        panel.setCorrectedKeyword(correctedKeyword);
        panel.setSearchSummary(buildSearchSummary(keyword, correctedKeyword, selectedTags, currentSort, rows));
        panel.setSynonymKeywords(synonymKeywords);
        panel.setSelectedTags(selectedTags);
        panel.setTotal((long) rows.size());
        panel.setSortOptions(buildSortOptions(keyword, currentSort, recommendedSort));
        panel.setMatchedFields(buildMatchFieldFacets(rows, keyword, facetLimit));
        panel.setCreators(buildFacetItems(rows, facetLimit, MusicVo::getCreatorName, "creator", Collections.emptyList()));
        panel.setTags(buildTagFacetItems(rows, facetLimit, selectedTags));
        panel.setStyleTags(buildStyleTagFacetItems(rows, facetLimit, style));
        panel.setActiveFilters(buildActiveFilterFacets(selectedTags, isOriginal, isAi, resourceStatus, startDate, endDate, playCountMin, playCountMax));
        panel.setHotKeywords(queryHotKeywords(Math.min(facetLimit, DEFAULT_HOT_KEYWORD_LIMIT), DEFAULT_HOT_KEYWORD_DAYS));
        return panel;
    }

    @Override
    public OpenChartVo queryPublicChart(String chartType, String periodKey, Integer limit) {
        MusicChartSnapshot snapshot = loadLatestSnapshot(chartType, periodKey);
        if (snapshot == null) return createEmptyChart(chartType, periodKey);
        int size = normalizeLimit(limit, DEFAULT_CHART_LIMIT, DEFAULT_CHART_LIMIT);
        List<MusicChartItemVo> items = musicChartItemMapper.selectVoList(QueryWrapper.create().where(MUSIC_CHART_ITEM.SNAPSHOT_ID.eq(snapshot.getId())).orderBy(MUSIC_CHART_ITEM.RANK_NO.asc()));
        if (items.size() > size) items = new ArrayList<>(items.subList(0, size));
        List<Long> musicIds = items.stream().map(MusicChartItemVo::getMusicId).filter(Objects::nonNull).toList();
        Map<Long, MusicVo> songMap = loadPublicSongs(musicIds);
        OpenChartVo chart = new OpenChartVo();
        chart.setSnapshotId(snapshot.getId());
        chart.setChartType(snapshot.getChartType());
        chart.setPeriodKey(snapshot.getPeriodKey());
        chart.setStatus(snapshot.getStatus());
        chart.setItems(items.stream().map(item -> toOpenChartItem(item, songMap.get(item.getMusicId()))).toList());
        return chart;
    }

    @Override
    public List<OpenChartArchiveVo> queryChartArchives(String chartType, Integer limit) {
        int size = normalizeLimit(limit, 12, 36);
        QueryWrapper wrapper = QueryWrapper.create().where(MUSIC_CHART_SNAPSHOT.STATUS.eq(CHART_ACTIVE));
        if (StringUtils.isNotBlank(chartType)) wrapper.and(MUSIC_CHART_SNAPSHOT.CHART_TYPE.eq(chartType));
        wrapper.orderBy(MUSIC_CHART_SNAPSHOT.ID.desc());
        List<MusicChartSnapshot> snapshots = musicChartSnapshotMapper.selectListByQueryAs(wrapper, MusicChartSnapshot.class);
        if (snapshots.size() > size) snapshots = new ArrayList<>(snapshots.subList(0, size));
        if (snapshots.isEmpty()) return Collections.emptyList();
        return snapshots.stream().map(snapshot -> {
            OpenChartArchiveVo vo = new OpenChartArchiveVo();
            vo.setSnapshotId(snapshot.getId());
            vo.setChartType(snapshot.getChartType());
            vo.setPeriodKey(snapshot.getPeriodKey());
            vo.setStatus(snapshot.getStatus());
            vo.setItemCount(musicChartItemMapper.selectCountByQuery(QueryWrapper.create().where(MUSIC_CHART_ITEM.SNAPSHOT_ID.eq(snapshot.getId()))));
            return vo;
        }).toList();
    }

    private TableDataInfo<MusicVo> searchFromDatabase(String keyword, String tag, String tags, String style, String sort, String isOriginal, String isAi, String resourceStatus, String startDate, String endDate, Long playCountMin, Long playCountMax, PageQuery pageQuery) {
        if (isRelevanceSort(sort, keyword)) return searchByRelevance(keyword, tag, tags, style, isOriginal, isAi, resourceStatus, startDate, endDate, playCountMin, playCountMax, pageQuery);
        QueryWrapper wrapper = buildPublicSearchWrapper(keyword, tag, tags, style, isOriginal, isAi, resourceStatus, startDate, endDate, playCountMin, playCountMax);
        applySort(wrapper, sort);
        Page<MusicVo> result = musicMapper.selectVoPage(pageQuery.build(), wrapper);
        musicInteractionService.fillDynamicStats(result.getRecords());
        applySearchHighlights(result.getRecords(), keyword);
        return TableDataInfo.build(result);
    }

    private TableDataInfo<MusicVo> searchByRelevance(String keyword, String tag, String tags, String style, String isOriginal, String isAi, String resourceStatus, String startDate, String endDate, Long playCountMin, Long playCountMax, PageQuery pageQuery) {
        QueryWrapper wrapper = buildPublicSearchWrapper(keyword, tag, tags, style, isOriginal, isAi, resourceStatus, startDate, endDate, playCountMin, playCountMax);
        wrapper.orderBy(MUSIC.PLAY_COUNT.desc(), MUSIC.PUBLISH_TIME.desc());
        Page<MusicVo> requestedPage = pageQuery.build();
        int fetchSize = Math.min(Math.max((int) requestedPage.getPageSize() * 8, DEFAULT_RELEVANCE_FETCH_LIMIT), MAX_RELEVANCE_FETCH_LIMIT);
        Page<MusicVo> candidatePage = musicMapper.selectVoPage(new Page<>(1, fetchSize), wrapper);
        List<MusicVo> candidates = candidatePage.getRecords() == null ? new ArrayList<>() : new ArrayList<>(candidatePage.getRecords());
        musicInteractionService.fillDynamicStats(candidates);
        applySearchHighlights(candidates, keyword);
        applyRelevanceRanking(candidates, keyword, tag, tags, style);
        int fromIndex = (int) Math.max(0, (requestedPage.getPageNumber() - 1) * requestedPage.getPageSize());
        if (fromIndex >= candidates.size()) return new TableDataInfo<>(Collections.emptyList(), candidatePage.getTotalRow());
        int toIndex = Math.min(candidates.size(), fromIndex + (int) requestedPage.getPageSize());
        return new TableDataInfo<>(new ArrayList<>(candidates.subList(fromIndex, toIndex)), candidatePage.getTotalRow());
    }

    private QueryWrapper buildPublicSearchWrapper(String keyword, String tag, String tags, String style, String isOriginal, String isAi, String resourceStatus, String startDate, String endDate, Long playCountMin, Long playCountMax) {
        QueryWrapper wrapper = QueryWrapper.create().where(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED).and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE)));
        QueryCondition keywordCondition = buildKeywordCondition(buildExpandedSearchTerms(keyword));
        if (keywordCondition != null) wrapper.and(keywordCondition);
        for (String tagValue : parseTagFilters(tag, tags, style)) {
            wrapper.and("lower(coalesce(tags_snapshot::text, '')) like ?", "%" + tagValue.toLowerCase(Locale.ROOT) + "%");
        }
        String normalizedOriginal = normalizeOriginalFilter(isOriginal);
        if (StringUtils.isNotBlank(normalizedOriginal)) wrapper.and(MUSIC.IS_ORIGINAL.eq(normalizedOriginal));
        applyAiFilter(wrapper, isAi);
        if (StringUtils.isNotBlank(resourceStatus)) wrapper.and(MUSIC.RESOURCE_STATUS.eq(resourceStatus.trim()));
        if (playCountMin != null && playCountMin >= 0) wrapper.and(MUSIC.PLAY_COUNT.ge(playCountMin));
        if (playCountMax != null && playCountMax >= 0) wrapper.and(MUSIC.PLAY_COUNT.le(playCountMax));
        java.util.Date parsedStartDate = parseDateStart(startDate);
        if (parsedStartDate != null) wrapper.and(MUSIC.PUBLISH_TIME.ge(parsedStartDate));
        java.util.Date parsedEndDate = parseDateEnd(endDate);
        if (parsedEndDate != null) wrapper.and(MUSIC.PUBLISH_TIME.le(parsedEndDate));
        return wrapper;
    }

    private void applyAiFilter(QueryWrapper wrapper, String isAi) {
        String normalized = normalizeBooleanLikeFilter(isAi);
        if (StringUtils.isBlank(normalized)) return;
        if ("1".equals(normalized)) {
            wrapper.and("(lower(coalesce(tags_snapshot::text, '')) like ? or lower(coalesce(extend_data::text, '')) like ?)", "%ai%", "%\"isai\":true%");
            return;
        }
        wrapper.and("(lower(coalesce(tags_snapshot::text, '')) not like ? and lower(coalesce(extend_data::text, '')) not like ?)", "%ai%", "%\"isai\":true%");
    }

    private QueryCondition buildKeywordCondition(List<String> terms) {
        if (terms == null || terms.isEmpty()) return null;
        QueryCondition combined = null;
        for (String term : terms) {
            QueryCondition current = MUSIC.TITLE.like(term).or(MUSIC.SUBTITLE.like(term)).or(MUSIC.ORIGINAL_TITLE.like(term)).or(MUSIC.CREATOR_NAME.like(term));
            combined = combined == null ? current : combined.and(current);
        }
        return combined;
    }

    private MusicChartSnapshot loadLatestSnapshot(String chartType, String periodKey) {
        QueryWrapper wrapper = QueryWrapper.create().where(MUSIC_CHART_SNAPSHOT.STATUS.eq(CHART_ACTIVE));
        if (StringUtils.isNotBlank(chartType)) wrapper.and(MUSIC_CHART_SNAPSHOT.CHART_TYPE.eq(chartType));
        if (StringUtils.isNotBlank(periodKey)) wrapper.and(MUSIC_CHART_SNAPSHOT.PERIOD_KEY.eq(periodKey));
        wrapper.orderBy(MUSIC_CHART_SNAPSHOT.ID.desc());
        return musicChartSnapshotMapper.selectOneByQuery(wrapper);
    }

    private Map<Long, MusicVo> loadPublicSongs(List<Long> musicIds) {
        if (musicIds == null || musicIds.isEmpty()) return Collections.emptyMap();
        List<MusicVo> songs = musicMapper.selectVoList(QueryWrapper.create().where(MUSIC.ID.in(musicIds).and(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED)).and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE))));
        musicInteractionService.fillDynamicStats(songs);
        return songs.stream().collect(Collectors.toMap(MusicVo::getId, item -> item, (left, right) -> left, LinkedHashMap::new));
    }

    private OpenChartItemVo toOpenChartItem(MusicChartItemVo item, MusicVo song) {
        OpenChartItemVo vo = new OpenChartItemVo();
        vo.setId(item.getId());
        vo.setSnapshotId(item.getSnapshotId());
        vo.setMusicId(item.getMusicId());
        vo.setRankNo(item.getRankNo());
        vo.setScore(item.getScore());
        vo.setPlayCount(item.getPlayCount());
        vo.setLikeCount(item.getLikeCount());
        vo.setSong(song);
        return vo;
    }

    private OpenChartVo createEmptyChart(String chartType, String periodKey) {
        OpenChartVo chart = new OpenChartVo();
        chart.setChartType(StringUtils.defaultIfBlank(chartType, "week"));
        chart.setPeriodKey(periodKey);
        chart.setStatus("empty");
        chart.setItems(Collections.emptyList());
        return chart;
    }

    private OpenUserProfileVo createEmptyUserProfile(String uid, Long creatorId) {
        OpenUserProfileVo profile = new OpenUserProfileVo();
        profile.setUid(creatorId == null ? uid : String.valueOf(creatorId));
        profile.setDisplayName(uid);
        profile.setSongCount(0L);
        profile.setTotalPlayCount(0L);
        profile.setTotalLikeCount(0L);
        profile.setTotalCollectCount(0L);
        profile.setTotalCommentCount(0L);
        profile.setSongs(Collections.emptyList());
        return profile;
    }

    private OpenSearchSuggestVo toSongSuggestVo(MusicVo musicVo, String keyword) {
        OpenSearchSuggestVo vo = new OpenSearchSuggestVo();
        vo.setMusicId(musicVo.getId());
        vo.setKeyword(keyword);
        vo.setTitle(musicVo.getTitle());
        vo.setSubtitle(firstNonBlank(musicVo.getSubtitle(), musicVo.getOriginalTitle()));
        vo.setCreatorName(musicVo.getCreatorName());
        vo.setSuggestType("song");
        vo.setMatchType(resolveMatchType(musicVo, keyword));
        vo.setMatchedText(resolveMatchedText(musicVo, vo.getMatchType()));
        vo.setHighlightedText(buildHighlightedText(vo.getMatchedText(), keyword));
        return vo;
    }

    private List<OpenSearchSuggestVo> buildCreatorSuggestions(List<MusicVo> rows, String keyword, int limit) {
        return rows.stream().map(MusicVo::getCreatorName).filter(value -> containsIgnoreCase(value, keyword)).filter(StringUtils::isNotBlank).distinct().limit(Math.max(1, limit / 3)).map(value -> {
            OpenSearchSuggestVo vo = new OpenSearchSuggestVo();
            vo.setKeyword(value);
            vo.setTitle(value);
            vo.setSuggestType("creator");
            vo.setMatchType("creator_name");
            vo.setMatchedText(value);
            vo.setHighlightedText(buildHighlightedText(value, keyword));
            return vo;
        }).toList();
    }

    private List<OpenSearchSuggestVo> buildTagSuggestions(String keyword, int limit) {
        List<TagVo> tags = tagMapper.selectVoList(QueryWrapper.create().where(TAG.STATUS.eq("0").and(TAG.NAME.like(keyword).or(TAG.TAG_ALIAS.like(keyword)))).orderBy(TAG.IS_HOT.desc(), TAG.USE_COUNT.desc(), TAG.SORT_ORDER.asc(), TAG.ID.asc()));
        if (tags.isEmpty()) return Collections.emptyList();
        return tags.stream().limit(Math.max(1, limit / 3)).map(tag -> {
            OpenSearchSuggestVo vo = new OpenSearchSuggestVo();
            vo.setTagId(tag.getId());
            vo.setKeyword(tag.getName());
            vo.setTitle(tag.getName());
            vo.setSubtitle(firstNonBlank(tag.getTagAlias(), tag.getType()));
            vo.setSuggestType("tag");
            vo.setMatchType("tag");
            vo.setMatchedText(firstNonBlank(tag.getName(), tag.getTagAlias()));
            vo.setHighlightedText(buildHighlightedText(vo.getMatchedText(), keyword));
            return vo;
        }).toList();
    }

    private String resolveMatchedText(MusicVo musicVo, String matchType) {
        return switch (matchType) {
            case "title" -> musicVo.getTitle();
            case "subtitle" -> firstNonBlank(musicVo.getSubtitle(), musicVo.getOriginalTitle());
            case "original_title" -> musicVo.getOriginalTitle();
            case "creator_name" -> musicVo.getCreatorName();
            case "tag" -> musicVo.getTagsSnapshot();
            default -> firstNonBlank(musicVo.getTitle(), musicVo.getCreatorName());
        };
    }

    private String buildHighlightedText(String text, String keyword) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(keyword)) return text;
        String lowerText = text.toLowerCase();
        String lowerKeyword = keyword.toLowerCase();
        int start = lowerText.indexOf(lowerKeyword);
        if (start < 0) return text;
        int end = start + keyword.length();
        return text.substring(0, start) + "<em>" + text.substring(start, end) + "</em>" + text.substring(end);
    }

    private String resolveMatchType(MusicVo musicVo, String keyword) {
        if (containsIgnoreCase(musicVo.getTitle(), keyword)) return "title";
        if (containsIgnoreCase(musicVo.getSubtitle(), keyword)) return "subtitle";
        if (containsIgnoreCase(musicVo.getOriginalTitle(), keyword)) return "original_title";
        if (containsIgnoreCase(musicVo.getCreatorName(), keyword)) return "creator_name";
        if (containsIgnoreCase(musicVo.getTagsSnapshot(), keyword)) return "tag";
        return "keyword";
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return StringUtils.isNotBlank(value) && StringUtils.isNotBlank(keyword) && value.toLowerCase().contains(keyword.toLowerCase());
    }

    private void applyRelevanceRanking(List<MusicVo> rows, String keyword, String tag, String tags, String style) {
        if (rows == null || rows.isEmpty()) return;
        List<String> baseTerms = parseSearchTerms(keyword);
        List<String> expandedTerms = buildExpandedSearchTerms(keyword);
        List<String> selectedTags = parseTagFilters(tag, tags, style);
        for (MusicVo row : rows) row.setSearchScore(computeSearchScore(row, baseTerms, expandedTerms, selectedTags));
        rows.sort(Comparator.comparing(MusicVo::getSearchScore, Comparator.nullsLast(Comparator.reverseOrder())).thenComparing(MusicVo::getPlayCount, Comparator.nullsLast(Comparator.reverseOrder())).thenComparing(MusicVo::getLikeCount, Comparator.nullsLast(Comparator.reverseOrder())).thenComparing(MusicVo::getPublishTime, Comparator.nullsLast(Comparator.reverseOrder())).thenComparing(MusicVo::getId, Comparator.nullsLast(Comparator.reverseOrder())));
    }

    private double computeSearchScore(MusicVo row, List<String> baseTerms, List<String> expandedTerms, List<String> selectedTags) {
        double score = 0D;
        for (String term : baseTerms) score += computeFieldMatchScore(row, term, true);
        for (String term : expandedTerms) if (!baseTerms.contains(term)) score += computeFieldMatchScore(row, term, false);
        List<String> tags = extractTagNames(row.getTagsSnapshot());
        for (String selectedTag : selectedTags) if (tags.stream().anyMatch(item -> equalsIgnoreCase(item, selectedTag))) score += 20D;
        score += Math.log1p(safeLong(row.getPlayCount())) * 2.2D;
        score += Math.log1p(safeLong(row.getLikeCount())) * 3.0D;
        score += Math.log1p(safeLong(row.getCollectCount())) * 3.6D;
        score += Math.log1p(safeLong(row.getShareCount())) * 2.8D;
        score += Math.log1p(safeLong(row.getDownloadCount())) * 2.0D;
        score += recencyScore(row.getPublishTime());
        return score;
    }

    private double computeFieldMatchScore(MusicVo row, String term, boolean directTerm) {
        if (StringUtils.isBlank(term)) return 0D;
        double factor = directTerm ? 1D : 0.55D;
        double score = 0D;
        score += fieldScore(row.getTitle(), term, 140D, 90D) * factor;
        score += fieldScore(row.getSubtitle(), term, 90D, 55D) * factor;
        score += fieldScore(row.getOriginalTitle(), term, 80D, 50D) * factor;
        score += fieldScore(row.getCreatorName(), term, 70D, 40D) * factor;
        score += tagsScore(row.getTagsSnapshot(), term, directTerm ? 55D : 30D);
        return score;
    }

    private double fieldScore(String value, String term, double exactScore, double containsScore) {
        if (StringUtils.isBlank(value) || StringUtils.isBlank(term)) return 0D;
        String normalizedValue = normalizeComparableText(value);
        String normalizedTerm = normalizeComparableText(term);
        if (normalizedValue.equals(normalizedTerm)) return exactScore;
        if (normalizedValue.startsWith(normalizedTerm)) return containsScore + 10D;
        if (normalizedValue.contains(normalizedTerm)) return containsScore;
        return 0D;
    }

    private double tagsScore(String tagsSnapshot, String term, double hitScore) {
        if (StringUtils.isBlank(term)) return 0D;
        for (String item : extractTagNames(tagsSnapshot)) if (fieldScore(item, term, hitScore + 10D, hitScore) > 0D) return fieldScore(item, term, hitScore + 10D, hitScore);
        return 0D;
    }

    private double recencyScore(java.util.Date publishTime) {
        if (publishTime == null) return 0D;
        long days = ChronoUnit.DAYS.between(publishTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), LocalDateTime.now(ZoneId.systemDefault()));
        return Math.max(0D, 30D - Math.max(0L, days)) * 0.8D;
    }

    private String normalizeOriginalFilter(String value) {
        if (StringUtils.isBlank(value)) return null;
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if ("1".equals(normalized) || "true".equals(normalized) || "yes".equals(normalized) || "original".equals(normalized)) return "1";
        if ("0".equals(normalized) || "false".equals(normalized) || "no".equals(normalized) || "cover".equals(normalized)) return "0";
        return null;
    }

    private String normalizeBooleanLikeFilter(String value) {
        if (StringUtils.isBlank(value)) return null;
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if ("1".equals(normalized) || "true".equals(normalized) || "yes".equals(normalized) || "ai".equals(normalized)) return "1";
        if ("0".equals(normalized) || "false".equals(normalized) || "no".equals(normalized) || "non_ai".equals(normalized) || "no_ai".equals(normalized)) return "0";
        return null;
    }

    private java.util.Date parseDateStart(String value) {
        LocalDate date = parseDateFilter(value, false);
        if (date == null) return null;
        return java.util.Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private java.util.Date parseDateEnd(String value) {
        LocalDate date = parseDateFilter(value, true);
        if (date == null) return null;
        return java.util.Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant());
    }

    private LocalDate parseDateFilter(String value, boolean useMonthEnd) {
        if (StringUtils.isBlank(value)) return null;
        String normalized = value.trim();
        try {
            if (normalized.matches("\\d{4}-\\d{2}")) {
                LocalDate monthStart = LocalDate.parse(normalized + "-01");
                return useMonthEnd ? monthStart.withDayOfMonth(monthStart.lengthOfMonth()) : monthStart;
            }
            return LocalDate.parse(normalized);
        } catch (Exception ex) {
            return null;
        }
    }

    private Long parseCreatorId(String uid) {
        try {
            return Long.valueOf(uid);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeComparableText(String value) {
        return normalizeKeyword(value) == null ? "" : normalizeKeyword(value).toLowerCase(Locale.ROOT);
    }

    private boolean isRelevanceSort(String sort, String keyword) {
        if (StringUtils.isBlank(keyword)) return false;
        String normalized = StringUtils.defaultIfBlank(sort, "relevance").toLowerCase(Locale.ROOT);
        return "relevance".equals(normalized) || "smart".equals(normalized) || "composite_hot".equals(normalized) || "hot_score_desc".equals(normalized);
    }

    private void applySearchHighlights(List<MusicVo> rows, String keyword) {
        if (rows == null || rows.isEmpty()) return;
        List<String> searchTerms = parseSearchTerms(keyword);
        if (searchTerms.isEmpty()) return;
        for (MusicVo row : rows) {
            row.setHighlightTitle(highlightByTerms(row.getTitle(), searchTerms));
            row.setHighlightSubtitle(highlightByTerms(row.getSubtitle(), searchTerms));
            row.setHighlightOriginalTitle(highlightByTerms(row.getOriginalTitle(), searchTerms));
            row.setHighlightCreatorName(highlightByTerms(row.getCreatorName(), searchTerms));
            row.setHighlightTags(highlightByTerms(joinTagText(row.getTagsSnapshot()), searchTerms));
        }
    }

    private List<OpenSearchFacetItemVo> buildFacetItems(List<MusicVo> rows, int limit, Function<MusicVo, String> extractor, String type, List<String> selectedValues) {
        Map<String, Long> counts = rows.stream().map(extractor).filter(StringUtils::isNotBlank).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return counts.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry::getKey)).limit(limit).map(entry -> toFacetItem(type, entry.getKey(), entry.getValue(), selectedValues.contains(entry.getKey()))).toList();
    }

    private List<OpenSearchFacetItemVo> buildTagFacetItems(List<MusicVo> rows, int limit, List<String> selectedTags) {
        Map<String, Long> counts = rows.stream().map(MusicVo::getTagsSnapshot).filter(StringUtils::isNotBlank).flatMap(value -> extractTagNames(value).stream()).filter(StringUtils::isNotBlank).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return counts.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry::getKey)).limit(limit).map(entry -> toFacetItem("tag", entry.getKey(), entry.getValue(), selectedTags.contains(entry.getKey()))).toList();
    }

    private List<OpenSearchFacetItemVo> buildStyleTagFacetItems(List<MusicVo> rows, int limit, String selectedStyle) {
        List<String> selectedStyles = parseTagFilters(null, null, selectedStyle);
        List<TagVo> configuredStyles = tagMapper.selectVoList(QueryWrapper.create().where(TAG.STATUS.eq("0").and(TAG.TYPE.eq("style").or(TAG.TYPE.eq("genre")).or(TAG.TYPE.eq("风格")))).orderBy(TAG.IS_RECOMMEND.desc(), TAG.IS_HOT.desc(), TAG.USE_COUNT.desc(), TAG.SORT_ORDER.asc(), TAG.ID.asc()));
        Map<String, Long> styleCounts = rows.stream().map(MusicVo::getTagsSnapshot).filter(StringUtils::isNotBlank).flatMap(value -> extractTagNames(value).stream()).filter(StringUtils::isNotBlank).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        LinkedHashMap<String, Long> merged = new LinkedHashMap<>();
        for (TagVo tag : configuredStyles) if (StringUtils.isNotBlank(tag.getName())) merged.put(tag.getName(), styleCounts.getOrDefault(tag.getName(), 0L));
        styleCounts.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry::getKey)).forEach(entry -> merged.putIfAbsent(entry.getKey(), entry.getValue()));
        return merged.entrySet().stream().filter(entry -> entry.getValue() > 0 || selectedStyles.contains(entry.getKey())).limit(limit).map(entry -> toFacetItem("style", entry.getKey(), entry.getValue(), selectedStyles.contains(entry.getKey()))).toList();
    }

    private List<OpenSearchFacetItemVo> buildActiveFilterFacets(List<String> selectedTags, String isOriginal, String isAi, String resourceStatus, String startDate, String endDate, Long playCountMin, Long playCountMax) {
        List<OpenSearchFacetItemVo> filters = new ArrayList<>();
        if (selectedTags != null) selectedTags.stream().filter(StringUtils::isNotBlank).forEach(tag -> filters.add(toActiveFilter("tag", tag, tag)));
        String normalizedOriginal = normalizeOriginalFilter(isOriginal);
        if ("1".equals(normalizedOriginal)) filters.add(toActiveFilter("isOriginal", "原创作品", normalizedOriginal));
        else if ("0".equals(normalizedOriginal)) filters.add(toActiveFilter("isOriginal", "翻唱/二创", normalizedOriginal));
        String normalizedAi = normalizeBooleanLikeFilter(isAi);
        if ("1".equals(normalizedAi)) filters.add(toActiveFilter("isAi", "AI 作品", normalizedAi));
        else if ("0".equals(normalizedAi)) filters.add(toActiveFilter("isAi", "排除 AI", normalizedAi));
        if (StringUtils.isNotBlank(resourceStatus)) filters.add(toActiveFilter("resourceStatus", resolveResourceStatusLabel(resourceStatus), resourceStatus.trim()));
        if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) filters.add(toActiveFilter("dateRange", buildRangeLabel(startDate, endDate, "发布时间"), firstNonBlank(startDate, "") + "~" + firstNonBlank(endDate, "")));
        if (playCountMin != null || playCountMax != null) filters.add(toActiveFilter("playRange", buildRangeLabel(playCountMin, playCountMax, "播放量"), String.valueOf(playCountMin) + "~" + playCountMax));
        return filters;
    }

    private OpenSearchFacetItemVo toActiveFilter(String type, String label, String value) {
        OpenSearchFacetItemVo item = new OpenSearchFacetItemVo();
        item.setType(type);
        item.setLabel(label);
        item.setValue(value);
        item.setDescription("active");
        item.setCount(null);
        item.setSelected(Boolean.TRUE);
        return item;
    }

    private String resolveResourceStatusLabel(String resourceStatus) {
        String normalized = resourceStatus == null ? "" : resourceStatus.trim();
        return switch (normalized) {
            case "0" -> "资源正常";
            case "1" -> "部分失效";
            case "2" -> "需补档";
            default -> "资源状态 " + normalized;
        };
    }

    private String buildRangeLabel(String start, String end, String prefix) {
        if (StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) return prefix + " " + start + " 至 " + end;
        if (StringUtils.isNotBlank(start)) return prefix + " 自 " + start;
        return prefix + " 至 " + end;
    }

    private String buildRangeLabel(Long min, Long max, String prefix) {
        if (min != null && max != null) return prefix + " " + min + " 至 " + max;
        if (min != null) return prefix + " ≥ " + min;
        return prefix + " ≤ " + max;
    }

    private OpenSearchFacetItemVo toFacetItem(String type, String value, Long count, boolean selected) {
        OpenSearchFacetItemVo item = new OpenSearchFacetItemVo();
        item.setType(type);
        item.setLabel(value);
        item.setValue(value);
        item.setDescription(null);
        item.setCount(count);
        item.setSelected(selected);
        return item;
    }

    private List<OpenSearchFacetItemVo> buildMatchFieldFacets(List<MusicVo> rows, String keyword, int limit) {
        List<String> searchTerms = parseSearchTerms(keyword);
        if (rows == null || rows.isEmpty() || searchTerms.isEmpty()) return Collections.emptyList();
        Map<String, Long> fieldCounts = new LinkedHashMap<>();
        fieldCounts.put("title", countMatches(rows, MusicVo::getTitle, searchTerms));
        fieldCounts.put("subtitle", countMatches(rows, MusicVo::getSubtitle, searchTerms));
        fieldCounts.put("original_title", countMatches(rows, MusicVo::getOriginalTitle, searchTerms));
        fieldCounts.put("creator_name", countMatches(rows, MusicVo::getCreatorName, searchTerms));
        fieldCounts.put("tag", countMatches(rows, MusicVo::getTagsSnapshot, searchTerms));
        return fieldCounts.entrySet().stream().filter(entry -> entry.getValue() > 0).sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(Math.max(1, limit / 2)).map(entry -> toMatchFieldFacet(entry.getKey(), entry.getValue())).toList();
    }

    private long countMatches(List<MusicVo> rows, Function<MusicVo, String> extractor, List<String> searchTerms) {
        return rows.stream().filter(row -> searchTerms.stream().anyMatch(term -> containsIgnoreCase(extractor.apply(row), term))).count();
    }

    private OpenSearchFacetItemVo toMatchFieldFacet(String field, Long count) {
        OpenSearchFacetItemVo item = new OpenSearchFacetItemVo();
        item.setType("match_field");
        item.setLabel(resolveFieldLabel(field));
        item.setValue(field);
        item.setDescription(resolveFieldDescription(field));
        item.setCount(count);
        item.setSelected(Boolean.FALSE);
        return item;
    }

    private String resolveFieldLabel(String field) {
        return switch (field) {
            case "title" -> "标题";
            case "subtitle" -> "副标题";
            case "original_title" -> "原曲名";
            case "creator_name" -> "创作者";
            case "tag" -> "标签";
            default -> field;
        };
    }

    private String resolveFieldDescription(String field) {
        return switch (field) {
            case "title" -> "更适合按作品名精确收敛结果";
            case "subtitle" -> "可用别名或副标题继续缩小范围";
            case "original_title" -> "适合按原曲来源继续检索";
            case "creator_name" -> "建议结合创作者筛选提升命中率";
            case "tag" -> "建议继续叠加标签做二次筛选";
            default -> null;
        };
    }

    private List<OpenSearchSortOptionVo> buildSortOptions(String keyword, String currentSort, String recommendedSort) {
        List<OpenSearchSortOptionVo> options = new ArrayList<>();
        if (StringUtils.isNotBlank(normalizeKeyword(keyword))) options.add(toSortOption("relevance", "综合相关", "优先按标题、创作者、标签和热度综合排序", currentSort, recommendedSort));
        options.add(toSortOption("publish_time_desc", "最新发布", "适合快速看近期新增作品", currentSort, recommendedSort));
        options.add(toSortOption("play_count_desc", "播放最多", "按播放量优先，适合看热门结果", currentSort, recommendedSort));
        options.add(toSortOption("like_count_desc", "点赞最多", "按点赞量优先，适合看口碑结果", currentSort, recommendedSort));
        options.add(toSortOption("collect_count_desc", "收藏最多", "按收藏量优先，适合看高留存作品", currentSort, recommendedSort));
        options.add(toSortOption("share_count_desc", "分享最多", "按分享量优先，适合看传播强的作品", currentSort, recommendedSort));
        options.add(toSortOption("download_count_desc", "下载最多", "按下载量优先，适合看强需求作品", currentSort, recommendedSort));
        return options;
    }

    private OpenSearchSortOptionVo toSortOption(String code, String label, String description, String currentSort, String recommendedSort) {
        OpenSearchSortOptionVo option = new OpenSearchSortOptionVo();
        option.setCode(code);
        option.setLabel(label);
        option.setDescription(description);
        option.setSelected(equalsIgnoreCase(code, currentSort));
        option.setRecommended(equalsIgnoreCase(code, recommendedSort));
        return option;
    }

    private String buildSearchSummary(String keyword, String correctedKeyword, List<String> selectedTags, String currentSort, List<MusicVo> rows) {
        long total = rows == null ? 0L : rows.size();
        String normalizedKeyword = normalizeKeyword(keyword);
        if (total <= 0) {
            if (StringUtils.isNotBlank(correctedKeyword) && !equalsIgnoreCase(correctedKeyword, normalizedKeyword)) return "当前条件暂无结果，建议改用纠错词“" + correctedKeyword + "”重新检索。";
            return "当前条件暂无结果，建议放宽标签或切换到更宽松的排序方式。";
        }
        if (StringUtils.isBlank(normalizedKeyword) && (selectedTags == null || selectedTags.isEmpty())) return "当前为公开曲库总览，建议优先使用最新发布或热门排序浏览。";
        StringBuilder summary = new StringBuilder();
        summary.append("当前条件命中 ").append(total).append(" 首作品");
        if (StringUtils.isNotBlank(normalizedKeyword)) summary.append("，关键词为“").append(normalizedKeyword).append("”");
        if (selectedTags != null && !selectedTags.isEmpty()) summary.append("，已叠加 ").append(selectedTags.size()).append(" 个标签筛选");
        if (!equalsIgnoreCase(currentSort, resolveRecommendedSort(keyword))) summary.append("，可切换到推荐排序提升结果相关性");
        summary.append("。");
        return summary.toString();
    }

    private String normalizePanelSort(String sort, String keyword) {
        if (isRelevanceSort(sort, keyword)) return "relevance";
        SortSpec sortSpec = resolveSort(StringUtils.defaultIfBlank(sort, resolveRecommendedSort(keyword)));
        return resolveSortCode(sortSpec);
    }

    private String resolveRecommendedSort(String keyword) {
        return StringUtils.isNotBlank(normalizeKeyword(keyword)) ? "relevance" : DEFAULT_SORT;
    }

    private List<String> buildExpandedSearchTerms(String keyword) {
        List<String> baseTerms = parseSearchTerms(keyword);
        if (baseTerms.isEmpty()) return Collections.emptyList();
        LinkedHashSet<String> expanded = new LinkedHashSet<>(baseTerms);
        expanded.addAll(resolveBuiltinSynonyms(baseTerms));
        for (Tag tag : loadRelatedTags(baseTerms)) {
            if (StringUtils.isNotBlank(tag.getName())) expanded.add(tag.getName().trim());
            expanded.addAll(parseAliasTerms(tag.getTagAlias()));
        }
        return new ArrayList<>(expanded);
    }

    private List<String> resolveSynonymKeywords(String keyword) {
        List<String> baseTerms = parseSearchTerms(keyword);
        if (baseTerms.isEmpty()) return Collections.emptyList();
        LinkedHashSet<String> synonyms = new LinkedHashSet<>();
        synonyms.addAll(resolveBuiltinSynonyms(baseTerms));
        for (Tag tag : loadRelatedTags(baseTerms)) {
            if (StringUtils.isNotBlank(tag.getName())) synonyms.add(tag.getName().trim());
            synonyms.addAll(parseAliasTerms(tag.getTagAlias()));
        }
        synonyms.removeAll(baseTerms);
        return new ArrayList<>(synonyms);
    }

    private String resolveCorrectedKeyword(String keyword, List<String> synonymKeywords) {
        String normalizedKeyword = normalizeKeyword(keyword);
        if (StringUtils.isBlank(normalizedKeyword)) return null;
        for (Tag tag : loadRelatedTags(parseSearchTerms(keyword))) {
            if (equalsIgnoreCase(tag.getName(), normalizedKeyword)) return normalizedKeyword;
            for (String alias : parseAliasTerms(tag.getTagAlias())) if (equalsIgnoreCase(alias, normalizedKeyword)) return tag.getName();
        }
        if (synonymKeywords != null && !synonymKeywords.isEmpty()) return synonymKeywords.get(0);
        Tag nearestTag = findNearestTag(normalizedKeyword);
        if (nearestTag != null && StringUtils.isNotBlank(nearestTag.getName()) && !equalsIgnoreCase(nearestTag.getName(), normalizedKeyword)) return nearestTag.getName();
        return normalizedKeyword;
    }

    private List<Tag> loadRelatedTags(List<String> baseTerms) {
        if (baseTerms == null || baseTerms.isEmpty()) return Collections.emptyList();
        QueryWrapper wrapper = QueryWrapper.create().where(TAG.STATUS.eq("0"));
        QueryCondition condition = null;
        for (String term : baseTerms) {
            QueryCondition current = TAG.NAME.like(term).or(TAG.TAG_ALIAS.like(term));
            condition = condition == null ? current : condition.or(current);
        }
        wrapper.and(condition);
        wrapper.orderBy(TAG.USE_COUNT.desc(), TAG.IS_HOT.desc(), TAG.SORT_ORDER.asc(), TAG.ID.asc());
        return tagMapper.selectListByQueryAs(wrapper, Tag.class);
    }

    private Tag findNearestTag(String keyword) {
        if (StringUtils.isBlank(keyword)) return null;
        List<Tag> tags = tagMapper.selectListByQueryAs(QueryWrapper.create().where(TAG.STATUS.eq("0")).orderBy(TAG.USE_COUNT.desc(), TAG.IS_HOT.desc(), TAG.SORT_ORDER.asc(), TAG.ID.asc()), Tag.class);
        Tag best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Tag tag : tags) {
            for (String candidate : buildTagCandidates(tag)) {
                int distance = levenshteinDistance(keyword, candidate);
                if (distance < bestDistance) {
                    best = tag;
                    bestDistance = distance;
                }
            }
        }
        return bestDistance <= typoThreshold(keyword) ? best : null;
    }

    private List<String> buildTagCandidates(Tag tag) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        if (StringUtils.isNotBlank(tag.getName())) candidates.add(tag.getName().trim());
        candidates.addAll(parseAliasTerms(tag.getTagAlias()));
        return new ArrayList<>(candidates);
    }

    private List<String> parseAliasTerms(String rawAlias) {
        if (StringUtils.isBlank(rawAlias)) return Collections.emptyList();
        return Arrays.stream(rawAlias.split("[|,，/、；;\\s]+")).map(this::normalizeSearchTerm).filter(StringUtils::isNotBlank).filter(item -> !isStopWord(item)).distinct().toList();
    }

    private String normalizeSearchTerm(String term) {
        if (StringUtils.isBlank(term)) return null;
        String value = term.trim().replaceAll("^[\\p{Punct}，。！？、；：“”‘’（）【】《》]+", "").replaceAll("[\\p{Punct}，。！？、；：“”‘’（）【】《》]+$", "");
        return StringUtils.isBlank(value) ? null : value;
    }

    private boolean isStopWord(String term) {
        return SEARCH_STOP_WORDS.contains(term.toLowerCase(Locale.ROOT));
    }

    private List<String> resolveBuiltinSynonyms(List<String> terms) {
        if (terms == null || terms.isEmpty()) return Collections.emptyList();
        LinkedHashSet<String> results = new LinkedHashSet<>();
        Map<String, List<String>> mergedGroups = mergeSynonymGroups();
        for (String term : terms) {
            String normalized = term.toLowerCase(Locale.ROOT);
            for (Map.Entry<String, List<String>> entry : mergedGroups.entrySet()) {
                if (normalized.equals(entry.getKey().toLowerCase(Locale.ROOT))) {
                    results.addAll(entry.getValue().stream().map(this::normalizeSearchTerm).filter(StringUtils::isNotBlank).filter(item -> !isStopWord(item)).toList());
                }
            }
        }
        results.removeAll(terms);
        return new ArrayList<>(results);
    }

    private Map<String, List<String>> mergeSynonymGroups() {
        LinkedHashMap<String, List<String>> merged = new LinkedHashMap<>(BUILTIN_SYNONYM_GROUPS);
        Map<String, List<String>> custom = parseCustomSynonyms(customSynonymsConfig);
        if (custom.isEmpty()) return merged;
        custom.forEach((key, values) -> {
            LinkedHashSet<String> mergedValues = new LinkedHashSet<>();
            if (merged.containsKey(key)) mergedValues.addAll(merged.get(key));
            mergedValues.addAll(values);
            merged.put(key, new ArrayList<>(mergedValues));
        });
        return merged;
    }

    private Map<String, List<String>> parseCustomSynonyms(String rawConfig) {
        if (StringUtils.isBlank(rawConfig)) return Collections.emptyMap();
        LinkedHashMap<String, List<String>> groups = new LinkedHashMap<>();
        for (String group : rawConfig.split(";")) {
            if (StringUtils.isBlank(group) || !group.contains(":")) continue;
            String[] pair = group.split(":", 2);
            String key = normalizeSearchTerm(pair[0]);
            if (StringUtils.isBlank(key)) continue;
            List<String> values = Arrays.stream(pair[1].split("\\|")).map(this::normalizeSearchTerm).filter(StringUtils::isNotBlank).filter(item -> !isStopWord(item)).distinct().toList();
            if (!values.isEmpty()) groups.put(key, values);
        }
        return groups;
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return StringUtils.isNotBlank(left) && StringUtils.isNotBlank(right) && left.trim().toLowerCase(Locale.ROOT).equals(right.trim().toLowerCase(Locale.ROOT));
    }

    private int typoThreshold(String keyword) {
        if (keyword.length() <= 4) return 1;
        return 2;
    }

    private int levenshteinDistance(String left, String right) {
        if (Objects.equals(left, right)) return 0;
        if (StringUtils.isBlank(left)) return right.length();
        if (StringUtils.isBlank(right)) return left.length();
        int[] previous = new int[right.length() + 1];
        int[] current = new int[right.length() + 1];
        for (int index = 0; index <= right.length(); index++) previous[index] = index;
        for (int leftIndex = 1; leftIndex <= left.length(); leftIndex++) {
            current[0] = leftIndex;
            for (int rightIndex = 1; rightIndex <= right.length(); rightIndex++) {
                int substitutionCost = left.charAt(leftIndex - 1) == right.charAt(rightIndex - 1) ? 0 : 1;
                current[rightIndex] = Math.min(Math.min(current[rightIndex - 1] + 1, previous[rightIndex] + 1), previous[rightIndex - 1] + substitutionCost);
            }
            int[] temp = previous;
            previous = current;
            current = temp;
        }
        return previous[right.length()];
    }

    private String joinTagText(String tagsSnapshot) {
        List<String> tags = extractTagNames(tagsSnapshot);
        if (tags.isEmpty()) return null;
        return String.join(" / ", tags);
    }

    private String highlightByTerms(String text, List<String> searchTerms) {
        if (StringUtils.isBlank(text) || searchTerms == null || searchTerms.isEmpty()) return text;
        String highlighted = text;
        for (String term : searchTerms) highlighted = buildHighlightedText(highlighted, term);
        return highlighted;
    }

    private void recordSearchKeyword(String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);
        if (StringUtils.isBlank(normalizedKeyword)) return;
        try {
            RedisUtils.getClient().getScoredSortedSet(HOT_SEARCH_KEY).addScore(normalizedKeyword, 1D);
            RedisUtils.getClient().getScoredSortedSet(currentDailyHotKey()).addScore(normalizedKeyword, 1D);
            RedisUtils.expire(currentDailyHotKey(), Duration.ofDays(HOT_KEYWORD_RETENTION_DAYS));
        } catch (Exception ex) {
            log.warn("记录搜索热词失败", ex);
        }
    }

    private List<String> loadKeywordRange(String key, int size) {
        Collection<Object> keywords = RedisUtils.getClient().getScoredSortedSet(key).valueRangeReversed(0, size - 1);
        if (keywords == null || keywords.isEmpty()) return Collections.emptyList();
        return keywords.stream().map(String::valueOf).filter(StringUtils::isNotBlank).toList();
    }

    private String currentDailyHotKey() {
        return HOT_SEARCH_DAILY_KEY_PREFIX + LocalDate.now(ZoneId.systemDefault());
    }

    private String dailyHotKey(int daysAgo) {
        return HOT_SEARCH_DAILY_KEY_PREFIX + LocalDate.now(ZoneId.systemDefault()).minusDays(daysAgo);
    }

    private String normalizeKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) return null;
        String value = keyword.replace('\u3000', ' ').replace('，', ' ').replace('、', ' ').replace('；', ' ').replace(';', ' ').replace('|', ' ').trim().replaceAll("\\s+", " ");
        return value.length() > 64 ? value.substring(0, 64) : value;
    }

    private List<String> extractTagNames(String tagsSnapshot) {
        if (StringUtils.isBlank(tagsSnapshot)) return Collections.emptyList();
        try {
            JSONArray array = JSON.parseArray(tagsSnapshot);
            if (array == null || array.isEmpty()) return Collections.emptyList();
            List<String> tags = new ArrayList<>(array.size());
            for (Object item : array) {
                if (item instanceof String value && StringUtils.isNotBlank(value)) tags.add(value);
                else if (item instanceof JSONObject jsonObject) {
                    String value = firstNonBlank(jsonObject.getString("name"), jsonObject.getString("tagName"), jsonObject.getString("label"));
                    if (StringUtils.isNotBlank(value)) tags.add(value);
                }
            }
            return tags;
        } catch (Exception ex) {
            log.debug("解析标签快照失败: {}", tagsSnapshot, ex);
            return Collections.emptyList();
        }
    }

    private String firstNonBlank(String... values) {
        return Arrays.stream(values).filter(StringUtils::isNotBlank).findFirst().orElse(null);
    }

    private List<String> parseSearchTerms(String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);
        if (StringUtils.isBlank(normalizedKeyword)) return Collections.emptyList();
        return Arrays.stream(normalizedKeyword.split("[\\s,/]+")).map(this::normalizeSearchTerm).filter(StringUtils::isNotBlank).filter(item -> item.length() <= 32).filter(item -> !isStopWord(item)).collect(Collectors.collectingAndThen(Collectors.toCollection(LinkedHashSet::new), ArrayList::new));
    }

    private List<String> parseTagFilters(String tag, String tags, String style) {
        String rawTags = String.join(",", Arrays.stream(new String[]{tag, tags, style}).filter(StringUtils::isNotBlank).toList());
        if (StringUtils.isBlank(rawTags)) return Collections.emptyList();
        return Arrays.stream(rawTags.split("[,，|/]+")).map(String::trim).filter(StringUtils::isNotBlank).distinct().toList();
    }

    private long safeLong(Long value) {
        return value == null ? 0L : Math.max(0L, value);
    }

    private void applySort(QueryWrapper wrapper, String sort) {
        SortSpec sortSpec = resolveSort(sort);
        switch (sortSpec.field()) {
            case "play_count" -> wrapper.orderBy(MUSIC.PLAY_COUNT, sortSpec.asc());
            case "like_count" -> wrapper.orderBy(MUSIC.LIKE_COUNT, sortSpec.asc());
            case "collect_count" -> wrapper.orderBy(MUSIC.COLLECT_COUNT, sortSpec.asc());
            case "share_count" -> wrapper.orderBy(MUSIC.SHARE_COUNT, sortSpec.asc());
            case "download_count" -> wrapper.orderBy(MUSIC.DOWNLOAD_COUNT, sortSpec.asc());
            default -> wrapper.orderBy(MUSIC.PUBLISH_TIME, sortSpec.asc());
        }
    }

    private SortSpec resolveSort(String sort) {
        String normalized = StringUtils.defaultIfBlank(sort, DEFAULT_SORT).toLowerCase();
        return switch (normalized) {
            case "play_count_asc" -> new SortSpec("play_count", true);
            case "play_count_desc", "hot" -> new SortSpec("play_count", false);
            case "like_count_asc" -> new SortSpec("like_count", true);
            case "like_count_desc" -> new SortSpec("like_count", false);
            case "collect_count_asc" -> new SortSpec("collect_count", true);
            case "collect_count_desc" -> new SortSpec("collect_count", false);
            case "share_count_asc" -> new SortSpec("share_count", true);
            case "share_count_desc" -> new SortSpec("share_count", false);
            case "download_count_asc" -> new SortSpec("download_count", true);
            case "download_count_desc" -> new SortSpec("download_count", false);
            case "publish_time_asc" -> new SortSpec("publish_time", true);
            default -> new SortSpec("publish_time", false);
        };
    }

    private String resolveSortCode(SortSpec sortSpec) {
        if (sortSpec == null) return DEFAULT_SORT;
        return switch (sortSpec.field()) {
            case "play_count" -> sortSpec.asc() ? "play_count_asc" : "play_count_desc";
            case "like_count" -> sortSpec.asc() ? "like_count_asc" : "like_count_desc";
            case "collect_count" -> sortSpec.asc() ? "collect_count_asc" : "collect_count_desc";
            case "share_count" -> sortSpec.asc() ? "share_count_asc" : "share_count_desc";
            case "download_count" -> sortSpec.asc() ? "download_count_asc" : "download_count_desc";
            default -> sortSpec.asc() ? "publish_time_asc" : "publish_time_desc";
        };
    }

    private int normalizeLimit(Integer limit, int defaultValue, int maxValue) {
        if (limit == null || limit <= 0) return defaultValue;
        return Math.min(limit, maxValue);
    }

    private record SortSpec(String field, boolean asc) {
    }
}
