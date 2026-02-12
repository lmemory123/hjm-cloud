package org.dromara.resource.domain.bo;

import lombok.Data;

@Data
public class SysOssMultipartAbortBo {
    /**
     * 上传ID
     */
    private String uploadId;

    /**
     * 对象KEY
     */
    private String objectKey;

    /**
     * OSS服务商key
     */
    private String service;
}
