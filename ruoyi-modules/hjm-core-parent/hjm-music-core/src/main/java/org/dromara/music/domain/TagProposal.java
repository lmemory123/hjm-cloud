package org.dromara.music.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;

/**
 * 标签提报申请对象 tag_proposal
 *
 * @author momao
 * @date 2026-01-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("tag_proposal")
public class TagProposal extends QueryBaseEntity {

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 提报用户ID
     */
    private Long userId;

    /**
     * 关联的音乐ID（可选）
     */
    private Long musicId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 标签描述/申请理由
     */
    private String description;

    /**
     * 审核状态: 0-待审核 1-已通过 2-已拒绝
     */
    private String status;

    /**
     * 审核人ID
     */
    private Long auditorId;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核备注/拒绝原因
     */
    private String auditRemark;

    /**
     * 通过后关联的正式标签ID
     */
    private Long tagId;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志
     */
    @Column(isLogicDelete = true)
    private String delFlag;
}
