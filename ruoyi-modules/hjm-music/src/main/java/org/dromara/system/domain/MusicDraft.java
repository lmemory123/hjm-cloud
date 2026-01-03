package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 投稿草稿对象 music_draft
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_draft")
public class MusicDraft extends BaseEntity {

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
     * 表单草稿(JSONB)
     */
    private String content;


}
