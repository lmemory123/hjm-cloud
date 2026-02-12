package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.TagProposalAuditBo;
import org.dromara.music.domain.bo.TagProposalBo;
import org.dromara.music.domain.vo.TagProposalVo;

import java.util.List;

public interface ITagProposalService {

    /**
     * 分页查询标签提报
     */
    TableDataInfo<TagProposalVo> queryPageList(TagProposalBo bo, PageQuery pageQuery);

    /**
     * 查询标签提报列表
     */
    List<TagProposalVo> queryList(TagProposalBo bo);

    /**
     * 审核标签提报
     */
    Boolean audit(TagProposalAuditBo bo, Long auditorId);
}
