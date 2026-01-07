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
import org.dromara.system.domain.vo.MusicTagRelVo;
import org.dromara.system.domain.bo.MusicTagRelBo;
import org.dromara.system.service.music.IMusicTagRelService;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;

/**
 * 音乐标签关联
 * 前端访问路由地址为:/music/tagRel
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/tagRel")
public class MusicTagRelController extends BaseController {

    private final IMusicTagRelService musicTagRelService;

    /**
     * 查询音乐标签关联列表
     */
    @SaCheckPermission("music:tagRel:list")
    @GetMapping("/list")
    public TableDataInfo<MusicTagRelVo> list(MusicTagRelBo bo, PageQuery pageQuery) {
        return musicTagRelService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐标签关联列表
     */
    @SaCheckPermission("music:tagRel:export")
    @Log(title = "音乐标签关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicTagRelBo bo, HttpServletResponse response) {
        List<MusicTagRelVo> list = musicTagRelService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐标签关联", MusicTagRelVo.class, response);
    }

    /**
     * 获取音乐标签关联详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:tagRel:query")
    @GetMapping("/{id}")
    public R<MusicTagRelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicTagRelService.queryById(id));
    }

    /**
     * 新增音乐标签关联
     */
    @SaCheckPermission("music:tagRel:add")
    @Log(title = "音乐标签关联", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicTagRelBo bo) {
        return toAjax(musicTagRelService.insertByBo(bo));
    }

    /**
     * 修改音乐标签关联
     */
    @SaCheckPermission("music:tagRel:edit")
    @Log(title = "音乐标签关联", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicTagRelBo bo) {
        return toAjax(musicTagRelService.updateByBo(bo));
    }

    /**
     * 删除音乐标签关联
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:tagRel:remove")
    @Log(title = "音乐标签关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicTagRelService.deleteWithValidByIds(List.of(ids), true));
    }
}
