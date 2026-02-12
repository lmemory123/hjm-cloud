package org.dromara.resource.domain.bo;

import lombok.Data;

@Data
public class SysOssMultipartInitBo {
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
     * 分片大小
     */
    private Long partSize;

    /**
     * 分片数量
     */
    private Integer partCount;

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
