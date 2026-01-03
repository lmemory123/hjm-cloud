package org.dromara.system.domain.vo;

import org.dromara.system.domain.MusicAction;
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
 * 音乐互动动作视图对象 music_action
 *
 * @author momao
 * @date 2025-12-30
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
     * 目标类型: song/comment
     */
    @ExcelProperty(value = "目标类型: song/comment")
    private String targetType;

    /**
     * 动作: like/dislike
     */
    @ExcelProperty(value = "动作: like/dislike")
    private String action;


}
