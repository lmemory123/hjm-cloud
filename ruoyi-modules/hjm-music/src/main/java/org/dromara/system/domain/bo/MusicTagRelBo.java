package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicTagRel;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 音乐标签关联业务对象 music_tag_rel
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicTagRel.class, reverseConvertGenerate = false)
public class MusicTagRelBo extends BaseEntity {

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
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空", groups = { AddGroup.class, EditGroup.class })
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
