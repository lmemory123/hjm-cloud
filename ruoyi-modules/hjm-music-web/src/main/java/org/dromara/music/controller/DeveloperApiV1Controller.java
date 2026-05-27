package org.dromara.music.controller;

import cn.hutool.v7.core.map.Dict;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.dromara.music.constant.DeveloperApiConstants;
import org.dromara.music.domain.bo.DeveloperWebhookDispatchBo;
import org.dromara.music.domain.vo.DeveloperApiMetaVo;
import org.dromara.music.domain.vo.DeveloperStatsOverviewVo;
import org.dromara.music.domain.vo.DeveloperWebhookDispatchVo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.domain.vo.OpenChartArchiveVo;
import org.dromara.music.domain.vo.OpenChartVo;
import org.dromara.music.domain.vo.OpenCommentVo;
import org.dromara.music.domain.vo.OpenSearchPanelVo;
import org.dromara.music.domain.vo.OpenSearchSuggestVo;
import org.dromara.music.domain.vo.OpenUserProfileVo;
import org.dromara.music.domain.vo.TagVo;
import org.dromara.music.service.IDeveloperWebhookService;
import org.dromara.music.service.IOpenMusicService;
import org.dromara.music.service.IPortalTagService;
import org.dromara.music.service.MusicCommentFacadeService;
import org.dromara.system.api.RemoteConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Tag(name = "哈基哈米开放 API v1")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/api/v1")
public class DeveloperApiV1Controller {

    private static final String BASE_PATH = "/music/open/api/v1";
    private static final int RATE_LIMIT_COUNT = 60;
    private static final int RATE_LIMIT_SECONDS = 60;

    private final IOpenMusicService openMusicService;
    private final IPortalTagService portalTagService;
    private final MusicCommentFacadeService musicCommentFacadeService;
    private final IDeveloperWebhookService developerWebhookService;

    @DubboReference
    private RemoteConfigService remoteConfigService;

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "开放 API 元信息", tags = {"1. 元信息与统计"})
    @GetMapping("/meta")
    public R<DeveloperApiMetaVo> meta(HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        DeveloperApiMetaVo vo = new DeveloperApiMetaVo();
        vo.setApiVersion("v1");
        vo.setBasePath(BASE_PATH);
        vo.setAuthMode("public-readonly + optional-api-key");
        vo.setRateLimit(RATE_LIMIT_COUNT + "/min/ip");
        vo.setApiKeyHeader(DeveloperApiConstants.API_KEY_HEADER);
        vo.setApiKeyConfigKey(DeveloperApiConstants.API_KEYS_CONFIG_KEY);
        vo.setWebhookConfigKey(DeveloperApiConstants.WEBHOOKS_CONFIG_KEY);
        vo.setDocsPath("/doc/30-open-api/hajihami-open-api-v1.md");
        vo.setEndpoints(List.of(
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/songs", "公开歌曲搜索和分页列表", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/songs/{id}", "公开歌曲详情", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/songs/random", "随机推荐歌曲", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/songs/{id}/comments", "公开评论树", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/search", "公开搜索别名，参数同 /songs", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/search/panel", "搜索筛选面板", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/search/suggest", "搜索建议词", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/search/hot-keywords", "热搜词", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/charts/{type}", "周榜/月榜详情", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/charts/{type}/archives", "榜单历史归档", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/users/{uid}", "公开用户主页", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/tags", "公开标签列表", false),
            DeveloperApiMetaVo.Endpoint.of("GET", BASE_PATH + "/stats/overview", "开放数据概览", false),
            DeveloperApiMetaVo.Endpoint.of("POST", BASE_PATH + "/webhooks/dispatch", "开发者 Webhook 推送测试/分发", true)
        ));
        return R.ok(vo);
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "公开歌曲列表", tags = {"2. 歌曲检索"})
    @GetMapping({"/songs", "/search"})
    public TableDataInfo<MusicVo> songs(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "tag", required = false) String tag,
                                        @RequestParam(value = "tags", required = false) String tags,
                                        @RequestParam(value = "style", required = false) String style,
                                        @RequestParam(value = "sort", required = false) String sort,
                                        @RequestParam(value = "isOriginal", required = false) String isOriginal,
                                        @RequestParam(value = "isAi", required = false) String isAi,
                                        @RequestParam(value = "resourceStatus", required = false) String resourceStatus,
                                        @RequestParam(value = "startDate", required = false) String startDate,
                                        @RequestParam(value = "endDate", required = false) String endDate,
                                        @RequestParam(value = "playCountMin", required = false) Long playCountMin,
                                        @RequestParam(value = "playCountMax", required = false) Long playCountMax,
                                        PageQuery pageQuery,
                                        HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return openMusicService.searchPublic(keyword, tag, tags, style, sort, isOriginal, isAi, resourceStatus,
            startDate, endDate, playCountMin, playCountMax, pageQuery);
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "公开歌曲详情", tags = {"2. 歌曲检索"})
    @GetMapping("/songs/{id}")
    public R<MusicDetailVo> songDetail(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                                       HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(openMusicService.queryPublicDetail(id));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "随机推荐歌曲", tags = {"2. 歌曲检索"})
    @GetMapping("/songs/random")
    public R<List<MusicVo>> randomSongs(@RequestParam(value = "limit", required = false) Integer limit,
                                        HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(openMusicService.queryRandomPublic(limit));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "公开评论树", tags = {"5. 用户与互动"})
    @GetMapping("/songs/{id}/comments")
    public R<List<OpenCommentVo>> comments(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                                           HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(musicCommentFacadeService.queryPublicComments(id));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "搜索筛选面板", tags = {"3. 搜索辅助"})
    @GetMapping("/search/panel")
    public R<OpenSearchPanelVo> searchPanel(@RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "tag", required = false) String tag,
                                            @RequestParam(value = "tags", required = false) String tags,
                                            @RequestParam(value = "style", required = false) String style,
                                            @RequestParam(value = "sort", required = false) String sort,
                                            @RequestParam(value = "isOriginal", required = false) String isOriginal,
                                            @RequestParam(value = "isAi", required = false) String isAi,
                                            @RequestParam(value = "resourceStatus", required = false) String resourceStatus,
                                            @RequestParam(value = "startDate", required = false) String startDate,
                                            @RequestParam(value = "endDate", required = false) String endDate,
                                            @RequestParam(value = "playCountMin", required = false) Long playCountMin,
                                            @RequestParam(value = "playCountMax", required = false) Long playCountMax,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(openMusicService.querySearchPanel(keyword, tag, tags, style, sort, isOriginal, isAi, resourceStatus,
            startDate, endDate, playCountMin, playCountMax, limit));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "搜索建议词", tags = {"3. 搜索辅助"})
    @GetMapping("/search/suggest")
    public R<List<OpenSearchSuggestVo>> suggest(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "limit", required = false) Integer limit,
                                                HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(openMusicService.querySearchSuggestions(keyword, limit));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "热搜词", tags = {"3. 搜索辅助"})
    @GetMapping("/search/hot-keywords")
    public R<List<String>> hotKeywords(@RequestParam(value = "limit", required = false) Integer limit,
                                       @RequestParam(value = "days", required = false) Integer days,
                                       HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(openMusicService.queryHotKeywords(limit, days));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "榜单详情", tags = {"4. 榜单与标签"})
    @GetMapping("/charts/{type}")
    public R<OpenChartVo> chart(@PathVariable("type") String type,
                                @RequestParam(value = "period", required = false) String period,
                                @RequestParam(value = "limit", required = false) Integer limit,
                                HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(openMusicService.queryPublicChart(type, period, limit));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "榜单历史归档", tags = {"4. 榜单与标签"})
    @GetMapping("/charts/{type}/archives")
    public R<List<OpenChartArchiveVo>> chartArchives(@PathVariable("type") String type,
                                                     @RequestParam(value = "limit", required = false) Integer limit,
                                                     HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(openMusicService.queryChartArchives(type, limit));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "公开用户主页", tags = {"5. 用户与互动"})
    @GetMapping("/users/{uid}")
    public R<OpenUserProfileVo> user(@PathVariable("uid") String uid,
                                     HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(openMusicService.queryPublicUserProfile(uid));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "公开标签列表", tags = {"4. 榜单与标签"})
    @GetMapping("/tags")
    public R<List<TagVo>> tags(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "type", required = false) String type,
                               HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        return R.ok(portalTagService.listTags(keyword, type));
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "开放数据概览", tags = {"1. 元信息与统计"})
    @GetMapping("/stats/overview")
    public R<DeveloperStatsOverviewVo> statsOverview(HttpServletRequest request) {
        authorizeDeveloperClient(request, false);
        PageQuery firstPage = new PageQuery(1, 1);
        TableDataInfo<MusicVo> songs = openMusicService.searchPublic(null, null, null, null, null, null, null,
            null, null, null, null, null, firstPage);
        OpenChartVo weekChart = openMusicService.queryPublicChart("week", null, 20);
        OpenChartVo monthChart = openMusicService.queryPublicChart("month", null, 20);

        DeveloperStatsOverviewVo vo = new DeveloperStatsOverviewVo();
        vo.setPublicSongCount(songs.getTotal());
        vo.setWeekChartItemCount(weekChart == null || weekChart.getItems() == null ? 0 : weekChart.getItems().size());
        vo.setMonthChartItemCount(monthChart == null || monthChart.getItems() == null ? 0 : monthChart.getItems().size());
        vo.setTagCount(portalTagService.listTags(null, null).size());
        vo.setHotKeywords(openMusicService.queryHotKeywords(10, 7));
        vo.setGeneratedAt(OffsetDateTime.now().toString());
        return R.ok(vo);
    }

    @SaIgnore
    @RateLimiter(time = RATE_LIMIT_SECONDS, count = RATE_LIMIT_COUNT, limitType = LimitType.IP)
    @Operation(summary = "开发者 Webhook 推送测试/分发", tags = {"6. Webhook 推送"})
    @PostMapping("/webhooks/dispatch")
    public R<DeveloperWebhookDispatchVo> dispatchWebhook(@Valid @RequestBody DeveloperWebhookDispatchBo bo,
                                                         HttpServletRequest request) {
        DeveloperClient client = authorizeDeveloperClient(request, true);
        developerWebhookService.pushAsync(client.appId(), bo);

        DeveloperWebhookDispatchVo vo = new DeveloperWebhookDispatchVo();
        vo.setEventType(bo.getEventType());
        vo.setTriggeredBy(client.name());
        vo.setDispatchedAt(OffsetDateTime.now().toString());
        vo.setMessage("Webhook 推送任务已异步提交，请稍后在调用日志中查看结果。");
        return R.ok(vo);
    }

    private DeveloperClient authorizeDeveloperClient(HttpServletRequest request, boolean required) {
        String apiKey = resolveApiKey(request);
        if (StringUtils.isBlank(apiKey)) {
            if (required) {
                throw new ServiceException("需要提供开发者 token", HttpStatus.UNAUTHORIZED);
            }
            return DeveloperClient.anonymous();
        }
        for (Dict item : safeArrayConfig(DeveloperApiConstants.API_KEYS_CONFIG_KEY)) {
            String token = stringValue(item.get("token"));
            if (StringUtils.equals(apiKey, token) && isDeveloperKeyEnabled(item)) {
                String appId = stringValue(item.get("appId"));
                String name = StringUtils.defaultIfBlank(stringValue(item.get("name")), appId);
                return new DeveloperClient(StringUtils.defaultIfBlank(appId, "configured-client"),
                    StringUtils.defaultIfBlank(name, "Configured Client"));
            }
        }
        throw new ServiceException("开发者 token 无效或已停用", HttpStatus.UNAUTHORIZED);
    }

    private String resolveApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader(DeveloperApiConstants.API_KEY_HEADER);
        if (StringUtils.isNotBlank(apiKey)) {
            return apiKey.trim();
        }
        String authorization = request.getHeader("Authorization");
        if (StringUtils.startsWithIgnoreCase(authorization, "Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        return null;
    }

    private List<Dict> safeArrayConfig(String configKey) {
        try {
            List<Dict> list = remoteConfigService.getConfigArrayMap(configKey);
            return list == null ? List.of() : list;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private boolean isEnabled(Object value) {
        if (value == null) {
            return true;
        }
        String text = stringValue(value);
        return StringUtils.isBlank(text)
            || StringUtils.equalsAnyIgnoreCase(text, "enabled", "enable", "true", "1", "yes");
    }

    private boolean isDeveloperKeyEnabled(Dict item) {
        Object status = item.get("status");
        if (status != null) {
            return isEnabled(status);
        }
        return isEnabled(item.get("enabled"));
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value).trim();
    }

    private record DeveloperClient(String appId, String name) {

        static DeveloperClient anonymous() {
            return new DeveloperClient("anonymous", "Anonymous");
        }
    }
}
