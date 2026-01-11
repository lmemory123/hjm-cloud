package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.system.domain.MusicResource;

import java.util.Date;

/**
 * 音乐资源文件业务对象 music_resource
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicResource.class, reverseConvertGenerate = false)
public class MusicResourceBo extends QueryBaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 关联音乐
     */
    @NotNull(message = "关联音乐不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long musicId;

    /**
     * 资源类型
     */
    @NotBlank(message = "资源类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String resType;

    /**
     * 质量等级
     */
    private String qualityTier;

    /**
     * 来源类型
     */
    private String sourceType;

    /**
     * 原始来源
     */
    private String sourceUrl;

    /**
     * 来源平台资源
     */
    private String sourceId;

    /**
     * 资源实际链接
     */
    @NotBlank(message = "资源实际链接不能为空", groups = { AddGroup.class, EditGroup.class })
    private String url;

    /**
     * 处理后文件路径
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件格式
     */
    private String fileFormat;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件哈希
     */
    private String fileHash;

    /**
     * 规格信息
     */
    private String specInfo;

    /**
     * CDN加速地址
     */
    private String cdnUrl;

    /**
     * 访问次数
     */
    private Long accessCount;

    /**
     * 是否主版本
     */
    private String isPrimary;

    /**
     * 资源状态
     */
    private String status;

    /**
     * 处理状态
     */
    private String processStatus;

    /**
     * 重试次数
     */
    private Long retryCount;

    /**
     * 连续检测失败次数
     */
    private Long failCount;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 最后检测时间
     */
    private Date lastCheckTime;

    /**
     * 排序号
     */
    private Long sortOrder;


}
