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
import org.dromara.system.domain.bo.MusicResourceBo;
import org.dromara.system.domain.vo.MusicResourceVo;
import org.dromara.system.domain.MusicResource;
import org.dromara.system.mapper.MusicResourceMapper;
import org.dromara.system.service.IMusicResourceService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicResourceTableDef.MUSIC_RESOURCE;

/**
 * 音乐资源文件Service业务层处理
 *
 * @author momao
 * @date 2025-12-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicResourceServiceImpl implements IMusicResourceService {

    private final MusicResourceMapper baseMapper;

    /**
     * 查询音乐资源文件
     *
     * @param id 主键
     * @return 音乐资源文件
     */
    @Override
    public MusicResourceVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询音乐资源文件列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐资源文件分页列表
     */
    @Override
    public TableDataInfo<MusicResourceVo> queryPageList(MusicResourceBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicResourceVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的音乐资源文件列表
     *
     * @param bo 查询条件
     * @return 音乐资源文件列表
     */
    @Override
    public List<MusicResourceVo> queryList(MusicResourceBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicResourceBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_RESOURCE.MUSIC_ID.eq(bo.getMusicId())
                .and(MUSIC_RESOURCE.RES_TYPE.eq(bo.getResType()))
                .and(MUSIC_RESOURCE.QUALITY_TIER.eq(bo.getQualityTier()))
                .and(MUSIC_RESOURCE.SOURCE_TYPE.eq(bo.getSourceType()))
                .and(MUSIC_RESOURCE.SOURCE_URL.eq(bo.getSourceUrl()))
                .and(MUSIC_RESOURCE.SOURCE_ID.eq(bo.getSourceId()))
                .and(MUSIC_RESOURCE.URL.eq(bo.getUrl()))
                .and(MUSIC_RESOURCE.FILE_PATH.eq(bo.getFilePath()))
                .and(MUSIC_RESOURCE.FILE_NAME.like(bo.getFileName()))
                .and(MUSIC_RESOURCE.FILE_FORMAT.eq(bo.getFileFormat()))
                .and(MUSIC_RESOURCE.FILE_SIZE.eq(bo.getFileSize()))
                .and(MUSIC_RESOURCE.FILE_HASH.eq(bo.getFileHash()))
                .and(MUSIC_RESOURCE.SPEC_INFO.eq(bo.getSpecInfo()))
                .and(MUSIC_RESOURCE.CDN_URL.eq(bo.getCdnUrl()))
                .and(MUSIC_RESOURCE.ACCESS_COUNT.eq(bo.getAccessCount()))
                .and(MUSIC_RESOURCE.IS_PRIMARY.eq(bo.getIsPrimary()))
                .and(MUSIC_RESOURCE.STATUS.eq(bo.getStatus()))
                .and(MUSIC_RESOURCE.PROCESS_STATUS.eq(bo.getProcessStatus()))
                .and(MUSIC_RESOURCE.RETRY_COUNT.eq(bo.getRetryCount()))
                .and(MUSIC_RESOURCE.FAIL_COUNT.eq(bo.getFailCount()))
                .and(MUSIC_RESOURCE.FAIL_REASON.eq(bo.getFailReason()))
                .and(MUSIC_RESOURCE.LAST_CHECK_TIME.eq(bo.getLastCheckTime()))
                .and(MUSIC_RESOURCE.SORT_ORDER.eq(bo.getSortOrder()))
            )
            .orderBy(MUSIC_RESOURCE.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增音乐资源文件
     *
     * @param bo 音乐资源文件
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicResourceBo bo) {
        MusicResource add = MapstructUtils.convert(bo, MusicResource.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改音乐资源文件
     *
     * @param bo 音乐资源文件
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicResourceBo bo) {
        MusicResource update = MapstructUtils.convert(bo, MusicResource.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicResource entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除音乐资源文件信息
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
