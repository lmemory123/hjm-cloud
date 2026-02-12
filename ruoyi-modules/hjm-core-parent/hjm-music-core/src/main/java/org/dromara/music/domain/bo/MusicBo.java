package org.dromara.music.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;
import org.dromara.music.domain.Music;

import java.util.Date;

/**
 * 音乐曲库主业务对象 music
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Music.class, reverseConvertGenerate = false)
public class MusicBo extends QueryBaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 作品标题
     */
    @NotBlank(message = "作品标题不能为空", groups = {AddGroup.class, EditGroup.class})
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
     * 原作者名称
     */
    @NotBlank(message = "原作者名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String creatorName;

    /**
     * 原作者主页链接
     */
    private String creatorLink;

    /**
     * 全民制作人标签
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
     * 原曲关联信息快照
     */
    private String originalData;

    /**
     * 资源展示快照
     */
    private String resourceData;

    /**
     * 标签展示快照
     */
    private String tagsSnapshot;

    /**
     * 扩展字段
     */
    private String extendData;

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


}
