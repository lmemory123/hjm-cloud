package org.dromara.music.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MusicDetailVo extends MusicVo {

    /**
     * 原曲信息
     */
    private List<MusicOriginalVo> originals;

    /**
     * 资源信息
     */
    private List<MusicResourceVo> resources;

    /**
     * 标签信息
     */
    private List<TagVo> tags;

    /**
     * 审核日志
     */
    private List<MusicAuditLogVo> auditLogs;
}
