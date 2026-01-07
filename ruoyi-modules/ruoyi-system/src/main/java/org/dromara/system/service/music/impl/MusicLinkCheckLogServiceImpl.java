package org.dromara.system.service.music.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.system.service.music.IMusicLinkCheckLogService;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.MusicLinkCheckLogBo;
import org.dromara.system.domain.vo.MusicLinkCheckLogVo;
import org.dromara.system.domain.MusicLinkCheckLog;
import org.dromara.system.mapper.MusicLinkCheckLogMapper;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicLinkCheckLogTableDef.MUSIC_LINK_CHECK_LOG;

/**
 * 链接检测日志Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicLinkCheckLogServiceImpl implements IMusicLinkCheckLogService {

    private final MusicLinkCheckLogMapper baseMapper;

    /**
     * 查询链接检测日志
     *
     * @param id 主键
     * @return 链接检测日志
     */
    @Override
    public MusicLinkCheckLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询链接检测日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 链接检测日志分页列表
     */
    @Override
    public TableDataInfo<MusicLinkCheckLogVo> queryPageList(MusicLinkCheckLogBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicLinkCheckLogVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的链接检测日志列表
     *
     * @param bo 查询条件
     * @return 链接检测日志列表
     */
    @Override
    public List<MusicLinkCheckLogVo> queryList(MusicLinkCheckLogBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicLinkCheckLogBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_LINK_CHECK_LOG.RESOURCE_ID.eq(bo.getResourceId())
                .and(MUSIC_LINK_CHECK_LOG.RESOURCE_TYPE.eq(bo.getResourceType()))
                .and(MUSIC_LINK_CHECK_LOG.MUSIC_ID.eq(bo.getMusicId()))
                .and(MUSIC_LINK_CHECK_LOG.CHECK_URL.eq(bo.getCheckUrl()))
                .and(MUSIC_LINK_CHECK_LOG.CHECK_RESULT.eq(bo.getCheckResult()))
                .and(MUSIC_LINK_CHECK_LOG.HTTP_STATUS.eq(bo.getHttpStatus()))
                .and(MUSIC_LINK_CHECK_LOG.RESPONSE_TIME.eq(bo.getResponseTime()))
                .and(MUSIC_LINK_CHECK_LOG.ERROR_MESSAGE.eq(bo.getErrorMessage()))
                .and(MUSIC_LINK_CHECK_LOG.CHECK_TIME.eq(bo.getCheckTime()))
                .and(MUSIC_LINK_CHECK_LOG.CHECK_BATCH.eq(bo.getCheckBatch()))
            )
            .orderBy(MUSIC_LINK_CHECK_LOG.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增链接检测日志
     *
     * @param bo 链接检测日志
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicLinkCheckLogBo bo) {
        MusicLinkCheckLog add = MapstructUtils.convert(bo, MusicLinkCheckLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改链接检测日志
     *
     * @param bo 链接检测日志
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicLinkCheckLogBo bo) {
        MusicLinkCheckLog update = MapstructUtils.convert(bo, MusicLinkCheckLog.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicLinkCheckLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除链接检测日志信息
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
