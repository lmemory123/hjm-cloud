package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 */
@RestController
public class HealthController {

    @SaIgnore
    @GetMapping("/checkAliveServer")
    public String checkAliveServer() {
        return "OK";
    }
}
