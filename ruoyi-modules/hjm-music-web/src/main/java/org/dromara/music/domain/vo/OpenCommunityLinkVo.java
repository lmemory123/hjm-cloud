package org.dromara.music.domain.vo;

import lombok.Data;

@Data
public class OpenCommunityLinkVo {

    private String id;

    private String name;

    private String desc;

    private String icon;

    private String link;

    private String members;

    private String note;

    private Long clickCount;
}
