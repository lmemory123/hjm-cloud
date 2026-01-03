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
 * @date 2025-12-30
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
     * 关联音乐ID
     */
    @ExcelProperty(value = "关联音乐ID")
    private Long musicId;

    /**
     * 资源类型: audio(音频)/cover(封面)
     */
    @ExcelProperty(value = "资源类型: audio(音频)/cover(封面)")
    private String resType;

    /**
     * 质量等级: audio(128k/320k/flac) cover(200/600/1200)
     */
    @ExcelProperty(value = "质量等级: audio(128k/320k/flac) cover(200/600/1200)")
    private String qualityTier;

    /**
     * 来源类型: local/bilibili/netease/soundcloud/youtube
     */
    @ExcelProperty(value = "来源类型: local/bilibili/netease/soundcloud/youtube")
    private String sourceType;

    /**
     * 原始来源地址（外链）
     */
    @ExcelProperty(value = "原始来源地址", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "外=链")
    private String sourceUrl;

    /**
     * 来源平台资源ID（如BV号）
     */
    @ExcelProperty(value = "来源平台资源ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=BV号")
    private String sourceId;

    /**
     * 资源实际链接 (OSS或外链)
     */
    @ExcelProperty(value = "资源实际链接 (OSS或外链)")
    private String url;

    /**
     * 处理后文件路径（本地/OSS）
     */
    @ExcelProperty(value = "处理后文件路径", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "本=地/OSS")
    private String filePath;

    /**
     * 文件名
     */
    @ExcelProperty(value = "文件名")
    private String fileName;

    /**
     * 文件格式: mp3/flac/webp/jpeg/png
     */
    @ExcelProperty(value = "文件格式: mp3/flac/webp/jpeg/png")
    private String fileFormat;

    /**
     * 文件大小（字节）
     */
    @ExcelProperty(value = "文件大小", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "字=节")
    private Long fileSize;

    /**
     * 文件哈希（去重用）
     */
    @ExcelProperty(value = "文件哈希", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "去=重用")
    private String fileHash;

    /**
     * 规格信息(JSON): 音频{"bitrate":"320k"} 图片{"width":1200}
     */
    @ExcelProperty(value = "规格信息(JSON)")
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
     * 是否主版本: 0否 1是
     */
    @ExcelProperty(value = "是否主版本: 0否 1是")
    private String isPrimary;

    /**
     * 资源状态: 0正常 1失效 2需补档 3处理中
     */
    @ExcelProperty(value = "资源状态: 0正常 1失效 2需补档 3处理中")
    private String status;

    /**
     * 处理状态: 0待处理 1处理中 2已完成 3失败
     */
    @ExcelProperty(value = "处理状态: 0待处理 1处理中 2已完成 3失败")
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
