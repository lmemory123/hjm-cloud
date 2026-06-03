package org.dromara.music.domain.bo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class MusicSubmitBo {
    private Long userId;

    @NotBlank(message = "作品标题不能为空")
    @Size(max = 100, message = "作品标题长度不能超过100")
    private String title;

    @Size(max = 200, message = "副标题长度不能超过200")
    private String subtitle;

    @NotBlank(message = "原创属性不能为空")
    private String isOriginal;

    @Size(max = 500, message = "版权说明长度不能超过500")
    private String copyrightInfo;

    @Size(max = 1000, message = "备注长度不能超过1000")
    private String remark;

    private Long coverResourceId;

    @NotEmpty(message = "资源列表不能为空")
    private List<Long> resourceIds;

    @Valid
    @NotEmpty(message = "上传资源详情不能为空")
    private List<ResourceInfoBo> uploadedResources;

    private List<Long> tagIds;

    @Valid
    private List<OriginalInfoBo> originalInfoList;

    @Valid
    private List<TagProposalBo> tagProposals;

    @Data
    public static class OriginalInfoBo {
        @NotBlank(message = "原曲标题不能为空")
        @Size(max = 200)
        private String originalTitle;
        @Size(max = 200)
        private String originalAuthor;
        @Size(max = 200)
        private String originalAlbum;
        @Size(max = 500)
        private String originalLink;
        private String sourceType;
        private String relationType;
    }

    @Data
    public static class TagProposalBo {
        @NotBlank(message = "建议标签名不能为空")
        @Size(max = 50)
        private String tagName;
        @Size(max = 50)
        private String tagType;
        @Size(max = 500)
        private String description;
    }

    @Data
    public static class ResourceInfoBo {
        @NotBlank(message = "资源ID不能为空")
        private String id;
        @NotBlank(message = "资源名称不能为空")
        @Size(max = 255)
        private String name;
        @NotBlank(message = "资源地址不能为空")
        private String url;
        private Long size;
        private String contentType;
        private String kind;
    }
}
