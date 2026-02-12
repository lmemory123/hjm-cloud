package org.dromara.music.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 标签字典对象 tag
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("tag")
public class Tag extends QueryBaseEntity {


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

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志
     */
    @Column(isLogicDelete = true)
    private String delFlag;


}
