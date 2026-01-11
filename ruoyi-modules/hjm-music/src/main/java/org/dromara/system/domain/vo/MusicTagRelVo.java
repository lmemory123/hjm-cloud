package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.MusicTagRel;

import java.io.Serial;
import java.io.Serializable;



/**
 * 音乐标签关联视图对象 music_tag_rel
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicTagRel.class)
public class MusicTagRelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 音乐ID
     */
    @ExcelProperty(value = "音乐ID")
    private Long musicId;

    /**
     * 标签ID
     */
    @ExcelProperty(value = "标签ID")
    private Long tagId;

    /**
     * 标签权重
     */
    @ExcelProperty(value = "标签权重")
    private Long tagWeight;

    /**
     * 是否主标签
     */
    @ExcelProperty(value = "是否主标签")
    private String isPrimary;

    /**
     * 标签来源
     */
    @ExcelProperty(value = "标签来源")
    private String source;


}
