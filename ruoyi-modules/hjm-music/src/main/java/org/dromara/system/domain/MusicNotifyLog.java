package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 音乐通知日志对象 music_notify_log
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_notify_log")
public class MusicNotifyLog extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 音乐ID
     */
    private Long musicId;

    /**
     * 接收用户ID（UP主）
     */
    private Long userId;

    /**
     * 通知类型: link_invalid/audit_result/resource_update
     */
    private String notifyType;

    /**
     * 通知标题
     */
    private String notifyTitle;

    /**
     * 通知内容
     */
    private String notifyContent;

    /**
     * 发送渠道: system/email/sms/wechat
     */
    private String sendChannel;

    /**
     * 发送状态: 0待发送 1已发送 2发送失败
     */
    private String sendStatus;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 阅读状态: 0未读 1已读
     */
    private String readStatus;

    /**
     * 阅读时间
     */
    private Date readTime;


}
