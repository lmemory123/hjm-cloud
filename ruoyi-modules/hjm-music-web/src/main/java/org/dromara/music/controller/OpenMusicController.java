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
import org.dromara.music.domain.vo.OpenChartVo;
import org.dromara.music.domain.vo.OpenSearchPanelVo;
import org.dromara.music.domain.vo.OpenSearchSuggestVo;
import org.dromara.music.service.IOpenMusicService;
import org.dromara.music.service.MusicCommentFacadeService;
import org.dromara.music.service.MusicInteractionService;
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

    @SaIgnore
    @GetMapping("/list")
    public TableDataInfo<MusicVo> list(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "tag", required = false) String tag,
                                       @RequestParam(value = "sort", required = false) String sort,
                                       PageQuery pageQuery) {
        return openMusicService.searchPublic(keyword, tag, sort, pageQuery);
    }

    @SaIgnore
    @GetMapping("/{id}")
    public R<MusicDetailVo> detail(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(openMusicService.queryPublicDetail(id));
    }

    @SaIgnore
    @GetMapping("/random")
    public R<List<MusicVo>> random(@RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.queryRandomPublic(limit));
    }

    @SaIgnore
    @GetMapping("/suggest")
    public R<List<OpenSearchSuggestVo>> suggest(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.querySearchSuggestions(keyword, limit));
    }

    @SaIgnore
    @GetMapping("/hot-keywords")
    public R<List<String>> hotKeywords(@RequestParam(value = "limit", required = false) Integer limit,
                                       @RequestParam(value = "days", required = false) Integer days) {
        return R.ok(openMusicService.queryHotKeywords(limit, days));
    }

    @SaIgnore
    @GetMapping("/panel")
    public R<OpenSearchPanelVo> panel(@RequestParam(value = "keyword", required = false) String keyword,
                                      @RequestParam(value = "tag", required = false) String tag,
                                      @RequestParam(value = "sort", required = false) String sort,
                                      @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.querySearchPanel(keyword, tag, sort, limit));
    }

    @SaIgnore
    @GetMapping("/chart/{type}")
    public R<OpenChartVo> chart(@PathVariable("type") String type,
                                @RequestParam(value = "period", required = false) String period,
                                @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(openMusicService.queryPublicChart(type, period, limit));
    }

    @SaIgnore
    @GetMapping("/{id}/comments")
    public R<List<OpenCommentVo>> comments(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(musicCommentFacadeService.queryPublicComments(id));
    }

    @SaIgnore
    @PostMapping("/{id}/play")
    public R<Void> play(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        musicInteractionService.recordPlay(id);
        return R.ok();
    }

    @SaIgnore
    @PostMapping("/{id}/share")
    public R<Void> share(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        musicInteractionService.recordShare(id);
        return R.ok();
    }

    @SaIgnore
    @PostMapping("/{id}/download")
    public R<Void> download(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        musicInteractionService.recordDownload(id);
        return R.ok();
    }
}
