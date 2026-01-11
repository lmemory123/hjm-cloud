package org.dromara.system.service.impl;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.util.ObjUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.redis.utils.CacheUtils;
import org.dromara.system.domain.SysDictData;
import org.dromara.system.domain.SysDictType;
import org.dromara.system.domain.bo.SysDictTypeBo;
import org.dromara.system.domain.vo.SysDictDataVo;
import org.dromara.system.domain.vo.SysDictTypeVo;
import org.dromara.system.mapper.SysDictDataMapper;
import org.dromara.system.mapper.SysDictTypeMapper;
import org.dromara.system.service.ISysDictTypeService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.dromara.system.domain.table.SysDictTypeTableDef.SYS_DICT_TYPE;

/**
 * 字典 业务层处理
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService {

    private final SysDictTypeMapper baseMapper;
    private final SysDictDataMapper dictDataMapper;

    /**
     * 分页查询字典类型列表
     *
     * @param dictType  查询条件
     * @param pageQuery 分页参数
     * @return 字典类型分页列表
     */
    @Override
    public TableDataInfo<SysDictTypeVo> selectPageDictTypeList(SysDictTypeBo dictType, PageQuery pageQuery) {
        QueryWrapper lqw = buildQueryWrapper(dictType);
        Page<SysDictTypeVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictTypeVo> selectDictTypeList(SysDictTypeBo dictType) {
        QueryWrapper lqw = buildQueryWrapper(dictType);
        return baseMapper.selectVoList(lqw);
    }

    private QueryWrapper buildQueryWrapper(SysDictTypeBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(SYS_DICT_TYPE.DICT_NAME.like(bo.getDictName())
                .and(SYS_DICT_TYPE.DICT_TYPE.like(bo.getDictType()))
            )
            .orderBy(SYS_DICT_TYPE.DICT_ID.asc());
        // 处理日期范围
        if (params.get("beginTime") != null && params.get("endTime") != null) {
            queryWrapper.and(SYS_DICT_TYPE.CREATE_TIME.between(params.get("beginTime"), params.get("endTime")));
        }
        return queryWrapper;
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictTypeVo> selectDictTypeAll() {
        return baseMapper.selectVoList();
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Cacheable(cacheNames = CacheNames.SYS_DICT, key = "#dictType")
    @Override
    public List<SysDictDataVo> selectDictDataByType(String dictType) {
        List<SysDictDataVo> dictDatas = dictDataMapper.selectDictDataByType(dictType);
        return CollUtil.isNotEmpty(dictDatas) ? dictDatas : null;
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictTypeVo selectDictTypeById(Long dictId) {
        return baseMapper.selectVoById(dictId);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Cacheable(cacheNames = CacheNames.SYS_DICT_TYPE, key = "#dictType")
    @Override
    public SysDictTypeVo selectDictTypeByType(String dictType) {
        return baseMapper.selectVoOne(QueryWrapper.create().eq(SysDictType::getDictType, dictType));
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     */
    @Override
    public void deleteDictTypeByIds(List<Long> dictIds) {
        List<SysDictType> list = baseMapper.selectListByIds(dictIds);
        list.forEach(x -> {
            boolean assigned = dictDataMapper.selectCountByQuery(QueryWrapper.create()
                .eq(SysDictData::getDictType, x.getDictType())) > 0;
            if (assigned) {
                throw new ServiceException("{}已分配,不能删除", x.getDictName());
            }
        });
        baseMapper.deleteBatchByIds(dictIds);
        list.forEach(x -> {
            CacheUtils.evict(CacheNames.SYS_DICT, x.getDictType());
            CacheUtils.evict(CacheNames.SYS_DICT_TYPE, x.getDictType());
        });
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        CacheUtils.clear(CacheNames.SYS_DICT);
        CacheUtils.clear(CacheNames.SYS_DICT_TYPE);
    }

    /**
     * 新增保存字典类型信息
     *
     * @param bo 字典类型信息
     * @return 结果
     */
    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#bo.dictType")
    @Override
    public List<SysDictDataVo> insertDictType(SysDictTypeBo bo) {
        SysDictType dict = MapstructUtils.convert(bo, SysDictType.class);
        int row = baseMapper.insert(dict);
        if (row > 0) {
            // 新增 type 下无 data 数据 返回空防止缓存穿透
            return new ArrayList<>();
        }
        throw new ServiceException("操作失败");
    }

    /**
     * 修改保存字典类型信息
     *
     * @param bo 字典类型信息
     * @return 结果
     */
    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#bo.dictType")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SysDictDataVo> updateDictType(SysDictTypeBo bo) {
        SysDictType dict = MapstructUtils.convert(bo, SysDictType.class);
        SysDictType oldDict = baseMapper.selectOneById(dict.getDictId());
        SysDictData update = new SysDictData();
        update.setDictType(dict.getDictType());
        dictDataMapper.updateByQuery(update, QueryWrapper.create()
            .eq(SysDictData::getDictType, oldDict.getDictType()));
        int row = baseMapper.update(dict);
        if (row > 0) {
            CacheUtils.evict(CacheNames.SYS_DICT, oldDict.getDictType());
            return dictDataMapper.selectDictDataByType(dict.getDictType());
        }
        throw new ServiceException("操作失败");
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictTypeBo dictType) {
        boolean exist = baseMapper.selectCountByQuery(QueryWrapper.create()
            .eq(SysDictType::getDictType, dictType.getDictType())
            .ne(SysDictType::getDictId, dictType.getDictId(), ObjUtil.isNotNull(dictType.getDictId()))) > 0;
        return !exist;
    }

}
