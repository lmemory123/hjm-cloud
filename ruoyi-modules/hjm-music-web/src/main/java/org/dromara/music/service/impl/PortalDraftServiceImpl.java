package org.dromara.music.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.domain.bo.MusicSubmitBo;
import org.dromara.music.service.IPortalDraftService;
import org.dromara.music.domain.*;
import org.dromara.music.domain.vo.MusicDraftVo;
import org.dromara.music.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 前台草稿服务实现
 *
 * @author momao
 * @date 2026-01-31
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PortalDraftServiceImpl implements IPortalDraftService {

    /**
     * 审核状态常量
     */
    private static final String AUDIT_STATUS_PENDING = "0";
    private final MusicDraftMapper draftMapper;
    private final MusicMapper musicMapper;
    private final MusicOriginalMapper originalMapper;
    private final MusicTagRelMapper tagRelMapper;
    private final MusicResourceMapper resourceMapper;
    private final TagProposalMapper tagProposalMapper;

    @Override
    public Long saveDraft(Long userId, String content) {
        MusicDraft draft = new MusicDraft();
        draft.setUserId(userId);
        draft.setContent(content);
        draft.setCreateTime(new Date());
        draft.setUpdateTime(new Date());
        draftMapper.insert(draft);
        return draft.getId();
    }

    @Override
    public MusicDraftVo getDraft(Long id, Long userId) {
        MusicDraft draft = draftMapper.selectOneById(id);
        if (draft == null) {
            throw new RuntimeException("草稿不存在");
        }
        if (!userId.equals(draft.getUserId())) {
            throw new RuntimeException("无权查看他人的草稿");
        }
        MusicDraftVo vo = new MusicDraftVo();
        vo.setId(draft.getId());
        vo.setUserId(draft.getUserId());
        vo.setContent(draft.getContent());
        return vo;
    }

    @Override
    public Boolean updateDraft(Long id, Long userId, String content) {
        MusicDraft draft = draftMapper.selectOneById(id);
        if (draft == null) {
            throw new RuntimeException("草稿不存在");
        }
        if (!userId.equals(draft.getUserId())) {
            throw new RuntimeException("无权修改他人的草稿");
        }
        MusicDraft update = new MusicDraft();
        update.setId(id);
        update.setUserId(draft.getUserId());
        update.setCreateTime(draft.getCreateTime());
        update.setContent(content);
        update.setUpdateTime(new Date());
        return draftMapper.update(update, false) > 0;
    }

    @Override
    public Boolean deleteDraft(Long id, Long userId) {
        MusicDraft draft = draftMapper.selectOneById(id);
        if (draft == null) {
            throw new RuntimeException("草稿不存在");
        }
        if (!userId.equals(draft.getUserId())) {
            throw new RuntimeException("无权删除他人的草稿");
        }
        return draftMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitForAudit(MusicSubmitBo bo) {
        String creatorName = LoginHelper.getUsername();
        if (creatorName == null || creatorName.isBlank()) {
            creatorName = "用户" + bo.getUserId();
        }
        // 1. 创建音乐主记录
        Music music = new Music();
        music.setTitle(bo.getTitle());
        music.setSubtitle(bo.getSubtitle());
        music.setCreatorId(bo.getUserId());
        music.setCreatorName(creatorName);
        music.setIsOriginal(bo.getIsOriginal());
        music.setCopyrightInfo(bo.getCopyrightInfo());
        music.setRemark(bo.getRemark());
        music.setAuditStatus(AUDIT_STATUS_PENDING);
        music.setCreateBy(bo.getUserId());
        music.setCreateTime(new Date());

        // 保存原曲信息快照
        if (CollUtil.isNotEmpty(bo.getOriginalInfoList())) {
            music.setOriginalData(JSON.toJSONString(bo.getOriginalInfoList()));
        }

        musicMapper.insert(music);
        Long musicId = music.getId();

        // 2. 保存原曲关联
        if (CollUtil.isNotEmpty(bo.getOriginalInfoList())) {
            int sortOrder = 0;
            for (MusicSubmitBo.OriginalInfoBo info : bo.getOriginalInfoList()) {
                MusicOriginal original = new MusicOriginal();
                original.setMusicId(musicId);
                original.setOriginalTitle(info.getOriginalTitle());
                original.setOriginalAuthor(info.getOriginalAuthor());
                original.setOriginalAlbum(info.getOriginalAlbum());
                original.setOriginalLink(info.getOriginalLink());
                original.setSourceType(info.getSourceType());
                original.setRelationType(info.getRelationType());
                original.setSortOrder((long) sortOrder++);
                original.setCreateBy(bo.getUserId());
                original.setCreateTime(new Date());
                originalMapper.insert(original);
            }
        }

        // 3. 关联资源
        if (CollUtil.isNotEmpty(bo.getResourceIds())) {
            for (Long resourceId : bo.getResourceIds()) {
                MusicResource resource = resourceMapper.selectOneById(resourceId);
                if (resource != null) {
                    MusicResource update = new MusicResource();
                    update.setId(resourceId);
                    update.setMusicId(musicId);
                    resourceMapper.update(update, false);
                }
            }
        }

        // 4. 关联标签
        if (CollUtil.isNotEmpty(bo.getTagIds())) {
            int weight = bo.getTagIds().size();
            for (Long tagId : bo.getTagIds()) {
                MusicTagRel rel = new MusicTagRel();
                rel.setMusicId(musicId);
                rel.setTagId(tagId);
                rel.setTagWeight((long) weight--);
                rel.setSource("user");
                rel.setCreateBy(bo.getUserId());
                rel.setCreateTime(new Date());
                tagRelMapper.insert(rel);
            }
        }

        // 5. 处理标签申请
        if (CollUtil.isNotEmpty(bo.getTagProposals())) {
            for (MusicSubmitBo.TagProposalBo proposal : bo.getTagProposals()) {
                TagProposal tagProposal = new TagProposal();
                tagProposal.setUserId(bo.getUserId());
                tagProposal.setMusicId(musicId);
                tagProposal.setTagName(proposal.getTagName());
                tagProposal.setTagType(proposal.getTagType());
                tagProposal.setDescription(proposal.getDescription());
                tagProposal.setStatus("0"); // 待审核
                tagProposal.setCreateBy(bo.getUserId());
                tagProposal.setCreateTime(new Date());
                tagProposalMapper.insert(tagProposal);
            }
        }

        return musicId;
    }
}
