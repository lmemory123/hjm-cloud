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
    private Long coverResourceId;
    private List<Long> resourceIds;
    private List<ResourceInfoBo> uploadedResources;
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

    @Data
    public static class ResourceInfoBo {
        private String id;
        private String name;
        private String url;
        private Long size;
        private String contentType;
        private String kind;
    }
}
