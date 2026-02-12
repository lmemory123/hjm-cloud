package org.dromara.music.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 榜单明细对象 music_chart_item
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_chart_item")
public class MusicChartItem extends QueryBaseEntity {


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

    /**
     * 创建时间
     */
    private Date createTime;


}
