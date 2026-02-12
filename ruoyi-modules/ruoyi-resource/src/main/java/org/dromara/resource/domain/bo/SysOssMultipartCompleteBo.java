package org.dromara.resource.domain.bo;

import lombok.Data;

import java.util.List;

@Data
public class SysOssMultipartCompleteBo {
    /**
     * 上传ID
     */
    private String uploadId;

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
     * 分片列表
     */
    private List<SysOssMultipartPartBo> parts;

    /**
     * OSS服务商key
     */
    private String service;
}
