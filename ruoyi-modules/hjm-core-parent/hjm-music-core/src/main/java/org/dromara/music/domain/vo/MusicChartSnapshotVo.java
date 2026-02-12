package org.dromara.music.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.music.domain.MusicChartSnapshot;

import java.io.Serial;
import java.io.Serializable;


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
