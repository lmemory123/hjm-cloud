package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 音乐互动动作对象 music_action
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_action")
public class MusicAction extends QueryBaseEntity {



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
     * 目标类型
     */
    private String targetType;

    /**
     * 动作
     */
    private String action;

    /**
     * 创建时间
     */
    private Date createTime;


}
