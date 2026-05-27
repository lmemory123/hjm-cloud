package org.dromara.music.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.music.domain.MusicWebhookLog;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 开发者 Webhook 调用日志视图对象
 *
 * @author momao
 * @date 2026-05-27
 */
@Data
@AutoMapper(target = MusicWebhookLog.class)
public class MusicWebhookLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * Webhook配置ID
     */
    private String webhookId;

    /**
     * Webhook配置名称
     */
    private String webhookName;

    /**
     * 推送目标URL
     */
    private String url;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 推送报文
     */
    private String payload;

    /**
     * 响应状态码
     */
    private Integer statusCode;

    /**
     * 是否成功: 0否 1是
     */
    private String success;

    /**
     * 错误或响应信息
     */
    private String message;

    /**
     * 已重试次数
     */
    private Integer retryCount;

    /**
     * 触发应用ID
     */
    private String clientId;

    /**
     * 创建时间
     */
    private Date createTime;

}
