package org.dromara.music.controller;

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
import org.dromara.music.domain.bo.MusicNotifyLogBo;
import org.dromara.music.domain.vo.MusicNotifyLogVo;
import org.dromara.music.service.IMusicNotifyLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音乐通知日志
 * 前端访问路由地址为:/music/notifyLog
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifyLog")
public class MusicNotifyLogController extends BaseController {

    private final IMusicNotifyLogService musicNotifyLogService;

    /**
     * 查询音乐通知日志列表
     */
    @SaCheckPermission("music:notifyLog:list")
    @GetMapping("/list")
    public TableDataInfo<MusicNotifyLogVo> list(MusicNotifyLogBo bo, PageQuery pageQuery) {
        return musicNotifyLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐通知日志列表
     */
    @SaCheckPermission("music:notifyLog:export")
    @Log(title = "音乐通知日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicNotifyLogBo bo, HttpServletResponse response) {
        List<MusicNotifyLogVo> list = musicNotifyLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐通知日志", MusicNotifyLogVo.class, response);
    }

    /**
     * 获取音乐通知日志详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:notifyLog:query")
    @GetMapping("/{id}")
    public R<MusicNotifyLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicNotifyLogService.queryById(id));
    }

    /**
     * 新增音乐通知日志
     */
    @SaCheckPermission("music:notifyLog:add")
    @Log(title = "音乐通知日志", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicNotifyLogBo bo) {
        return toAjax(musicNotifyLogService.insertByBo(bo));
    }

    /**
     * 修改音乐通知日志
     */
    @SaCheckPermission("music:notifyLog:edit")
    @Log(title = "音乐通知日志", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicNotifyLogBo bo) {
        return toAjax(musicNotifyLogService.updateByBo(bo));
    }

    /**
     * 删除音乐通知日志
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:notifyLog:remove")
    @Log(title = "音乐通知日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicNotifyLogService.deleteWithValidByIds(List.of(ids), true));
    }
}
