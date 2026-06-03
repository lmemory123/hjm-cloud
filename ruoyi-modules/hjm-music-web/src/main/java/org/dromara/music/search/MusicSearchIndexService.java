package org.dromara.music.search;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.momao.valkey.core.BulkSaveItem;
import com.momao.valkey.core.BulkWriteOptions;
import com.momao.valkey.core.BulkWriteResult;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.mapper.MusicMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import static org.dromara.music.domain.table.MusicTableDef.MUSIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicSearchIndexService {

    private static final String AUDIT_APPROVED = "1";
    private static final String PUBLIC_VISIBLE = "1";

    private final MusicMapper musicMapper;
    private final MusicSearchRepository musicSearchRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void syncPublicIndexOnReady() {
        try {
            ensureSearchIndex();
            syncPublicIndex();
        } catch (Exception ex) {
            log.error("应用启动后同步音乐 Valkey 搜索索引失败", ex);
        }
    }

    public void ensureSearchIndex() {
        String result = musicSearchRepository.checkAndCreateIndex();
        log.info("Valkey music search index checked. result={}", result);
    }

    public void syncPublicIndex() {
        List<MusicVo> rows = musicMapper.selectVoList(publicWrapper());
        if (rows == null || rows.isEmpty()) {
            log.info("Valkey music search sync skipped: no public songs.");
            return;
        }
        List<BulkSaveItem<MusicSearchDocument>> items = rows.stream()
            .map(this::toDocument)
            .filter(Objects::nonNull)
            .map(document -> BulkSaveItem.of(document.getId(), document))
            .toList();
        if (items.isEmpty()) {
            return;
        }
        BulkWriteResult result = musicSearchRepository.saveAll(items, BulkWriteOptions.unordered());
        log.info("Valkey music search sync completed. submitted={}, succeeded={}, failed={}",
            result.submitted(), result.succeeded(), result.failed());
    }

    public boolean syncMusicIndex(Long musicId) {
        if (musicId == null) {
            return false;
        }
        MusicVo music = musicMapper.selectVoById(musicId);
        if (music == null || !AUDIT_APPROVED.equals(music.getAuditStatus()) || !PUBLIC_VISIBLE.equals(music.getIsPublic())) {
            return removeMusicIndex(musicId);
        }
        musicSearchRepository.save(toDocument(music));
        return true;
    }

    public boolean removeMusicIndex(Long musicId) {
        if (musicId == null) {
            return false;
        }
        musicSearchRepository.deleteAll(List.of(String.valueOf(musicId)), BulkWriteOptions.unordered());
        return true;
    }

    private QueryWrapper publicWrapper() {
        return QueryWrapper.create()
            .where(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED).and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE)))
            .orderBy(MUSIC.PUBLISH_TIME.desc(), MUSIC.ID.desc());
    }

    private MusicSearchDocument toDocument(MusicVo music) {
        if (music == null || music.getId() == null) {
            return null;
        }
        MusicSearchDocument document = new MusicSearchDocument();
        document.setId(String.valueOf(music.getId()));
        document.setTitle(music.getTitle());
        document.setSubtitle(music.getSubtitle());
        document.setOriginalTitle(music.getOriginalTitle());
        document.setCreatorId(music.getCreatorId());
        document.setCreatorName(music.getCreatorName());
        document.setCreatorLink(music.getCreatorLink());
        document.setProducerMark(music.getProducerMark());
        document.setDuration(music.getDuration());
        document.setBpm(music.getBpm());
        document.setPublishTimeMillis(toEpochMillis(music.getPublishTime()));
        document.setPlayCount(safeLong(music.getPlayCount()));
        document.setLikeCount(safeLong(music.getLikeCount()));
        document.setCollectCount(safeLong(music.getCollectCount()));
        document.setCommentCount(safeLong(music.getCommentCount()));
        document.setShareCount(safeLong(music.getShareCount()));
        document.setDownloadCount(safeLong(music.getDownloadCount()));
        document.setOriginalData(music.getOriginalData());
        document.setResourceData(music.getResourceData());
        document.setTagsSnapshot(music.getTagsSnapshot());
        document.setExtendData(music.getExtendData());
        document.setAuditStatus(music.getAuditStatus());
        document.setIsPublic(music.getIsPublic());
        document.setIsOriginal(music.getIsOriginal());
        document.setIsAi(resolveAiFlag(music));
        document.setResourceStatus(music.getResourceStatus());
        document.setCopyrightInfo(music.getCopyrightInfo());
        document.setRemark(music.getRemark());
        document.setTags(extractTagNames(music.getTagsSnapshot()));
        document.setSearchText(buildSearchText(music, document.getTags()));
        return document;
    }

    private Long toEpochMillis(Date date) {
        return date == null ? 0L : date.getTime();
    }

    private long safeLong(Long value) {
        return value == null ? 0L : Math.max(0L, value);
    }

    private String resolveAiFlag(MusicVo music) {
        String text = (safeString(music.getTagsSnapshot()) + " " + safeString(music.getExtendData())).toLowerCase();
        return text.contains("ai") || text.contains("\"isai\":true") || text.contains("\"is_ai\":true") ? "1" : "0";
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private String buildSearchText(MusicVo music, List<String> tags) {
        List<String> parts = new ArrayList<>();
        addSearchTokens(parts, music.getTitle());
        addSearchTokens(parts, music.getSubtitle());
        addSearchTokens(parts, music.getOriginalTitle());
        addSearchTokens(parts, music.getCreatorName());
        addSearchTokens(parts, music.getProducerMark());
        if (tags != null) {
            tags.forEach(item -> addSearchTokens(parts, item));
        }
        return String.join(" ", parts);
    }

    private void addSearchTokens(List<String> parts, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        String normalized = value.trim();
        parts.add(normalized);
        parts.addAll(buildShortTokens(normalized));
    }

    private List<String> buildShortTokens(String value) {
        String compact = value.replaceAll("\\s+", "");
        if (compact.length() < 2 || compact.length() > 32) {
            return Collections.emptyList();
        }
        LinkedHashSet<String> tokens = new LinkedHashSet<>();
        for (int size = 2; size <= 4; size++) {
            if (compact.length() < size) {
                continue;
            }
            for (int index = 0; index <= compact.length() - size; index++) {
                tokens.add(compact.substring(index, index + size));
            }
        }
        return new ArrayList<>(tokens);
    }

    private void addIfPresent(List<String> parts, String value) {
        if (StringUtils.isNotBlank(value)) {
            parts.add(value.trim());
        }
    }

    private List<String> extractTagNames(String tagsSnapshot) {
        if (StringUtils.isBlank(tagsSnapshot)) {
            return Collections.emptyList();
        }
        try {
            JSONArray array = JSON.parseArray(tagsSnapshot);
            if (array == null || array.isEmpty()) {
                return Collections.emptyList();
            }
            LinkedHashSet<String> tags = new LinkedHashSet<>();
            for (Object item : array) {
                if (item instanceof String value && StringUtils.isNotBlank(value)) {
                    tags.add(value.trim());
                } else if (item instanceof JSONObject jsonObject) {
                    String value = firstNonBlank(jsonObject.getString("name"), jsonObject.getString("tagName"), jsonObject.getString("label"));
                    if (StringUtils.isNotBlank(value)) {
                        tags.add(value.trim());
                    }
                }
            }
            return new ArrayList<>(tags);
        } catch (Exception ex) {
            log.debug("解析标签快照失败: {}", tagsSnapshot, ex);
            return Collections.emptyList();
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
