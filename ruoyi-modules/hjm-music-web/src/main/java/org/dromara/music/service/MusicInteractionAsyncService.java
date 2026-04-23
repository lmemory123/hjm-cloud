package org.dromara.music.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.config.VirtualThreadExecutionConfig;
import org.dromara.common.mybatisflex.helper.DataBaseHelper;
import org.dromara.music.domain.MusicAction;
import org.dromara.music.mapper.MusicActionMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

import static org.dromara.music.constant.MusicInteractionCacheConstants.ACTION_LIKE;
import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_SONG;
import static org.dromara.music.domain.table.MusicActionTableDef.MUSIC_ACTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicInteractionAsyncService {

    private final MusicActionMapper musicActionMapper;

    @Async(VirtualThreadExecutionConfig.ASYNC_TASK_EXECUTOR_BEAN)
    public void syncLikeAction(Long musicId, Long userId, boolean liked) {
        try {
            MusicAction action = musicActionMapper.selectOneByQuery(QueryWrapper.create()
                .where(MUSIC_ACTION.USER_ID.eq(userId)
                    .and(MUSIC_ACTION.TARGET_ID.eq(musicId))
                    .and(MUSIC_ACTION.TARGET_TYPE.eq(TARGET_TYPE_SONG))));

            if (liked) {
                if (action == null) {
                    MusicAction add = new MusicAction();
                    add.setId(DataBaseHelper.nextId());
                    add.setUserId(userId);
                    add.setTargetId(musicId);
                    add.setTargetType(TARGET_TYPE_SONG);
                    add.setAction(ACTION_LIKE);
                    add.setCreateTime(new Date());
                    musicActionMapper.insert(add);
                } else if (!ACTION_LIKE.equals(action.getAction())) {
                    action.setAction(ACTION_LIKE);
                    musicActionMapper.update(action, false);
                }
                return;
            }

            if (action != null) {
                musicActionMapper.deleteById(action.getId());
            }
        } catch (Exception ex) {
            log.warn("异步同步点赞动作失败, musicId={}, userId={}, liked={}", musicId, userId, liked, ex);
        }
    }
}
