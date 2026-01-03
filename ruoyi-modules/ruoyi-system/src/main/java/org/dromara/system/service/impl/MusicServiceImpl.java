package org.dromara.system.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.MusicBo;
import org.dromara.system.domain.vo.MusicVo;
import org.dromara.system.domain.Music;
import org.dromara.system.mapper.MusicMapper;
import org.dromara.system.service.IMusicService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicTableDef.MUSIC;

/**
 * 音乐曲库主Service业务层处理
 *
 * @author momao
 * @date 2025-12-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicServiceImpl implements IMusicService {

    private final MusicMapper baseMapper;

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
}
