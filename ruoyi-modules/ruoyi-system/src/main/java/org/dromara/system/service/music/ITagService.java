package org.dromara.system.service.music;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.system.domain.bo.TagBo;
import org.dromara.system.domain.vo.TagVo;

import java.util.Collection;
import java.util.List;

/**
 * 标签字典Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface ITagService {

    /**
     * 查询标签字典
     *
     * @param id 主键
     * @return 标签字典
     */
    TagVo queryById(Long id);

    /**
     * 分页查询标签字典列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 标签字典分页列表
     */
    TableDataInfo<TagVo> queryPageList(TagBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的标签字典列表
     *
     * @param bo 查询条件
     * @return 标签字典列表
     */
    List<TagVo> queryList(TagBo bo);

    /**
     * 新增标签字典
     *
     * @param bo 标签字典
     * @return 是否新增成功
     */
    Boolean insertByBo(TagBo bo);

    /**
     * 修改标签字典
     *
     * @param bo 标签字典
     * @return 是否修改成功
     */
    Boolean updateByBo(TagBo bo);

    /**
     * 校验并批量删除标签字典信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
