package org.dromara.music.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.music.domain.MusicComment;

/**
 * 音乐评论业务对象 music_comment
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicComment.class, reverseConvertGenerate = false)
public class MusicCommentBo extends QueryBaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 音乐ID
     */
    @NotNull(message = "音乐ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long musicId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long userId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空", groups = {AddGroup.class, EditGroup.class})
    private String content;

    /**
     * 顶级评论ID
     */
    private Long rootId;

    /**
     * 父评论ID
     */
    private Long parentId;


}
