package org.dromara.resource.domain.vo;

import lombok.Data;

@Data
public class SysOssPresignVo {
    /**
     * 上传URL
     */
    private String uploadUrl;

    /**
     * 对象KEY
     */
    private String objectKey;

    /**
     * 过期时间(时间戳毫秒)
     */
    private Long expireAt;

    /**
     * 上传方法
     */
    private String method;
}
