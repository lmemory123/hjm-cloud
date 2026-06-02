package org.dromara.music.search;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class MusicSearchDocument implements Serializable {
    private String id;
    private String title;
    private String subtitle;
    private String originalTitle;
    private String creatorName;
    private String producerMark;
    private Integer duration;
    private Integer bpm;
    private Long publishTime;
    private Long playCount;
    private Long likeCount;
    private Long collectCount;
    private Long shareCount;
    private Long downloadCount;
    private String auditStatus;
    private String isPublic;
    private List<String> tags;
}
