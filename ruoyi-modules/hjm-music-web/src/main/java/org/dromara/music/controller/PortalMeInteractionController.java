package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.service.MusicInteractionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@SaCheckLogin
@RequiredArgsConstructor
@RestController
@RequestMapping("/portal/me")
public class PortalMeInteractionController {

    private final MusicInteractionService musicInteractionService;

    @GetMapping("/likes")
    public R<List<MusicVo>> likes(@RequestParam(value = "limit", required = false) Integer limit) {
        return R.ok(musicInteractionService.queryLikedSongs(LoginHelper.getUserId(), limit));
    }
}
