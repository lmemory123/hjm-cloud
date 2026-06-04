package org.dromara.music.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.music.domain.bo.EmojiBo;
import org.dromara.music.service.IPortalEmojiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 门户表情包控制器
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/portal/emoji")
public class PortalEmojiController {

    private final IPortalEmojiService emojiService;

    /**
     * 提交表情包
     */
    @PostMapping("/submit")
    public R<Void> submit(@Validated(AddGroup.class) @RequestBody EmojiBo bo) {
        bo.setStatus(0); // 待审核
        return emojiService.submitEmoji(bo) ? R.ok() : R.fail();
    }
}
