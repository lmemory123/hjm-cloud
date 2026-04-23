package org.dromara.music.api;

public interface RemoteMusicSearchService {

    Boolean syncMusicIndex(Long musicId);

    Boolean removeMusicIndex(Long musicId);
}
