package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 榜单快照对象 music_chart_snapshot
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_chart_snapshot")
public class MusicChartSnapshot extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 榜单类型: week/month
     */
    private String chartType;

    /**
     * 周期标识(如 2025-W04)
     */
    private String periodKey;

    /**
     * 状态: calculating/published
     */
    private String status;


}
