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
import org.dromara.system.domain.vo.MusicActionVo;
import org.dromara.system.domain.bo.MusicActionBo;
import org.dromara.system.service.music.IMusicActionService;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;

/**
 * 音乐互动动作
 * 前端访问路由地址为:/music/action
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/action")
public class MusicActionController extends BaseController {

    private final IMusicActionService musicActionService;

    /**
     * 查询音乐互动动作列表
     */
    @SaCheckPermission("music:action:list")
    @GetMapping("/list")
    public TableDataInfo<MusicActionVo> list(MusicActionBo bo, PageQuery pageQuery) {
        return musicActionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐互动动作列表
     */
    @SaCheckPermission("music:action:export")
    @Log(title = "音乐互动动作", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicActionBo bo, HttpServletResponse response) {
        List<MusicActionVo> list = musicActionService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐互动动作", MusicActionVo.class, response);
    }

    /**
     * 获取音乐互动动作详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:action:query")
    @GetMapping("/{id}")
    public R<MusicActionVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicActionService.queryById(id));
    }

    /**
     * 新增音乐互动动作
     */
    @SaCheckPermission("music:action:add")
    @Log(title = "音乐互动动作", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicActionBo bo) {
        return toAjax(musicActionService.insertByBo(bo));
    }

    /**
     * 修改音乐互动动作
     */
    @SaCheckPermission("music:action:edit")
    @Log(title = "音乐互动动作", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicActionBo bo) {
        return toAjax(musicActionService.updateByBo(bo));
    }

    /**
     * 删除音乐互动动作
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:action:remove")
    @Log(title = "音乐互动动作", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicActionService.deleteWithValidByIds(List.of(ids), true));
    }
}
