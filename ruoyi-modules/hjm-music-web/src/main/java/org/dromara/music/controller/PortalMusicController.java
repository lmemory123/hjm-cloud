package org.dromara.music.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.music.domain.bo.PortalMusicUpdateBo;
import org.dromara.music.service.IPortalMusicService;
import org.dromara.music.domain.bo.MusicBo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/portal/music")
public class PortalMusicController extends BaseController {

    private final IPortalMusicService portalMusicService;

    @GetMapping("/my")
    public TableDataInfo<MusicVo> myList(MusicBo bo, PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        return portalMusicService.queryMyPage(bo, pageQuery, userId);
    }

    @GetMapping("/{id}")
    public R<MusicDetailVo> detail(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        Long userId = LoginHelper.getUserId();
        return R.ok(portalMusicService.queryDetail(id, userId));
    }

    @PutMapping("/{id}")
    public R<Void> update(@NotNull(message = "主键不能为空") @PathVariable("id") Long id,
                          @RequestBody PortalMusicUpdateBo bo) {
        Long userId = LoginHelper.getUserId();
        bo.setId(id);
        return toAjax(portalMusicService.updateMusic(bo, userId));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        Long userId = LoginHelper.getUserId();
        return toAjax(portalMusicService.deleteMusic(id, userId));
    }
}
