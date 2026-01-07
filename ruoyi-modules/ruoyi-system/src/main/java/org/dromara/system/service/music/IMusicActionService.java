package org.dromara.system.service.music;

import org.dromara.system.domain.vo.MusicActionVo;
import org.dromara.system.domain.bo.MusicActionBo;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 音乐互动动作Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicActionService {

    /**
     * 查询音乐互动动作
     *
     * @param id 主键
     * @return 音乐互动动作
     */
    MusicActionVo queryById(Long id);

    /**
     * 分页查询音乐互动动作列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐互动动作分页列表
     */
    TableDataInfo<MusicActionVo> queryPageList(MusicActionBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐互动动作列表
     *
     * @param bo 查询条件
     * @return 音乐互动动作列表
     */
    List<MusicActionVo> queryList(MusicActionBo bo);

    /**
     * 新增音乐互动动作
     *
     * @param bo 音乐互动动作
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicActionBo bo);

    /**
     * 修改音乐互动动作
     *
     * @param bo 音乐互动动作
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicActionBo bo);

    /**
     * 校验并批量删除音乐互动动作信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
