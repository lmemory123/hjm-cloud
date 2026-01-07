package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicAuditLog;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 音乐审核流水日志业务对象 music_audit_log
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicAuditLog.class, reverseConvertGenerate = false)
public class MusicAuditLogBo extends QueryBaseEntity {

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
     * 目标类型
     */
    private String targetType;

    /**
     * 审核动作
     */
    @NotNull(message = "审核动作不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long action;

    /**
     * 修改前状态
     */
    private Long oldStatus;

    /**
     * 修改后状态
     */
    private Long newStatus;

    /**
     * 拒绝或下架原因
     */
    private String reason;

    /**
     * 下架/审核快照
     */
    private String snapshot;

    /**
     * 操作人ID
     */
    private Long operatorId;


}
