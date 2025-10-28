package org.dromara.system.service.impl;

<<<<<<< HEAD
import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.crypto.SecureUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
=======
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
>>>>>>> 7e54246af (update 客户端管理新增客户端key唯一校验逻辑)
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.system.domain.SysClient;
import org.dromara.system.domain.bo.SysClientBo;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.system.mapper.SysClientMapper;
import org.dromara.system.service.ISysClientService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static org.dromara.system.domain.table.SysClientTableDef.SYS_CLIENT;

/**
 * 客户端管理Service业务层处理
 *
 * @author Michelle.Chung
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysClientServiceImpl implements ISysClientService {

    private final SysClientMapper baseMapper;

    /**
     * 查询客户端管理
     */
    @Override
    public SysClientVo queryById(Long id) {
        SysClientVo vo = baseMapper.selectVoById(id);
        vo.setGrantTypeList(StringUtils.splitList(vo.getGrantType()));
        return vo;
    }

    /**
     * 查询客户端管理
     */
    @Cacheable(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    @Override
    public SysClientVo queryByClientId(String clientId) {
        return baseMapper.selectVoOne(QueryWrapper.create().eq(SysClient::getClientId, clientId));
    }

    /**
     * 查询客户端管理列表
     */
    @Override
    public TableDataInfo<SysClientVo> queryPageList(SysClientBo bo, PageQuery pageQuery) {
        QueryWrapper lqw = buildQueryWrapper(bo);
        Page<SysClientVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        result.getRecords().forEach(r -> r.setGrantTypeList(StringUtils.splitList(r.getGrantType())));
        return TableDataInfo.build(result);
    }

    /**
     * 查询客户端管理列表
     */
    @Override
    public List<SysClientVo> queryList(SysClientBo bo) {
        QueryWrapper lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private QueryWrapper buildQueryWrapper(SysClientBo bo) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(SYS_CLIENT.CLIENT_ID.eq(bo.getClientId())
                .and(SYS_CLIENT.CLIENT_KEY.eq(bo.getClientKey()))
                .and(SYS_CLIENT.CLIENT_SECRET.eq(bo.getClientSecret()))
                .and(SYS_CLIENT.STATUS.eq(bo.getStatus()))
            )
            .orderBy(SYS_CLIENT.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增客户端管理
     */
    @Override
    public Boolean insertByBo(SysClientBo bo) {
        SysClient add = MapstructUtils.convert(bo, SysClient.class);
        add.setGrantType(CollUtil.join(bo.getGrantTypeList(), StringUtils.SEPARATOR));
        // 生成clientId
        String clientKey = bo.getClientKey();
        String clientSecret = bo.getClientSecret();
        add.setClientId(SecureUtil.md5(clientKey + clientSecret));
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改客户端管理
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#bo.clientId")
    @Override
    public Boolean updateByBo(SysClientBo bo) {
        SysClient update = MapstructUtils.convert(bo, SysClient.class);
        update.setGrantType(StringUtils.joinComma(bo.getGrantTypeList()));
        return baseMapper.update(update) > 0;
    }

    /**
     * 修改状态
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, key = "#clientId")
    @Override
    public int updateClientStatus(String clientId, String status) {
        SysClient update = new SysClient();
        update.setStatus(status);
        return baseMapper.updateByQuery(update, QueryWrapper.create().eq(SysClient::getClientId, clientId));
    }

    /**
     * 批量删除客户端管理
     */
    @CacheEvict(cacheNames = CacheNames.SYS_CLIENT, allEntries = true)
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchByIds(ids) > 0;
    }

    /**
     * 校验客户端key是否唯一
     *
     * @param client 客户端信息
     * @return 结果
     */
    @Override
    public boolean checkClickKeyUnique(SysClientBo client) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<SysClient>()
            .eq(SysClient::getClientKey, client.getClientKey())
            .ne(ObjectUtil.isNotNull(client.getId()), SysClient::getId, client.getId()));
        return !exist;
    }

}
