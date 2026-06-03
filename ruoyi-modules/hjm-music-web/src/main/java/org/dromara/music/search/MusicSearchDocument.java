package org.dromara.music.search;

import com.momao.valkey.annotation.StorageType;
import com.momao.valkey.annotation.ValkeyDocument;
import com.momao.valkey.annotation.ValkeyId;
import com.momao.valkey.annotation.ValkeyNumeric;
import com.momao.valkey.annotation.ValkeySearchable;
import com.momao.valkey.annotation.ValkeyTag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ValkeyDocument(indexName = "idx:music:public", prefixes = {"music:public:"}, storageType = StorageType.JSON)
public class MusicSearchDocument implements Serializable {
    @ValkeyId
    private String id;

    @ValkeySearchable(weight = 5.0d, noStem = true)
    private String title;

    @ValkeySearchable(weight = 3.0d, noStem = true)
    private String subtitle;

    @ValkeySearchable(value = "original_title", weight = 2.4d, noStem = true)
    private String originalTitle;

    @ValkeyNumeric(value = "creator_id", sortable = true)
    private Long creatorId;

    @ValkeySearchable(value = "creator_name", weight = 2.8d, noStem = true)
    private String creatorName;

    private String creatorLink;

    @ValkeyTag("producer_mark")
    private String producerMark;

    @ValkeyNumeric(sortable = true)
    private Long duration;

    @ValkeyNumeric(sortable = true)
    private Long bpm;

    @ValkeyNumeric(value = "publish_time", sortable = true)
    private Long publishTimeMillis;

    @ValkeyNumeric(value = "play_count", sortable = true)
    private Long playCount;

    @ValkeyNumeric(value = "like_count", sortable = true)
    private Long likeCount;

    @ValkeyNumeric(value = "collect_count", sortable = true)
    private Long collectCount;

    @ValkeyNumeric(value = "comment_count", sortable = true)
    private Long commentCount;

    @ValkeyNumeric(value = "share_count", sortable = true)
    private Long shareCount;

    @ValkeyNumeric(value = "download_count", sortable = true)
    private Long downloadCount;

    private String originalData;

    private String resourceData;

    private String tagsSnapshot;

    private String extendData;

    @ValkeyTag("audit_status")
    private String auditStatus;

    @ValkeyTag("is_public")
    private String isPublic;

    @ValkeyTag("is_original")
    private String isOriginal;

    @ValkeyTag("is_ai")
    private String isAi;

    @ValkeyTag("resource_status")
    private String resourceStatus;

    private String copyrightInfo;

    private String remark;

    @ValkeyTag("tag")
    private List<String> tags;

    @ValkeySearchable(value = "search_text", weight = 1.0d, noStem = true)
    private String searchText;
}
