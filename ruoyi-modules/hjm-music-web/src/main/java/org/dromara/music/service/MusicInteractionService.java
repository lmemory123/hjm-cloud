package org.dromara.music.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatisflex.helper.DataBaseHelper;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.music.domain.Music;
import org.dromara.music.domain.MusicAction;
import org.dromara.music.domain.MusicAuditLog;
import org.dromara.music.domain.MusicComment;
import org.dromara.music.domain.MusicStat;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.domain.vo.OpenCommentVo;
import org.dromara.music.mapper.MusicActionMapper;
import org.dromara.music.mapper.MusicAuditLogMapper;
import org.dromara.music.mapper.MusicCommentMapper;
import org.dromara.music.mapper.MusicMapper;
import org.dromara.music.mapper.MusicStatMapper;
import org.dromara.music.search.MusicSearchIndexService;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.dromara.music.constant.MusicInteractionCacheConstants.ACTION_COLLECT;
import static org.dromara.music.constant.MusicInteractionCacheConstants.ACTION_FOLLOW;
import static org.dromara.music.constant.MusicInteractionCacheConstants.ACTION_LIKE;
import static org.dromara.music.constant.MusicInteractionCacheConstants.ACTION_PLAY;
import static org.dromara.music.constant.MusicInteractionCacheConstants.ACTION_REPORT;
import static org.dromara.music.constant.MusicInteractionCacheConstants.collectDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.collectStateKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.LIKE_STATE_LIKED;
import static org.dromara.music.constant.MusicInteractionCacheConstants.LIKE_STATE_UNLIKED;
import static org.dromara.music.constant.MusicInteractionCacheConstants.STAT_DIRTY_SET_KEY;
import static org.dromara.music.constant.MusicInteractionCacheConstants.STAT_FLUSH_LOCK_KEY;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_COMMENT_LIKE;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_COMMENT_REPORT;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_SONG;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_SONG_COLLECT;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_SONG_HISTORY;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_SONG_REPORT;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_USER_FOLLOW;
import static org.dromara.music.constant.MusicInteractionCacheConstants.commentDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.commentLikeStateKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.downloadDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.likeDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.likeStateKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.playDeltaKey;
import static org.dromara.music.constant.MusicInteractionCacheConstants.shareDeltaKey;
import static org.dromara.music.domain.table.MusicActionTableDef.MUSIC_ACTION;
import static org.dromara.music.domain.table.MusicTableDef.MUSIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicInteractionService {

    private static final String AUDIT_APPROVED = "1";
    private static final String PUBLIC_VISIBLE = "1";

    private final MusicMapper musicMapper;
    private final MusicStatMapper musicStatMapper;
    private final MusicActionMapper musicActionMapper;
    private final MusicAuditLogMapper musicAuditLogMapper;
    private final MusicCommentMapper musicCommentMapper;
    private final MusicInteractionAsyncService musicInteractionAsyncService;
    private final MusicSearchIndexService musicSearchIndexService;

    public void recordPlay(Long musicId) {
        ensurePublicMusic(musicId);
        RedisUtils.incrAtomicValue(playDeltaKey(musicId));
        markDirty(musicId);
    }

    public void recordHistory(Long musicId, Long userId) {
        ensurePublicMusic(musicId);
        upsertAction(userId, musicId, TARGET_TYPE_SONG_HISTORY, ACTION_PLAY);
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
        musicInteractionAsyncService.syncAction(musicId, userId, TARGET_TYPE_SONG_COLLECT, ACTION_COLLECT, true);
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
        musicInteractionAsyncService.syncAction(musicId, userId, TARGET_TYPE_SONG_COLLECT, ACTION_COLLECT, false);
        return true;
    }

    public boolean hasCollected(Long musicId, Long userId) {
        ensurePublicMusic(musicId);
        return hasCollectedInternal(musicId, userId);
    }

    public List<MusicVo> queryLikedSongs(Long userId, Integer limit) {
        return queryActionSongs(userId, TARGET_TYPE_SONG, ACTION_LIKE, limit);
    }

    public List<MusicVo> queryCollectedSongs(Long userId, Integer limit) {
        return queryActionSongs(userId, TARGET_TYPE_SONG_COLLECT, ACTION_COLLECT, limit);
    }

    public List<MusicVo> queryHistorySongs(Long userId, Integer limit) {
        return queryActionSongs(userId, TARGET_TYPE_SONG_HISTORY, ACTION_PLAY, limit);
    }

    public boolean clearHistory(Long userId) {
        musicActionMapper.deleteByQuery(QueryWrapper.create()
            .where(MUSIC_ACTION.USER_ID.eq(userId)
                .and(MUSIC_ACTION.TARGET_TYPE.eq(TARGET_TYPE_SONG_HISTORY))
                .and(MUSIC_ACTION.ACTION.eq(ACTION_PLAY))));
        return true;
    }

    public boolean followUser(String uid, Long userId) {
        Long targetUserId = resolveCreatorUserId(uid);
        upsertAction(userId, targetUserId, TARGET_TYPE_USER_FOLLOW, ACTION_FOLLOW);
        return true;
    }

    public boolean unfollowUser(String uid, Long userId) {
        Long targetUserId = resolveCreatorUserId(uid);
        removeAction(userId, targetUserId, TARGET_TYPE_USER_FOLLOW, ACTION_FOLLOW);
        return true;
    }

    public boolean hasFollowed(String uid, Long userId) {
        Long targetUserId = resolveCreatorUserId(uid);
        return hasAction(userId, targetUserId, TARGET_TYPE_USER_FOLLOW, ACTION_FOLLOW);
    }

    public List<String> queryFollowIds(String uid, Integer limit) {
        Long sourceUserId = resolveCreatorUserId(uid);
        int size = limit == null || limit <= 0 ? 60 : Math.min(limit, 100);
        List<MusicAction> actions = musicActionMapper.selectListByQuery(QueryWrapper.create()
            .where(MUSIC_ACTION.USER_ID.eq(sourceUserId)
                .and(MUSIC_ACTION.TARGET_TYPE.eq(TARGET_TYPE_USER_FOLLOW))
                .and(MUSIC_ACTION.ACTION.eq(ACTION_FOLLOW)))
            .orderBy(MUSIC_ACTION.CREATE_TIME.desc())
            .limit(size));
        return actions.stream().map(a -> String.valueOf(a.getTargetId())).toList();
    }

    public List<String> queryFanIds(String uid, Integer limit) {
        Long targetUserId = resolveCreatorUserId(uid);
        int size = limit == null || limit <= 0 ? 60 : Math.min(limit, 100);
        List<MusicAction> actions = musicActionMapper.selectListByQuery(QueryWrapper.create()
            .where(MUSIC_ACTION.TARGET_ID.eq(targetUserId)
                .and(MUSIC_ACTION.TARGET_TYPE.eq(TARGET_TYPE_USER_FOLLOW))
                .and(MUSIC_ACTION.ACTION.eq(ACTION_FOLLOW)))
            .orderBy(MUSIC_ACTION.CREATE_TIME.desc())
            .limit(size));
        return actions.stream().map(a -> String.valueOf(a.getUserId())).toList();
    }

    public boolean reportSong(Long musicId, Long userId, String reason, String description) {
        ensurePublicMusic(musicId);
        writeReportLog(musicId, TARGET_TYPE_SONG_REPORT, userId, reason, Map.of(
            "musicId", musicId,
            "description", description == null ? "" : description
        ));
        return true;
    }

    public boolean likeComment(Long musicId, Long commentId, Long userId) {
        ensurePublicComment(musicId, commentId);
        if (hasCommentLiked(commentId, userId)) {
            return true;
        }
        RedisUtils.setCacheObject(commentLikeStateKey(commentId, userId), LIKE_STATE_LIKED);
        upsertAction(userId, commentId, TARGET_TYPE_COMMENT_LIKE, ACTION_LIKE);
        return true;
    }

    public boolean unlikeComment(Long musicId, Long commentId, Long userId) {
        ensurePublicComment(musicId, commentId);
        if (!hasCommentLiked(commentId, userId)) {
            return true;
        }
        RedisUtils.setCacheObject(commentLikeStateKey(commentId, userId), LIKE_STATE_UNLIKED);
        removeAction(userId, commentId, TARGET_TYPE_COMMENT_LIKE, ACTION_LIKE);
        return true;
    }

    public boolean hasCommentLiked(Long commentId, Long userId) {
        String cachedState = RedisUtils.getCacheObject(commentLikeStateKey(commentId, userId));
        if (LIKE_STATE_LIKED.equals(cachedState)) {
            return true;
        }
        if (LIKE_STATE_UNLIKED.equals(cachedState)) {
            return false;
        }
        boolean exists = hasAction(userId, commentId, TARGET_TYPE_COMMENT_LIKE, ACTION_LIKE);
        RedisUtils.setCacheObject(commentLikeStateKey(commentId, userId), exists ? LIKE_STATE_LIKED : LIKE_STATE_UNLIKED);
        return exists;
    }

    public boolean reportComment(Long musicId, Long commentId, Long userId, String reason, String description) {
        ensurePublicComment(musicId, commentId);
        writeReportLog(musicId, TARGET_TYPE_COMMENT_REPORT, userId, reason, Map.of(
            "musicId", musicId,
            "commentId", commentId,
            "description", description == null ? "" : description
        ));
        return true;
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

    public void fillCommentLikeStats(List<OpenCommentVo> comments) {
        if (comments == null || comments.isEmpty()) {
            return;
        }
        List<OpenCommentVo> flatComments = flattenComments(comments);
        List<Long> commentIds = flatComments.stream().map(OpenCommentVo::getId).filter(Objects::nonNull).distinct().toList();
        if (commentIds.isEmpty()) {
            return;
        }
        Map<Long, Long> likeCountMap = musicActionMapper.selectListByQuery(QueryWrapper.create()
                .where(MUSIC_ACTION.TARGET_ID.in(commentIds)
                    .and(MUSIC_ACTION.TARGET_TYPE.eq(TARGET_TYPE_COMMENT_LIKE))
                    .and(MUSIC_ACTION.ACTION.eq(ACTION_LIKE))))
            .stream()
            .collect(Collectors.groupingBy(MusicAction::getTargetId, Collectors.counting()));
        flatComments.forEach(comment -> comment.setLikeCount(likeCountMap.getOrDefault(comment.getId(), 0L)));
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

    private List<MusicVo> queryActionSongs(Long userId, String targetType, String actionName, Integer limit) {
        int size = limit == null || limit <= 0 ? 60 : Math.min(limit, 100);
        List<MusicAction> actions = musicActionMapper.selectListByQuery(QueryWrapper.create()
            .where(MUSIC_ACTION.USER_ID.eq(userId)
                .and(MUSIC_ACTION.TARGET_TYPE.eq(targetType))
                .and(MUSIC_ACTION.ACTION.eq(actionName)))
            .orderBy(MUSIC_ACTION.CREATE_TIME.desc()));
        if (actions.isEmpty()) {
            return Collections.emptyList();
        }
        if (actions.size() > size) {
            actions = actions.subList(0, size);
        }
        List<Long> musicIds = actions.stream().map(MusicAction::getTargetId).filter(Objects::nonNull).toList();
        Map<Long, Integer> orderMap = new LinkedHashMap<>();
        for (int index = 0; index < musicIds.size(); index++) {
            orderMap.put(musicIds.get(index), index);
        }
        List<MusicVo> songs = musicMapper.selectVoList(QueryWrapper.create()
            .where(MUSIC.ID.in(musicIds)
                .and(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED))
                .and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE))));
        fillDynamicStats(songs);
        return songs.stream()
            .sorted((left, right) -> Integer.compare(orderMap.getOrDefault(left.getId(), Integer.MAX_VALUE), orderMap.getOrDefault(right.getId(), Integer.MAX_VALUE)))
            .toList();
    }

    private Long resolveCreatorUserId(String uid) {
        if (StringUtils.isBlank(uid)) {
            throw new ServiceException("创作者不存在");
        }
        if (StringUtils.isNumeric(uid)) {
            return Long.parseLong(uid);
        }
        Music creatorSong = musicMapper.selectOneByQuery(QueryWrapper.create()
            .where(MUSIC.CREATOR_NAME.eq(uid)
                .and(MUSIC.CREATOR_ID.isNotNull())
                .and(MUSIC.AUDIT_STATUS.eq(AUDIT_APPROVED))
                .and(MUSIC.IS_PUBLIC.eq(PUBLIC_VISIBLE)))
            .orderBy(MUSIC.PUBLISH_TIME.desc())
            .orderBy(MUSIC.ID.desc()));
        if (creatorSong == null || creatorSong.getCreatorId() == null) {
            throw new ServiceException("创作者暂不支持关注");
        }
        return creatorSong.getCreatorId();
    }

    private void upsertAction(Long userId, Long targetId, String targetType, String actionName) {
        MusicAction action = musicActionMapper.selectOneByQuery(QueryWrapper.create()
            .where(MUSIC_ACTION.USER_ID.eq(userId)
                .and(MUSIC_ACTION.TARGET_ID.eq(targetId))
                .and(MUSIC_ACTION.TARGET_TYPE.eq(targetType))));
        Date now = new Date();
        if (action == null) {
            action = new MusicAction();
            action.setId(DataBaseHelper.nextId());
            action.setUserId(userId);
            action.setTargetId(targetId);
            action.setTargetType(targetType);
            action.setAction(actionName);
            action.setCreateTime(now);
            musicActionMapper.insert(action);
            return;
        }
        action.setAction(actionName);
        action.setCreateTime(now);
        musicActionMapper.update(action, false);
    }

    private void removeAction(Long userId, Long targetId, String targetType, String actionName) {
        musicActionMapper.deleteByQuery(QueryWrapper.create()
            .where(MUSIC_ACTION.USER_ID.eq(userId)
                .and(MUSIC_ACTION.TARGET_ID.eq(targetId))
                .and(MUSIC_ACTION.TARGET_TYPE.eq(targetType))
                .and(MUSIC_ACTION.ACTION.eq(actionName))));
    }

    private boolean hasAction(Long userId, Long targetId, String targetType, String actionName) {
        return musicActionMapper.selectOneByQuery(QueryWrapper.create()
            .where(MUSIC_ACTION.USER_ID.eq(userId)
                .and(MUSIC_ACTION.TARGET_ID.eq(targetId))
                .and(MUSIC_ACTION.TARGET_TYPE.eq(targetType))
                .and(MUSIC_ACTION.ACTION.eq(actionName)))) != null;
    }

    private void writeReportLog(Long musicId, String targetType, Long userId, String reason, Map<String, Object> snapshot) {
        MusicAuditLog log = new MusicAuditLog();
        log.setId(DataBaseHelper.nextId());
        log.setMusicId(musicId);
        log.setTargetType(targetType);
        log.setAction(4L);
        log.setOldStatus(0L);
        log.setNewStatus(0L);
        log.setReason(StringUtils.isBlank(reason) ? "用户举报" : reason.trim());
        log.setSnapshot(JsonUtils.toJsonString(snapshot));
        log.setOperatorId(userId);
        log.setCreateTime(new Date());
        musicAuditLogMapper.insert(log);
    }

    private void ensurePublicComment(Long musicId, Long commentId) {
        ensurePublicMusic(musicId);
        MusicComment comment = musicCommentMapper.selectOneById(commentId);
        if (comment == null || !Objects.equals(comment.getMusicId(), musicId)) {
            throw new ServiceException("评论不存在或暂不可访问");
        }
    }

    private List<OpenCommentVo> flattenComments(List<OpenCommentVo> comments) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream().flatMap(comment -> {
            List<OpenCommentVo> replies = flattenComments(comment.getReplies());
            return java.util.stream.Stream.concat(java.util.stream.Stream.of(comment), replies.stream());
        }).toList();
    }

    private boolean hasCollectedInternal(Long musicId, Long userId) {
        String cachedState = RedisUtils.getCacheObject(collectStateKey(musicId, userId));
        if (LIKE_STATE_LIKED.equals(cachedState)) {
            return true;
        }
        if (LIKE_STATE_UNLIKED.equals(cachedState)) {
            return false;
        }
        boolean exists = hasAction(userId, musicId, TARGET_TYPE_SONG_COLLECT, ACTION_COLLECT);
        RedisUtils.setCacheObject(collectStateKey(musicId, userId), exists ? LIKE_STATE_LIKED : LIKE_STATE_UNLIKED);
        return exists;
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
