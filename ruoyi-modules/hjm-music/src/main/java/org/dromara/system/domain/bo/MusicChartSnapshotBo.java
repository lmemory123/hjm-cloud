package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.system.domain.MusicChartSnapshot;

/**
 * 榜单快照业务对象 music_chart_snapshot
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicChartSnapshot.class, reverseConvertGenerate = false)
public class MusicChartSnapshotBo extends QueryBaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 榜单类型
     */
    @NotBlank(message = "榜单类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String chartType;

    /**
     * 周期标识
     */
    @NotBlank(message = "周期标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private String periodKey;

    /**
     * 状态
     */
    private String status;


}
