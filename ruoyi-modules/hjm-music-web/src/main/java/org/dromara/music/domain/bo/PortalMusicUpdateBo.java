package org.dromara.music.domain.bo;

import lombok.Data;

import java.util.List;

@Data
public class PortalMusicUpdateBo {
    private Long id;
    private String title;
    private String subtitle;
    private String isOriginal;
    private String copyrightInfo;
    private String remark;
    private List<Long> resourceIds;
    private List<Long> tagIds;
    private List<MusicSubmitBo.OriginalInfoBo> originalInfoList;
    private List<MusicSubmitBo.TagProposalBo> tagProposals;
}
