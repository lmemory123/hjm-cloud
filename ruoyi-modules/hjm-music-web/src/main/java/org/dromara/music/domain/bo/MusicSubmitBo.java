package org.dromara.music.domain.bo;

import lombok.Data;

import java.util.List;

@Data
public class MusicSubmitBo {
    private Long userId;
    private String title;
    private String subtitle;
    private String isOriginal;
    private String copyrightInfo;
    private String remark;
    private List<Long> resourceIds;
    private List<Long> tagIds;
    private List<OriginalInfoBo> originalInfoList;
    private List<TagProposalBo> tagProposals;

    @Data
    public static class OriginalInfoBo {
        private String originalTitle;
        private String originalAuthor;
        private String originalAlbum;
        private String originalLink;
        private String sourceType;
        private String relationType;
    }

    @Data
    public static class TagProposalBo {
        private String tagName;
        private String tagType;
        private String description;
    }
}