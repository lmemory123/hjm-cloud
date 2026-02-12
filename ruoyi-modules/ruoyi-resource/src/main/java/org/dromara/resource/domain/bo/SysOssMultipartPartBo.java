package org.dromara.resource.domain.bo;

import lombok.Data;

@Data
public class SysOssMultipartPartBo {
    /**
     * 分片序号(从1开始)
     */
    private Integer partNumber;

    /**
     * 分片ETag
     */
    private String eTag;
}
