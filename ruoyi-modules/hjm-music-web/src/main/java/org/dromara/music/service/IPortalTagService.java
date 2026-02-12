package org.dromara.music.service;

import org.dromara.music.domain.bo.PortalTagProposalBo;
import org.dromara.music.domain.vo.TagProposalVo;
import org.dromara.music.domain.vo.TagVo;

import java.util.List;

public interface IPortalTagService {

    List<TagVo> listTags(String keyword, String type);

    Long submitProposal(PortalTagProposalBo bo, Long userId);

    List<TagProposalVo> listMyProposals(Long userId);
}
