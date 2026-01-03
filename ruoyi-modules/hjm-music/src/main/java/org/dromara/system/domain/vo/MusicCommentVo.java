package org.dromara.system.domain.vo;

import org.dromara.system.domain.MusicComment;
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
 * 音乐评论视图对象 music_comment
 *
 * @author momao
 * @date 2025-12-30
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MusicComment.class)
public class MusicCommentVo implements Serializable {

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
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 评论内容
     */
    @ExcelProperty(value = "评论内容")
    private String content;

    /**
     * 顶级评论ID(0表示自身为顶级)
     */
    @ExcelProperty(value = "顶级评论ID(0表示自身为顶级)")
    private Long rootId;

    /**
     * 父评论ID(0表示直接回复音乐)
     */
    @ExcelProperty(value = "父评论ID(0表示直接回复音乐)")
    private Long parentId;


}
