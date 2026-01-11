package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.system.domain.MusicTagRel;

/**
 * 音乐标签关联业务对象 music_tag_rel
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicTagRel.class, reverseConvertGenerate = false)
public class MusicTagRelBo extends QueryBaseEntity {

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


}
