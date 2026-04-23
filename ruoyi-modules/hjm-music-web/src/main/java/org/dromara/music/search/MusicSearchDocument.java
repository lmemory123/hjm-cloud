package org.dromara.music.search;

import com.momao.valkey.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@ValkeyDocument(
    value = "music_search",
    indexName = "idx:music",
    prefixes = {"music:"}
)
public class MusicSearchDocument {

    @ValkeyId
    private String id;

    @ValkeySearchable(weight = 3.0d, noStem = true)
    private String title;

    @ValkeySearchable(weight = 1.5d, noStem = true)
    private String subtitle;

    @ValkeySearchable("original_title")
    private String originalTitle;

    @ValkeySearchable(value = "creator_name", weight = 2.0d, noStem = true)
    private String creatorName;

    @ValkeyIndexed("producer_mark")
    private String producerMark;

    @ValkeyIndexed(sortable = true)
    private Long duration;

    @ValkeyIndexed(value = "bpm", sortable = true)
    private Long bpm;

    @ValkeyIndexed(value = "publish_time", sortable = true)
    private Long publishTime;

    @ValkeyIndexed(value = "play_count", sortable = true)
    private Long playCount;

    @ValkeyIndexed(value = "like_count", sortable = true)
    private Long likeCount;

    @ValkeyIndexed(value = "collect_count", sortable = true)
    private Long collectCount;

    @ValkeyIndexed(value = "share_count", sortable = true)
    private Long shareCount;

    @ValkeyIndexed(value = "download_count", sortable = true)
    private Long downloadCount;

    @ValkeyIndexed(value = "audit_status")
    private String auditStatus;

    @ValkeyIndexed(value = "is_public")
    private String isPublic;

    @ValkeyIndexed
    private List<String> tags;
}
