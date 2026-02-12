package org.dromara.resource.domain.vo;

import lombok.Data;

@Data
public class SysOssMultipartPartVo {
    /**
     * 分片序号
     */
    private Integer partNumber;

    /**
     * 分片上传URL
     */
    private String uploadUrl;

    /**
     * 过期时间(时间戳毫秒)
     */
    private Long expireAt;
}
