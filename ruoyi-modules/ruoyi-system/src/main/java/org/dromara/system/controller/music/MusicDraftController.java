package org.dromara.system.controller.music;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.system.domain.vo.MusicDraftVo;
import org.dromara.system.domain.bo.MusicDraftBo;
import org.dromara.system.service.music.IMusicDraftService;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;

/**
 * 投稿草稿
 * 前端访问路由地址为:/music/draft
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/draft")
public class MusicDraftController extends BaseController {

    private final IMusicDraftService musicDraftService;

    /**
     * 查询投稿草稿列表
     */
    @SaCheckPermission("music:draft:list")
    @GetMapping("/list")
    public TableDataInfo<MusicDraftVo> list(MusicDraftBo bo, PageQuery pageQuery) {
        return musicDraftService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出投稿草稿列表
     */
    @SaCheckPermission("music:draft:export")
    @Log(title = "投稿草稿", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicDraftBo bo, HttpServletResponse response) {
        List<MusicDraftVo> list = musicDraftService.queryList(bo);
        ExcelUtil.exportExcel(list, "投稿草稿", MusicDraftVo.class, response);
    }

    /**
     * 获取投稿草稿详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:draft:query")
    @GetMapping("/{id}")
    public R<MusicDraftVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicDraftService.queryById(id));
    }

    /**
     * 新增投稿草稿
     */
    @SaCheckPermission("music:draft:add")
    @Log(title = "投稿草稿", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicDraftBo bo) {
        return toAjax(musicDraftService.insertByBo(bo));
    }

    /**
     * 修改投稿草稿
     */
    @SaCheckPermission("music:draft:edit")
    @Log(title = "投稿草稿", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicDraftBo bo) {
        return toAjax(musicDraftService.updateByBo(bo));
    }

    /**
     * 删除投稿草稿
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:draft:remove")
    @Log(title = "投稿草稿", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicDraftService.deleteWithValidByIds(List.of(ids), true));
    }
}
