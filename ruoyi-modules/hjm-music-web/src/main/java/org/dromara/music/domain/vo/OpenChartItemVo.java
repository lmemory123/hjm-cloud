package org.dromara.music.domain.vo;

import lombok.Data;

@Data
public class OpenChartItemVo {

    private Long id;

    private Long snapshotId;

    private Long musicId;

    private Long rankNo;

    private Long score;

    private Long playCount;

    private Long likeCount;

    private MusicVo song;
}
