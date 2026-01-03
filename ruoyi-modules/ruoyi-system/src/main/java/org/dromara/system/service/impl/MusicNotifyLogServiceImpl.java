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
import org.dromara.system.domain.bo.MusicNotifyLogBo;
import org.dromara.system.domain.vo.MusicNotifyLogVo;
import org.dromara.system.domain.MusicNotifyLog;
import org.dromara.system.mapper.MusicNotifyLogMapper;
import org.dromara.system.service.IMusicNotifyLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicNotifyLogTableDef.MUSIC_NOTIFY_LOG;

/**
 * 音乐通知日志Service业务层处理
 *
 * @author momao
 * @date 2025-12-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicNotifyLogServiceImpl implements IMusicNotifyLogService {

    private final MusicNotifyLogMapper baseMapper;

    /**
     * 查询音乐通知日志
     *
     * @param id 主键
     * @return 音乐通知日志
     */
    @Override
    public MusicNotifyLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询音乐通知日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐通知日志分页列表
     */
    @Override
    public TableDataInfo<MusicNotifyLogVo> queryPageList(MusicNotifyLogBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicNotifyLogVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的音乐通知日志列表
     *
     * @param bo 查询条件
     * @return 音乐通知日志列表
     */
    @Override
    public List<MusicNotifyLogVo> queryList(MusicNotifyLogBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicNotifyLogBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_NOTIFY_LOG.MUSIC_ID.eq(bo.getMusicId())
                .and(MUSIC_NOTIFY_LOG.USER_ID.eq(bo.getUserId()))
                .and(MUSIC_NOTIFY_LOG.NOTIFY_TYPE.eq(bo.getNotifyType()))
                .and(MUSIC_NOTIFY_LOG.NOTIFY_TITLE.eq(bo.getNotifyTitle()))
                .and(MUSIC_NOTIFY_LOG.NOTIFY_CONTENT.eq(bo.getNotifyContent()))
                .and(MUSIC_NOTIFY_LOG.SEND_CHANNEL.eq(bo.getSendChannel()))
                .and(MUSIC_NOTIFY_LOG.SEND_STATUS.eq(bo.getSendStatus()))
                .and(MUSIC_NOTIFY_LOG.SEND_TIME.eq(bo.getSendTime()))
                .and(MUSIC_NOTIFY_LOG.READ_STATUS.eq(bo.getReadStatus()))
                .and(MUSIC_NOTIFY_LOG.READ_TIME.eq(bo.getReadTime()))
            )
            .orderBy(MUSIC_NOTIFY_LOG.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增音乐通知日志
     *
     * @param bo 音乐通知日志
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicNotifyLogBo bo) {
        MusicNotifyLog add = MapstructUtils.convert(bo, MusicNotifyLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改音乐通知日志
     *
     * @param bo 音乐通知日志
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicNotifyLogBo bo) {
        MusicNotifyLog update = MapstructUtils.convert(bo, MusicNotifyLog.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicNotifyLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除音乐通知日志信息
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
