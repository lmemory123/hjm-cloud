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
import org.dromara.system.domain.bo.MusicLinkCheckLogBo;
import org.dromara.system.domain.vo.MusicLinkCheckLogVo;
import org.dromara.system.service.music.IMusicLinkCheckLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 链接检测日志
 * 前端访问路由地址为:/music/linkCheckLog
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/linkCheckLog")
public class MusicLinkCheckLogController extends BaseController {

    private final IMusicLinkCheckLogService musicLinkCheckLogService;

    /**
     * 查询链接检测日志列表
     */
    @SaCheckPermission("music:linkCheckLog:list")
    @GetMapping("/list")
    public TableDataInfo<MusicLinkCheckLogVo> list(MusicLinkCheckLogBo bo, PageQuery pageQuery) {
        return musicLinkCheckLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出链接检测日志列表
     */
    @SaCheckPermission("music:linkCheckLog:export")
    @Log(title = "链接检测日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicLinkCheckLogBo bo, HttpServletResponse response) {
        List<MusicLinkCheckLogVo> list = musicLinkCheckLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "链接检测日志", MusicLinkCheckLogVo.class, response);
    }

    /**
     * 获取链接检测日志详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:linkCheckLog:query")
    @GetMapping("/{id}")
    public R<MusicLinkCheckLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicLinkCheckLogService.queryById(id));
    }

    /**
     * 新增链接检测日志
     */
    @SaCheckPermission("music:linkCheckLog:add")
    @Log(title = "链接检测日志", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicLinkCheckLogBo bo) {
        return toAjax(musicLinkCheckLogService.insertByBo(bo));
    }

    /**
     * 修改链接检测日志
     */
    @SaCheckPermission("music:linkCheckLog:edit")
    @Log(title = "链接检测日志", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicLinkCheckLogBo bo) {
        return toAjax(musicLinkCheckLogService.updateByBo(bo));
    }

    /**
     * 删除链接检测日志
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:linkCheckLog:remove")
    @Log(title = "链接检测日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicLinkCheckLogService.deleteWithValidByIds(List.of(ids), true));
    }
}
