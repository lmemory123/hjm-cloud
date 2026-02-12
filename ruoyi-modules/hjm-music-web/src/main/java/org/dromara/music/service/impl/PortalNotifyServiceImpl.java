package org.dromara.music.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.service.IPortalNotifyService;
import org.dromara.music.domain.vo.MusicNotifyLogVo;
import org.dromara.music.mapper.MusicNotifyLogMapper;
import org.springframework.stereotype.Service;

import static org.dromara.music.domain.table.MusicNotifyLogTableDef.MUSIC_NOTIFY_LOG;

@Slf4j
@RequiredArgsConstructor
@Service
public class PortalNotifyServiceImpl implements IPortalNotifyService {

    private final MusicNotifyLogMapper notifyLogMapper;

    @Override
    public TableDataInfo<MusicNotifyLogVo> queryMyNotifies(PageQuery pageQuery, Long userId) {
        QueryWrapper wrapper = QueryWrapper.create().where(MUSIC_NOTIFY_LOG.USER_ID.eq(userId))
            .orderBy(MUSIC_NOTIFY_LOG.ID.desc());
        Page<MusicNotifyLogVo> result = notifyLogMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }
}
