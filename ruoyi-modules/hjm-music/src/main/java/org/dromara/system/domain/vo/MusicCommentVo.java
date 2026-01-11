package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.MusicComment;

import java.io.Serial;
import java.io.Serializable;



/**
 * 音乐评论视图对象 music_comment
 *
 * @author momao
 * @date 2026-01-07
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
     * 顶级评论ID
     */
    @ExcelProperty(value = "顶级评论ID")
    private Long rootId;

    /**
     * 父评论ID
     */
    @ExcelProperty(value = "父评论ID")
    private Long parentId;


}
