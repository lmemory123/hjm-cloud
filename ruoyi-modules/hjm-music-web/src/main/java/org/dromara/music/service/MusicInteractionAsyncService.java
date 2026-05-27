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

import static org.dromara.music.constant.MusicInteractionCacheConstants.TARGET_TYPE_SONG;
import static org.dromara.music.domain.table.MusicActionTableDef.MUSIC_ACTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicInteractionAsyncService {

    private final MusicActionMapper musicActionMapper;

    @Async(VirtualThreadExecutionConfig.ASYNC_TASK_EXECUTOR_BEAN)
    public void syncLikeAction(Long musicId, Long userId, boolean liked) {
        syncAction(musicId, userId, TARGET_TYPE_SONG, org.dromara.music.constant.MusicInteractionCacheConstants.ACTION_LIKE, liked);
    }

    @Async(VirtualThreadExecutionConfig.ASYNC_TASK_EXECUTOR_BEAN)
    public void syncAction(Long targetId, Long userId, String targetType, String actionName, boolean enabled) {
        try {
            MusicAction action = musicActionMapper.selectOneByQuery(QueryWrapper.create()
                .where(MUSIC_ACTION.USER_ID.eq(userId)
                    .and(MUSIC_ACTION.TARGET_ID.eq(targetId))
                    .and(MUSIC_ACTION.TARGET_TYPE.eq(targetType))));

            if (enabled) {
                if (action == null) {
                    MusicAction add = new MusicAction();
                    add.setId(DataBaseHelper.nextId());
                    add.setUserId(userId);
                    add.setTargetId(targetId);
                    add.setTargetType(targetType);
                    add.setAction(actionName);
                    add.setCreateTime(new Date());
                    musicActionMapper.insert(add);
                } else if (!actionName.equals(action.getAction())) {
                    action.setAction(actionName);
                    action.setCreateTime(new Date());
                    musicActionMapper.update(action, false);
                }
                return;
            }

            if (action != null) {
                musicActionMapper.deleteById(action.getId());
            }
        } catch (Exception ex) {
            log.warn("异步同步互动动作失败, targetId={}, userId={}, targetType={}, action={}, enabled={}",
                targetId, userId, targetType, actionName, enabled, ex);
        }
    }
}
