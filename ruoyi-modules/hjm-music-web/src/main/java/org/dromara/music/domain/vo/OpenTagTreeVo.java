package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OpenTagTreeVo {

    private Long id;

    private String name;

    private String type;

    private String tagAlias;

    private Long parentId;

    private String iconUrl;

    private String color;

    private String description;

    private Long useCount;

    private Long sortOrder;

    private String isHot;

    private String isRecommend;

    private String status;

    private List<OpenTagTreeVo> children = new ArrayList<>();
}
