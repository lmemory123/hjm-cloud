package org.dromara.music.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.domain.bo.PortalReportBo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.service.MusicInteractionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/portal/song")
public class PortalMusicInteractionController {

    private final MusicInteractionService musicInteractionService;

    @PostMapping("/{id}/like")
    public R<Boolean> like(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(musicInteractionService.likeMusic(id, LoginHelper.getUserId()));
    }

    @DeleteMapping("/{id}/like")
    public R<Boolean> unlike(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(musicInteractionService.unlikeMusic(id, LoginHelper.getUserId()));
    }

    @GetMapping("/{id}/liked")
    public R<Boolean> liked(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(musicInteractionService.hasLiked(id, LoginHelper.getUserId()));
    }

    @PostMapping("/{id}/collect")
    public R<Boolean> collect(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(musicInteractionService.collectMusic(id, LoginHelper.getUserId()));
    }

    @DeleteMapping("/{id}/collect")
    public R<Boolean> uncollect(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(musicInteractionService.uncollectMusic(id, LoginHelper.getUserId()));
    }

    @GetMapping("/{id}/collected")
    public R<Boolean> collected(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(musicInteractionService.hasCollected(id, LoginHelper.getUserId()));
    }

    @GetMapping("/collections")
    public R<List<MusicVo>> collections(@RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(musicInteractionService.queryCollectedSongs(LoginHelper.getUserId(), limit));
    }

    @PostMapping("/{id}/history")
    public R<Void> history(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        musicInteractionService.recordHistory(id, LoginHelper.getUserId());
        return R.ok();
    }

    @GetMapping("/history")
    public R<List<MusicVo>> historyList(@RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(musicInteractionService.queryHistorySongs(LoginHelper.getUserId(), limit));
    }

    @DeleteMapping("/history")
    public R<Boolean> clearHistory() {
        return R.ok(musicInteractionService.clearHistory(LoginHelper.getUserId()));
    }

    @PostMapping("/{id}/report")
    public R<Boolean> report(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                             @Valid @RequestBody PortalReportBo bo) {
        return R.ok(musicInteractionService.reportSong(id, LoginHelper.getUserId(), bo.getReason(), bo.getDescription()));
    }
}
