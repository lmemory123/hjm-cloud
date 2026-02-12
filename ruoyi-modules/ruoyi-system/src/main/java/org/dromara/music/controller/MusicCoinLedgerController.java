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
import org.dromara.music.domain.bo.MusicCoinLedgerBo;
import org.dromara.music.domain.vo.MusicCoinLedgerVo;
import org.dromara.music.service.IMusicCoinLedgerService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 哈气金流水
 * 前端访问路由地址为:/music/coinLedger
 *
 * @author momao
 * @date 2025-12-30
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coinLedger")
public class MusicCoinLedgerController extends BaseController {

    private final IMusicCoinLedgerService musicCoinLedgerService;

    /**
     * 查询哈气金流水列表
     */
    @SaCheckPermission("music:coinLedger:list")
    @GetMapping("/list")
    public TableDataInfo<MusicCoinLedgerVo> list(MusicCoinLedgerBo bo, PageQuery pageQuery) {
        return musicCoinLedgerService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出哈气金流水列表
     */
    @SaCheckPermission("music:coinLedger:export")
    @Log(title = "哈气金流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MusicCoinLedgerBo bo, HttpServletResponse response) {
        List<MusicCoinLedgerVo> list = musicCoinLedgerService.queryList(bo);
        ExcelUtil.exportExcel(list, "哈气金流水", MusicCoinLedgerVo.class, response);
    }

    /**
     * 获取哈气金流水详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("music:coinLedger:query")
    @GetMapping("/{id}")
    public R<MusicCoinLedgerVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable("id") Long id) {
        return R.ok(musicCoinLedgerService.queryById(id));
    }

    /**
     * 新增哈气金流水
     */
    @SaCheckPermission("music:coinLedger:add")
    @Log(title = "哈气金流水", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MusicCoinLedgerBo bo) {
        return toAjax(musicCoinLedgerService.insertByBo(bo));
    }

    /**
     * 修改哈气金流水
     */
    @SaCheckPermission("music:coinLedger:edit")
    @Log(title = "哈气金流水", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MusicCoinLedgerBo bo) {
        return toAjax(musicCoinLedgerService.updateByBo(bo));
    }

    /**
     * 删除哈气金流水
     *
     * @param ids 主键串
     */
    @SaCheckPermission("music:coinLedger:remove")
    @Log(title = "哈气金流水", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable("ids") Long[] ids) {
        return toAjax(musicCoinLedgerService.deleteWithValidByIds(List.of(ids), true));
    }
}
