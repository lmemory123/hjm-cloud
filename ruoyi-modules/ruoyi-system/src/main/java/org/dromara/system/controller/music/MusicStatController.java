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
import org.dromara.system.domain.vo.MusicStatVo;
import org.dromara.system.domain.bo.MusicStatBo;
import org.dromara.system.service.IMusicStatService;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;

/**
 * 音乐统计(高频读写)
 * 前端访问路由地址为:/music/stat
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/stat")
public class MusicStatController extends BaseController {

    private final IMusicStatService musicStatService;

    /**
     * 查询音乐统计(高频读写)列表
     */
    @SaCheckPermission("music:stat:list")
    @GetMapping("/list")
    public TableDataInfo<MusicStatVo> list(MusicStatBo bo, PageQuery pageQuery) {
        return musicStatService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出音乐统计(高频读写)列表
     */
    @SaCheckPermission("music:stat:export")
    @Log(title = "音乐统计(高频读写)", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicStatBo bo, HttpServletResponse response) {
        List<MusicStatVo> list = musicStatService.queryList(bo);
        ExcelUtil.exportExcel(list, "音乐统计(高频读写)", MusicStatVo.class, response);
    }

    /**
     * 获取音乐统计(高频读写)详细信息
     *
     * @param musicId 主键
     */
    @SaCheckPermission("music:stat:query")
    @GetMapping("/{musicId}")
    public R<MusicStatVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("musicId") Long musicId) {
        return R.ok(musicStatService.queryById(musicId));
    }

    /**
     * 新增音乐统计(高频读写)
     */
    @SaCheckPermission("music:stat:add")
    @Log(title = "音乐统计(高频读写)", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicStatBo bo) {
        return toAjax(musicStatService.insertByBo(bo));
    }

    /**
     * 修改音乐统计(高频读写)
     */
    @SaCheckPermission("music:stat:edit")
    @Log(title = "音乐统计(高频读写)", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicStatBo bo) {
        return toAjax(musicStatService.updateByBo(bo));
    }

    /**
     * 删除音乐统计(高频读写)
     *
     * @param musicIds 主键串
     */
    @SaCheckPermission("music:stat:remove")
    @Log(title = "音乐统计(高频读写)", businessType = BusinessType.DELETE)
    @DeleteMapping("/{musicIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("musicIds") Long[] musicIds) {
        return toAjax(musicStatService.deleteWithValidByIds(List.of(musicIds), true));
    }
}
