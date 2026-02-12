package org.dromara.music.domain.bo;

import lombok.Data;

@Data
public class MusicAuditBo {
    /**
     * 音乐ID
     */
    private Long musicId;

    /**
     * 审核动作：1通过 2拒绝 3下架
     */
    private Integer action;

    /**
     * 拒绝/下架原因
     */
    private String reason;
}
