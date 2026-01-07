package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.Music;
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
 * 音乐曲库主视图对象 music
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Music.class)
public class MusicVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 作品标题
     */
    @ExcelProperty(value = "作品标题")
    private String title;

    /**
     * 副标题/别名
     */
    @ExcelProperty(value = "副标题/别名")
    private String subtitle;

    /**
     * 原曲名
     */
    @ExcelProperty(value = "原曲名")
    private String originalTitle;

    /**
     * UP主/创作者ID
     */
    @ExcelProperty(value = "UP主/创作者ID")
    private Long creatorId;

    /**
     * 原作者名称
     */
    @ExcelProperty(value = "原作者名称")
    private String creatorName;

    /**
     * 原作者主页链接
     */
    @ExcelProperty(value = "原作者主页链接")
    private String creatorLink;

    /**
     * 全民制作人标签
     */
    @ExcelProperty(value = "全民制作人标签")
    private String producerMark;

    /**
     * 时长(秒)
     */
    @ExcelProperty(value = "时长(秒)")
    private Long duration;

    /**
     * 节拍数(BPM)
     */
    @ExcelProperty(value = "节拍数(BPM)")
    private Long bpm;

    /**
     * 发布时间(过审时间)
     */
    @ExcelProperty(value = "发布时间(过审时间)")
    private Date publishTime;

    /**
     * 播放量(缓存)
     */
    @ExcelProperty(value = "播放量(缓存)")
    private Long playCount;

    /**
     * 点赞量(缓存)
     */
    @ExcelProperty(value = "点赞量(缓存)")
    private Long likeCount;

    /**
     * 收藏量(缓存)
     */
    @ExcelProperty(value = "收藏量(缓存)")
    private Long collectCount;

    /**
     * 评论数(缓存)
     */
    @ExcelProperty(value = "评论数(缓存)")
    private Long commentCount;

    /**
     * 分享数(缓存)
     */
    @ExcelProperty(value = "分享数(缓存)")
    private Long shareCount;

    /**
     * 下载数(缓存)
     */
    @ExcelProperty(value = "下载数(缓存)")
    private Long downloadCount;

    /**
     * 原曲关联信息快照
     */
    @ExcelProperty(value = "原曲关联信息快照")
    private String originalData;

    /**
     * 资源展示快照
     */
    @ExcelProperty(value = "资源展示快照")
    private String resourceData;

    /**
     * 标签展示快照
     */
    @ExcelProperty(value = "标签展示快照")
    private String tagsSnapshot;

    /**
     * 扩展字段
     */
    @ExcelProperty(value = "扩展字段")
    private String extendData;

    /**
     * 审核状态
     */
    @ExcelProperty(value = "审核状态")
    private String auditStatus;

    /**
     * 是否公开
     */
    @ExcelProperty(value = "是否公开")
    private String isPublic;

    /**
     * 是否原创
     */
    @ExcelProperty(value = "是否原创")
    private String isOriginal;

    /**
     * 资源状态
     */
    @ExcelProperty(value = "资源状态")
    private String resourceStatus;

    /**
     * 版权信息
     */
    @ExcelProperty(value = "版权信息")
    private String copyrightInfo;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
