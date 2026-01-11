package org.dromara.system.service.music.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.system.domain.Tag;
import org.dromara.system.domain.bo.TagBo;
import org.dromara.system.domain.vo.TagVo;
import org.dromara.system.mapper.TagMapper;
import org.dromara.system.service.music.ITagService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.dromara.system.domain.table.TagTableDef.TAG;

/**
 * 标签字典Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TagServiceImpl implements ITagService {

    private final TagMapper baseMapper;

    /**
     * 查询标签字典
     *
     * @param id 主键
     * @return 标签字典
     */
    @Override
    public TagVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询标签字典列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 标签字典分页列表
     */
    @Override
    public TableDataInfo<TagVo> queryPageList(TagBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<TagVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的标签字典列表
     *
     * @param bo 查询条件
     * @return 标签字典列表
     */
    @Override
    public List<TagVo> queryList(TagBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(TagBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                TAG.NAME.like(bo.getName())
                .and(TAG.TYPE.eq(bo.getType()))
                .and(TAG.TAG_ALIAS.eq(bo.getTagAlias()))
                .and(TAG.PARENT_ID.eq(bo.getParentId()))
                .and(TAG.DESCRIPTION.like(bo.getDescription()))
                .and(TAG.IS_HOT.eq(bo.getIsHot()))
                .and(TAG.IS_RECOMMEND.eq(bo.getIsRecommend()))
                .and(TAG.STATUS.eq(bo.getStatus()))
            )
            .orderBy(TAG.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增标签字典
     *
     * @param bo 标签字典
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(TagBo bo) {
        Tag add = MapstructUtils.convert(bo, Tag.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改标签字典
     *
     * @param bo 标签字典
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(TagBo bo) {
        Tag update = MapstructUtils.convert(bo, Tag.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Tag entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除标签字典信息
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
