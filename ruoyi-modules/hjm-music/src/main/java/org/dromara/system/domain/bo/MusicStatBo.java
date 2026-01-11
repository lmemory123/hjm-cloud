package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.system.domain.MusicStat;

/**
 * 音乐统计(高频读写)业务对象 music_stat
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicStat.class, reverseConvertGenerate = false)
public class MusicStatBo extends QueryBaseEntity {

    /**
     * 音乐ID
     */
    @NotNull(message = "音乐ID不能为空", groups = { EditGroup.class })
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
