package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.MusicAuditLog;
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
 * 音乐审核流水日志视图对象 music_audit_log
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicAuditLog.class)
public class MusicAuditLogVo implements Serializable {

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
     * 目标类型
     */
    @ExcelProperty(value = "目标类型")
    private String targetType;

    /**
     * 审核动作
     */
    @ExcelProperty(value = "审核动作")
    private Long action;

    /**
     * 修改前状态
     */
    @ExcelProperty(value = "修改前状态")
    private Long oldStatus;

    /**
     * 修改后状态
     */
    @ExcelProperty(value = "修改后状态")
    private Long newStatus;

    /**
     * 拒绝或下架原因
     */
    @ExcelProperty(value = "拒绝或下架原因")
    private String reason;

    /**
     * 下架/审核快照
     */
    @ExcelProperty(value = "下架/审核快照")
    private String snapshot;

    /**
     * 操作人ID
     */
    @ExcelProperty(value = "操作人ID")
    private Long operatorId;


}
