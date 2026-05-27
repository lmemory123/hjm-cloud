package org.dromara.music.service;

import org.dromara.music.domain.bo.DeveloperWebhookDispatchBo;

/**
 * 开发者 Webhook 服务
 *
 * @author momao
 * @date 2026-05-27
 */
public interface IDeveloperWebhookService {

    /**
     * 异步推送 Webhook
     *
     * @param clientId 触发应用ID
     * @param bo 推送内容
     */
    void pushAsync(String clientId, DeveloperWebhookDispatchBo bo);
}
