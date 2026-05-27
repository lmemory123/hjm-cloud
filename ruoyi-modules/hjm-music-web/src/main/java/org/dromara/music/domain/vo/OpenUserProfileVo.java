package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class OpenUserProfileVo {

    private String uid;

    private String displayName;

    private String creatorLink;

    private String avatarUrl;

    private String bio;

    private Long songCount;

    private Long totalPlayCount;

    private Long totalLikeCount;

    private Long totalCollectCount;

    private Long totalCommentCount;

    private List<MusicVo> songs;
}
