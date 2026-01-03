package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 链接检测日志对象 music_link_check_log
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_link_check_log")
public class MusicLinkCheckLog extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 资源类型: audio/cover/original
     */
    private String resourceType;

    /**
     * 关联音乐ID
     */
    private Long musicId;

    /**
     * 检测的URL地址
     */
    private String checkUrl;

    /**
     * 检测结果: 0正常 1失效 2超时 3异常
     */
    private String checkResult;

    /**
     * HTTP状态码
     */
    private Long httpStatus;

    /**
     * 响应时间（毫秒）
     */
    private Long responseTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 检测时间
     */
    private Date checkTime;

    /**
     * 检测批次号
     */
    private String checkBatch;


}
