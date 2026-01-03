package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicChartSnapshot;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 榜单快照业务对象 music_chart_snapshot
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicChartSnapshot.class, reverseConvertGenerate = false)
public class MusicChartSnapshotBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 榜单类型: week/month
     */
    @NotBlank(message = "榜单类型: week/month不能为空", groups = { AddGroup.class, EditGroup.class })
    private String chartType;

    /**
     * 周期标识(如 2025-W04)
     */
    @NotBlank(message = "周期标识(如 2025-W04)不能为空", groups = { AddGroup.class, EditGroup.class })
    private String periodKey;

    /**
     * 状态: calculating/published
     */
    private String status;


}
