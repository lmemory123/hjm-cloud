package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 哈气金流水对象 music_coin_ledger
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_coin_ledger")
public class MusicCoinLedger extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 变动金额(+/-)
     */
    private Long amount;

    /**
     * 变动原因(upload_reward/system_grant)
     */
    private String reasonCode;

    /**
     * 变动后余额
     */
    private Long balanceAfter;


}
