package org.dromara.system.domain.bo;

import org.dromara.system.domain.MusicOriginal;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 音乐原曲关联业务对象 music_original
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MusicOriginal.class, reverseConvertGenerate = false)
public class MusicOriginalBo extends QueryBaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 关联的音乐
     */
    @NotNull(message = "关联的音乐不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long musicId;

    /**
     * 原曲标题
     */
    @NotBlank(message = "原曲标题不能为空", groups = { AddGroup.class, EditGroup.class })
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


}
