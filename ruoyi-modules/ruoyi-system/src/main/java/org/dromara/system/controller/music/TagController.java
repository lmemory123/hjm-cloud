package org.dromara.system.controller.music;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.TagBo;
import org.dromara.system.domain.vo.TagVo;
import org.dromara.system.service.music.ITagService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签字典
 * 前端访问路由地址为:/music/tag
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tag")
public class TagController extends BaseController {

    private final ITagService tagService;

    /**
     * 查询标签字典列表
     */
    @SaCheckPermission("music:tag:list")
    @GetMapping("/list")
    public TableDataInfo<TagVo> list(TagBo bo, PageQuery pageQuery) {
        return tagService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出标签字典列表
     */
    @SaCheckPermission("music:tag:export")
    @Log(title = "标签字典", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(TagBo bo, HttpServletResponse response) {
        List<TagVo> list = tagService.queryList(bo);
        ExcelUtil.exportExcel(list, "标签字典", TagVo.class, response);
    }

    /**
     * 获取标签字典详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:tag:query")
    @GetMapping("/{id}")
    public R<TagVo> getInfo(@NotNull(message = "主键不能为空")
                            @PathVariable("id") Long id) {
        return R.ok(tagService.queryById(id));
    }

    /**
     * 新增标签字典
     */
    @SaCheckPermission("music:tag:add")
    @Log(title = "标签字典", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TagBo bo) {
        return toAjax(tagService.insertByBo(bo));
    }

    /**
     * 修改标签字典
     */
    @SaCheckPermission("music:tag:edit")
    @Log(title = "标签字典", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TagBo bo) {
        return toAjax(tagService.updateByBo(bo));
    }

    /**
     * 删除标签字典
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:tag:remove")
    @Log(title = "标签字典", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(tagService.deleteWithValidByIds(List.of(ids), true));
    }
}
