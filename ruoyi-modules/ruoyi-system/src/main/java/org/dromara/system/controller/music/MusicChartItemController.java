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
import org.dromara.system.domain.vo.MusicChartItemVo;
import org.dromara.system.domain.bo.MusicChartItemBo;
import org.dromara.system.service.music.IMusicChartItemService;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;

/**
 * 榜单明细
 * 前端访问路由地址为:/music/chartItem
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/chartItem")
public class MusicChartItemController extends BaseController {

    private final IMusicChartItemService musicChartItemService;

    /**
     * 查询榜单明细列表
     */
    @SaCheckPermission("music:chartItem:list")
    @GetMapping("/list")
    public TableDataInfo<MusicChartItemVo> list(MusicChartItemBo bo, PageQuery pageQuery) {
        return musicChartItemService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出榜单明细列表
     */
    @SaCheckPermission("music:chartItem:export")
    @Log(title = "榜单明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicChartItemBo bo, HttpServletResponse response) {
        List<MusicChartItemVo> list = musicChartItemService.queryList(bo);
        ExcelUtil.exportExcel(list, "榜单明细", MusicChartItemVo.class, response);
    }

    /**
     * 获取榜单明细详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:chartItem:query")
    @GetMapping("/{id}")
    public R<MusicChartItemVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicChartItemService.queryById(id));
    }

    /**
     * 新增榜单明细
     */
    @SaCheckPermission("music:chartItem:add")
    @Log(title = "榜单明细", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicChartItemBo bo) {
        return toAjax(musicChartItemService.insertByBo(bo));
    }

    /**
     * 修改榜单明细
     */
    @SaCheckPermission("music:chartItem:edit")
    @Log(title = "榜单明细", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicChartItemBo bo) {
        return toAjax(musicChartItemService.updateByBo(bo));
    }

    /**
     * 删除榜单明细
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:chartItem:remove")
    @Log(title = "榜单明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicChartItemService.deleteWithValidByIds(List.of(ids), true));
    }
}
