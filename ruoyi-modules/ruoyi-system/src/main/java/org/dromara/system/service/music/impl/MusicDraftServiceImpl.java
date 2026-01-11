package org.dromara.system.service.music.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.system.domain.MusicDraft;
import org.dromara.system.domain.bo.MusicDraftBo;
import org.dromara.system.domain.vo.MusicDraftVo;
import org.dromara.system.mapper.MusicDraftMapper;
import org.dromara.system.service.music.IMusicDraftService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.dromara.system.domain.table.MusicDraftTableDef.MUSIC_DRAFT;

/**
 * 投稿草稿Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicDraftServiceImpl implements IMusicDraftService {

    private final MusicDraftMapper baseMapper;

    /**
     * 查询投稿草稿
     *
     * @param id 主键
     * @return 投稿草稿
     */
    @Override
    public MusicDraftVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询投稿草稿列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 投稿草稿分页列表
     */
    @Override
    public TableDataInfo<MusicDraftVo> queryPageList(MusicDraftBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicDraftVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的投稿草稿列表
     *
     * @param bo 查询条件
     * @return 投稿草稿列表
     */
    @Override
    public List<MusicDraftVo> queryList(MusicDraftBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicDraftBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_DRAFT.USER_ID.eq(bo.getUserId())
                .and(MUSIC_DRAFT.CONTENT.eq(bo.getContent()))
            )
            .orderBy(MUSIC_DRAFT.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增投稿草稿
     *
     * @param bo 投稿草稿
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicDraftBo bo) {
        MusicDraft add = MapstructUtils.convert(bo, MusicDraft.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改投稿草稿
     *
     * @param bo 投稿草稿
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicDraftBo bo) {
        MusicDraft update = MapstructUtils.convert(bo, MusicDraft.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicDraft entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除投稿草稿信息
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
