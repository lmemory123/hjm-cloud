package org.dromara.music.controller;

import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaIgnore;
import org.dromara.common.core.domain.R;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.dromara.music.domain.vo.OpenTagTreeVo;
import org.dromara.music.service.IPortalTagService;
import org.dromara.music.domain.vo.TagVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/open/tag")
public class OpenTagController {

    private final IPortalTagService portalTagService;

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/list")
    public R<List<TagVo>> list(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "type", required = false) String type) {
        return R.ok(portalTagService.listTags(keyword, type));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/tree")
    public R<List<OpenTagTreeVo>> tree(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "type", required = false) String type) {
        List<TagVo> tags = portalTagService.listTags(keyword, type);
        return R.ok(buildTree(tags));
    }

    private List<OpenTagTreeVo> buildTree(List<TagVo> tags) {
        Map<Long, OpenTagTreeVo> nodeMap = new LinkedHashMap<>();
        for (TagVo tag : tags) {
            OpenTagTreeVo node = toTreeNode(tag);
            nodeMap.put(tag.getId(), node);
        }

        List<OpenTagTreeVo> roots = new ArrayList<>();
        for (OpenTagTreeVo node : nodeMap.values()) {
            if (node.getParentId() == null || node.getParentId() == 0 || !nodeMap.containsKey(node.getParentId())) {
                roots.add(node);
                continue;
            }
            nodeMap.get(node.getParentId()).getChildren().add(node);
        }
        return roots;
    }

    private OpenTagTreeVo toTreeNode(TagVo tag) {
        OpenTagTreeVo node = new OpenTagTreeVo();
        node.setId(tag.getId());
        node.setName(tag.getName());
        node.setType(tag.getType());
        node.setTagAlias(tag.getTagAlias());
        node.setParentId(tag.getParentId());
        node.setIconUrl(tag.getIconUrl());
        node.setColor(tag.getColor());
        node.setDescription(tag.getDescription());
        node.setUseCount(tag.getUseCount());
        node.setSortOrder(tag.getSortOrder());
        node.setIsHot(tag.getIsHot());
        node.setIsRecommend(tag.getIsRecommend());
        node.setStatus(tag.getStatus());
        return node;
    }
}
