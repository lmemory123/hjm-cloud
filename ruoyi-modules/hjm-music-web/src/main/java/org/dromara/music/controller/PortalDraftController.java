package org.dromara.music.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.domain.bo.MusicSubmitBo;
import org.dromara.music.domain.vo.MusicDraftVo;
import org.dromara.music.service.IPortalDraftService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/draft")
@RequiredArgsConstructor
public class PortalDraftController {

    private final IPortalDraftService portalDraftService;

    @PostMapping("/save")
    public R<Long> saveDraft(@RequestBody String content) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalDraftService.saveDraft(userId, content));
    }

    @GetMapping("/list")
    public R<List<MusicDraftVo>> listDrafts() {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalDraftService.listDrafts(userId));
    }

    @GetMapping("/get")
    public R<MusicDraftVo> getDraft(@RequestParam Long id) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalDraftService.getDraft(id, userId));
    }

    @PostMapping("/update")
    public R<Boolean> updateDraft(@RequestParam Long id, @RequestBody String content) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalDraftService.updateDraft(id, userId, content));
    }

    @PostMapping("/delete")
    public R<Boolean> deleteDraft(@RequestParam Long id) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalDraftService.deleteDraft(id, userId));
    }

    @RepeatSubmit
    @PostMapping("/submit")
    public R<Long> submitForAudit(@Validated @RequestBody MusicSubmitBo bo) {
        bo.setUserId(LoginHelper.getUserId());
        return R.ok(portalDraftService.submitForAudit(bo));
    }
}
