package org.dromara.system.service.music.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.service.music.IMusicTagRelService;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.MusicTagRelBo;
import org.dromara.system.domain.vo.MusicTagRelVo;
import org.dromara.system.domain.MusicTagRel;
import org.dromara.system.mapper.MusicTagRelMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicTagRelTableDef.MUSIC_TAG_REL;

/**
 * 音乐标签关联Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicTagRelServiceImpl implements IMusicTagRelService {

    private final MusicTagRelMapper baseMapper;

    /**
     * 查询音乐标签关联
     *
     * @param id 主键
     * @return 音乐标签关联
     */
    @Override
    public MusicTagRelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询音乐标签关联列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐标签关联分页列表
     */
    @Override
    public TableDataInfo<MusicTagRelVo> queryPageList(MusicTagRelBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicTagRelVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的音乐标签关联列表
     *
     * @param bo 查询条件
     * @return 音乐标签关联列表
     */
    @Override
    public List<MusicTagRelVo> queryList(MusicTagRelBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicTagRelBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_TAG_REL.MUSIC_ID.eq(bo.getMusicId())
                .and(MUSIC_TAG_REL.TAG_ID.eq(bo.getTagId()))
                .and(MUSIC_TAG_REL.TAG_WEIGHT.eq(bo.getTagWeight()))
                .and(MUSIC_TAG_REL.IS_PRIMARY.eq(bo.getIsPrimary()))
                .and(MUSIC_TAG_REL.SOURCE.eq(bo.getSource()))
            )
            .orderBy(MUSIC_TAG_REL.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增音乐标签关联
     *
     * @param bo 音乐标签关联
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicTagRelBo bo) {
        MusicTagRel add = MapstructUtils.convert(bo, MusicTagRel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改音乐标签关联
     *
     * @param bo 音乐标签关联
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicTagRelBo bo) {
        MusicTagRel update = MapstructUtils.convert(bo, MusicTagRel.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicTagRel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除音乐标签关联信息
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
