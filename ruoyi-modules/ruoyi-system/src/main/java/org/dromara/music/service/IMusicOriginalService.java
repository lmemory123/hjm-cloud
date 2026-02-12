package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.MusicOriginalBo;
import org.dromara.music.domain.vo.MusicOriginalVo;

import java.util.Collection;
import java.util.List;

/**
 * 音乐原曲关联Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicOriginalService {

    /**
     * 查询音乐原曲关联
     *
     * @param id 主键
     * @return 音乐原曲关联
     */
    MusicOriginalVo queryById(Long id);

    /**
     * 分页查询音乐原曲关联列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐原曲关联分页列表
     */
    TableDataInfo<MusicOriginalVo> queryPageList(MusicOriginalBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐原曲关联列表
     *
     * @param bo 查询条件
     * @return 音乐原曲关联列表
     */
    List<MusicOriginalVo> queryList(MusicOriginalBo bo);

    /**
     * 新增音乐原曲关联
     *
     * @param bo 音乐原曲关联
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicOriginalBo bo);

    /**
     * 修改音乐原曲关联
     *
     * @param bo 音乐原曲关联
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicOriginalBo bo);

    /**
     * 校验并批量删除音乐原曲关联信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
