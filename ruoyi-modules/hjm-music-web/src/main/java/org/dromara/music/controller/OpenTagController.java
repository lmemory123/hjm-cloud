package org.dromara.music.controller;

import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaIgnore;
import org.dromara.common.core.domain.R;
import org.dromara.music.service.IPortalTagService;
import org.dromara.music.domain.vo.TagVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/open/tag")
public class OpenTagController {

    private final IPortalTagService portalTagService;

    @SaIgnore
    @GetMapping("/list")
    public R<List<TagVo>> list(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "type", required = false) String type) {
        return R.ok(portalTagService.listTags(keyword, type));
    }
}
