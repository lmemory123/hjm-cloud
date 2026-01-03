package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicResource;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 音乐资源文件业务对象 music_resource
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicResource.class, reverseConvertGenerate = false)
public class MusicResourceBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 关联音乐ID
     */
    @NotNull(message = "关联音乐ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long musicId;

    /**
     * 资源类型: audio(音频)/cover(封面)
     */
    @NotBlank(message = "资源类型: audio(音频)/cover(封面)不能为空", groups = { AddGroup.class, EditGroup.class })
    private String resType;

    /**
     * 质量等级: audio(128k/320k/flac) cover(200/600/1200)
     */
    private String qualityTier;

    /**
     * 来源类型: local/bilibili/netease/soundcloud/youtube
     */
    private String sourceType;

    /**
     * 原始来源地址（外链）
     */
    private String sourceUrl;

    /**
     * 来源平台资源ID（如BV号）
     */
    private String sourceId;

    /**
     * 资源实际链接 (OSS或外链)
     */
    @NotBlank(message = "资源实际链接 (OSS或外链)不能为空", groups = { AddGroup.class, EditGroup.class })
    private String url;

    /**
     * 处理后文件路径（本地/OSS）
     */
    private String filePath;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件格式: mp3/flac/webp/jpeg/png
     */
    private String fileFormat;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件哈希（去重用）
     */
    private String fileHash;

    /**
     * 规格信息(JSON): 音频{"bitrate":"320k"} 图片{"width":1200}
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
     * 是否主版本: 0否 1是
     */
    private String isPrimary;

    /**
     * 资源状态: 0正常 1失效 2需补档 3处理中
     */
    private String status;

    /**
     * 处理状态: 0待处理 1处理中 2已完成 3失败
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
