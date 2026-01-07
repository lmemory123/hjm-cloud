package org.dromara.system.service.music.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.service.music.IMusicActionService;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.MusicActionBo;
import org.dromara.system.domain.vo.MusicActionVo;
import org.dromara.system.domain.MusicAction;
import org.dromara.system.mapper.MusicActionMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicActionTableDef.MUSIC_ACTION;

/**
 * 音乐互动动作Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicActionServiceImpl implements IMusicActionService {

    private final MusicActionMapper baseMapper;

    /**
     * 查询音乐互动动作
     *
     * @param id 主键
     * @return 音乐互动动作
     */
    @Override
    public MusicActionVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询音乐互动动作列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐互动动作分页列表
     */
    @Override
    public TableDataInfo<MusicActionVo> queryPageList(MusicActionBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicActionVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的音乐互动动作列表
     *
     * @param bo 查询条件
     * @return 音乐互动动作列表
     */
    @Override
    public List<MusicActionVo> queryList(MusicActionBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicActionBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_ACTION.USER_ID.eq(bo.getUserId())
                .and(MUSIC_ACTION.TARGET_ID.eq(bo.getTargetId()))
                .and(MUSIC_ACTION.TARGET_TYPE.eq(bo.getTargetType()))
                .and(MUSIC_ACTION.ACTION.eq(bo.getAction()))
            )
            .orderBy(MUSIC_ACTION.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增音乐互动动作
     *
     * @param bo 音乐互动动作
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicActionBo bo) {
        MusicAction add = MapstructUtils.convert(bo, MusicAction.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改音乐互动动作
     *
     * @param bo 音乐互动动作
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicActionBo bo) {
        MusicAction update = MapstructUtils.convert(bo, MusicAction.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicAction entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除音乐互动动作信息
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
