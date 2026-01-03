package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicComment;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 音乐评论业务对象 music_comment
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicComment.class, reverseConvertGenerate = false)
public class MusicCommentBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 音乐ID
     */
    @NotNull(message = "音乐ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long musicId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;

    /**
     * 顶级评论ID(0表示自身为顶级)
     */
    private Long rootId;

    /**
     * 父评论ID(0表示直接回复音乐)
     */
    private Long parentId;


}
