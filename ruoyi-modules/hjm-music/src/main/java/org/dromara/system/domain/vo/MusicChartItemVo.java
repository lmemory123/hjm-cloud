package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.MusicChartItem;

import java.io.Serial;
import java.io.Serializable;



/**
 * 榜单明细视图对象 music_chart_item
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicChartItem.class)
public class MusicChartItemVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 榜单快照ID
     */
    @ExcelProperty(value = "榜单快照ID")
    private Long snapshotId;

    /**
     * 音乐ID
     */
    @ExcelProperty(value = "音乐ID")
    private Long musicId;

    /**
     * 排名
     */
    @ExcelProperty(value = "排名")
    private Long rankNo;

    /**
     * 综合热度分
     */
    @ExcelProperty(value = "综合热度分")
    private Long score;

    /**
     * 播放量
     */
    @ExcelProperty(value = "播放量")
    private Long playCount;

    /**
     * 点赞量
     */
    @ExcelProperty(value = "点赞量")
    private Long likeCount;


}
