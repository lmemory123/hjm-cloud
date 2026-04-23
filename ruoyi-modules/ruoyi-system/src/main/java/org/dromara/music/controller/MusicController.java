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
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.music.domain.bo.MusicAuditBo;
import org.dromara.music.domain.bo.MusicBo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicFullDetailVo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.service.IMusicService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音乐曲库主
 * 前端访问路由地址为:/music/music
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/music")
public class MusicController extends BaseController {

    private final IMusicService musicService;

    /**
     * 查询音乐曲库主列表
     */
    @SaCheckPermission("music:music:list")
    @GetMapping("/list")
    public TableDataInfo<MusicVo> list(MusicBo bo, PageQuery pageQuery) {
        return musicService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐曲库主列表
     */
    @SaCheckPermission("music:music:export")
    @Log(title = "音乐曲库主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicBo bo, HttpServletResponse response) {
        List<MusicVo> list = musicService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐曲库主", MusicVo.class, response);
    }

    /**
     * 获取音乐曲库主详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:music:query")
    @GetMapping("/{id}")
    public R<MusicVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicService.queryById(id));
    }

    /**
     * 获取音乐详情信息(含关联数据)
     *
     * @param id 主键
     */
    @SaCheckPermission("music:music:query")
    @GetMapping("/detail/{id}")
    public R<MusicDetailVo> getDetail(@NotNull(message = "主键不能为空")
                                      @PathVariable("id") Long id) {
        return R.ok(musicService.queryDetailById(id));
    }

    /**
     * 获取音乐完整聚合详情(含统计、通知、评论)
     */
    @SaCheckPermission("music:music:query")
    @GetMapping("/fullDetail/{id}")
    public R<MusicFullDetailVo> getFullDetail(@NotNull(message = "主键不能为空")
                                              @PathVariable("id") Long id) {
        return R.ok(musicService.queryFullDetailById(id));
    }

    /**
     * 审核音乐
     */
    @SaCheckPermission("music:music:audit")
    @Log(title = "音乐曲库主", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/audit")
    public R<Void> audit(@Validated @RequestBody MusicAuditBo bo) {
        Long operatorId = LoginHelper.getUserId();
        return toAjax(musicService.audit(bo, operatorId));
    }

    /**
     * 批量通过
     */
    @SaCheckPermission("music:music:audit")
    @Log(title = "音乐曲库主", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/batchPass/{ids}")
    public R<Void> batchPass(@NotEmpty(message = "主键不能为空")
                             @PathVariable("ids") Long[] ids) {
        Long operatorId = LoginHelper.getUserId();
        return toAjax(musicService.batchPass(List.of(ids), operatorId));
    }

    /**
     * 批量下架
     */
    @SaCheckPermission("music:music:audit")
    @Log(title = "音乐曲库主", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PostMapping("/batchOffline")
    public R<Void> batchOffline(@NotEmpty(message = "主键不能为空")
                                @RequestParam("ids") Long[] ids,
                                @RequestParam("reason") String reason) {
        Long operatorId = LoginHelper.getUserId();
        return toAjax(musicService.batchOffline(List.of(ids), reason, operatorId));
    }

    /**
     * 新增音乐曲库主
     */
    @SaCheckPermission("music:music:add")
    @Log(title = "音乐曲库主", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicBo bo) {
        return toAjax(musicService.insertByBo(bo));
    }

    /**
     * 修改音乐曲库主
     */
    @SaCheckPermission("music:music:edit")
    @Log(title = "音乐曲库主", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicBo bo) {
        return toAjax(musicService.updateByBo(bo));
    }

    /**
     * 删除音乐曲库主
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:music:remove")
    @Log(title = "音乐曲库主", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicService.deleteWithValidByIds(List.of(ids), true));
    }
}
