package org.dromara.system.domain.bo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 音乐审核业务对象
 *
 * @author momao
 * @date 2025-11-30
 */
@Data
public class MusicAuditBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 音乐ID
     */
    @NotNull(message = "音乐ID不能为空")
    private Long musicId;

    /**
     * 审核动作: 1通过 2拒绝 3下架
     */
    @NotNull(message = "审核动作不能为空")
    private Integer action;

    /**
     * 拒绝或下架原因（action为2或3时必填）
     */
    private String reason;

}
