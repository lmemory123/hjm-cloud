package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.MusicNotifyLog;
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
 * 音乐通知日志视图对象 music_notify_log
 *
 * @author momao
 * @date 2025-12-30
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
     * 接收用户ID（UP主）
     */
    @ExcelProperty(value = "接收用户ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "U=P主")
    private Long userId;

    /**
     * 通知类型: link_invalid/audit_result/resource_update
     */
    @ExcelProperty(value = "通知类型: link_invalid/audit_result/resource_update")
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
     * 发送渠道: system/email/sms/wechat
     */
    @ExcelProperty(value = "发送渠道: system/email/sms/wechat")
    private String sendChannel;

    /**
     * 发送状态: 0待发送 1已发送 2发送失败
     */
    @ExcelProperty(value = "发送状态: 0待发送 1已发送 2发送失败")
    private String sendStatus;

    /**
     * 发送时间
     */
    @ExcelProperty(value = "发送时间")
    private Date sendTime;

    /**
     * 阅读状态: 0未读 1已读
     */
    @ExcelProperty(value = "阅读状态: 0未读 1已读")
    private String readStatus;

    /**
     * 阅读时间
     */
    @ExcelProperty(value = "阅读时间")
    private Date readTime;


}
