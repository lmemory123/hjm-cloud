package org.dromara.music.constant;

public final class MusicInteractionCacheConstants {

    public static final String CACHE_PREFIX = "music:interaction:";
    public static final String STAT_DIRTY_SET_KEY = CACHE_PREFIX + "stat:dirty";
    public static final String STAT_FLUSH_LOCK_KEY = CACHE_PREFIX + "stat:flush:lock";
    public static final String TARGET_TYPE_SONG = "song";
    public static final String ACTION_LIKE = "like";
    public static final String LIKE_STATE_LIKED = "1";
    public static final String LIKE_STATE_UNLIKED = "0";

    private MusicInteractionCacheConstants() {
    }

    public static String playDeltaKey(Long musicId) {
        return CACHE_PREFIX + "play:delta:" + musicId;
    }

    public static String likeDeltaKey(Long musicId) {
        return CACHE_PREFIX + "like:delta:" + musicId;
    }

    public static String likeStateKey(Long musicId, Long userId) {
        return CACHE_PREFIX + "like:state:" + musicId + ':' + userId;
    }

    public static String collectDeltaKey(Long musicId) {
        return CACHE_PREFIX + "collect:delta:" + musicId;
    }

    public static String collectStateKey(Long musicId, Long userId) {
        return CACHE_PREFIX + "collect:state:" + musicId + ':' + userId;
    }

    public static String commentDeltaKey(Long musicId) {
        return CACHE_PREFIX + "comment:delta:" + musicId;
    }

    public static String shareDeltaKey(Long musicId) {
        return CACHE_PREFIX + "share:delta:" + musicId;
    }

    public static String downloadDeltaKey(Long musicId) {
        return CACHE_PREFIX + "download:delta:" + musicId;
    }
}
