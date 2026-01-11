package org.dromara.system.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 榜单快照对象 music_chart_snapshot
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_chart_snapshot")
public class MusicChartSnapshot extends QueryBaseEntity {



    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 榜单类型
     */
    private String chartType;

    /**
     * 周期标识
     */
    private String periodKey;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;


}
