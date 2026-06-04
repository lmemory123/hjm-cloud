package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.dromara.music.domain.bo.EmojiBo;
import org.dromara.music.domain.vo.EmojiVo;
import org.dromara.music.service.IOpenEmojiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 开放表情包控制器
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/emoji")
public class OpenEmojiController {

    private final IOpenEmojiService emojiService;

    /**
     * 查询已通过的表情包列表
     */
    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/list")
    public TableDataInfo<EmojiVo> list(EmojiBo bo, PageQuery pageQuery) {
        bo.setStatus(1); // 仅查询已通过审核的
        return emojiService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取表情包分类列表
     */
    @RateLimiter(count = 60, limitType = LimitType.IP)
    @SaIgnore
    @GetMapping("/category")
    public R<List<String>> category() {
        return R.ok(emojiService.queryCategoryList());
    }
}
