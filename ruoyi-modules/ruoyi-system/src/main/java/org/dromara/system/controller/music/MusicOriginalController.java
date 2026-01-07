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
import org.dromara.system.domain.vo.MusicOriginalVo;
import org.dromara.system.domain.bo.MusicOriginalBo;
import org.dromara.system.service.music.IMusicOriginalService;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;

/**
 * 音乐原曲关联
 * 前端访问路由地址为:/music/original
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/original")
public class MusicOriginalController extends BaseController {

    private final IMusicOriginalService musicOriginalService;

    /**
     * 查询音乐原曲关联列表
     */
    @SaCheckPermission("music:original:list")
    @GetMapping("/list")
    public TableDataInfo<MusicOriginalVo> list(MusicOriginalBo bo, PageQuery pageQuery) {
        return musicOriginalService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐原曲关联列表
     */
    @SaCheckPermission("music:original:export")
    @Log(title = "音乐原曲关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicOriginalBo bo, HttpServletResponse response) {
        List<MusicOriginalVo> list = musicOriginalService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐原曲关联", MusicOriginalVo.class, response);
    }

    /**
     * 获取音乐原曲关联详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:original:query")
    @GetMapping("/{id}")
    public R<MusicOriginalVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicOriginalService.queryById(id));
    }

    /**
     * 新增音乐原曲关联
     */
    @SaCheckPermission("music:original:add")
    @Log(title = "音乐原曲关联", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicOriginalBo bo) {
        return toAjax(musicOriginalService.insertByBo(bo));
    }

    /**
     * 修改音乐原曲关联
     */
    @SaCheckPermission("music:original:edit")
    @Log(title = "音乐原曲关联", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicOriginalBo bo) {
        return toAjax(musicOriginalService.updateByBo(bo));
    }

    /**
     * 删除音乐原曲关联
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:original:remove")
    @Log(title = "音乐原曲关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicOriginalService.deleteWithValidByIds(List.of(ids), true));
    }
}
