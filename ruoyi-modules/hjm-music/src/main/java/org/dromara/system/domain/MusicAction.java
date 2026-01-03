package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 音乐互动动作对象 music_action
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_action")
public class MusicAction extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 目标类型: song/comment
     */
    private String targetType;

    /**
     * 动作: like/dislike
     */
    private String action;


}
