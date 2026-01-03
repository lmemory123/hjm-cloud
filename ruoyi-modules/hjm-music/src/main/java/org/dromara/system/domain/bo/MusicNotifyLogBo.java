package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicNotifyLog;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 音乐通知日志业务对象 music_notify_log
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicNotifyLog.class, reverseConvertGenerate = false)
public class MusicNotifyLogBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 音乐ID
     */
    @NotNull(message = "音乐ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long musicId;

    /**
     * 接收用户ID（UP主）
     */
    @NotNull(message = "接收用户ID（UP主）不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 通知类型: link_invalid/audit_result/resource_update
     */
    @NotBlank(message = "通知类型: link_invalid/audit_result/resource_update不能为空", groups = { AddGroup.class, EditGroup.class })
    private String notifyType;

    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空", groups = { AddGroup.class, EditGroup.class })
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
