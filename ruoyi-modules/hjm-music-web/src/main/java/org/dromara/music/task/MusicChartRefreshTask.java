package org.dromara.music.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.music.service.MusicChartGenerationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicChartRefreshTask {

    private final MusicChartGenerationService musicChartGenerationService;

    @Scheduled(
        initialDelayString = "${music.chart.refresh-initial-delay:30000}",
        fixedDelayString = "${music.chart.refresh-delay:3600000}"
    )
    public void refreshCharts() {
        musicChartGenerationService.refreshPublishedCharts();
    }
}
