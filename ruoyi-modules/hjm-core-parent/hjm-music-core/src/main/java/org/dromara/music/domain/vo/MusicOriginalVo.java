package org.dromara.music.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.music.domain.MusicOriginal;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 音乐原曲关联视图对象 music_original
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicOriginal.class)
public class MusicOriginalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 关联的音乐
     */
    @ExcelProperty(value = "关联的音乐")
    private Long musicId;

    /**
     * 原曲标题
     */
    @ExcelProperty(value = "原曲标题")
    private String originalTitle;

    /**
     * 原曲作者/艺术家
     */
    @ExcelProperty(value = "原曲作者/艺术家")
    private String originalAuthor;

    /**
     * 原曲专辑
     */
    @ExcelProperty(value = "原曲专辑")
    private String originalAlbum;

    /**
     * 原曲外链地址
     */
    @ExcelProperty(value = "原曲外链地址")
    private String originalLink;

    /**
     * 来源平台
     */
    @ExcelProperty(value = "来源平台")
    private String sourceType;

    /**
     * 关系类型
     */
    @ExcelProperty(value = "关系类型")
    private String relationType;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Long sortOrder;

    /**
     * 链接状态
     */
    @ExcelProperty(value = "链接状态")
    private String linkStatus;

    /**
     * 最后检测时间
     */
    @ExcelProperty(value = "最后检测时间")
    private Date lastCheckTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
