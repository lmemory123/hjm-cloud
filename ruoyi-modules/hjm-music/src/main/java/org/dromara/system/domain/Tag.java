package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 标签字典对象 tag
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("tag")
public class Tag extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 标签名称
     */
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

    /**
     * 删除标志: 0存在 1删除
     */
    @Column(isLogicDelete = true)
    private String delFlag;


}
