package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.music.domain.bo.TagProposalAuditBo;
import org.dromara.music.domain.bo.TagProposalBo;
import org.dromara.music.domain.vo.TagProposalVo;
import org.dromara.music.service.ITagProposalService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签提报申请
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tagProposal")
public class TagProposalController extends BaseController {

    private final ITagProposalService tagProposalService;

    /**
     * 查询标签提报列表
     */
    @SaCheckPermission("music:tagProposal:list")
    @GetMapping("/list")
    public TableDataInfo<TagProposalVo> list(TagProposalBo bo, PageQuery pageQuery) {
        return tagProposalService.queryPageList(bo, pageQuery);
    }

    /**
     * 审核标签提报
     */
    @SaCheckPermission("music:tagProposal:audit")
    @Log(title = "标签提报", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public R<Void> audit(@Validated @RequestBody TagProposalAuditBo bo) {
        Long auditorId = LoginHelper.getUserId();
        return toAjax(tagProposalService.audit(bo, auditorId));
    }

    /**
     * 获取单条详情
     */
    @SaCheckPermission("music:tagProposal:query")
    @GetMapping("/{id}")
    public R<TagProposalVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        TagProposalBo bo = new TagProposalBo();
        bo.setId(id);
        List<TagProposalVo> list = tagProposalService.queryList(bo);
        return R.ok(list.isEmpty() ? null : list.get(0));
    }
}
