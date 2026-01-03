package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 音乐审核流水日志对象 music_audit_log
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_audit_log")
public class MusicAuditLog extends BaseEntity {

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
     * 目标类型: music/comment
     */
    private String targetType;

    /**
     * 审核动作: 1通过 2拒绝 3下架
     */
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
