package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 榜单明细对象 music_chart_item
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_chart_item")
public class MusicChartItem extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 榜单快照ID
     */
    private Long snapshotId;

    /**
     * 音乐ID
     */
    private Long musicId;

    /**
     * 排名
     */
    private Long rankNo;

    /**
     * 综合热度分
     */
    private Long score;

    /**
     * 播放量
     */
    private Long playCount;

    /**
     * 点赞量
     */
    private Long likeCount;


}
