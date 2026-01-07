package org.dromara.system.service.music;

import org.dromara.system.domain.vo.MusicResourceVo;
import org.dromara.system.domain.bo.MusicResourceBo;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 音乐资源文件Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicResourceService {

    /**
     * 查询音乐资源文件
     *
     * @param id 主键
     * @return 音乐资源文件
     */
    MusicResourceVo queryById(Long id);

    /**
     * 分页查询音乐资源文件列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐资源文件分页列表
     */
    TableDataInfo<MusicResourceVo> queryPageList(MusicResourceBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐资源文件列表
     *
     * @param bo 查询条件
     * @return 音乐资源文件列表
     */
    List<MusicResourceVo> queryList(MusicResourceBo bo);

    /**
     * 新增音乐资源文件
     *
     * @param bo 音乐资源文件
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicResourceBo bo);

    /**
     * 修改音乐资源文件
     *
     * @param bo 音乐资源文件
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicResourceBo bo);

    /**
     * 校验并批量删除音乐资源文件信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
