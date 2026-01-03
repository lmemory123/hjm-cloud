package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 音乐资源文件对象 music_resource
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_resource")
public class MusicResource extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 关联音乐ID
     */
    private Long musicId;

    /**
     * 资源类型: audio(音频)/cover(封面)
     */
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

    /**
     * 删除标志: 0存在 1删除
     */
    @Column(isLogicDelete = true)
    private String delFlag;


}
