package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicAction;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 音乐互动动作业务对象 music_action
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicAction.class, reverseConvertGenerate = false)
public class MusicActionBo extends QueryBaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 目标对象ID
     */
    @NotNull(message = "目标对象ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long targetId;

    /**
     * 目标类型
     */
    @NotBlank(message = "目标类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String targetType;

    /**
     * 动作
     */
    @NotBlank(message = "动作不能为空", groups = { AddGroup.class, EditGroup.class })
    private String action;


}
