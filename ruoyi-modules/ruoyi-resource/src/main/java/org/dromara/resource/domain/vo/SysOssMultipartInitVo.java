package org.dromara.resource.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class SysOssMultipartInitVo {
    /**
     * 上传ID
     */
    private String uploadId;

    /**
     * 对象KEY
     */
    private String objectKey;

    /**
     * 分片URL列表
     */
    private List<SysOssMultipartPartVo> parts;

    /**
     * 过期时间(时间戳毫秒)
     */
    private Long expireAt;
}
