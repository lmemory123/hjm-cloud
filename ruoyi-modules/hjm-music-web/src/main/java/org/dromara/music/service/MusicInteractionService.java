package org.dromara.music.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.music.domain.Music;
import org.dromara.music.domain.MusicStat;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.mapper.MusicActionMapper;
import org.dromara.music.mapper.MusicMapper;
import org.dromara.music.mapper.MusicStatMapper;
import org.dromara.music.search.MusicSearchIndexService;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.dromara.music.constant.MusicInteractionCacheConstants.ACTION_LIKE;
import static org.dromara.music.constant.MusicInteractionCacheConstants.collectDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.collectStateKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.LIKE_STATE_LIKED;
import static org.dromara.music.constant.MusicInteractionCacheConstants.LIKE_STATE_UNLIKED;
import static org.dromara.music.constant.MusicInteractionCacheConstants.STAT_DIRTY_SET_KEY;
import static org.dromara.music.constant.MusicInteractionCacheConstants.STAT_FLUSH_LOCK_KEY;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_SONG;
import static org.dromara.music.constant.MusicInteractionCacheConstants.commentDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.downloadDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.likeDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.likeStateKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.playDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.shareDeltaKey;
import static org.dromara.music.domain.table.MusicActionTableDef.MUSIC_ACTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicInteractionService {

    private static final String AUDIT_APPROVED = "1";
    private static final String PUBLIC_VISIBLE = "1";

    private final MusicMapper musicMapper;
    private final MusicStatMapper musicStatMapper;
    private final MusicActionMapper musicActionMapper;
    private final MusicInteractionAsyncService musicInteractionAsyncService;
    private final MusicSearchIndexService musicSearchIndexService;

    public void recordPlay(Long musicId) {
        ensurePublicMusic(musicId);
        RedisUtils.incrAtomicValue(playDeltaKey(musicId));
        markDirty(musicId);
    }

    public boolean likeMusic(Long musicId, Long userId) {
        ensurePublicMusic(musicId);
        if (resolveLikeState(musicId, userId)) {
            return true;
        }
        RedisUtils.setCacheObject(likeStateKey(musicId, userId), LIKE_STATE_LIKED);
        RedisUtils.incrAtomicValue(likeDeltaKey(musicId));
        markDirty(musicId);
        musicInteractionAsyncService.syncLikeAction(musicId, userId, true);
        return true;
    }

    public boolean unlikeMusic(Long musicId, Long userId) {
        ensurePublicMusic(musicId);
        if (!resolveLikeState(musicId, userId)) {
            return true;
        }
        RedisUtils.setCacheObject(likeStateKey(musicId, userId), LIKE_STATE_UNLIKED);
        RedisUtils.decrAtomicValue(likeDeltaKey(musicId));
        markDirty(musicId);
        musicInteractionAsyncService.syncLikeAction(musicId, userId, false);
        return true;
    }

    public boolean hasLiked(Long musicId, Long userId) {
        ensurePublicMusic(musicId);
        return resolveLikeState(musicId, userId);
    }

    public boolean collectMusic(Long musicId, Long userId) {
        ensurePublicMusic(musicId);
        if (hasCollectedInternal(musicId, userId)) {
            return true;
        }
        RedisUtils.setCacheObject(collectStateKey(musicId, userId), LIKE_STATE_LIKED);
        RedisUtils.incrAtomicValue(collectDeltaKey(musicId));
        markDirty(musicId);
        return true;
    }

    public boolean uncollectMusic(Long musicId, Long userId) {
        ensurePublicMusic(musicId);
        if (!hasCollectedInternal(musicId, userId)) {
            return true;
        }
        RedisUtils.setCacheObject(collectStateKey(musicId, userId), LIKE_STATE_UNLIKED);
        RedisUtils.decrAtomicValue(collectDeltaKey(musicId));
        markDirty(musicId);
        return true;
    }

    public boolean hasCollected(Long musicId, Long userId) {
        ensurePublicMusic(musicId);
        return hasCollectedInternal(musicId, userId);
    }

    public void recordShare(Long musicId) {
        ensurePublicMusic(musicId);
        RedisUtils.incrAtomicValue(shareDeltaKey(musicId));
        markDirty(musicId);
    }

    public void recordDownload(Long musicId) {
        ensurePublicMusic(musicId);
        RedisUtils.incrAtomicValue(downloadDeltaKey(musicId));
        markDirty(musicId);
    }

    public void changeCommentCount(Long musicId, long delta) {
        ensurePublicMusic(musicId);
        if (delta == 0L) {
            return;
        }
        if (delta > 0L) {
            RedisUtils.getClient().getAtomicLong(commentDeltaKey(musicId)).addAndGet(delta);
        } else {
            RedisUtils.getClient().getAtomicLong(commentDeltaKey(musicId)).addAndGet(delta);
        }
        markDirty(musicId);
    }

    public void fillDynamicStats(MusicVo musicVo) {
        if (musicVo == null || musicVo.getId() == null) {
            return;
        }
        musicVo.setPlayCount(safeValue(musicVo.getPlayCount()) + RedisUtils.getAtomicValue(playDeltaKey(musicVo.getId())));
        musicVo.setLikeCount(Math.max(0L, safeValue(musicVo.getLikeCount()) + RedisUtils.getAtomicValue(likeDeltaKey(musicVo.getId()))));
        musicVo.setCollectCount(Math.max(0L, safeValue(musicVo.getCollectCount()) + RedisUtils.getAtomicValue(collectDeltaKey(musicVo.getId()))));
        musicVo.setCommentCount(Math.max(0L, safeValue(musicVo.getCommentCount()) + RedisUtils.getAtomicValue(commentDeltaKey(musicVo.getId()))));
        musicVo.setShareCount(Math.max(0L, safeValue(musicVo.getShareCount()) + RedisUtils.getAtomicValue(shareDeltaKey(musicVo.getId()))));
        musicVo.setDownloadCount(Math.max(0L, safeValue(musicVo.getDownloadCount()) + RedisUtils.getAtomicValue(downloadDeltaKey(musicVo.getId()))));
    }

    public void fillDynamicStats(List<MusicVo> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        rows.forEach(this::fillDynamicStats);
    }

    public void fillDynamicStats(MusicDetailVo detail) {
        fillDynamicStats((MusicVo) detail);
    }

    public int flushDirtyStats() {
        Set<String> dirtyIds = RedisUtils.getCacheSet(STAT_DIRTY_SET_KEY);
        if (dirtyIds == null || dirtyIds.isEmpty()) {
            return 0;
        }

        RLock lock = RedisUtils.getClient().getLock(STAT_FLUSH_LOCK_KEY);
        boolean locked = false;
        try {
            locked = lock.tryLock(0, 30, TimeUnit.SECONDS);
            if (!locked) {
                return 0;
            }
            int successCount = 0;
            for (String rawMusicId : dirtyIds) {
                if (!StringUtils.isNumeric(rawMusicId)) {
                    RedisUtils.getClient().getSet(STAT_DIRTY_SET_KEY).remove(rawMusicId);
                    continue;
                }
                if (flushSingleMusicStat(Long.parseLong(rawMusicId))) {
                    successCount++;
                }
            }
            return successCount;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("获取音乐互动刷盘锁被中断", ex);
            return 0;
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    private boolean flushSingleMusicStat(Long musicId) {
        long playDelta = RedisUtils.getClient().getAtomicLong(playDeltaKey(musicId)).getAndSet(0L);
        long likeDelta = RedisUtils.getClient().getAtomicLong(likeDeltaKey(musicId)).getAndSet(0L);
        long collectDelta = RedisUtils.getClient().getAtomicLong(collectDeltaKey(musicId)).getAndSet(0L);
        long commentDelta = RedisUtils.getClient().getAtomicLong(commentDeltaKey(musicId)).getAndSet(0L);
        long shareDelta = RedisUtils.getClient().getAtomicLong(shareDeltaKey(musicId)).getAndSet(0L);
        long downloadDelta = RedisUtils.getClient().getAtomicLong(downloadDeltaKey(musicId)).getAndSet(0L);
        if (playDelta == 0L && likeDelta == 0L && collectDelta == 0L && commentDelta == 0L && shareDelta == 0L && downloadDelta == 0L) {
            RedisUtils.getClient().getSet(STAT_DIRTY_SET_KEY).remove(String.valueOf(musicId));
            return false;
        }

        try {
            persistDelta(musicId, playDelta, likeDelta, collectDelta, commentDelta, shareDelta, downloadDelta);
            RedisUtils.getClient().getSet(STAT_DIRTY_SET_KEY).remove(String.valueOf(musicId));
            if (RedisUtils.getAtomicValue(playDeltaKey(musicId)) != 0L
                || RedisUtils.getAtomicValue(likeDeltaKey(musicId)) != 0L
                || RedisUtils.getAtomicValue(collectDeltaKey(musicId)) != 0L
                || RedisUtils.getAtomicValue(commentDeltaKey(musicId)) != 0L
                || RedisUtils.getAtomicValue(shareDeltaKey(musicId)) != 0L
                || RedisUtils.getAtomicValue(downloadDeltaKey(musicId)) != 0L) {
                markDirty(musicId);
            }
            musicSearchIndexService.syncMusicIndex(musicId);
            return true;
        } catch (Exception ex) {
            if (playDelta != 0L) {
                RedisUtils.getClient().getAtomicLong(playDeltaKey(musicId)).addAndGet(playDelta);
            }
            if (likeDelta != 0L) {
                RedisUtils.getClient().getAtomicLong(likeDeltaKey(musicId)).addAndGet(likeDelta);
            }
            if (collectDelta != 0L) {
                RedisUtils.getClient().getAtomicLong(collectDeltaKey(musicId)).addAndGet(collectDelta);
            }
            if (commentDelta != 0L) {
                RedisUtils.getClient().getAtomicLong(commentDeltaKey(musicId)).addAndGet(commentDelta);
            }
            if (shareDelta != 0L) {
                RedisUtils.getClient().getAtomicLong(shareDeltaKey(musicId)).addAndGet(shareDelta);
            }
            if (downloadDelta != 0L) {
                RedisUtils.getClient().getAtomicLong(downloadDeltaKey(musicId)).addAndGet(downloadDelta);
            }
            markDirty(musicId);
            log.warn("刷盘音乐互动统计失败, musicId={}, playDelta={}, likeDelta={}, collectDelta={}, commentDelta={}, shareDelta={}, downloadDelta={}", musicId, playDelta, likeDelta, collectDelta, commentDelta, shareDelta, downloadDelta, ex);
            return false;
        }
    }

    private void persistDelta(Long musicId, long playDelta, long likeDelta, long collectDelta, long commentDelta, long shareDelta, long downloadDelta) {
        Music music = musicMapper.selectOneById(musicId);
        if (music == null) {
            return;
        }

        long nextPlayCount = Math.max(0L, safeValue(music.getPlayCount()) + playDelta);
        long nextLikeCount = Math.max(0L, safeValue(music.getLikeCount()) + likeDelta);
        long nextCollectCount = Math.max(0L, safeValue(music.getCollectCount()) + collectDelta);
        long nextCommentCount = Math.max(0L, safeValue(music.getCommentCount()) + commentDelta);
        long nextShareCount = Math.max(0L, safeValue(music.getShareCount()) + shareDelta);
        long nextDownloadCount = Math.max(0L, safeValue(music.getDownloadCount()) + downloadDelta);
        Date now = new Date();

        music.setPlayCount(nextPlayCount);
        music.setLikeCount(nextLikeCount);
        music.setCollectCount(nextCollectCount);
        music.setCommentCount(nextCommentCount);
        music.setShareCount(nextShareCount);
        music.setDownloadCount(nextDownloadCount);
        music.setUpdateTime(now);
        musicMapper.update(music, false);

        MusicStat stat = musicStatMapper.selectOneById(musicId);
        if (stat == null) {
            stat = new MusicStat();
            stat.setMusicId(musicId);
            stat.setPlayCount(nextPlayCount);
            stat.setLikeCount(nextLikeCount);
            stat.setCollectCount(nextCollectCount);
            stat.setCommentCount(nextCommentCount);
            stat.setShareCount(nextShareCount);
            stat.setDownloadCount(nextDownloadCount);
            stat.setCreateTime(now);
            stat.setUpdateTime(now);
            musicStatMapper.insert(stat);
            return;
        }

        stat.setPlayCount(nextPlayCount);
        stat.setLikeCount(nextLikeCount);
        stat.setCollectCount(nextCollectCount);
        stat.setCommentCount(nextCommentCount);
        stat.setShareCount(nextShareCount);
        stat.setDownloadCount(nextDownloadCount);
        stat.setUpdateTime(now);
        musicStatMapper.update(stat, false);
    }

    private boolean hasCollectedInternal(Long musicId, Long userId) {
        String cachedState = RedisUtils.getCacheObject(collectStateKey(musicId, userId));
        return LIKE_STATE_LIKED.equals(cachedState);
    }

    private boolean resolveLikeState(Long musicId, Long userId) {
        String cacheKey = likeStateKey(musicId, userId);
        String cachedState = RedisUtils.getCacheObject(cacheKey);
        if (LIKE_STATE_LIKED.equals(cachedState)) {
            return true;
        }
        if (LIKE_STATE_UNLIKED.equals(cachedState)) {
            return false;
        }

        boolean exists = musicActionMapper.selectOneByQuery(QueryWrapper.create()
            .where(MUSIC_ACTION.USER_ID.eq(userId)
                .and(MUSIC_ACTION.TARGET_ID.eq(musicId))
                .and(MUSIC_ACTION.TARGET_TYPE.eq(TARGET_TYPE_SONG))
                .and(MUSIC_ACTION.ACTION.eq(ACTION_LIKE)))) != null;
        RedisUtils.setCacheObject(cacheKey, exists ? LIKE_STATE_LIKED : LIKE_STATE_UNLIKED);
        return exists;
    }

    private void ensurePublicMusic(Long musicId) {
        Music music = musicMapper.selectOneById(musicId);
        if (music == null || !AUDIT_APPROVED.equals(music.getAuditStatus()) || !PUBLIC_VISIBLE.equals(music.getIsPublic())) {
            throw new ServiceException("歌曲不存在或暂不可访问");
        }
    }

    private void markDirty(Long musicId) {
        RedisUtils.addCacheSet(STAT_DIRTY_SET_KEY, String.valueOf(musicId));
    }

    private long safeValue(Long value) {
        return value == null ? 0L : value;
    }
}
