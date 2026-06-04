package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.music.domain.bo.EmojiBo;
import org.dromara.music.domain.vo.EmojiVo;
import org.dromara.music.service.IEmojiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 表情包控制器
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/music/emoji")
public class EmojiController extends BaseController {

    private final IEmojiService emojiService;

    /**
     * 查询表情包列表
     */
    @SaCheckPermission("music:emoji:list")
    @GetMapping("/list")
    public TableDataInfo<EmojiVo> list(EmojiBo bo, PageQuery pageQuery) {
        return emojiService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取表情包详细信息
     */
    @SaCheckPermission("music:emoji:query")
    @GetMapping("/{id}")
    public R<EmojiVo> getInfo(@PathVariable("id") Long id) {
        return R.ok(emojiService.queryById(id));
    }

    /**
     * 新增表情包
     */
    @SaCheckPermission("music:emoji:add")
    @Log(title = "表情包", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody EmojiBo bo) {
        return toAjax(emojiService.insertByBo(bo));
    }

    /**
     * 修改表情包
     */
    @SaCheckPermission("music:emoji:edit")
    @Log(title = "表情包", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody EmojiBo bo) {
        return toAjax(emojiService.updateByBo(bo));
    }

    /**
     * 删除表情包
     */
    @SaCheckPermission("music:emoji:remove")
    @Log(title = "表情包", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        return toAjax(emojiService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 审核通过
     */
    @SaCheckPermission("music:emoji:audit")
    @Log(title = "表情包", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{id}")
    public R<Void> approve(@PathVariable Long id) {
        return toAjax(emojiService.approve(id));
    }

    /**
     * 审核拒绝
     */
    @SaCheckPermission("music:emoji:audit")
    @Log(title = "表情包", businessType = BusinessType.UPDATE)
    @PutMapping("/reject/{id}")
    public R<Void> reject(@PathVariable Long id) {
        return toAjax(emojiService.reject(id));
    }
}
