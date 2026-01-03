package org.dromara.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.system.domain.SysLogininfor;
import org.dromara.system.domain.bo.SysLogininforBo;
import org.dromara.system.domain.vo.SysLogininforVo;
import org.dromara.system.mapper.SysLogininforMapper;
import org.dromara.system.service.ISysLogininforService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysLogininforServiceImpl implements ISysLogininforService {

    private final SysLogininforMapper baseMapper;

    /**
     * 分页查询登录日志列表
     *
     * @param logininfor 查询条件
     * @param pageQuery  分页参数
     * @return 登录日志分页列表
     */
    @Override
    public TableDataInfo<SysLogininforVo> selectPageLogininforList(SysLogininforBo logininfor, PageQuery pageQuery) {
        Map<String, Object> params = logininfor.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .like(SysLogininfor::getIpaddr, logininfor.getIpaddr())
            .eq(SysLogininfor::getStatus, logininfor.getStatus())
            .like(SysLogininfor::getUserName, logininfor.getUserName());
        // 处理日期范围
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            queryWrapper.between(SysLogininfor::getLoginTime, params.get("beginTime"), params.get("endTime"));
        }
        if (StringUtils.isBlank(pageQuery.getOrderByColumn())) {
            queryWrapper.orderBy(SysLogininfor::getInfoId, false);
        }
        Page<SysLogininforVo> page = baseMapper.selectVoPage(pageQuery.build(), queryWrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 新增系统登录日志
     *
     * @param bo 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininforBo bo) {
        SysLogininfor logininfor = MapstructUtils.convert(bo, SysLogininfor.class);
        logininfor.setLoginTime(new Date());
        baseMapper.insert(logininfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininforVo> selectLogininforList(SysLogininforBo logininfor) {
        Map<String, Object> params = logininfor.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .like(SysLogininfor::getIpaddr, logininfor.getIpaddr())
            .eq(SysLogininfor::getStatus, logininfor.getStatus())
            .like(SysLogininfor::getUserName, logininfor.getUserName())
            .orderBy(SysLogininfor::getInfoId, false);
        // 处理日期范围
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            queryWrapper.between(SysLogininfor::getLoginTime, params.get("beginTime"), params.get("endTime"));
        }
        return baseMapper.selectVoList(queryWrapper);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds) {
        return baseMapper.deleteBatchByIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        baseMapper.deleteByQuery(QueryWrapper.create());
    }
}
