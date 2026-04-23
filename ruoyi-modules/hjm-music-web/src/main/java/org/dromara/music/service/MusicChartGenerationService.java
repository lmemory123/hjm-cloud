package org.dromara.music.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.mybatisflex.helper.DataBaseHelper;
import org.dromara.music.domain.MusicChartItem;
import org.dromara.music.domain.MusicChartSnapshot;
import org.dromara.music.domain.MusicStat;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.mapper.MusicChartItemMapper;
import org.dromara.music.mapper.MusicChartSnapshotMapper;
import org.dromara.music.mapper.MusicMapper;
import org.dromara.music.mapper.MusicStatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.dromara.music.domain.table.MusicChartItemTableDef.MUSIC_CHART_ITEM;
import static org.dromara.music.domain.table.MusicChartSnapshotTableDef.MUSIC_CHART_SNAPSHOT;
import static org.dromara.music.domain.table.MusicTableDef.MUSIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicChartGenerationService {

    private static final String AUDIT_APPROVED = "1";
    private static final String PUBLIC_VISIBLE = "1";
    private static final String CHART_TYPE_WEEK = "week";
    private static final String CHART_TYPE_MONTH = "month";
    private static final String STATUS_CALCULATING = "calculating";
    private static final String STATUS_PUBLISHED = "published";
    private static final int MAX_CHART_SIZE = 100;

    private final MusicMapper musicMapper;
    private final MusicStatMapper musicStatMapper;
    private final MusicChartSnapshotMapper musicChartSnapshotMapper;
    private final MusicChartItemMapper musicChartItemMapper;
    private final MusicInteractionService musicInteractionService;

    @Transactional(rollbackFor = Exception.class)
    public void refreshPublishedCharts() {
        regenerateChart(CHART_TYPE_WEEK, buildWeekPeriodKey(LocalDate.now()));
        regenerateChart(CHART_TYPE_MONTH, buildMonthPeriodKey(YearMonth.now()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void regenerateChart(String chartType, String periodKey) {
        MusicChartSnapshot snapshot = musicChartSnapshotMapper.selectOneByQuery(QueryWrapper.create()
            .where(MUSIC_CHART_SNAPSHOT.CHART_TYPE.eq(chartType)
                .and(MUSIC_CHART_SNAPSHOT.PERIOD_KEY.eq(periodKey))));

        Date now = new Date();
        if (snapshot == null) {
            snapshot = new MusicChartSnapshot();
            snapshot.setId(DataBaseHelper.nextId());
            snapshot.setChartType(chartType);
            snapshot.setPeriodKey(periodKey);
            snapshot.setCreateTime(now);
            snapshot.setUpdateTime(now);
            snapshot.setStatus(STATUS_CALCULATING);
            musicChartSnapshotMapper.insert(snapshot);
        } else {
            snapshot.setStatus(STATUS_CALCULATING);
            snapshot.setUpdateTime(now);
            musicChartSnapshotMapper.update(snapshot, false);
            musicChartItemMapper.deleteByQuery(QueryWrapper.create().where(MUSIC_CHART_ITEM.SNAPSHOT_ID.eq(snapshot.getId())));
        }

        List<MusicVo> candidates = musicMapper.selectVoList(QueryWrapper.create()
            .where(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED).and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE))));
        musicInteractionService.fillDynamicStats(candidates);

        List<RankedSong> rankedSongs = candidates.stream()
            .map(song -> new RankedSong(song, calculateHotScore(song)))
            .sorted(Comparator.comparingLong(RankedSong::score).reversed()
                .thenComparing((RankedSong item) -> safeValue(item.song().getPlayCount()), Comparator.reverseOrder())
                .thenComparing((RankedSong item) -> safeValue(item.song().getLikeCount()), Comparator.reverseOrder())
                .thenComparing(item -> item.song().getId()))
            .limit(MAX_CHART_SIZE)
            .toList();

        List<MusicChartItem> items = new ArrayList<>(rankedSongs.size());
        for (int index = 0; index < rankedSongs.size(); index++) {
            RankedSong rankedSong = rankedSongs.get(index);
            MusicVo song = rankedSong.song();

            MusicChartItem item = new MusicChartItem();
            item.setId(DataBaseHelper.nextId());
            item.setSnapshotId(snapshot.getId());
            item.setMusicId(song.getId());
            item.setRankNo((long) index + 1);
            item.setScore(rankedSong.score());
            item.setPlayCount(safeValue(song.getPlayCount()));
            item.setLikeCount(safeValue(song.getLikeCount()));
            item.setCreateTime(now);
            items.add(item);

            syncSongScore(song.getId(), rankedSong.score(), now);
        }

        if (!items.isEmpty()) {
            musicChartItemMapper.insertBatch(items);
        }

        snapshot.setStatus(STATUS_PUBLISHED);
        snapshot.setUpdateTime(now);
        musicChartSnapshotMapper.update(snapshot, false);
        log.info("音乐榜单快照生成完成, chartType={}, periodKey={}, items={}", chartType, periodKey, items.size());
    }

    private long calculateHotScore(MusicVo song) {
        return safeValue(song.getPlayCount())
            + safeValue(song.getLikeCount()) * 3
            + safeValue(song.getCollectCount()) * 5
            + safeValue(song.getCommentCount()) * 4
            + safeValue(song.getShareCount()) * 2
            + safeValue(song.getDownloadCount()) * 3;
    }

    private void syncSongScore(Long musicId, long score, Date now) {
        MusicStat stat = musicStatMapper.selectOneById(musicId);
        if (stat == null) {
            stat = new MusicStat();
            stat.setMusicId(musicId);
            stat.setPlayCount(0L);
            stat.setLikeCount(0L);
            stat.setCollectCount(0L);
            stat.setCommentCount(0L);
            stat.setShareCount(0L);
            stat.setDownloadCount(0L);
            stat.setScore(score);
            stat.setCreateTime(now);
            stat.setUpdateTime(now);
            musicStatMapper.insert(stat);
            return;
        }
        stat.setScore(score);
        stat.setUpdateTime(now);
        musicStatMapper.update(stat, false);
    }

    private String buildWeekPeriodKey(LocalDate date) {
        WeekFields weekFields = WeekFields.ISO;
        return date.getYear() + "-W" + String.format(Locale.ROOT, "%02d", date.get(weekFields.weekOfWeekBasedYear()));
    }

    private String buildMonthPeriodKey(YearMonth yearMonth) {
        return yearMonth.toString();
    }

    private long safeValue(Long value) {
        return value == null ? 0L : value;
    }

    private record RankedSong(MusicVo song, long score) {
    }
}
