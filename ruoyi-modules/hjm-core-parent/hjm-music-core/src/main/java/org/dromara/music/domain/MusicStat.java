package org.dromara.music.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 音乐统计(高频读写)对象 music_stat
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_stat")
public class MusicStat extends QueryBaseEntity {


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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
