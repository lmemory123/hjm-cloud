package org.dromara.music.domain.vo;

import lombok.Data;

@Data
public class OpenChartArchiveVo {

    private Long snapshotId;

    private String chartType;

    private String periodKey;

    private String status;

    private Long itemCount;
}
