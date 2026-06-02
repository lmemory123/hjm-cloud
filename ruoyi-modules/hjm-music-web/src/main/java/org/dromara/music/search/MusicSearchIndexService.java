package org.dromara.music.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.music.mapper.MusicMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicSearchIndexService {

    private final MusicMapper musicMapper;

    public void syncPublicIndex() {
        log.info("Valkey search is disabled. Skipping syncPublicIndex.");
    }

    public boolean syncMusicIndex(Long musicId) {
        return false;
    }

    public boolean removeMusicIndex(Long musicId) {
        return false;
    }
}
