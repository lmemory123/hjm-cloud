package org.dromara.music.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.music.domain.bo.MusicSubmitBo;
import org.dromara.music.service.IPortalDraftService;
import org.dromara.music.domain.*;
import org.dromara.music.domain.vo.MusicDraftVo;
import org.dromara.music.domain.vo.TagVo;
import org.dromara.music.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.dromara.music.domain.table.MusicDraftTableDef.MUSIC_DRAFT;
import static org.dromara.music.domain.table.TagTableDef.TAG;

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
    private static final String PUBLIC_HIDDEN = "0";
    private static final String RESOURCE_STATUS_NORMAL = "0";
    private static final String PROCESS_STATUS_UPLOADED = "uploaded";
    private final MusicDraftMapper draftMapper;
    private final MusicMapper musicMapper;
    private final MusicOriginalMapper originalMapper;
    private final MusicTagRelMapper tagRelMapper;
    private final MusicResourceMapper resourceMapper;
    private final MusicAuditLogMapper auditLogMapper;
    private final TagMapper tagMapper;
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
        vo.setCreateTime(draft.getCreateTime());
        vo.setUpdateTime(draft.getUpdateTime());
        return vo;
    }

    @Override
    public List<MusicDraftVo> listDrafts(Long userId) {
        List<MusicDraft> drafts = draftMapper.selectListByQuery(
            QueryWrapper.create().where(MUSIC_DRAFT.USER_ID.eq(userId)).orderBy(MUSIC_DRAFT.UPDATE_TIME.desc())
        );
        List<MusicDraftVo> vos = new ArrayList<>(drafts.size());
        for (MusicDraft draft : drafts) {
            MusicDraftVo vo = new MusicDraftVo();
            vo.setId(draft.getId());
            vo.setUserId(draft.getUserId());
            vo.setContent(draft.getContent());
            vo.setCreateTime(draft.getCreateTime());
            vo.setUpdateTime(draft.getUpdateTime());
            vos.add(vo);
        }
        return vos;
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
        music.setIsPublic(PUBLIC_HIDDEN);
        music.setResourceStatus(RESOURCE_STATUS_NORMAL);
        music.setPlayCount(0L);
        music.setLikeCount(0L);
        music.setCollectCount(0L);
        music.setCommentCount(0L);
        music.setShareCount(0L);
        music.setDownloadCount(0L);
        music.setCreateBy(bo.getUserId());
        music.setCreateTime(new Date());

        // 保存原曲信息快照
        if (CollUtil.isNotEmpty(bo.getOriginalInfoList())) {
            music.setOriginalData(JSON.toJSONString(bo.getOriginalInfoList()));
        }

        musicMapper.insert(music);
        Long musicId = music.getId();
        List<Map<String, Object>> resourceSnapshot = new ArrayList<>();

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
        if (CollUtil.isNotEmpty(bo.getUploadedResources())) {
            int sortOrder = 0;
            for (MusicSubmitBo.ResourceInfoBo info : bo.getUploadedResources()) {
                MusicResource resource = new MusicResource();
                resource.setMusicId(musicId);
                resource.setResType(resolveResourceType(info));
                resource.setQualityTier("original");
                resource.setSourceType("upload");
                resource.setSourceId(info.getId());
                resource.setUrl(info.getUrl());
                resource.setCdnUrl(info.getUrl());
                resource.setFileName(info.getName());
                resource.setFileFormat(resolveFileFormat(info));
                resource.setFileSize(info.getSize());
                resource.setAccessCount(0L);
                resource.setIsPrimary(isPrimaryResource(info, bo.getCoverResourceId()) ? "1" : "0");
                resource.setStatus(RESOURCE_STATUS_NORMAL);
                resource.setProcessStatus(PROCESS_STATUS_UPLOADED);
                resource.setRetryCount(0L);
                resource.setFailCount(0L);
                resource.setSortOrder((long) sortOrder++);
                resource.setCreateBy(bo.getUserId());
                resource.setCreateTime(new Date());
                resourceMapper.insert(resource);
                resourceSnapshot.add(toResourceSnapshot(resource, info));
            }
        }
        if (CollUtil.isNotEmpty(bo.getResourceIds())) {
            for (Long resourceId : bo.getResourceIds()) {
                MusicResource resource = resourceMapper.selectOneById(resourceId);
                if (resource != null) {
                    MusicResource update = new MusicResource();
                    update.setId(resourceId);
                    update.setMusicId(musicId);
                    resourceMapper.update(update, false);
                    resourceSnapshot.add(toResourceSnapshot(resource, null));
                }
            }
        }

        // 4. 关联标签
        List<Map<String, Object>> tagSnapshot = buildTagSnapshot(bo.getTagIds());
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

        Music update = new Music();
        update.setId(musicId);
        update.setOriginalTitle(resolveOriginalTitle(bo));
        if (!resourceSnapshot.isEmpty()) {
            update.setResourceData(JSON.toJSONString(resourceSnapshot));
        }
        if (!tagSnapshot.isEmpty()) {
            update.setTagsSnapshot(JSON.toJSONString(tagSnapshot));
        }
        update.setUpdateBy(bo.getUserId());
        update.setUpdateTime(new Date());
        musicMapper.update(update, false);

        MusicAuditLog log = new MusicAuditLog();
        log.setMusicId(musicId);
        log.setTargetType("music");
        log.setAction(0L);
        log.setOldStatus(null);
        log.setNewStatus(0L);
        log.setReason("用户提交审核");
        log.setSnapshot(JSON.toJSONString(bo));
        log.setOperatorId(bo.getUserId());
        log.setCreateTime(new Date());
        auditLogMapper.insert(log);

        return musicId;
    }

    private String resolveOriginalTitle(MusicSubmitBo bo) {
        if (CollUtil.isEmpty(bo.getOriginalInfoList())) {
            return null;
        }
        for (MusicSubmitBo.OriginalInfoBo info : bo.getOriginalInfoList()) {
            if (info.getOriginalTitle() != null && !info.getOriginalTitle().isBlank()) {
                return info.getOriginalTitle();
            }
        }
        return null;
    }

    private String resolveResourceType(MusicSubmitBo.ResourceInfoBo info) {
        if (info == null) {
            return "file";
        }
        if ("image".equalsIgnoreCase(info.getKind()) || startsWith(info.getContentType(), "image/")) {
            return "cover";
        }
        if ("audio".equalsIgnoreCase(info.getKind()) || startsWith(info.getContentType(), "audio/")) {
            return "audio";
        }
        return "file";
    }

    private boolean isPrimaryResource(MusicSubmitBo.ResourceInfoBo info, Long coverResourceId) {
        if (info == null) {
            return false;
        }
        if (coverResourceId != null && String.valueOf(coverResourceId).equals(info.getId())) {
            return true;
        }
        return "audio".equals(resolveResourceType(info));
    }

    private boolean startsWith(String value, String prefix) {
        return value != null && value.startsWith(prefix);
    }

    private String resolveFileFormat(MusicSubmitBo.ResourceInfoBo info) {
        if (info == null || info.getName() == null || !info.getName().contains(".")) {
            return null;
        }
        return info.getName().substring(info.getName().lastIndexOf('.') + 1).toLowerCase();
    }

    private Map<String, Object> toResourceSnapshot(MusicResource resource, MusicSubmitBo.ResourceInfoBo info) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("id", resource.getId());
        snapshot.put("sourceId", resource.getSourceId());
        snapshot.put("name", firstNonBlank(resource.getFileName(), info == null ? null : info.getName()));
        snapshot.put("fileName", firstNonBlank(resource.getFileName(), info == null ? null : info.getName()));
        snapshot.put("url", firstNonBlank(resource.getUrl(), info == null ? null : info.getUrl()));
        snapshot.put("type", resource.getResType());
        snapshot.put("contentType", info == null ? null : info.getContentType());
        snapshot.put("fileSize", resource.getFileSize());
        snapshot.put("isPrimary", resource.getIsPrimary());
        return snapshot;
    }

    private List<Map<String, Object>> buildTagSnapshot(List<Long> tagIds) {
        if (CollUtil.isEmpty(tagIds)) {
            return new ArrayList<>();
        }
        List<TagVo> tags = tagMapper.selectVoList(QueryWrapper.create().where(TAG.ID.in(tagIds)));
        List<Map<String, Object>> result = new ArrayList<>(tags.size());
        for (TagVo tag : tags) {
            Map<String, Object> snapshot = new LinkedHashMap<>();
            snapshot.put("id", tag.getId());
            snapshot.put("name", tag.getName());
            snapshot.put("type", tag.getType());
            snapshot.put("color", tag.getColor());
            result.add(snapshot);
        }
        return result;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
