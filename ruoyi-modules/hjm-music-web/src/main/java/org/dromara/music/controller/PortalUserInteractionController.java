package org.dromara.music.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.service.MusicInteractionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/portal/user")
public class PortalUserInteractionController {

    private final MusicInteractionService musicInteractionService;

    @SaCheckLogin
    @PostMapping("/{uid}/follow")
    public R<Boolean> follow(@PathVariable("uid") String uid) {
        return R.ok(musicInteractionService.followUser(uid, LoginHelper.getUserId()));
    }

    @SaCheckLogin
    @DeleteMapping("/{uid}/follow")
    public R<Boolean> unfollow(@PathVariable("uid") String uid) {
        return R.ok(musicInteractionService.unfollowUser(uid, LoginHelper.getUserId()));
    }

    @SaCheckLogin
    @GetMapping("/{uid}/followed")
    public R<Boolean> followed(@PathVariable("uid") String uid) {
        return R.ok(musicInteractionService.hasFollowed(uid, LoginHelper.getUserId()));
    }

    @GetMapping("/{uid}/fans")
    public R<List<String>> fans(@PathVariable("uid") String uid, @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(musicInteractionService.queryFanIds(uid, limit));
    }

    @GetMapping("/{uid}/follows")
    public R<List<String>> follows(@PathVariable("uid") String uid, @RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(musicInteractionService.queryFollowIds(uid, limit));
    }
}
