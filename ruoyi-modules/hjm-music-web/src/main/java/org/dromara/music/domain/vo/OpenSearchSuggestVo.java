package org.dromara.music.domain.vo;

import lombok.Data;

@Data
public class OpenSearchSuggestVo {

    private Long musicId;

    private Long tagId;

    private String keyword;

    private String title;

    private String subtitle;

    private String creatorName;

    private String suggestType;

    private String matchType;

    private String matchedText;

    private String highlightedText;
}
