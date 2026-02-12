package org.dromara.music.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.MusicSubmitBo;
import org.dromara.music.domain.bo.PortalMusicUpdateBo;
import org.dromara.music.service.IPortalMusicService;
import org.dromara.music.domain.*;
import org.dromara.music.domain.bo.MusicBo;
import org.dromara.music.domain.vo.*;
import org.dromara.music.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.dromara.music.domain.table.MusicTableDef.MUSIC;
import static org.dromara.music.domain.table.MusicOriginalTableDef.MUSIC_ORIGINAL;
import static org.dromara.music.domain.table.MusicResourceTableDef.MUSIC_RESOURCE;
import static org.dromara.music.domain.table.MusicTagRelTableDef.MUSIC_TAG_REL;
import static org.dromara.music.domain.table.MusicAuditLogTableDef.MUSIC_AUDIT_LOG;
import static org.dromara.music.domain.table.TagTableDef.TAG;

@Slf4j
@RequiredArgsConstructor
@Service
public class PortalMusicServiceImpl implements IPortalMusicService {

    private final MusicMapper musicMapper;
    private final MusicOriginalMapper originalMapper;
    private final MusicTagRelMapper tagRelMapper;
    private final MusicResourceMapper resourceMapper;
    private final TagMapper tagMapper;
    private final MusicAuditLogMapper auditLogMapper;
    private final TagProposalMapper tagProposalMapper;

    private static final String AUDIT_PENDING = "0";
    private static final String AUDIT_REJECT = "2";

    @Override
    public TableDataInfo<MusicVo> queryMyPage(MusicBo bo, PageQuery pageQuery, Long userId) {
        MusicBo query = bo == null ? new MusicBo() : bo;
        query.setCreatorId(userId);
        QueryWrapper wrapper = buildQueryWrapper(query);
        Page<MusicVo> result = musicMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    @Override
    public MusicDetailVo queryDetail(Long id, Long userId) {
        Music music = musicMapper.selectOneById(id);
        if (music == null) {
            return null;
        }
        if (!userId.equals(music.getCreatorId())) {
            throw new ServiceException("无权访问该作品");
        }
        MusicVo musicVo = musicMapper.selectVoById(id);
        MusicDetailVo detail = new MusicDetailVo();
        BeanUtils.copyProperties(musicVo, detail);
        List<MusicOriginalVo> originals = originalMapper.selectVoList(
            QueryWrapper.create().where(MUSIC_ORIGINAL.MUSIC_ID.eq(id)));
        List<MusicResourceVo> resources = resourceMapper.selectVoList(
            QueryWrapper.create().where(MUSIC_RESOURCE.MUSIC_ID.eq(id)));
        List<MusicTagRelVo> rels = tagRelMapper.selectVoList(
            QueryWrapper.create().where(MUSIC_TAG_REL.MUSIC_ID.eq(id)));
        List<Long> tagIds = rels.stream().map(MusicTagRelVo::getTagId).collect(Collectors.toList());
        List<TagVo> tags = tagIds.isEmpty()
            ? new ArrayList<>()
            : tagMapper.selectVoList(QueryWrapper.create().where(TAG.ID.in(tagIds)));
        List<MusicAuditLogVo> auditLogs = auditLogMapper.selectVoList(
            QueryWrapper.create().where(MUSIC_AUDIT_LOG.MUSIC_ID.eq(id)));
        detail.setOriginals(originals);
        detail.setResources(resources);
        detail.setTags(tags);
        detail.setAuditLogs(auditLogs);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMusic(PortalMusicUpdateBo bo, Long userId) {
        if (bo == null || bo.getId() == null) {
            throw new ServiceException("音乐ID不能为空");
        }
        Music music = musicMapper.selectOneById(bo.getId());
        if (music == null) {
            throw new ServiceException("作品不存在");
        }
        if (!userId.equals(music.getCreatorId())) {
            throw new ServiceException("无权修改该作品");
        }
        if (!AUDIT_PENDING.equals(music.getAuditStatus()) && !AUDIT_REJECT.equals(music.getAuditStatus())) {
            throw new ServiceException("当前状态不允许修改");
        }
        Music update = new Music();
        BeanUtils.copyProperties(music, update);
        update.setTitle(bo.getTitle());
        update.setSubtitle(bo.getSubtitle());
        update.setIsOriginal(bo.getIsOriginal());
        update.setCopyrightInfo(bo.getCopyrightInfo());
        update.setRemark(bo.getRemark());
        if (CollUtil.isNotEmpty(bo.getOriginalInfoList())) {
            update.setOriginalData(JSON.toJSONString(bo.getOriginalInfoList()));
        }
        update.setUpdateBy(userId);
        update.setUpdateTime(new Date());
        musicMapper.update(update, false);

        originalMapper.deleteByQuery(QueryWrapper.create().where(MUSIC_ORIGINAL.MUSIC_ID.eq(bo.getId())));
        if (CollUtil.isNotEmpty(bo.getOriginalInfoList())) {
            int sortOrder = 0;
            for (MusicSubmitBo.OriginalInfoBo info : bo.getOriginalInfoList()) {
                MusicOriginal original = new MusicOriginal();
                original.setMusicId(bo.getId());
                original.setOriginalTitle(info.getOriginalTitle());
                original.setOriginalAuthor(info.getOriginalAuthor());
                original.setOriginalAlbum(info.getOriginalAlbum());
                original.setOriginalLink(info.getOriginalLink());
                original.setSourceType(info.getSourceType());
                original.setRelationType(info.getRelationType());
                original.setSortOrder((long) sortOrder++);
                original.setCreateBy(userId);
                original.setCreateTime(new Date());
                originalMapper.insert(original);
            }
        }

        tagRelMapper.deleteByQuery(QueryWrapper.create().where(MUSIC_TAG_REL.MUSIC_ID.eq(bo.getId())));
        if (CollUtil.isNotEmpty(bo.getTagIds())) {
            int weight = bo.getTagIds().size();
            for (Long tagId : bo.getTagIds()) {
                MusicTagRel rel = new MusicTagRel();
                rel.setMusicId(bo.getId());
                rel.setTagId(tagId);
                rel.setTagWeight((long) weight--);
                rel.setSource("user");
                rel.setCreateBy(userId);
                rel.setCreateTime(new Date());
                tagRelMapper.insert(rel);
            }
        }

        if (CollUtil.isNotEmpty(bo.getResourceIds())) {
            for (Long resourceId : bo.getResourceIds()) {
                MusicResource updateResource = new MusicResource();
                updateResource.setId(resourceId);
                updateResource.setMusicId(bo.getId());
                resourceMapper.update(updateResource, false);
            }
        }

        if (CollUtil.isNotEmpty(bo.getTagProposals())) {
            for (MusicSubmitBo.TagProposalBo proposal : bo.getTagProposals()) {
                TagProposal tagProposal = new TagProposal();
                tagProposal.setUserId(userId);
                tagProposal.setMusicId(bo.getId());
                tagProposal.setTagName(proposal.getTagName());
                tagProposal.setTagType(proposal.getTagType());
                tagProposal.setDescription(proposal.getDescription());
                tagProposal.setStatus("0");
                tagProposal.setCreateBy(userId);
                tagProposal.setCreateTime(new Date());
                tagProposalMapper.insert(tagProposal);
            }
        }

        return true;
    }

    @Override
    public Boolean deleteMusic(Long id, Long userId) {
        Music music = musicMapper.selectOneById(id);
        if (music == null) {
            return false;
        }
        if (!userId.equals(music.getCreatorId())) {
            throw new ServiceException("无权删除该作品");
        }
        return musicMapper.deleteById(id) > 0;
    }

    private QueryWrapper buildQueryWrapper(MusicBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC.TITLE.like(bo.getTitle())
                    .and(MUSIC.SUBTITLE.eq(bo.getSubtitle()))
                    .and(MUSIC.ORIGINAL_TITLE.eq(bo.getOriginalTitle()))
                    .and(MUSIC.CREATOR_ID.eq(bo.getCreatorId()))
                    .and(MUSIC.AUDIT_STATUS.eq(bo.getAuditStatus()))
                    .and(MUSIC.RESOURCE_STATUS.eq(bo.getResourceStatus()))
            )
            .orderBy(MUSIC.ID.desc());
        if (params.get("beginCreateTime") != null && params.get("endCreateTime") != null) {
            queryWrapper.and("create_time between ? and ?", params.get("beginCreateTime"), params.get("endCreateTime"));
        }
        return queryWrapper;
    }
}
