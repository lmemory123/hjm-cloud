package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.Tag;
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
 * 标签字典视图对象 tag
 *
 * @author momao
 * @date 2026-01-07
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
     * 标签类型
     */
    @ExcelProperty(value = "标签类型")
    private String type;

    /**
     * 标签别名
     */
    @ExcelProperty(value = "标签别名")
    private String tagAlias;

    /**
     * 父标签ID
     */
    @ExcelProperty(value = "父标签ID")
    private Long parentId;

    /**
     * 标签图标
     */
    @ExcelProperty(value = "标签图标")
    private String iconUrl;

    /**
     * 标签颜色
     */
    @ExcelProperty(value = "标签颜色")
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
     * 是否热门
     */
    @ExcelProperty(value = "是否热门", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_yes_no")
    private String isHot;

    /**
     * 是否推荐
     */
    @ExcelProperty(value = "是否推荐", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_yes_no")
    private String isRecommend;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;


}
