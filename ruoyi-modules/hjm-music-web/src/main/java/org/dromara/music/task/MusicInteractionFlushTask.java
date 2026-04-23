package org.dromara.music.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.music.service.MusicInteractionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicInteractionFlushTask {

    private final MusicInteractionService musicInteractionService;

    @Scheduled(
        initialDelayString = "${music.interaction.flush-initial-delay:15000}",
        fixedDelayString = "${music.interaction.flush-delay:15000}"
    )
    public void flushMusicInteractionStats() {
        int flushed = musicInteractionService.flushDirtyStats();
        if (flushed > 0) {
            log.info("音乐互动统计刷盘完成, songs={}", flushed);
        }
    }
}
