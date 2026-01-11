package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.system.domain.MusicLinkCheckLog;

import java.util.Date;

/**
 * 链接检测日志业务对象 music_link_check_log
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicLinkCheckLog.class, reverseConvertGenerate = false)
public class MusicLinkCheckLogBo extends QueryBaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 资源ID
     */
    @NotNull(message = "资源ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long resourceId;

    /**
     * 资源类型
     */
    @NotBlank(message = "资源类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String resourceType;

    /**
     * 关联音乐ID
     */
    private Long musicId;

    /**
     * 检测的URL地址
     */
    @NotBlank(message = "检测的URL地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String checkUrl;

    /**
     * 检测结果
     */
    @NotBlank(message = "检测结果不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotNull(message = "检测时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date checkTime;

    /**
     * 检测批次号
     */
    private String checkBatch;


}
