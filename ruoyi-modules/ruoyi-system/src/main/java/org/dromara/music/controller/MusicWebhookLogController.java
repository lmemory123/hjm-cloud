package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.music.domain.MusicWebhookLog;
import org.dromara.music.domain.vo.MusicWebhookLogVo;
import org.dromara.music.service.IMusicWebhookLogService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 开发者 Webhook 调用日志控制器
 *
 * @author momao
 * @date 2026-05-27
 */
@Tag(name = "开发者 Webhook 调用日志管理")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/webhookLog")
public class MusicWebhookLogController extends BaseController {

    private final IMusicWebhookLogService musicWebhookLogService;

    /**
     * 查询开发者 Webhook 调用日志列表
     */
    @Operation(summary = "查询开发者 Webhook 调用日志列表")
    @SaCheckPermission("music:webhookLog:list")
    @GetMapping("/list")
    public TableDataInfo<MusicWebhookLogVo> list(MusicWebhookLog webhookLog, PageQuery pageQuery) {
        return musicWebhookLogService.queryPageList(webhookLog, pageQuery);
    }

    /**
     * 获取开发者 Webhook 调用日志详细信息
     *
     * @param id 主键
     */
    @Operation(summary = "获取开发者 Webhook 调用日志详细信息")
    @SaCheckPermission("music:webhookLog:query")
    @GetMapping("/{id}")
    public R<MusicWebhookLogVo> getInfo(@PathVariable Long id) {
        return R.ok(musicWebhookLogService.queryById(id));
    }

    /**
     * 删除开发者 Webhook 调用日志
     *
     * @param ids 主键串
     */
    @Operation(summary = "删除开发者 Webhook 调用日志")
    @SaCheckPermission("music:webhookLog:remove")
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        return toAjax(musicWebhookLogService.deleteByIds(List.of(ids)));
    }
}
