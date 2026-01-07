package org.dromara.system.service.music.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.service.music.IMusicChartItemService;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.MusicChartItemBo;
import org.dromara.system.domain.vo.MusicChartItemVo;
import org.dromara.system.domain.MusicChartItem;
import org.dromara.system.mapper.MusicChartItemMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicChartItemTableDef.MUSIC_CHART_ITEM;

/**
 * 榜单明细Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicChartItemServiceImpl implements IMusicChartItemService {

    private final MusicChartItemMapper baseMapper;

    /**
     * 查询榜单明细
     *
     * @param id 主键
     * @return 榜单明细
     */
    @Override
    public MusicChartItemVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询榜单明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 榜单明细分页列表
     */
    @Override
    public TableDataInfo<MusicChartItemVo> queryPageList(MusicChartItemBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicChartItemVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的榜单明细列表
     *
     * @param bo 查询条件
     * @return 榜单明细列表
     */
    @Override
    public List<MusicChartItemVo> queryList(MusicChartItemBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicChartItemBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_CHART_ITEM.SNAPSHOT_ID.eq(bo.getSnapshotId())
                .and(MUSIC_CHART_ITEM.MUSIC_ID.eq(bo.getMusicId()))
                .and(MUSIC_CHART_ITEM.RANK_NO.eq(bo.getRankNo()))
                .and(MUSIC_CHART_ITEM.SCORE.eq(bo.getScore()))
                .and(MUSIC_CHART_ITEM.PLAY_COUNT.eq(bo.getPlayCount()))
                .and(MUSIC_CHART_ITEM.LIKE_COUNT.eq(bo.getLikeCount()))
            )
            .orderBy(MUSIC_CHART_ITEM.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增榜单明细
     *
     * @param bo 榜单明细
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicChartItemBo bo) {
        MusicChartItem add = MapstructUtils.convert(bo, MusicChartItem.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改榜单明细
     *
     * @param bo 榜单明细
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicChartItemBo bo) {
        MusicChartItem update = MapstructUtils.convert(bo, MusicChartItem.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicChartItem entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除榜单明细信息
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
