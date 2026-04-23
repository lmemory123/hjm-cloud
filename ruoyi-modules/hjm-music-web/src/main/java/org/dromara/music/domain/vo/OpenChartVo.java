package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class OpenChartVo {

    private Long snapshotId;

    private String chartType;

    private String periodKey;

    private String status;

    private List<OpenChartItemVo> items;
}
