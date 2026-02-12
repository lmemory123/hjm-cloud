package org.dromara.music.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.domain.bo.PortalTagProposalBo;
import org.dromara.music.service.IPortalTagService;
import org.dromara.music.domain.vo.TagProposalVo;
import org.dromara.music.domain.vo.TagVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/portal/tag")
public class PortalTagController {

    private final IPortalTagService portalTagService;

    @GetMapping("/list")
    public R<List<TagVo>> list(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "type", required = false) String type) {
        return R.ok(portalTagService.listTags(keyword, type));
    }

    @PostMapping("/proposal")
    public R<Long> proposal(@RequestBody PortalTagProposalBo bo) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalTagService.submitProposal(bo, userId));
    }

    @GetMapping("/proposal/my")
    public R<List<TagProposalVo>> myProposals() {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalTagService.listMyProposals(userId));
    }
}
