package org.dromara.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 音乐详情视图对象 (包含关联数据)
 *
 * @author momao
 * @date 2025-11-30
 */
@Data
public class MusicDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
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
     * 原曲名
     */
    private String originalTitle;

    /**
     * UP主/创作者ID
     */
    private Long creatorId;

    /**
     * UP主/创作者名称
     */
    private String creatorName;

    /**
     * 外部原作者主页链接
     */
    private String creatorLink;

    /**
     * 全民制作人标签
     */
    private String producerMark;

    /**
     * 时长（秒）
     */
    private Integer duration;

    /**
     * 节拍数(BPM)
     */
    private Integer bpm;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 播放量
     */
    private Long playCount;

    /**
     * 点赞量
     */
    private Long likeCount;

    /**
     * 收藏量
     */
    private Long collectCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 分享数
     */
    private Long shareCount;

    /**
     * 下载数
     */
    private Long downloadCount;

    /**
     * 扩展字段
     */
    private Map<String, Object> extendData;

    /**
     * 审核状态
     */
    private String auditStatus;

    /**
     * 是否公开
     */
    private String isPublic;

    /**
     * 是否原创
     */
    private String isOriginal;

    /**
     * 资源状态
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    // ========= 关联数据 =========

    /**
     * 原曲关联列表
     */
    private List<MusicOriginalVo> originals;

    /**
     * 资源文件列表
     */
    private List<MusicResourceVo> resources;

    /**
     * 标签列表
     */
    private List<TagVo> tags;

    /**
     * 最近审核记录
     */
    private List<MusicAuditLogVo> auditLogs;

}
