package org.dromara.auth.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.auth.form.FrontPasswordLoginBody;
import org.dromara.auth.form.FrontRegisterBody;
import org.dromara.auth.service.FrontAuthService;
import org.dromara.common.core.domain.R;
import org.dromara.common.encrypt.annotation.ApiEncrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台认证入口
 */
@RestController
@RequiredArgsConstructor
public class FrontTokenController {

    private final FrontAuthService frontAuthService;

    /**
     * 前台密码登录
     */
    @ApiEncrypt
    @PostMapping("/front/login")
    public R<LoginVo> login(@RequestBody FrontPasswordLoginBody body) {
        return R.ok(frontAuthService.login(body));
    }

    /**
     * 前台注册
     */
    @ApiEncrypt
    @PostMapping("/front/register")
    public R<Void> register(@RequestBody FrontRegisterBody body) {
        frontAuthService.register(body);
        return R.ok();
    }

}
