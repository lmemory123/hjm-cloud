package org.dromara.music.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.Music;
import org.dromara.music.domain.MusicAuditLog;
import org.dromara.music.domain.bo.MusicAuditBo;
import org.dromara.music.domain.bo.MusicBo;
import org.dromara.music.domain.vo.MusicAuditLogVo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicOriginalVo;
import org.dromara.music.domain.vo.MusicResourceVo;
import org.dromara.music.domain.vo.MusicTagRelVo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.domain.vo.TagVo;
import org.dromara.music.mapper.MusicAuditLogMapper;
import org.dromara.music.mapper.MusicMapper;
import org.dromara.music.mapper.MusicOriginalMapper;
import org.dromara.music.mapper.MusicResourceMapper;
import org.dromara.music.mapper.MusicTagRelMapper;
import org.dromara.music.mapper.TagMapper;
import org.dromara.music.service.IMusicService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.dromara.music.domain.table.MusicTableDef.MUSIC;
import static org.dromara.music.domain.table.MusicOriginalTableDef.MUSIC_ORIGINAL;
import static org.dromara.music.domain.table.MusicResourceTableDef.MUSIC_RESOURCE;
import static org.dromara.music.domain.table.MusicTagRelTableDef.MUSIC_TAG_REL;
import static org.dromara.music.domain.table.MusicAuditLogTableDef.MUSIC_AUDIT_LOG;
import static org.dromara.music.domain.table.TagTableDef.TAG;

/**
 * 音乐曲库主Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicServiceImpl implements IMusicService {

    private final MusicMapper baseMapper;
    private final MusicOriginalMapper originalMapper;
    private final MusicResourceMapper resourceMapper;
    private final MusicTagRelMapper tagRelMapper;
    private final TagMapper tagMapper;
    private final MusicAuditLogMapper auditLogMapper;

    private static final String AUDIT_PENDING = "0";
    private static final String AUDIT_PASS = "1";
    private static final String AUDIT_REJECT = "2";
    private static final String AUDIT_OFFLINE = "3";

    /**
     * 查询音乐曲库主
     *
     * @param id 主键
     * @return 音乐曲库主
     */
    @Override
    public MusicVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    @Override
    public MusicDetailVo queryDetailById(Long id) {
        MusicVo musicVo = baseMapper.selectVoById(id);
        if (musicVo == null) {
            return null;
        }
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

    /**
     * 分页查询音乐曲库主列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐曲库主分页列表
     */
    @Override
    public TableDataInfo<MusicVo> queryPageList(MusicBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的音乐曲库主列表
     *
     * @param bo 查询条件
     * @return 音乐曲库主列表
     */
    @Override
    public List<MusicVo> queryList(MusicBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC.TITLE.eq(bo.getTitle())
                .and(MUSIC.SUBTITLE.eq(bo.getSubtitle()))
                .and(MUSIC.ORIGINAL_TITLE.eq(bo.getOriginalTitle()))
                .and(MUSIC.CREATOR_ID.eq(bo.getCreatorId()))
                .and(MUSIC.CREATOR_NAME.like(bo.getCreatorName()))
                .and(MUSIC.CREATOR_LINK.eq(bo.getCreatorLink()))
                .and(MUSIC.PRODUCER_MARK.eq(bo.getProducerMark()))
                .and(MUSIC.DURATION.eq(bo.getDuration()))
                .and(MUSIC.BPM.eq(bo.getBpm()))
                .and(MUSIC.PUBLISH_TIME.eq(bo.getPublishTime()))
                .and(MUSIC.PLAY_COUNT.eq(bo.getPlayCount()))
                .and(MUSIC.LIKE_COUNT.eq(bo.getLikeCount()))
                .and(MUSIC.COLLECT_COUNT.eq(bo.getCollectCount()))
                .and(MUSIC.COMMENT_COUNT.eq(bo.getCommentCount()))
                .and(MUSIC.SHARE_COUNT.eq(bo.getShareCount()))
                .and(MUSIC.DOWNLOAD_COUNT.eq(bo.getDownloadCount()))
                .and(MUSIC.ORIGINAL_DATA.eq(bo.getOriginalData()))
                .and(MUSIC.RESOURCE_DATA.eq(bo.getResourceData()))
                .and(MUSIC.TAGS_SNAPSHOT.eq(bo.getTagsSnapshot()))
                .and(MUSIC.EXTEND_DATA.eq(bo.getExtendData()))
                .and(MUSIC.AUDIT_STATUS.eq(bo.getAuditStatus()))
                .and(MUSIC.IS_PUBLIC.eq(bo.getIsPublic()))
                .and(MUSIC.IS_ORIGINAL.eq(bo.getIsOriginal()))
                .and(MUSIC.RESOURCE_STATUS.eq(bo.getResourceStatus()))
                .and(MUSIC.COPYRIGHT_INFO.eq(bo.getCopyrightInfo()))
            )
            .orderBy(MUSIC.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增音乐曲库主
     *
     * @param bo 音乐曲库主
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicBo bo) {
        Music add = MapstructUtils.convert(bo, Music.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改音乐曲库主
     *
     * @param bo 音乐曲库主
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicBo bo) {
        Music update = MapstructUtils.convert(bo, Music.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Music entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除音乐曲库主信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchByIds(ids) > 0;
    }

    @Override
    public Boolean audit(MusicAuditBo bo, Long operatorId) {
        if (bo == null || bo.getMusicId() == null || bo.getAction() == null) {
            throw new ServiceException("审核参数不能为空");
        }
        Music music = baseMapper.selectOneById(bo.getMusicId());
        if (music == null) {
            throw new ServiceException("音乐不存在");
        }
        String newStatus = resolveAuditStatus(bo.getAction());
        if ((AUDIT_REJECT.equals(newStatus) || AUDIT_OFFLINE.equals(newStatus)) && StringUtils.isBlank(bo.getReason())) {
            throw new ServiceException("拒绝/下架原因不能为空");
        }
        Music update = new Music();
        update.setId(music.getId());
        update.setAuditStatus(newStatus);
        update.setUpdateBy(operatorId);
        update.setUpdateTime(new Date());
        if (AUDIT_PASS.equals(newStatus)) {
            update.setPublishTime(new Date());
        }
        baseMapper.update(update);

        MusicAuditLog log = new MusicAuditLog();
        log.setMusicId(music.getId());
        log.setTargetType("music");
        log.setAction(bo.getAction().longValue());
        log.setOldStatus(parseStatus(music.getAuditStatus()));
        log.setNewStatus(parseStatus(newStatus));
        log.setReason(bo.getReason());
        log.setOperatorId(operatorId);
        log.setCreateTime(new Date());
        auditLogMapper.insert(log);
        return true;
    }

    @Override
    public Boolean batchPass(Collection<Long> ids, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("音乐ID不能为空");
        }
        for (Long id : ids) {
            MusicAuditBo bo = new MusicAuditBo();
            bo.setMusicId(id);
            bo.setAction(1);
            audit(bo, operatorId);
        }
        return true;
    }

    @Override
    public Boolean batchOffline(Collection<Long> ids, String reason, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("音乐ID不能为空");
        }
        if (StringUtils.isBlank(reason)) {
            throw new ServiceException("下架原因不能为空");
        }
        for (Long id : ids) {
            MusicAuditBo bo = new MusicAuditBo();
            bo.setMusicId(id);
            bo.setAction(3);
            bo.setReason(reason);
            audit(bo, operatorId);
        }
        return true;
    }

    private String resolveAuditStatus(Integer action) {
        if (action == null) {
            throw new ServiceException("审核动作不能为空");
        }
        switch (action) {
            case 1:
                return AUDIT_PASS;
            case 2:
                return AUDIT_REJECT;
            case 3:
                return AUDIT_OFFLINE;
            default:
                throw new ServiceException("非法审核动作");
        }
    }

    private Long parseStatus(String status) {
        if (StringUtils.isBlank(status)) {
            return null;
        }
        try {
            return Long.parseLong(status);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
