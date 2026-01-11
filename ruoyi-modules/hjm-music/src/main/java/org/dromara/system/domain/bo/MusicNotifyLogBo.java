package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.system.domain.MusicNotifyLog;

import java.util.Date;

/**
 * 音乐通知日志业务对象 music_notify_log
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicNotifyLog.class, reverseConvertGenerate = false)
public class MusicNotifyLogBo extends QueryBaseEntity {

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
     * 接收用户ID
     */
    @NotNull(message = "接收用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 通知类型
     */
    @NotBlank(message = "通知类型不能为空", groups = { AddGroup.class, EditGroup.class })
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
     * 发送渠道
     */
    private String sendChannel;

    /**
     * 发送状态
     */
    private String sendStatus;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 阅读状态
     */
    private String readStatus;

    /**
     * 阅读时间
     */
    private Date readTime;


}
