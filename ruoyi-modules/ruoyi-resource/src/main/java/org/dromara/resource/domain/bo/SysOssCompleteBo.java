package org.dromara.resource.domain.bo;

import lombok.Data;

@Data
public class SysOssCompleteBo {
    /**
     * 对象KEY
     */
    private String objectKey;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件哈希
     */
    private String fileHash;

    /**
     * OSS服务商key
     */
    private String service;
}
