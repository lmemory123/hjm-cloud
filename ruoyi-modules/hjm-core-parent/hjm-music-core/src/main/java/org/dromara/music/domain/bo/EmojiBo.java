package org.dromara.music.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.music.domain.Emoji;

/**
 * 表情包业务对象 emoji
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Emoji.class, reverseConvertGenerate = false)
public class EmojiBo extends QueryBaseEntity {

    @NotNull(message = "主键ID不能为空", groups = {EditGroup.class})
    private Long id;

    @NotBlank(message = "表情URL不能为空", groups = {AddGroup.class, EditGroup.class})
    private String url;

    @NotBlank(message = "表情名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    @NotBlank(message = "分类不能为空", groups = {AddGroup.class, EditGroup.class})
    private String category;

    private Integer status;
}
