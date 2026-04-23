package org.dromara.music.search;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.momao.valkey.adapter.ValkeyClientRouting;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.music.domain.Music;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.mapper.MusicMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.dromara.music.domain.table.MusicTableDef.MUSIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicSearchIndexService {

    private static final String AUDIT_APPROVED = "1";
    private static final String PUBLIC_VISIBLE = "1";
    private static final String KEY_PREFIX = "music:";

    private final MusicMapper musicMapper;
    private final ObjectProvider<MusicSearchRepository> musicSearchRepositoryProvider;
    private final ObjectProvider<ValkeyClientRouting> clientRoutingProvider;

    @Value("${valkey.query.enabled:false}")
    private boolean valkeyQueryEnabled;

    public void syncPublicIndex() {
        if (!isSearchAvailable()) {
            return;
        }
        MusicSearchRepository musicSearchRepository = musicSearchRepositoryProvider.getIfAvailable();
        String indexResult = musicSearchRepository.checkAndCreateIndex();
        log.debug("Valkey 索引检查结果: {}", indexResult);

        List<MusicVo> eligibleSongs = musicMapper.selectVoList(
            com.mybatisflex.core.query.QueryWrapper.create()
                .where(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED).and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE)))
        );
        for (MusicVo song : eligibleSongs) {
            musicSearchRepository.save(String.valueOf(song.getId()), toSearchDocument(song));
        }

        List<Music> ineligibleSongs = musicMapper.selectListByQuery(
            com.mybatisflex.core.query.QueryWrapper.create()
                .where(MUSIC.AUDIT_STATUS.ne(AUDIT_APPROVED).or(MUSIC.IS_PUBLIC.ne(PUBLIC_VISIBLE)))
        );
        for (Music song : ineligibleSongs) {
            deleteIndexDocument(song.getId());
        }

        log.info("音乐公开搜索索引同步完成，上架 {} 条，清理 {} 条", eligibleSongs.size(), ineligibleSongs.size());
    }

    public boolean syncMusicIndex(Long musicId) {
        if (!isSearchAvailable()) {
            return false;
        }
        if (musicId == null) {
            return false;
        }
        MusicSearchRepository musicSearchRepository = musicSearchRepositoryProvider.getIfAvailable();
        String indexResult = musicSearchRepository.checkAndCreateIndex();
        log.debug("Valkey 单曲索引检查结果: {}", indexResult);
        MusicVo song = musicMapper.selectVoById(musicId);
        if (song == null || !AUDIT_APPROVED.equals(song.getAuditStatus()) || !PUBLIC_VISIBLE.equals(song.getIsPublic())) {
            deleteIndexDocument(musicId);
            return true;
        }
        musicSearchRepository.save(String.valueOf(song.getId()), toSearchDocument(song));
        return true;
    }

    public boolean removeMusicIndex(Long musicId) {
        if (!isSearchAvailable()) {
            return false;
        }
        if (musicId == null) {
            return false;
        }
        deleteIndexDocument(musicId);
        return true;
    }

    private void deleteIndexDocument(Long id) {
        ValkeyClientRouting clientRouting = clientRoutingProvider.getIfAvailable();
        if (id == null || clientRouting == null) {
            return;
        }
        try {
            clientRouting.executeWrite(new String[]{"DEL", KEY_PREFIX + id});
        } catch (Exception ex) {
            log.warn("删除 Valkey 索引文档失败, musicId={}", id, ex);
        }
    }

    private MusicSearchDocument toSearchDocument(MusicVo row) {
        MusicSearchDocument document = new MusicSearchDocument();
        document.setId(String.valueOf(row.getId()));
        document.setTitle(row.getTitle());
        document.setSubtitle(row.getSubtitle());
        document.setOriginalTitle(row.getOriginalTitle());
        document.setCreatorName(row.getCreatorName());
        document.setProducerMark(row.getProducerMark());
        document.setDuration(row.getDuration());
        document.setBpm(row.getBpm());
        document.setPublishTime(row.getPublishTime() == null ? null : row.getPublishTime().getTime());
        document.setPlayCount(row.getPlayCount());
        document.setLikeCount(row.getLikeCount());
        document.setCollectCount(row.getCollectCount());
        document.setShareCount(row.getShareCount());
        document.setDownloadCount(row.getDownloadCount());
        document.setAuditStatus(row.getAuditStatus());
        document.setIsPublic(row.getIsPublic());
        document.setTags(extractTagNames(row.getTagsSnapshot()));
        return document;
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
            List<String> tags = new ArrayList<>(array.size());
            for (Object item : array) {
                if (item instanceof String value && StringUtils.isNotBlank(value)) {
                    tags.add(value);
                } else if (item instanceof JSONObject jsonObject) {
                    String value = firstNonBlank(
                        jsonObject.getString("name"),
                        jsonObject.getString("tagName"),
                        jsonObject.getString("label")
                    );
                    if (StringUtils.isNotBlank(value)) {
                        tags.add(value);
                    }
                }
            }
            return tags;
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

    private boolean isSearchAvailable() {
        return valkeyQueryEnabled
            && musicSearchRepositoryProvider.getIfAvailable() != null
            && clientRoutingProvider.getIfAvailable() != null;
    }
}
