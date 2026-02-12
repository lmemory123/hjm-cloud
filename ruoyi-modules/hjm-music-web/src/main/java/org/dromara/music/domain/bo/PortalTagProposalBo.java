package org.dromara.music.domain.bo;

import lombok.Data;

@Data
public class PortalTagProposalBo {
    private Long musicId;
    private String tagName;
    private String tagType;
    private String description;
}
