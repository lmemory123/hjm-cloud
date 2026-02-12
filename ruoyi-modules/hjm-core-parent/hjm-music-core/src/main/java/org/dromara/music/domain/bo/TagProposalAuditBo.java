package org.dromara.music.domain.bo;

import lombok.Data;

@Data
public class TagProposalAuditBo {
    /**
     * 提报ID
     */
    private Long id;

    /**
     * 审核动作：1通过 2拒绝
     */
    private Integer action;

    /**
     * 审核备注/拒绝原因
     */
    private String remark;
}
