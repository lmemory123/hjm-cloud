package org.dromara.system.domain.vo;

import org.dromara.system.domain.MusicStat;
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
 * 音乐统计(高频读写)视图对象 music_stat
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicStat.class)
public class MusicStatVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 音乐ID
     */
    @ExcelProperty(value = "音乐ID")
    private Long musicId;

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

    /**
     * 收藏量
     */
    @ExcelProperty(value = "收藏量")
    private Long collectCount;

    /**
     * 评论数
     */
    @ExcelProperty(value = "评论数")
    private Long commentCount;

    /**
     * 分享数
     */
    @ExcelProperty(value = "分享数")
    private Long shareCount;

    /**
     * 下载数
     */
    @ExcelProperty(value = "下载数")
    private Long downloadCount;

    /**
     * 综合热度分
     */
    @ExcelProperty(value = "综合热度分")
    private Long score;


}
