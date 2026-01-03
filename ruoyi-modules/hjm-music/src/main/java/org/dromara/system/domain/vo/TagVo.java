package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.Tag;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 标签字典视图对象 tag
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Tag.class)
public class TagVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 标签名称
     */
    @ExcelProperty(value = "标签名称")
    private String name;

    /**
     * 标签类型: style/mood/scene/language/instrument/era/theme
     */
    @ExcelProperty(value = "标签类型: style/mood/scene/language/instrument/era/theme")
    private String type;

    /**
     * 标签别名/同义词（逗号分隔）
     */
    @ExcelProperty(value = "标签别名/同义词", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "逗=号分隔")
    private String tagAlias;

    /**
     * 父标签ID（支持层级）
     */
    @ExcelProperty(value = "父标签ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "支=持层级")
    private Long parentId;

    /**
     * 标签图标URL
     */
    @ExcelProperty(value = "标签图标URL")
    private String iconUrl;

    /**
     * 标签颜色(HEX)
     */
    @ExcelProperty(value = "标签颜色(HEX)")
    private String color;

    /**
     * 标签描述
     */
    @ExcelProperty(value = "标签描述")
    private String description;

    /**
     * 使用次数统计
     */
    @ExcelProperty(value = "使用次数统计")
    private Long useCount;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Long sortOrder;

    /**
     * 是否热门: 0否 1是
     */
    @ExcelProperty(value = "是否热门: 0否 1是")
    private String isHot;

    /**
     * 是否推荐: 0否 1是
     */
    @ExcelProperty(value = "是否推荐: 0否 1是")
    private String isRecommend;

    /**
     * 状态: 0禁用 1启用
     */
    @ExcelProperty(value = "状态: 0禁用 1启用")
    private String status;


}
