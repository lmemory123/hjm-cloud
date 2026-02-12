package org.dromara.music.domain;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 音乐资源文件对象 music_resource
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_resource")
public class MusicResource extends QueryBaseEntity {


    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 关联音乐
     */
    private Long musicId;

    /**
     * 资源类型
     */
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

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标志
     */
    @Column(isLogicDelete = true)
    private String delFlag;


}
