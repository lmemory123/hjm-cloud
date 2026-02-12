package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.MusicTagRelBo;
import org.dromara.music.domain.vo.MusicTagRelVo;

import java.util.Collection;
import java.util.List;

/**
 * 音乐标签关联Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicTagRelService {

    /**
     * 查询音乐标签关联
     *
     * @param id 主键
     * @return 音乐标签关联
     */
    MusicTagRelVo queryById(Long id);

    /**
     * 分页查询音乐标签关联列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐标签关联分页列表
     */
    TableDataInfo<MusicTagRelVo> queryPageList(MusicTagRelBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐标签关联列表
     *
     * @param bo 查询条件
     * @return 音乐标签关联列表
     */
    List<MusicTagRelVo> queryList(MusicTagRelBo bo);

    /**
     * 新增音乐标签关联
     *
     * @param bo 音乐标签关联
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicTagRelBo bo);

    /**
     * 修改音乐标签关联
     *
     * @param bo 音乐标签关联
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicTagRelBo bo);

    /**
     * 校验并批量删除音乐标签关联信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
