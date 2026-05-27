package org.dromara.music.constant;

/**
 * 开发者 API 相关常量
 *
 * @author momao
 * @date 2026-05-27
 */
public interface DeveloperApiConstants {

    /**
     * API Key 请求头
     */
    String API_KEY_HEADER = "X-Hakimi-Api-Key";

    /**
     * 开发者 Token 配置键
     */
    String API_KEYS_CONFIG_KEY = "hajihami.developer.api_keys";

    /**
     * Webhook 配置键
     */
    String WEBHOOKS_CONFIG_KEY = "hajihami.developer.webhooks";

    /**
     * Webhook 签名密钥请求头
     */
    String WEBHOOK_SECRET_HEADER = "X-Hakimi-Webhook-Secret";

    /**
     * Webhook 事件类型请求头
     */
    String WEBHOOK_EVENT_HEADER = "X-Hakimi-Event";

    /**
     * Webhook 签名摘要请求头 (HMAC-SHA256)
     */
    String WEBHOOK_SIGNATURE_HEADER = "X-Hakimi-Signature";

    /**
     * Webhook 签名时间戳请求头
     */
    String WEBHOOK_TIMESTAMP_HEADER = "X-Hakimi-Timestamp";
}
