package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 音乐统计(高频读写)对象 music_stat
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_stat")
public class MusicStat extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 音乐ID
     */
    @Id
    private Long musicId;

    /**
     * 播放量
     */
    private Long playCount;

    /**
     * 点赞量
     */
    private Long likeCount;

    /**
     * 收藏量
     */
    private Long collectCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 分享数
     */
    private Long shareCount;

    /**
     * 下载数
     */
    private Long downloadCount;

    /**
     * 综合热度分
     */
    private Long score;


}
