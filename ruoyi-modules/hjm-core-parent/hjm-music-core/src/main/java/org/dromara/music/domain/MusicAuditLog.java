package org.dromara.music.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 音乐审核流水日志对象 music_audit_log
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_audit_log")
public class MusicAuditLog extends QueryBaseEntity {


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
     * 目标类型
     */
    private String targetType;

    /**
     * 审核动作
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
     * 下架/审核快照
     */
    private String snapshot;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作时间
     */
    private Date createTime;


}
