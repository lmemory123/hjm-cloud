package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.domain.vo.OpenCommentVo;
import org.dromara.music.domain.vo.OpenChartArchiveVo;
import org.dromara.music.domain.vo.OpenChartVo;
import org.dromara.music.domain.vo.OpenSearchPanelVo;
import org.dromara.music.domain.vo.OpenSearchSuggestVo;
import org.dromara.music.service.IOpenMusicService;
import org.dromara.music.service.MusicCommentFacadeService;
import org.dromara.music.service.MusicInteractionService;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/song")
public class OpenMusicController {

    private final IOpenMusicService openMusicService;
    private final MusicInteractionService musicInteractionService;
    private final MusicCommentFacadeService musicCommentFacadeService;

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/list")
    public TableDataInfo<MusicVo> list(@RequestParam(value = "keyword", required = false) String keyword,
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
                                       PageQuery pageQuery) {
        return openMusicService.searchPublic(keyword, tag, tags, style, sort, isOriginal, isAi, resourceStatus,
            startDate, endDate, playCountMin, playCountMax, pageQuery);
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/{id}")
    public R<MusicDetailVo> detail(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(openMusicService.queryPublicDetail(id));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/random")
    public R<List<MusicVo>> random(@RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.queryRandomPublic(limit));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/suggest")
    public R<List<OpenSearchSuggestVo>> suggest(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.querySearchSuggestions(keyword, limit));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/hot-keywords")
    public R<List<String>> hotKeywords(@RequestParam(value = "limit", required = false) Integer limit,
                                       @RequestParam(value = "days", required = false) Integer days) {
        return R.ok(openMusicService.queryHotKeywords(limit, days));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/panel")
    public R<OpenSearchPanelVo> panel(@RequestParam(value = "keyword", required = false) String keyword,
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
                                      @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.querySearchPanel(keyword, tag, tags, style, sort, isOriginal, isAi, resourceStatus,
            startDate, endDate, playCountMin, playCountMax, limit));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/chart/{type}")
    public R<OpenChartVo> chart(@PathVariable("type") String type,
                                @RequestParam(value = "period", required = false) String period,
                                @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.queryPublicChart(type, period, limit));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/chart/{type}/archives")
    public R<List<OpenChartArchiveVo>> chartArchives(@PathVariable("type") String type,
                                                     @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.queryChartArchives(type, limit));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/{id}/comments")
    public R<List<OpenCommentVo>> comments(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(musicCommentFacadeService.queryPublicComments(id));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @PostMapping("/{id}/play")
    public R<Void> play(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        musicInteractionService.recordPlay(id);
        return R.ok();
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @PostMapping("/{id}/share")
    public R<Void> share(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        musicInteractionService.recordShare(id);
        return R.ok();
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @PostMapping("/{id}/download")
    public R<Void> download(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        musicInteractionService.recordDownload(id);
        return R.ok();
    }
}
