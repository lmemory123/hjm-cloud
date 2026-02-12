package org.dromara.music.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.music.domain.TagProposal;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 标签提报申请视图对象 tag_proposal
 *
 * @author momao
 * @date 2026-01-31
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = TagProposal.class)
public class TagProposalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 提报用户ID
     */
    @ExcelProperty(value = "提报用户ID")
    private Long userId;

    /**
     * 关联的音乐ID
     */
    @ExcelProperty(value = "关联音乐ID")
    private Long musicId;

    /**
     * 标签名称
     */
    @ExcelProperty(value = "标签名称")
    private String tagName;

    /**
     * 标签类型
     */
    @ExcelProperty(value = "标签类型")
    private String tagType;

    /**
     * 标签描述/申请理由
     */
    @ExcelProperty(value = "描述")
    private String description;

    /**
     * 审核状态: 0-待审核 1-已通过 2-已拒绝
     */
    @ExcelProperty(value = "审核状态")
    private String status;

    /**
     * 审核人ID
     */
    @ExcelProperty(value = "审核人ID")
    private Long auditorId;

    /**
     * 审核时间
     */
    @ExcelProperty(value = "审核时间")
    private Date auditTime;

    /**
     * 审核备注/拒绝原因
     */
    @ExcelProperty(value = "审核备注")
    private String auditRemark;

    /**
     * 通过后关联的正式标签ID
     */
    @ExcelProperty(value = "正式标签ID")
    private Long tagId;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;
}
