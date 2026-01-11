package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.system.domain.MusicLinkCheckLog;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 链接检测日志视图对象 music_link_check_log
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicLinkCheckLog.class)
public class MusicLinkCheckLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 资源ID
     */
    @ExcelProperty(value = "资源ID")
    private Long resourceId;

    /**
     * 资源类型
     */
    @ExcelProperty(value = "资源类型")
    private String resourceType;

    /**
     * 关联音乐ID
     */
    @ExcelProperty(value = "关联音乐ID")
    private Long musicId;

    /**
     * 检测的URL地址
     */
    @ExcelProperty(value = "检测的URL地址")
    private String checkUrl;

    /**
     * 检测结果
     */
    @ExcelProperty(value = "检测结果")
    private String checkResult;

    /**
     * HTTP状态码
     */
    @ExcelProperty(value = "HTTP状态码")
    private Long httpStatus;

    /**
     * 响应时间（毫秒）
     */
    @ExcelProperty(value = "响应时间", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "毫=秒")
    private Long responseTime;

    /**
     * 错误信息
     */
    @ExcelProperty(value = "错误信息")
    private String errorMessage;

    /**
     * 检测时间
     */
    @ExcelProperty(value = "检测时间")
    private Date checkTime;

    /**
     * 检测批次号
     */
    @ExcelProperty(value = "检测批次号")
    private String checkBatch;


}
