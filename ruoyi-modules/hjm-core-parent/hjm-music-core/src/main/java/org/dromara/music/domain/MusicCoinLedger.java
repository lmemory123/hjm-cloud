package org.dromara.music.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 哈气金流水对象 music_coin_ledger
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_coin_ledger")
public class MusicCoinLedger extends QueryBaseEntity {


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
     * 变动金额
     */
    private Long amount;

    /**
     * 变动原因
     */
    private String reasonCode;

    /**
     * 变动后余额
     */
    private Long balanceAfter;

    /**
     * 创建时间
     */
    private Date createTime;


}
