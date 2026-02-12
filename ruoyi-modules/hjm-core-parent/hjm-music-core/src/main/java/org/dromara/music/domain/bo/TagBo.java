package org.dromara.music.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.music.domain.Tag;

/**
 * 标签字典业务对象 tag
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Tag.class, reverseConvertGenerate = false)
public class TagBo extends QueryBaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 标签类型
     */
    private String type;

    /**
     * 标签别名
     */
    private String tagAlias;

    /**
     * 父标签ID
     */
    private Long parentId;

    /**
     * 标签图标
     */
    private String iconUrl;

    /**
     * 标签颜色
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
     * 是否热门
     */
    private String isHot;

    /**
     * 是否推荐
     */
    private String isRecommend;

    /**
     * 状态
     */
    private String status;


}
