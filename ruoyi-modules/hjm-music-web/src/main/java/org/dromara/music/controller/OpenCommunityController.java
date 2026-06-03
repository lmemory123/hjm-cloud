package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.v7.core.map.Dict;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.music.domain.vo.OpenCommunityLinkVo;
import org.dromara.system.api.RemoteConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.dromara.music.constant.MusicInteractionCacheConstants.communityClickKey;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/community")
public class OpenCommunityController {

    private static final String COMMUNITY_CONFIG_KEY = "hajihami.community.links";

    @DubboReference
    private final RemoteConfigService remoteConfigService;

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping
    public R<List<OpenCommunityLinkVo>> list() {
        List<OpenCommunityLinkVo> links = loadConfiguredLinks();
        if (links.isEmpty()) {
            links = defaultLinks();
        }
        links.forEach(link -> link.setClickCount(RedisUtils.getAtomicValue(communityClickKey(link.getId()))));
        return R.ok(links);
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @PostMapping("/{id}/click")
    public R<Void> click(@PathVariable("id") String id) {
        if (StringUtils.isNotBlank(id)) {
            RedisUtils.incrAtomicValue(communityClickKey(id));
        }
        return R.ok();
    }

    private List<OpenCommunityLinkVo> loadConfiguredLinks() {
        try {
            List<Dict> values = remoteConfigService.getConfigArrayMap(COMMUNITY_CONFIG_KEY);
            if (values == null || values.isEmpty()) {
                return List.of();
            }
            List<OpenCommunityLinkVo> links = new ArrayList<>();
            for (int index = 0; index < values.size(); index++) {
                Dict item = values.get(index);
                OpenCommunityLinkVo link = new OpenCommunityLinkVo();
                link.setId(toString(item.get("id"), "community-" + index));
                link.setName(toString(item.get("name"), ""));
                link.setDesc(toString(item.get("desc"), ""));
                link.setIcon(toString(item.get("icon"), "lucide:users"));
                link.setLink(toString(item.get("link"), "#"));
                link.setMembers(toString(item.get("members"), ""));
                link.setNote(toString(item.get("note"), ""));
                if (StringUtils.isNotBlank(link.getName())) {
                    links.add(link);
                }
            }
            return links;
        } catch (Exception ex) {
            return List.of();
        }
    }

    private List<OpenCommunityLinkVo> defaultLinks() {
        List<OpenCommunityLinkVo> links = new ArrayList<>();
        links.add(defaultLink("qq", "哈基哈米 QQ 群", "投稿讨论、周榜围观和补档反馈入口。", "simple-icons:tencentqq", "#", "运营配置后展示", "后台配置 hajihami.community.links"));
        links.add(defaultLink("channel", "腾讯频道", "发布榜单、活动公告和专题征集。", "lucide:radio", "#", "待配置", "可统计点击"));
        links.add(defaultLink("bilibili", "B 站动态", "同步二创精选、投稿教程和站点更新。", "simple-icons:bilibili", "#", "待配置", "外链入口"));
        return links;
    }

    private OpenCommunityLinkVo defaultLink(String id, String name, String desc, String icon, String link, String members, String note) {
        OpenCommunityLinkVo vo = new OpenCommunityLinkVo();
        vo.setId(id);
        vo.setName(name);
        vo.setDesc(desc);
        vo.setIcon(icon);
        vo.setLink(link);
        vo.setMembers(members);
        vo.setNote(note);
        vo.setClickCount(0L);
        return vo;
    }

    private String toString(Object value, String fallback) {
        return value == null ? fallback : String.valueOf(value);
    }
}
