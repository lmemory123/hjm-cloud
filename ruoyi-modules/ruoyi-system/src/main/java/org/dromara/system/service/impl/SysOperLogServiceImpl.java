package org.dromara.system.service.impl;

import cn.hutool.v7.core.array.ArrayUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.system.domain.SysOperLog;
import org.dromara.system.domain.bo.SysOperLogBo;
import org.dromara.system.domain.vo.SysOperLogVo;
import org.dromara.system.mapper.SysOperLogMapper;
import org.dromara.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.dromara.system.domain.table.SysOperLogTableDef.SYS_OPER_LOG;

/**
 * 操作日志 服务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {

    private final SysOperLogMapper baseMapper;

    /**
     * 分页查询操作日志列表
     *
     * @param operLog   查询条件
     * @param pageQuery 分页参数
     * @return 操作日志分页列表
     */
    @Override
    public TableDataInfo<SysOperLogVo> selectPageOperLogList(SysOperLogBo operLog, PageQuery pageQuery) {
        QueryWrapper lqw = buildQueryWrapper(operLog);
        if (StringUtils.isBlank(pageQuery.getOrderByColumn())) {
            lqw.orderBy(SysOperLog::getOperId, false);
        }
        Page<SysOperLogVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    private QueryWrapper buildQueryWrapper(SysOperLogBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(SYS_OPER_LOG.OPER_IP.like(bo.getOperIp())
                .and(SYS_OPER_LOG.TITLE.like(bo.getTitle()))
                .and(SYS_OPER_LOG.BUSINESS_TYPE.eq(bo.getBusinessType()))
                .and(SYS_OPER_LOG.STATUS.eq(bo.getStatus()))
                .and(SYS_OPER_LOG.OPER_NAME.like(bo.getOperName()))
            );
        // 处理日期范围
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            queryWrapper.and(SYS_OPER_LOG.OPER_TIME.between(params.get("beginTime"), params.get("endTime")));
        }
        // 处理业务类型数组
        if (ArrayUtil.isNotEmpty(bo.getBusinessTypes())) {
            queryWrapper.and(SYS_OPER_LOG.BUSINESS_TYPE.in(Arrays.asList(bo.getBusinessTypes())));
        }
        return queryWrapper;
    }

    /**
     * 新增操作日志
     *
     * @param bo 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLogBo bo) {
        SysOperLog operLog = MapstructUtils.convert(bo, SysOperLog.class);
        operLog.setOperTime(new Date());
        baseMapper.insert(operLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLogVo> selectOperLogList(SysOperLogBo operLog) {
        QueryWrapper lqw = buildQueryWrapper(operLog);
        return baseMapper.selectVoList(lqw.orderBy(SysOperLog::getOperId, false));
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return baseMapper.deleteBatchByIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLogVo selectOperLogById(Long operId) {
        return baseMapper.selectVoById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        baseMapper.deleteByQuery(QueryWrapper.create());
    }
}
