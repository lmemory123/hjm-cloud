package org.dromara.system.domain.vo;

import org.dromara.system.domain.MusicTagRel;
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
 * 音乐标签关联视图对象 music_tag_rel
 *
 * @author momao
 * @date 2025-12-30
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
     * 标签权重（0-100）
     */
    @ExcelProperty(value = "标签权重", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=-100")
    private Long tagWeight;

    /**
     * 是否主标签: 0否 1是
     */
    @ExcelProperty(value = "是否主标签: 0否 1是")
    private String isPrimary;

    /**
     * 标签来源: manual/auto/user
     */
    @ExcelProperty(value = "标签来源: manual/auto/user")
    private String source;


}
