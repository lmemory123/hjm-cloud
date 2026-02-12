package org.dromara.music.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.music.domain.bo.PortalTagProposalBo;
import org.dromara.music.service.IPortalTagService;
import org.dromara.music.domain.TagProposal;
import org.dromara.music.domain.vo.TagProposalVo;
import org.dromara.music.domain.vo.TagVo;
import org.dromara.music.mapper.TagMapper;
import org.dromara.music.mapper.TagProposalMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static org.dromara.music.domain.table.TagTableDef.TAG;
import static org.dromara.music.domain.table.TagProposalTableDef.TAG_PROPOSAL;

@Slf4j
@RequiredArgsConstructor
@Service
public class PortalTagServiceImpl implements IPortalTagService {

    private final TagMapper tagMapper;
    private final TagProposalMapper tagProposalMapper;

    @Override
    public List<TagVo> listTags(String keyword, String type) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.where(TAG.NAME.like(keyword).or(TAG.TAG_ALIAS.like(keyword)));
        }
        if (StringUtils.isNotBlank(type)) {
            wrapper.and(TAG.TYPE.eq(type));
        }
        wrapper.and(TAG.STATUS.eq("0"));
        wrapper.orderBy(TAG.SORT_ORDER.asc(), TAG.ID.asc());
        return tagMapper.selectVoList(wrapper);
    }

    @Override
    public Long submitProposal(PortalTagProposalBo bo, Long userId) {
        if (bo == null || StringUtils.isBlank(bo.getTagName())) {
            throw new ServiceException("标签名称不能为空");
        }
        TagProposal proposal = new TagProposal();
        proposal.setUserId(userId);
        proposal.setMusicId(bo.getMusicId());
        proposal.setTagName(bo.getTagName());
        proposal.setTagType(bo.getTagType());
        proposal.setDescription(bo.getDescription());
        proposal.setStatus("0");
        proposal.setCreateBy(userId);
        proposal.setCreateTime(new Date());
        tagProposalMapper.insert(proposal);
        return proposal.getId();
    }

    @Override
    public List<TagProposalVo> listMyProposals(Long userId) {
        return tagProposalMapper.selectVoList(QueryWrapper.create().where(TAG_PROPOSAL.USER_ID.eq(userId)));
    }
}
