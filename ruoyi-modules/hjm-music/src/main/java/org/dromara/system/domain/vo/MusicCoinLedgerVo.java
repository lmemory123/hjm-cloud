package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.MusicCoinLedger;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 哈气金流水视图对象 music_coin_ledger
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicCoinLedger.class)
public class MusicCoinLedgerVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 变动金额
     */
    @ExcelProperty(value = "变动金额")
    private Long amount;

    /**
     * 变动原因
     */
    @ExcelProperty(value = "变动原因")
    private String reasonCode;

    /**
     * 变动后余额
     */
    @ExcelProperty(value = "变动后余额")
    private Long balanceAfter;


}
