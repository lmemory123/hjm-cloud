package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.music.domain.vo.OpenUserProfileVo;
import org.dromara.music.service.IOpenMusicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/open/user")
public class OpenUserController {

    private final IOpenMusicService openMusicService;

    @SaIgnore
    @GetMapping("/{uid}")
    public R<OpenUserProfileVo> detail(@PathVariable("uid") String uid) {
        return R.ok(openMusicService.queryPublicUserProfile(uid));
    }
}
