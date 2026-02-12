package org.dromara.music.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.common.mybatisflex.handler.PgJsonbTypeHandler;

import java.util.Date;


/**
 * 投稿草稿对象 music_draft
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_draft")
public class MusicDraft extends QueryBaseEntity {


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
     * 表单草稿
     */
    @Column(typeHandler = PgJsonbTypeHandler.class)
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
