package org.dromara.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.ObjectUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.oss.constant.OssConstant;
import org.dromara.common.redis.utils.CacheUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.resource.domain.SysOssConfig;
import org.dromara.resource.domain.bo.SysOssConfigBo;
import org.dromara.resource.domain.vo.SysOssConfigVo;
import org.dromara.resource.mapper.SysOssConfigMapper;
import org.dromara.resource.service.ISysOssConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 对象存储配置Service业务层处理
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysOssConfigServiceImpl implements ISysOssConfigService {

    private final SysOssConfigMapper baseMapper;

    /**
     * 项目启动时，初始化参数到缓存，加载配置类
     */
    @Override
    public void init() {
        List<SysOssConfig> list = baseMapper.selectAll();
        // 加载OSS初始化配置
        for (SysOssConfig config : list) {
            String configKey = config.getConfigKey();
            if ("0".equals(config.getStatus())) {
                RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, configKey);
            }
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
    }

    @Override
    public SysOssConfigVo queryById(Long ossConfigId) {
        return baseMapper.selectVoById(ossConfigId);
    }

    @Override
    public TableDataInfo<SysOssConfigVo> queryPageList(SysOssConfigBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<SysOssConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }


    private QueryWrapper buildQueryWrapper(SysOssConfigBo bo) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (StringUtils.isNotBlank(bo.getConfigKey())) {
            queryWrapper.and("config_key = ?", bo.getConfigKey());
        }
        if (StringUtils.isNotBlank(bo.getBucketName())) {
            queryWrapper.and("bucket_name like ?", "%" + bo.getBucketName() + "%");
        }
        if (StringUtils.isNotBlank(bo.getStatus())) {
            queryWrapper.and("status = ?", bo.getStatus());
        }
        queryWrapper.orderBy("oss_config_id", true);
        return queryWrapper;
    }

    @Override
    public Boolean insertByBo(SysOssConfigBo bo) {
        SysOssConfig config = BeanUtil.toBean(bo, SysOssConfig.class);
        validEntityBeforeSave(config);
        boolean flag = baseMapper.insert(config) > 0;
        if (flag) {
            // 从数据库查询完整的数据做缓存
            config = baseMapper.selectOneById(config.getOssConfigId());
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
        return flag;
    }

    @Override
    public Boolean updateByBo(SysOssConfigBo bo) {
        SysOssConfig config = BeanUtil.toBean(bo, SysOssConfig.class);
        validEntityBeforeSave(config);
        // 处理可能为null的字段
        if (ObjectUtil.isNull(config.getPrefix())) {
            config.setPrefix("");
        }
        if (ObjectUtil.isNull(config.getRegion())) {
            config.setRegion("");
        }
        if (ObjectUtil.isNull(config.getExt1())) {
            config.setExt1("");
        }
        if (ObjectUtil.isNull(config.getRemark())) {
            config.setRemark("");
        }
        boolean flag = baseMapper.update(config) > 0;
        if (flag) {
            // 从数据库查询完整的数据做缓存
            config = baseMapper.selectOneById(config.getOssConfigId());
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysOssConfig entity) {
        if (StringUtils.isNotEmpty(entity.getConfigKey()) && !checkConfigKeyUnique(entity)) {
            throw new ServiceException("操作配置'{}'失败, 配置key已存在!", entity.getConfigKey());
        }
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            if (CollUtil.containsAny(ids, OssConstant.SYSTEM_DATA_IDS)) {
                throw new ServiceException("系统内置, 不可删除!");
            }
        }
        List<SysOssConfig> list = CollUtil.newArrayList();
        for (Long configId : ids) {
            SysOssConfig config = baseMapper.selectOneById(configId);
            list.add(config);
        }
        boolean flag = baseMapper.deleteBatchByIds(ids) > 0;
        if (flag) {
            list.forEach(sysOssConfig ->
                CacheUtils.evict(CacheNames.SYS_OSS_CONFIG, sysOssConfig.getConfigKey()));
        }
        return flag;
    }

    /**
     * 判断configKey是否唯一
     */
    private boolean checkConfigKeyUnique(SysOssConfig sysOssConfig) {
        long ossConfigId = ObjectUtils.notNull(sysOssConfig.getOssConfigId(), -1L);
        SysOssConfig info = baseMapper.selectOneByQuery(QueryWrapper.create()
            .select("oss_config_id", "config_key")
            .where("config_key = ?", sysOssConfig.getConfigKey()));
        if (ObjectUtil.isNotNull(info) && info.getOssConfigId() != ossConfigId) {
            return false;
        }
        return true;
    }

    /**
     * 启用禁用状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOssConfigStatus(SysOssConfigBo bo) {
        SysOssConfig sysOssConfig = BeanUtil.toBean(bo, SysOssConfig.class);
        // 先将所有状态设为1(禁用)
        int row = baseMapper.updateByQuery(new SysOssConfig() {{ setStatus("1"); }}, QueryWrapper.create());
        row += baseMapper.update(sysOssConfig);
        if (row > 0) {
            RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, sysOssConfig.getConfigKey());
        }
        return row;
    }

}
