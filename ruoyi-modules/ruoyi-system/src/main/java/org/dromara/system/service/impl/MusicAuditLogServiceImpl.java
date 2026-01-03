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
import org.dromara.system.domain.bo.MusicAuditLogBo;
import org.dromara.system.domain.vo.MusicAuditLogVo;
import org.dromara.system.domain.MusicAuditLog;
import org.dromara.system.mapper.MusicAuditLogMapper;
import org.dromara.system.service.IMusicAuditLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicAuditLogTableDef.MUSIC_AUDIT_LOG;

/**
 * 音乐审核流水日志Service业务层处理
 *
 * @author momao
 * @date 2025-12-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicAuditLogServiceImpl implements IMusicAuditLogService {

    private final MusicAuditLogMapper baseMapper;

    /**
     * 查询音乐审核流水日志
     *
     * @param id 主键
     * @return 音乐审核流水日志
     */
    @Override
    public MusicAuditLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询音乐审核流水日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐审核流水日志分页列表
     */
    @Override
    public TableDataInfo<MusicAuditLogVo> queryPageList(MusicAuditLogBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicAuditLogVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的音乐审核流水日志列表
     *
     * @param bo 查询条件
     * @return 音乐审核流水日志列表
     */
    @Override
    public List<MusicAuditLogVo> queryList(MusicAuditLogBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicAuditLogBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_AUDIT_LOG.MUSIC_ID.eq(bo.getMusicId())
                .and(MUSIC_AUDIT_LOG.TARGET_TYPE.eq(bo.getTargetType()))
                .and(MUSIC_AUDIT_LOG.ACTION.eq(bo.getAction()))
                .and(MUSIC_AUDIT_LOG.OLD_STATUS.eq(bo.getOldStatus()))
                .and(MUSIC_AUDIT_LOG.NEW_STATUS.eq(bo.getNewStatus()))
                .and(MUSIC_AUDIT_LOG.REASON.eq(bo.getReason()))
                .and(MUSIC_AUDIT_LOG.SNAPSHOT.eq(bo.getSnapshot()))
                .and(MUSIC_AUDIT_LOG.OPERATOR_ID.eq(bo.getOperatorId()))
            )
            .orderBy(MUSIC_AUDIT_LOG.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增音乐审核流水日志
     *
     * @param bo 音乐审核流水日志
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicAuditLogBo bo) {
        MusicAuditLog add = MapstructUtils.convert(bo, MusicAuditLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改音乐审核流水日志
     *
     * @param bo 音乐审核流水日志
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicAuditLogBo bo) {
        MusicAuditLog update = MapstructUtils.convert(bo, MusicAuditLog.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicAuditLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除音乐审核流水日志信息
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
