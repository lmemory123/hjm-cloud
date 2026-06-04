package org.dromara.music.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;

/**
 * 表情包表对象 emoji
 *
 * @author momao
 * @date 2026-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("emoji")
public class Emoji extends QueryBaseEntity {

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 表情URL
     */
    private String url;

    /**
     * 表情名称
     */
    private String name;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态: 0=待审核, 1=已通过, 2=已拒绝
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志: 0存在 1删除
     */
    @Column(isLogicDelete = true)
    private String delFlag;
}
