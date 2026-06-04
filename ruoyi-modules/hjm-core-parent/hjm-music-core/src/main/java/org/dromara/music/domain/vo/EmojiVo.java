package org.dromara.music.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.music.domain.Emoji;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 表情包视图对象 emoji
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Emoji.class)
public class EmojiVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String url;
    private String name;
    private String category;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
