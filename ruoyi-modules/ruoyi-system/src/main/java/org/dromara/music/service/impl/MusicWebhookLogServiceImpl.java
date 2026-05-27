package org.dromara.music.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.MusicWebhookLog;
import org.dromara.music.domain.vo.MusicWebhookLogVo;
import org.dromara.music.mapper.MusicWebhookLogMapper;
import org.dromara.music.service.IMusicWebhookLogService;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 开发者 Webhook 调用日志 Service 业务层处理
 *
 * @author momao
 * @date 2026-05-27
 */
@Service
@RequiredArgsConstructor
public class MusicWebhookLogServiceImpl implements IMusicWebhookLogService {

    private final MusicWebhookLogMapper baseMapper;

    @Override
    public TableDataInfo<MusicWebhookLogVo> queryPageList(MusicWebhookLog query, PageQuery pageQuery) {
        QueryWrapper wrapper = QueryWrapper.create(query);
        wrapper.orderBy("create_time", false);
        Page<MusicWebhookLogVo> page = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(page);
    }

    @Override
    public MusicWebhookLogVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public Boolean deleteByIds(Collection<Long> ids) {
        return baseMapper.deleteBatchByIds(ids) > 0;
    }
}
