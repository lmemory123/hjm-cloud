package org.dromara.resource.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.resource.domain.bo.SysOssCompleteBo;
import org.dromara.resource.domain.bo.SysOssMultipartAbortBo;
import org.dromara.resource.domain.bo.SysOssMultipartCompleteBo;
import org.dromara.resource.domain.bo.SysOssMultipartInitBo;
import org.dromara.resource.domain.bo.SysOssPresignBo;
import org.dromara.resource.domain.vo.SysOssMultipartInitVo;
import org.dromara.resource.domain.vo.SysOssPresignVo;
import org.dromara.resource.domain.vo.SysOssUploadVo;
import org.dromara.resource.service.ISysOssService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 前台直传接口
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/oss/portal")
public class SysOssPortalController {

    private final ISysOssService ossService;

    /**
     * 获取直传预签名URL
     */
    @PostMapping("/presign")
    public R<SysOssPresignVo> presign(@RequestBody SysOssPresignBo bo) {
        return R.ok(ossService.presign(bo));
    }

    /**
     * 直传完成回调
     */
    @PostMapping("/complete")
    public R<SysOssUploadVo> complete(@RequestBody SysOssCompleteBo bo) {
        return R.ok(ossService.complete(bo));
    }

    /**
     * 初始化分片上传
     */
    @PostMapping("/multipart/init")
    public R<SysOssMultipartInitVo> multipartInit(@RequestBody SysOssMultipartInitBo bo) {
        return R.ok(ossService.multipartInit(bo));
    }

    /**
     * 完成分片上传
     */
    @PostMapping("/multipart/complete")
    public R<SysOssUploadVo> multipartComplete(@RequestBody SysOssMultipartCompleteBo bo) {
        return R.ok(ossService.multipartComplete(bo));
    }

    /**
     * 取消分片上传
     */
    @PostMapping("/multipart/abort")
    public R<Boolean> multipartAbort(@RequestBody SysOssMultipartAbortBo bo) {
        return R.ok(ossService.multipartAbort(bo));
    }
}
