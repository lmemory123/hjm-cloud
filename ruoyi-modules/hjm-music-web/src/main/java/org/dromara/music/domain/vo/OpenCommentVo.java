package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OpenCommentVo {

    private Long id;

    private Long musicId;

    private Long userId;

    private String nickName;

    private String content;

    private Long rootId;

    private Long parentId;

    private Date createTime;

    private List<OpenCommentVo> replies;
}
