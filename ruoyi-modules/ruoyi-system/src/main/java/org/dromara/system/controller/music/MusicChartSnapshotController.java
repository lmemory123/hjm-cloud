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
import org.dromara.system.domain.bo.MusicChartSnapshotBo;
import org.dromara.system.domain.vo.MusicChartSnapshotVo;
import org.dromara.system.service.music.IMusicChartSnapshotService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 榜单快照
 * 前端访问路由地址为:/music/chartSnapshot
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/chartSnapshot")
public class MusicChartSnapshotController extends BaseController {

    private final IMusicChartSnapshotService musicChartSnapshotService;

    /**
     * 查询榜单快照列表
     */
    @SaCheckPermission("music:chartSnapshot:list")
    @GetMapping("/list")
    public TableDataInfo<MusicChartSnapshotVo> list(MusicChartSnapshotBo bo, PageQuery pageQuery) {
        return musicChartSnapshotService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出榜单快照列表
     */
    @SaCheckPermission("music:chartSnapshot:export")
    @Log(title = "榜单快照", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicChartSnapshotBo bo, HttpServletResponse response) {
        List<MusicChartSnapshotVo> list = musicChartSnapshotService.queryList(bo);
        ExcelUtil.exportExcel(list, "榜单快照", MusicChartSnapshotVo.class, response);
    }

    /**
     * 获取榜单快照详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:chartSnapshot:query")
    @GetMapping("/{id}")
    public R<MusicChartSnapshotVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicChartSnapshotService.queryById(id));
    }

    /**
     * 新增榜单快照
     */
    @SaCheckPermission("music:chartSnapshot:add")
    @Log(title = "榜单快照", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicChartSnapshotBo bo) {
        return toAjax(musicChartSnapshotService.insertByBo(bo));
    }

    /**
     * 修改榜单快照
     */
    @SaCheckPermission("music:chartSnapshot:edit")
    @Log(title = "榜单快照", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicChartSnapshotBo bo) {
        return toAjax(musicChartSnapshotService.updateByBo(bo));
    }

    /**
     * 删除榜单快照
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:chartSnapshot:remove")
    @Log(title = "榜单快照", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicChartSnapshotService.deleteWithValidByIds(List.of(ids), true));
    }
}
