package org.dromara.music.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.domain.bo.PortalCommentSubmitBo;
import org.dromara.music.domain.bo.PortalReportBo;
import org.dromara.music.service.MusicCommentFacadeService;
import org.dromara.music.service.MusicInteractionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/song")
public class PortalMusicCommentController {

    private final MusicCommentFacadeService musicCommentFacadeService;
    private final MusicInteractionService musicInteractionService;

    @PostMapping("/{id}/comments")
    public R<Long> submit(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                          @Valid @RequestBody PortalCommentSubmitBo bo) {
        return R.ok(musicCommentFacadeService.submitComment(id, LoginHelper.getUserId(), bo));
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public R<Boolean> delete(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                             @NotNull(message = "评论ID不能为空") @PathVariable("commentId") Long commentId) {
        return R.ok(musicCommentFacadeService.deleteComment(id, commentId, LoginHelper.getUserId()));
    }

    @PostMapping("/{id}/comments/{commentId}/like")
    public R<Boolean> likeComment(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                                  @NotNull(message = "评论ID不能为空") @PathVariable("commentId") Long commentId) {
        return R.ok(musicInteractionService.likeComment(id, commentId, LoginHelper.getUserId()));
    }

    @DeleteMapping("/{id}/comments/{commentId}/like")
    public R<Boolean> unlikeComment(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                                    @NotNull(message = "评论ID不能为空") @PathVariable("commentId") Long commentId) {
        return R.ok(musicInteractionService.unlikeComment(id, commentId, LoginHelper.getUserId()));
    }

    @GetMapping("/{id}/comments/{commentId}/liked")
    public R<Boolean> likedComment(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                                   @NotNull(message = "评论ID不能为空") @PathVariable("commentId") Long commentId) {
        return R.ok(musicInteractionService.hasCommentLiked(commentId, LoginHelper.getUserId()));
    }

    @PostMapping("/{id}/comments/{commentId}/report")
    public R<Boolean> reportComment(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                                    @NotNull(message = "评论ID不能为空") @PathVariable("commentId") Long commentId,
                                    @Valid @RequestBody PortalReportBo bo) {
        return R.ok(musicInteractionService.reportComment(id, commentId, LoginHelper.getUserId(), bo.getReason(), bo.getDescription()));
    }
}
