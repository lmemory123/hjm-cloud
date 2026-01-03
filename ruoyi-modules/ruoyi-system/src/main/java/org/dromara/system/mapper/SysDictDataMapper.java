package org.dromara.system.mapper;

import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.mybatisflex.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysDictData;
import org.dromara.system.domain.vo.SysDictDataVo;

import java.util.List;

/**
 * 字典表 数据层
 *
 * @author Lion Li
 */
public interface SysDictDataMapper extends BaseMapperPlus<SysDictData, SysDictDataVo> {

    default List<SysDictDataVo> selectDictDataByType(String dictType) {
        return selectVoList(QueryWrapper.create()
            .eq(SysDictData::getDictType, dictType)
            .orderBy(SysDictData::getDictSort, true));
    }
}
