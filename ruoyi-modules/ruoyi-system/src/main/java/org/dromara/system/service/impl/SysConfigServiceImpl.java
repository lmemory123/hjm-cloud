package org.dromara.system.service.impl;

import cn.hutool.v7.core.convert.ConvertUtil;
import cn.hutool.v7.core.util.ObjUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Strings;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.ObjectUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.redis.utils.CacheUtils;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.domain.SysConfig;
import org.dromara.system.domain.bo.SysConfigBo;
import org.dromara.system.domain.vo.SysConfigVo;
import org.dromara.system.mapper.SysConfigMapper;
import org.dromara.system.service.ISysConfigService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.dromara.system.domain.table.SysConfigTableDef.SYS_CONFIG;

/**
 * 参数配置 服务层实现
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysConfigServiceImpl implements ISysConfigService {

    private final SysConfigMapper baseMapper;

    /**
     * 分页查询参数配置列表
     *
     * @param config    查询条件
     * @param pageQuery 分页参数
     * @return 参数配置分页列表
     */
    @Override
    public TableDataInfo<SysConfigVo> selectPageConfigList(SysConfigBo config, PageQuery pageQuery) {
        QueryWrapper lqw = buildQueryWrapper(config);
        Page<SysConfigVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfigVo selectConfigById(Long configId) {
        return baseMapper.selectVoById(configId);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Cacheable(cacheNames = CacheNames.SYS_CONFIG, key = "#configKey")
    @Override
    public String selectConfigByKey(String configKey) {
        SysConfig retConfig = baseMapper.selectOneByQuery(QueryWrapper.create()
            .eq(SysConfig::getConfigKey, configKey));
        return ObjectUtils.notNullGetter(retConfig, SysConfig::getConfigValue, StringUtils.EMPTY);
    }

    /**
     * 获取注册开关
     * @param tenantId 租户id
     * @return true开启，false关闭
     */
    @Override
    public boolean selectRegisterEnabled(String tenantId) {
        String configValue = TenantHelper.dynamic(tenantId, () ->
            this.selectConfigByKey("sys.account.registerUser")
        );
        return ConvertUtil.toBoolean(configValue);
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfigVo> selectConfigList(SysConfigBo config) {
        QueryWrapper lqw = buildQueryWrapper(config);
        return baseMapper.selectVoList(lqw);
    }

    private QueryWrapper buildQueryWrapper(SysConfigBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(SYS_CONFIG.CONFIG_NAME.like(bo.getConfigName())
                .and(SYS_CONFIG.CONFIG_TYPE.eq(bo.getConfigType()))
                .and(SYS_CONFIG.CONFIG_KEY.like(bo.getConfigKey()))
            )
            .orderBy(SYS_CONFIG.CONFIG_ID.asc());
        // 处理日期范围
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            queryWrapper.and(SYS_CONFIG.CREATE_TIME.between(params.get("beginTime"), params.get("endTime")));
        }
        return queryWrapper;
    }

    /**
     * 新增参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    @CachePut(cacheNames = CacheNames.SYS_CONFIG, key = "#bo.configKey")
    @Override
    public String insertConfig(SysConfigBo bo) {
        SysConfig config = MapstructUtils.convert(bo, SysConfig.class);
        int row = baseMapper.insert(config);
        if (row > 0) {
            return config.getConfigValue();
        }
        throw new ServiceException("操作失败");
    }

    /**
     * 修改参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    @CachePut(cacheNames = CacheNames.SYS_CONFIG, key = "#bo.configKey")
    @Override
    public String updateConfig(SysConfigBo bo) {
        int row = 0;
        SysConfig config = MapstructUtils.convert(bo, SysConfig.class);
        if (config.getConfigId() != null) {
            SysConfig temp = baseMapper.selectOneById(config.getConfigId());
            if (!Strings.CS.equals(temp.getConfigKey(), config.getConfigKey())) {
                CacheUtils.evict(CacheNames.SYS_CONFIG, temp.getConfigKey());
            }
            row = baseMapper.update(config);
        } else {
            CacheUtils.evict(CacheNames.SYS_CONFIG, config.getConfigKey());
            row = baseMapper.updateByQuery(config, QueryWrapper.create()
                .eq(SysConfig::getConfigKey, config.getConfigKey()));
        }
        if (row > 0) {
            return config.getConfigValue();
        }
        throw new ServiceException("操作失败");
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(List<Long> configIds) {
        List<SysConfig> list = baseMapper.selectListByIds(configIds);
        list.forEach(config -> {
            if (StringUtils.equals(SystemConstants.YES, config.getConfigType())) {
                throw new ServiceException("内置参数【{}】不能删除", config.getConfigKey());
            }
            CacheUtils.evict(CacheNames.SYS_CONFIG, config.getConfigKey());
        });
        baseMapper.deleteBatchByIds(configIds);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        CacheUtils.clear(CacheNames.SYS_CONFIG);
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfigBo config) {
        boolean exist = baseMapper.selectCountByQuery(QueryWrapper.create()
            .eq(SysConfig::getConfigKey, config.getConfigKey())
            .ne(SysConfig::getConfigId, config.getConfigId(), ObjUtil.isNotNull(config.getConfigId()))) > 0;
        return !exist;
    }

}
