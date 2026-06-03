package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.dromara.music.domain.vo.OpenUserProfileVo;
import org.dromara.music.service.IOpenMusicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/open/user")
public class OpenUserController {

    private final IOpenMusicService openMusicService;

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/{uid}")
    public R<OpenUserProfileVo> detail(@PathVariable("uid") String uid) {
        return R.ok(openMusicService.queryPublicUserProfile(uid));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/{uid}/follows")
    public R<List<OpenUserProfileVo>> follows(@PathVariable("uid") String uid, @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        return R.ok(openMusicService.queryUserFollows(uid, limit));
    }

    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/{uid}/fans")
    public R<List<OpenUserProfileVo>> fans(@PathVariable("uid") String uid, @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        return R.ok(openMusicService.queryUserFans(uid, limit));
    }
}
