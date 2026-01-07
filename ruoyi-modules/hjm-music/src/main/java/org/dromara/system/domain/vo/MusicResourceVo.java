package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.MusicResource;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 音乐资源文件视图对象 music_resource
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicResource.class)
public class MusicResourceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 关联音乐
     */
    @ExcelProperty(value = "关联音乐")
    private Long musicId;

    /**
     * 资源类型
     */
    @ExcelProperty(value = "资源类型")
    private String resType;

    /**
     * 质量等级
     */
    @ExcelProperty(value = "质量等级")
    private String qualityTier;

    /**
     * 来源类型
     */
    @ExcelProperty(value = "来源类型")
    private String sourceType;

    /**
     * 原始来源
     */
    @ExcelProperty(value = "原始来源")
    private String sourceUrl;

    /**
     * 来源平台资源
     */
    @ExcelProperty(value = "来源平台资源")
    private String sourceId;

    /**
     * 资源实际链接
     */
    @ExcelProperty(value = "资源实际链接")
    private String url;

    /**
     * 处理后文件路径
     */
    @ExcelProperty(value = "处理后文件路径")
    private String filePath;

    /**
     * 文件名
     */
    @ExcelProperty(value = "文件名")
    private String fileName;

    /**
     * 文件格式
     */
    @ExcelProperty(value = "文件格式")
    private String fileFormat;

    /**
     * 文件大小
     */
    @ExcelProperty(value = "文件大小")
    private Long fileSize;

    /**
     * 文件哈希
     */
    @ExcelProperty(value = "文件哈希")
    private String fileHash;

    /**
     * 规格信息
     */
    @ExcelProperty(value = "规格信息")
    private String specInfo;

    /**
     * CDN加速地址
     */
    @ExcelProperty(value = "CDN加速地址")
    private String cdnUrl;

    /**
     * 访问次数
     */
    @ExcelProperty(value = "访问次数")
    private Long accessCount;

    /**
     * 是否主版本
     */
    @ExcelProperty(value = "是否主版本")
    private String isPrimary;

    /**
     * 资源状态
     */
    @ExcelProperty(value = "资源状态")
    private String status;

    /**
     * 处理状态
     */
    @ExcelProperty(value = "处理状态")
    private String processStatus;

    /**
     * 重试次数
     */
    @ExcelProperty(value = "重试次数")
    private Long retryCount;

    /**
     * 连续检测失败次数
     */
    @ExcelProperty(value = "连续检测失败次数")
    private Long failCount;

    /**
     * 失败原因
     */
    @ExcelProperty(value = "失败原因")
    private String failReason;

    /**
     * 最后检测时间
     */
    @ExcelProperty(value = "最后检测时间")
    private Date lastCheckTime;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Long sortOrder;


}
