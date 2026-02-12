package org.dromara.music.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.music.domain.TagProposal;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = TagProposal.class, reverseConvertGenerate = false)
public class TagProposalBo extends BaseEntity {
    private Long id;
    private Long userId;
    private Long musicId;
    private String tagName;
    private String tagType;
    private String description;
    private String status;
    private Long auditorId;
    private Long tagId;
}
