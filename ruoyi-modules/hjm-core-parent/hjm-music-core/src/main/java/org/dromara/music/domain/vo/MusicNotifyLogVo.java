package org.dromara.music.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.music.domain.MusicNotifyLog;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 音乐通知日志视图对象 music_notify_log
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicNotifyLog.class)
public class MusicNotifyLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 音乐ID
     */
    @ExcelProperty(value = "音乐ID")
    private Long musicId;

    /**
     * 接收用户ID
     */
    @ExcelProperty(value = "接收用户ID")
    private Long userId;

    /**
     * 通知类型
     */
    @ExcelProperty(value = "通知类型")
    private String notifyType;

    /**
     * 通知标题
     */
    @ExcelProperty(value = "通知标题")
    private String notifyTitle;

    /**
     * 通知内容
     */
    @ExcelProperty(value = "通知内容")
    private String notifyContent;

    /**
     * 发送渠道
     */
    @ExcelProperty(value = "发送渠道")
    private String sendChannel;

    /**
     * 发送状态
     */
    @ExcelProperty(value = "发送状态")
    private String sendStatus;

    /**
     * 发送时间
     */
    @ExcelProperty(value = "发送时间")
    private Date sendTime;

    /**
     * 阅读状态
     */
    @ExcelProperty(value = "阅读状态")
    private String readStatus;

    /**
     * 阅读时间
     */
    @ExcelProperty(value = "阅读时间")
    private Date readTime;


}
