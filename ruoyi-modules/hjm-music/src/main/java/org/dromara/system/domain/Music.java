package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 音乐曲库主对象 music
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music")
public class Music extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID (雪花算法)
     */
    @Id
    private Long id;

    /**
     * 作品标题
     */
    private String title;

    /**
     * 副标题/别名
     */
    private String subtitle;

    /**
     * 原曲名（冗余，方便搜索）
     */
    private String originalTitle;

    /**
     * UP主/创作者ID（关联用户表）
     */
    private Long creatorId;

    /**
     * 外部原作者名称 (如: ilem)
     */
    private String creatorName;

    /**
     * 外部原作者主页链接
     */
    private String creatorLink;

    /**
     * 全民制作人标签（JSON数组或逗号分隔）
     */
    private String producerMark;

    /**
     * 时长(秒)
     */
    private Long duration;

    /**
     * 节拍数(BPM)
     */
    private Long bpm;

    /**
     * 发布时间(过审时间)
     */
    private Date publishTime;

    /**
     * 播放量(缓存)
     */
    private Long playCount;

    /**
     * 点赞量(缓存)
     */
    private Long likeCount;

    /**
     * 收藏量(缓存)
     */
    private Long collectCount;

    /**
     * 评论数(缓存)
     */
    private Long commentCount;

    /**
     * 分享数(缓存)
     */
    private Long shareCount;

    /**
     * 下载数(缓存)
     */
    private Long downloadCount;

    /**
     * 原曲关联信息快照(JSON数组)
     */
    private String originalData;

    /**
     * 资源展示快照(JSON对象): 封面和音频的最佳链接
     */
    private String resourceData;

    /**
     * 标签展示快照(JSON数组): 标签名和颜色
     */
    private String tagsSnapshot;

    /**
     * 扩展字段(JSON对象): 歌词、备注、PV链接等
     */
    private String extendData;

    /**
     * 审核状态: 0待审 1通过 2拒绝 3下架
     */
    private String auditStatus;

    /**
     * 是否公开: 0否 1是
     */
    private String isPublic;

    /**
     * 是否原创: 0否 1是
     */
    private String isOriginal;

    /**
     * 资源状态: 0正常 1部分失效 2全部失效
     */
    private String resourceStatus;

    /**
     * 版权信息
     */
    private String copyrightInfo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志: 0存在 1删除
     */
    @Column(isLogicDelete = true)
    private String delFlag;


}
