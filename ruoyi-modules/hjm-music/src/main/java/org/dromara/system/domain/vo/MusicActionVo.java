package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.MusicAction;

import java.io.Serial;
import java.io.Serializable;



/**
 * 音乐互动动作视图对象 music_action
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicAction.class)
public class MusicActionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 目标对象ID
     */
    @ExcelProperty(value = "目标对象ID")
    private Long targetId;

    /**
     * 目标类型
     */
    @ExcelProperty(value = "目标类型")
    private String targetType;

    /**
     * 动作
     */
    @ExcelProperty(value = "动作")
    private String action;


}
