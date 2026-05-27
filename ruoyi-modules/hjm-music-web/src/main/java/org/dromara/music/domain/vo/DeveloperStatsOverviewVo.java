package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeveloperStatsOverviewVo {

    private Long publicSongCount;

    private Integer weekChartItemCount;

    private Integer monthChartItemCount;

    private Integer tagCount;

    private List<String> hotKeywords;

    private String generatedAt;
}
