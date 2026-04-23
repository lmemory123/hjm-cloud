package org.dromara.music.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "valkey.query.sync", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MusicSearchSyncTask {

    private final MusicSearchIndexService musicSearchIndexService;

    @Scheduled(
        initialDelayString = "${valkey.query.sync.initial-delay:30000}",
        fixedDelayString = "${valkey.query.sync.fixed-delay:300000}"
    )
    public void syncPublicMusicIndex() {
        try {
            musicSearchIndexService.syncPublicIndex();
        } catch (Exception ex) {
            log.error("音乐公开搜索索引同步任务执行失败", ex);
        }
    }
}
