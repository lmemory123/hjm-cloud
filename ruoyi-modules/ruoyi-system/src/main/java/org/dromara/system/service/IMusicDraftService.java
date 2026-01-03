package org.dromara.system.service;

import org.dromara.system.domain.MusicDraft;
import org.dromara.system.domain.vo.MusicDraftVo;
import org.dromara.system.domain.bo.MusicDraftBo;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 投稿草稿Service接口
 *
 * @author momao
 * @date 2025-12-30
 */
public interface IMusicDraftService {

    /**
     * 查询投稿草稿
     *
     * @param id 主键
     * @return 投稿草稿
     */
    MusicDraftVo queryById(Long id);

    /**
     * 分页查询投稿草稿列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 投稿草稿分页列表
     */
    TableDataInfo<MusicDraftVo> queryPageList(MusicDraftBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的投稿草稿列表
     *
     * @param bo 查询条件
     * @return 投稿草稿列表
     */
    List<MusicDraftVo> queryList(MusicDraftBo bo);

    /**
     * 新增投稿草稿
     *
     * @param bo 投稿草稿
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicDraftBo bo);

    /**
     * 修改投稿草稿
     *
     * @param bo 投稿草稿
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicDraftBo bo);

    /**
     * 校验并批量删除投稿草稿信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
