package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 音乐标签关联对象 music_tag_rel
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_tag_rel")
public class MusicTagRel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 标签权重（0-100）
     */
    private Long tagWeight;

    /**
     * 是否主标签: 0否 1是
     */
    private String isPrimary;

    /**
     * 标签来源: manual/auto/user
     */
    private String source;


}
