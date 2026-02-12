package org.dromara.music.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.music.domain.MusicCoinLedger;

/**
 * 哈气金流水业务对象 music_coin_ledger
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicCoinLedger.class, reverseConvertGenerate = false)
public class MusicCoinLedgerBo extends QueryBaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long userId;

    /**
     * 变动金额
     */
    @NotNull(message = "变动金额不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long amount;

    /**
     * 变动原因
     */
    @NotBlank(message = "变动原因不能为空", groups = {AddGroup.class, EditGroup.class})
    private String reasonCode;

    /**
     * 变动后余额
     */
    @NotNull(message = "变动后余额不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long balanceAfter;


}
