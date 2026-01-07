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
import org.dromara.system.domain.vo.MusicAuditLogVo;
import org.dromara.system.domain.bo.MusicAuditLogBo;
import org.dromara.system.service.music.IMusicAuditLogService;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;

/**
 * 音乐审核流水日志
 * 前端访问路由地址为:/music/auditLog
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auditLog")
public class MusicAuditLogController extends BaseController {

    private final IMusicAuditLogService musicAuditLogService;

    /**
     * 查询音乐审核流水日志列表
     */
    @SaCheckPermission("music:auditLog:list")
    @GetMapping("/list")
    public TableDataInfo<MusicAuditLogVo> list(MusicAuditLogBo bo, PageQuery pageQuery) {
        return musicAuditLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐审核流水日志列表
     */
    @SaCheckPermission("music:auditLog:export")
    @Log(title = "音乐审核流水日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicAuditLogBo bo, HttpServletResponse response) {
        List<MusicAuditLogVo> list = musicAuditLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐审核流水日志", MusicAuditLogVo.class, response);
    }

    /**
     * 获取音乐审核流水日志详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:auditLog:query")
    @GetMapping("/{id}")
    public R<MusicAuditLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicAuditLogService.queryById(id));
    }

    /**
     * 新增音乐审核流水日志
     */
    @SaCheckPermission("music:auditLog:add")
    @Log(title = "音乐审核流水日志", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicAuditLogBo bo) {
        return toAjax(musicAuditLogService.insertByBo(bo));
    }

    /**
     * 修改音乐审核流水日志
     */
    @SaCheckPermission("music:auditLog:edit")
    @Log(title = "音乐审核流水日志", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicAuditLogBo bo) {
        return toAjax(musicAuditLogService.updateByBo(bo));
    }

    /**
     * 删除音乐审核流水日志
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:auditLog:remove")
    @Log(title = "音乐审核流水日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicAuditLogService.deleteWithValidByIds(List.of(ids), true));
    }
}
