package org.dromara.system.domain.bo;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.dromara.system.domain.Tag;

/**
 * 标签字典业务对象 tag
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Tag.class, reverseConvertGenerate = false)
public class TagBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 标签类型: style/mood/scene/language/instrument/era/theme
     */
    private String type;

    /**
     * 标签别名/同义词（逗号分隔）
     */
    private String tagAlias;

    /**
     * 父标签ID（支持层级）
     */
    private Long parentId;

    /**
     * 标签图标URL
     */
    private String iconUrl;

    /**
     * 标签颜色(HEX)
     */
    private String color;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 使用次数统计
     */
    private Long useCount;

    /**
     * 排序号
     */
    private Long sortOrder;

    /**
     * 是否热门: 0否 1是
     */
    private String isHot;

    /**
     * 是否推荐: 0否 1是
     */
    private String isRecommend;

    /**
     * 状态: 0禁用 1启用
     */
    private String status;


}
