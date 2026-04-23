package org.dromara.music.domain.vo;

import lombok.Data;

@Data
public class OpenSearchSortOptionVo {

    private String code;

    private String label;

    private String description;

    private Boolean selected;

    private Boolean recommended;
}
