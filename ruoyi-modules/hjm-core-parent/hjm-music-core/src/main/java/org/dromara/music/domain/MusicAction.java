package org.dromara.music.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


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
