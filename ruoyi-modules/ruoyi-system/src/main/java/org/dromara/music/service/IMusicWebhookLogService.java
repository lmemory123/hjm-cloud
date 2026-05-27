package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.MusicWebhookLog;
import org.dromara.music.domain.vo.MusicWebhookLogVo;

import java.util.Collection;

/**
 * 开发者 Webhook 调用日志 Service 接口
 *
 * @author momao
 * @date 2026-05-27
 */
public interface IMusicWebhookLogService {

    /**
     * 分页查询 Webhook 调用日志
     */
    TableDataInfo<MusicWebhookLogVo> queryPageList(MusicWebhookLog query, PageQuery pageQuery);

    /**
     * 获取详情
     */
    MusicWebhookLogVo queryById(Long id);

    /**
     * 批量删除
     */
    Boolean deleteByIds(Collection<Long> ids);
}
