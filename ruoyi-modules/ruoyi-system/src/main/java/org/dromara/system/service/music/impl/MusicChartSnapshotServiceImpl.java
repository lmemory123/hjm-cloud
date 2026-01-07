package org.dromara.system.service.music.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.service.music.IMusicChartSnapshotService;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.MusicChartSnapshotBo;
import org.dromara.system.domain.vo.MusicChartSnapshotVo;
import org.dromara.system.domain.MusicChartSnapshot;
import org.dromara.system.mapper.MusicChartSnapshotMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicChartSnapshotTableDef.MUSIC_CHART_SNAPSHOT;

/**
 * 榜单快照Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicChartSnapshotServiceImpl implements IMusicChartSnapshotService {

    private final MusicChartSnapshotMapper baseMapper;

    /**
     * 查询榜单快照
     *
     * @param id 主键
     * @return 榜单快照
     */
    @Override
    public MusicChartSnapshotVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询榜单快照列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 榜单快照分页列表
     */
    @Override
    public TableDataInfo<MusicChartSnapshotVo> queryPageList(MusicChartSnapshotBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicChartSnapshotVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的榜单快照列表
     *
     * @param bo 查询条件
     * @return 榜单快照列表
     */
    @Override
    public List<MusicChartSnapshotVo> queryList(MusicChartSnapshotBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicChartSnapshotBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_CHART_SNAPSHOT.CHART_TYPE.eq(bo.getChartType())
                .and(MUSIC_CHART_SNAPSHOT.PERIOD_KEY.eq(bo.getPeriodKey()))
                .and(MUSIC_CHART_SNAPSHOT.STATUS.eq(bo.getStatus()))
            )
            .orderBy(MUSIC_CHART_SNAPSHOT.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增榜单快照
     *
     * @param bo 榜单快照
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicChartSnapshotBo bo) {
        MusicChartSnapshot add = MapstructUtils.convert(bo, MusicChartSnapshot.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改榜单快照
     *
     * @param bo 榜单快照
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicChartSnapshotBo bo) {
        MusicChartSnapshot update = MapstructUtils.convert(bo, MusicChartSnapshot.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicChartSnapshot entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除榜单快照信息
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
