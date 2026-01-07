package org.dromara.system.service.music.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.service.music.IMusicOriginalService;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.MusicOriginalBo;
import org.dromara.system.domain.vo.MusicOriginalVo;
import org.dromara.system.domain.MusicOriginal;
import org.dromara.system.mapper.MusicOriginalMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicOriginalTableDef.MUSIC_ORIGINAL;

/**
 * 音乐原曲关联Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicOriginalServiceImpl implements IMusicOriginalService {

    private final MusicOriginalMapper baseMapper;

    /**
     * 查询音乐原曲关联
     *
     * @param id 主键
     * @return 音乐原曲关联
     */
    @Override
    public MusicOriginalVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询音乐原曲关联列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐原曲关联分页列表
     */
    @Override
    public TableDataInfo<MusicOriginalVo> queryPageList(MusicOriginalBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicOriginalVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的音乐原曲关联列表
     *
     * @param bo 查询条件
     * @return 音乐原曲关联列表
     */
    @Override
    public List<MusicOriginalVo> queryList(MusicOriginalBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicOriginalBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_ORIGINAL.MUSIC_ID.eq(bo.getMusicId())
                .and(MUSIC_ORIGINAL.ORIGINAL_TITLE.eq(bo.getOriginalTitle()))
                .and(MUSIC_ORIGINAL.ORIGINAL_AUTHOR.eq(bo.getOriginalAuthor()))
                .and(MUSIC_ORIGINAL.ORIGINAL_ALBUM.eq(bo.getOriginalAlbum()))
                .and(MUSIC_ORIGINAL.ORIGINAL_LINK.eq(bo.getOriginalLink()))
                .and(MUSIC_ORIGINAL.SOURCE_TYPE.eq(bo.getSourceType()))
                .and(MUSIC_ORIGINAL.RELATION_TYPE.eq(bo.getRelationType()))
                .and(MUSIC_ORIGINAL.SORT_ORDER.eq(bo.getSortOrder()))
                .and(MUSIC_ORIGINAL.LINK_STATUS.eq(bo.getLinkStatus()))
                .and(MUSIC_ORIGINAL.LAST_CHECK_TIME.eq(bo.getLastCheckTime()))
            )
            .orderBy(MUSIC_ORIGINAL.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增音乐原曲关联
     *
     * @param bo 音乐原曲关联
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicOriginalBo bo) {
        MusicOriginal add = MapstructUtils.convert(bo, MusicOriginal.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改音乐原曲关联
     *
     * @param bo 音乐原曲关联
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicOriginalBo bo) {
        MusicOriginal update = MapstructUtils.convert(bo, MusicOriginal.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicOriginal entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除音乐原曲关联信息
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
