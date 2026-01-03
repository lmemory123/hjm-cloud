package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 音乐评论对象 music_comment
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_comment")
public class MusicComment extends BaseEntity {

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
     * 用户ID
     */
    private Long userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 顶级评论ID(0表示自身为顶级)
     */
    private Long rootId;

    /**
     * 父评论ID(0表示直接回复音乐)
     */
    private Long parentId;

    /**
     * 删除标志: 0存在 1删除
     */
    @Column(isLogicDelete = true)
    private String delFlag;


}
