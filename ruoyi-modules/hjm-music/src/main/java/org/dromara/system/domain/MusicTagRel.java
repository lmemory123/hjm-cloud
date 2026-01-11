package org.dromara.system.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 音乐标签关联对象 music_tag_rel
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_tag_rel")
public class MusicTagRel extends QueryBaseEntity {



    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 音乐ID
     */
    private Long musicId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签权重
     */
    private Long tagWeight;

    /**
     * 是否主标签
     */
    private String isPrimary;

    /**
     * 标签来源
     */
    private String source;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;


}
