package org.dromara.music.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.service.IPortalNotifyService;
import org.dromara.music.domain.vo.MusicNotifyLogVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/portal/notify")
public class PortalNotifyController {

    private final IPortalNotifyService portalNotifyService;

    @GetMapping
    public TableDataInfo<MusicNotifyLogVo> list(PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        return portalNotifyService.queryMyNotifies(pageQuery, userId);
    }
}
