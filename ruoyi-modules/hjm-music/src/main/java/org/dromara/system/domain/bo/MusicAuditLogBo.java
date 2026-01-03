package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicAuditLog;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 音乐审核流水日志业务对象 music_audit_log
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicAuditLog.class, reverseConvertGenerate = false)
public class MusicAuditLogBo extends BaseEntity {

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
     * 目标类型: music/comment
     */
    private String targetType;

    /**
     * 审核动作: 1通过 2拒绝 3下架
     */
    @NotNull(message = "审核动作: 1通过 2拒绝 3下架不能为空", groups = { AddGroup.class, EditGroup.class })
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
     * 下架/审核快照(JSONB)
     */
    private String snapshot;

    /**
     * 操作人ID (后台管理员ID)
     */
    private Long operatorId;


}
