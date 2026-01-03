package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 音乐原曲关联对象 music_original
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_original")
public class MusicOriginal extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 关联的音乐ID
     */
    private Long musicId;

    /**
     * 原曲标题
     */
    private String originalTitle;

    /**
     * 原曲作者/艺术家
     */
    private String originalAuthor;

    /**
     * 原曲专辑
     */
    private String originalAlbum;

    /**
     * 原曲外链地址
     */
    private String originalLink;

    /**
     * 来源平台: bilibili/netease/youtube/spotify/other
     */
    private String sourceType;

    /**
     * 关系类型: original/cover/remix/arrange/sample
     */
    private String relationType;

    /**
     * 排序号
     */
    private Long sortOrder;

    /**
     * 链接状态: 0正常 1失效 2未检测
     */
    private String linkStatus;

    /**
     * 最后检测时间
     */
    private Date lastCheckTime;

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
