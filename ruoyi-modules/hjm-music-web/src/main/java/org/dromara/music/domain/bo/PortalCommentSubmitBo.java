package org.dromara.music.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PortalCommentSubmitBo {

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long rootId;

    private Long parentId;
}
