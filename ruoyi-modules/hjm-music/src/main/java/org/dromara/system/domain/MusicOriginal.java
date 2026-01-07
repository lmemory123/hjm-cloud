package org.dromara.system.domain;

import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import com.mybatisflex.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * 音乐原曲关联对象 music_original
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_original")
public class MusicOriginal extends QueryBaseEntity {



    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 关联的音乐
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
     * 来源平台
     */
    private String sourceType;

    /**
     * 关系类型
     */
    private String relationType;

    /**
     * 排序号
     */
    private Long sortOrder;

    /**
     * 链接状态
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
