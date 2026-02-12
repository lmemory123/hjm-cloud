package org.dromara.music.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.Tag;
import org.dromara.music.domain.TagProposal;
import org.dromara.music.domain.bo.TagProposalAuditBo;
import org.dromara.music.domain.bo.TagProposalBo;
import org.dromara.music.domain.vo.TagProposalVo;
import org.dromara.music.mapper.TagMapper;
import org.dromara.music.mapper.TagProposalMapper;
import org.dromara.music.service.ITagProposalService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.dromara.music.domain.table.TagProposalTableDef.TAG_PROPOSAL;

@Slf4j
@RequiredArgsConstructor
@Service
public class TagProposalServiceImpl implements ITagProposalService {

    private final TagProposalMapper baseMapper;
    private final TagMapper tagMapper;

    @Override
    public TableDataInfo<TagProposalVo> queryPageList(TagProposalBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<TagProposalVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    @Override
    public List<TagProposalVo> queryList(TagProposalBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(TagProposalBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                TAG_PROPOSAL.TAG_NAME.like(bo.getTagName())
                    .and(TAG_PROPOSAL.TAG_TYPE.eq(bo.getTagType()))
                    .and(TAG_PROPOSAL.STATUS.eq(bo.getStatus()))
                    .and(TAG_PROPOSAL.USER_ID.eq(bo.getUserId()))
                    .and(TAG_PROPOSAL.MUSIC_ID.eq(bo.getMusicId()))
            )
            .orderBy(TAG_PROPOSAL.ID.desc());
        if (params.get("beginCreateTime") != null && params.get("endCreateTime") != null) {
            queryWrapper.and("create_time between ? and ?", params.get("beginCreateTime"), params.get("endCreateTime"));
        }
        return queryWrapper;
    }

    @Override
    public Boolean audit(TagProposalAuditBo bo, Long auditorId) {
        if (bo == null || bo.getId() == null || bo.getAction() == null) {
            throw new ServiceException("审核参数不能为空");
        }
        TagProposal proposal = baseMapper.selectOneById(bo.getId());
        if (proposal == null) {
            throw new ServiceException("提报不存在");
        }
        String status;
        if (bo.getAction() == 1) {
            status = "1";
        } else if (bo.getAction() == 2) {
            status = "2";
        } else {
            throw new ServiceException("非法审核动作");
        }
        if ("2".equals(status) && StringUtils.isBlank(bo.getRemark())) {
            throw new ServiceException("拒绝原因不能为空");
        }
        TagProposal update = new TagProposal();
        update.setId(proposal.getId());
        update.setStatus(status);
        update.setAuditorId(auditorId);
        update.setAuditTime(new Date());
        update.setAuditRemark(bo.getRemark());
        if ("1".equals(status)) {
            Tag tag = new Tag();
            tag.setName(proposal.getTagName());
            tag.setType(proposal.getTagType());
            tag.setDescription(proposal.getDescription());
            tag.setStatus("0");
            tag.setCreateBy(auditorId);
            tag.setCreateTime(new Date());
            tagMapper.insert(tag);
            update.setTagId(tag.getId());
        }
        return baseMapper.update(update) > 0;
    }
}
