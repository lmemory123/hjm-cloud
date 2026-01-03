package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicCoinLedger;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 哈气金流水业务对象 music_coin_ledger
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicCoinLedger.class, reverseConvertGenerate = false)
public class MusicCoinLedgerBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 变动金额(+/-)
     */
    @NotNull(message = "变动金额(+/-)不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long amount;

    /**
     * 变动原因(upload_reward/system_grant)
     */
    @NotBlank(message = "变动原因(upload_reward/system_grant)不能为空", groups = { AddGroup.class, EditGroup.class })
    private String reasonCode;

    /**
     * 变动后余额
     */
    @NotNull(message = "变动后余额不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long balanceAfter;


}
