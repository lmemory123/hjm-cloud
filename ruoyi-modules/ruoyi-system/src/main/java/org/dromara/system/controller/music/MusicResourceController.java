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
import org.dromara.system.domain.vo.MusicResourceVo;
import org.dromara.system.domain.bo.MusicResourceBo;
import org.dromara.system.service.IMusicResourceService;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;

/**
 * 音乐资源文件
 * 前端访问路由地址为:/music/resource
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/resource")
public class MusicResourceController extends BaseController {

    private final IMusicResourceService musicResourceService;

    /**
     * 查询音乐资源文件列表
     */
    @SaCheckPermission("music:resource:list")
    @GetMapping("/list")
    public TableDataInfo<MusicResourceVo> list(MusicResourceBo bo, PageQuery pageQuery) {
        return musicResourceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐资源文件列表
     */
    @SaCheckPermission("music:resource:export")
    @Log(title = "音乐资源文件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicResourceBo bo, HttpServletResponse response) {
        List<MusicResourceVo> list = musicResourceService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐资源文件", MusicResourceVo.class, response);
    }

    /**
     * 获取音乐资源文件详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:resource:query")
    @GetMapping("/{id}")
    public R<MusicResourceVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicResourceService.queryById(id));
    }

    /**
     * 新增音乐资源文件
     */
    @SaCheckPermission("music:resource:add")
    @Log(title = "音乐资源文件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicResourceBo bo) {
        return toAjax(musicResourceService.insertByBo(bo));
    }

    /**
     * 修改音乐资源文件
     */
    @SaCheckPermission("music:resource:edit")
    @Log(title = "音乐资源文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicResourceBo bo) {
        return toAjax(musicResourceService.updateByBo(bo));
    }

    /**
     * 删除音乐资源文件
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:resource:remove")
    @Log(title = "音乐资源文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicResourceService.deleteWithValidByIds(List.of(ids), true));
    }
}
