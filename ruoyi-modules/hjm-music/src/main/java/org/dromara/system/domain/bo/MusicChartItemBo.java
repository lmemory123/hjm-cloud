package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicChartItem;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 榜单明细业务对象 music_chart_item
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicChartItem.class, reverseConvertGenerate = false)
public class MusicChartItemBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 榜单快照ID
     */
    @NotNull(message = "榜单快照ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long snapshotId;

    /**
     * 音乐ID
     */
    @NotNull(message = "音乐ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long musicId;

    /**
     * 排名
     */
    @NotNull(message = "排名不能为空", groups = { AddGroup.class, EditGroup.class })
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
