package org.dromara.music.domain.vo;

import lombok.Data;

@Data
public class OpenSearchFacetItemVo {

    private String type;

    private String label;

    private String value;

    private String description;

    private Long count;

    private Boolean selected;
}
