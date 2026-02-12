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
import org.dromara.music.domain.bo.MusicCommentBo;
import org.dromara.music.domain.vo.MusicCommentVo;
import org.dromara.music.service.IMusicCommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音乐评论
 * 前端访问路由地址为:/music/comment
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class MusicCommentController extends BaseController {

    private final IMusicCommentService musicCommentService;

    /**
     * 查询音乐评论列表
     */
    @SaCheckPermission("music:comment:list")
    @GetMapping("/list")
    public TableDataInfo<MusicCommentVo> list(MusicCommentBo bo, PageQuery pageQuery) {
        return musicCommentService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐评论列表
     */
    @SaCheckPermission("music:comment:export")
    @Log(title = "音乐评论", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicCommentBo bo, HttpServletResponse response) {
        List<MusicCommentVo> list = musicCommentService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐评论", MusicCommentVo.class, response);
    }

    /**
     * 获取音乐评论详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:comment:query")
    @GetMapping("/{id}")
    public R<MusicCommentVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicCommentService.queryById(id));
    }

    /**
     * 新增音乐评论
     */
    @SaCheckPermission("music:comment:add")
    @Log(title = "音乐评论", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicCommentBo bo) {
        return toAjax(musicCommentService.insertByBo(bo));
    }

    /**
     * 修改音乐评论
     */
    @SaCheckPermission("music:comment:edit")
    @Log(title = "音乐评论", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicCommentBo bo) {
        return toAjax(musicCommentService.updateByBo(bo));
    }

    /**
     * 删除音乐评论
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:comment:remove")
    @Log(title = "音乐评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicCommentService.deleteWithValidByIds(List.of(ids), true));
    }
}
