package org.dromara.music.dubbo;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.music.api.RemoteMusicSearchService;
import org.dromara.music.search.MusicSearchIndexService;
import org.springframework.stereotype.Service;

@Service
@DubboService
@RequiredArgsConstructor
public class RemoteMusicSearchServiceImpl implements RemoteMusicSearchService {

    private final MusicSearchIndexService musicSearchIndexService;

    @Override
    public Boolean syncMusicIndex(Long musicId) {
        return musicSearchIndexService.syncMusicIndex(musicId);
    }

    @Override
    public Boolean removeMusicIndex(Long musicId) {
        return musicSearchIndexService.removeMusicIndex(musicId);
    }
}
