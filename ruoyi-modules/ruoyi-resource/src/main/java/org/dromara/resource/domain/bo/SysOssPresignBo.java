package org.dromara.resource.domain.bo;

import lombok.Data;

@Data
public class SysOssPresignBo {
    /**
     * 原始文件名
     */
    private String fileName;

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
     * 业务类型/目录
     */
    private String bizType;

    /**
     * OSS服务商key
     */
    private String service;
}
