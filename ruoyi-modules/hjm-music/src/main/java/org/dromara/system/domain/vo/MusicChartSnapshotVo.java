package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.MusicChartSnapshot;
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
 * 榜单快照视图对象 music_chart_snapshot
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicChartSnapshot.class)
public class MusicChartSnapshotVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 榜单类型
     */
    @ExcelProperty(value = "榜单类型")
    private String chartType;

    /**
     * 周期标识
     */
    @ExcelProperty(value = "周期标识")
    private String periodKey;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;


}
