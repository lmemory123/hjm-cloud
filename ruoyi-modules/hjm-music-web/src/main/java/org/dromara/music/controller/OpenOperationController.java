package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.v7.core.map.Dict;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.domain.R;
import org.dromara.music.domain.vo.OpenOperationVo;
import org.dromara.system.api.RemoteConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/operation")
public class OpenOperationController {

    private static final String ANNOUNCEMENTS_KEY = "hajihami.operation.announcements";
    private static final String RECOMMEND_KEY = "hajihami.operation.recommend_cards";
    private static final String INCENTIVE_KEY = "hajihami.incentive.activity";

    @DubboReference
    private final RemoteConfigService remoteConfigService;

    @SaIgnore
    @GetMapping
    public R<OpenOperationVo> detail() {
        OpenOperationVo vo = new OpenOperationVo();
        vo.setAnnouncements(loadAnnouncements());
        vo.setRecommendCards(loadRecommendCards());
        vo.setIncentiveActivity(loadIncentiveActivity());
        return R.ok(vo);
    }

    private List<OpenOperationVo.Announcement> loadAnnouncements() {
        List<Dict> values = safeArrayConfig(ANNOUNCEMENTS_KEY);
        if (values.isEmpty()) {
            OpenOperationVo.Announcement announcement = new OpenOperationVo.Announcement();
            announcement.setId("default");
            announcement.setTitle("哈基哈米音乐社区 MVP 持续建设中");
            announcement.setContent("投稿、榜单、评论和收藏已经逐步接入，欢迎继续提交二创音乐。");
            announcement.setLink("/upload");
            announcement.setLevel("info");
            return List.of(announcement);
        }
        return values.stream().map(item -> {
            OpenOperationVo.Announcement announcement = new OpenOperationVo.Announcement();
            announcement.setId(toString(item.get("id"), ""));
            announcement.setTitle(toString(item.get("title"), ""));
            announcement.setContent(toString(item.get("content"), ""));
            announcement.setLink(toString(item.get("link"), ""));
            announcement.setLevel(toString(item.get("level"), "info"));
            return announcement;
        }).filter(item -> item.getTitle() != null && !item.getTitle().isBlank()).toList();
    }

    private List<OpenOperationVo.RecommendCard> loadRecommendCards() {
        List<Dict> values = safeArrayConfig(RECOMMEND_KEY);
        if (values.isEmpty()) {
            OpenOperationVo.RecommendCard card = new OpenOperationVo.RecommendCard();
            card.setId("week-chart");
            card.setTitle("本周哈基金曲");
            card.setDesc("运营推荐位可在系统参数中配置，用于承接专题、榜单和活动。");
            card.setLink("/chart/week");
            card.setIcon("lucide:bar-chart-3");
            return List.of(card);
        }
        return values.stream().map(item -> {
            OpenOperationVo.RecommendCard card = new OpenOperationVo.RecommendCard();
            card.setId(toString(item.get("id"), ""));
            card.setTitle(toString(item.get("title"), ""));
            card.setDesc(toString(item.get("desc"), ""));
            card.setLink(toString(item.get("link"), ""));
            card.setIcon(toString(item.get("icon"), "lucide:sparkles"));
            return card;
        }).filter(item -> item.getTitle() != null && !item.getTitle().isBlank()).toList();
    }

    private OpenOperationVo.IncentiveActivity loadIncentiveActivity() {
        Dict value = safeMapConfig(INCENTIVE_KEY);
        OpenOperationVo.IncentiveActivity activity = new OpenOperationVo.IncentiveActivity();
        activity.setTitle(toString(value.get("title"), "哈气金激励计划"));
        activity.setDesc(toString(value.get("desc"), "周榜、月榜和新人投稿可纳入运营发放，发放记录在后台哈气金流水中追踪。"));
        activity.setRule(toString(value.get("rule"), "后台按活动规则手动发放，后续接自动规则。"));
        activity.setLink(toString(value.get("link"), "/upload"));
        activity.setPoolAmount(toLong(value.get("poolAmount"), 0L));
        return activity;
    }

    private List<Dict> safeArrayConfig(String key) {
        try {
            List<Dict> values = remoteConfigService.getConfigArrayMap(key);
            return values == null ? List.of() : values;
        } catch (Exception ex) {
            return List.of();
        }
    }

    private Dict safeMapConfig(String key) {
        try {
            Dict value = remoteConfigService.getConfigMap(key);
            return value == null ? new Dict() : value;
        } catch (Exception ex) {
            return new Dict();
        }
    }

    private String toString(Object value, String fallback) {
        return value == null ? fallback : String.valueOf(value);
    }

    private Long toLong(Object value, Long fallback) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return value == null ? fallback : Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
