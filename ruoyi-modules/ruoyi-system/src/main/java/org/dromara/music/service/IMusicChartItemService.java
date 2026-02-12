package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.MusicChartItemBo;
import org.dromara.music.domain.vo.MusicChartItemVo;

import java.util.Collection;
import java.util.List;

/**
 * 榜单明细Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicChartItemService {

    /**
     * 查询榜单明细
     *
     * @param id 主键
     * @return 榜单明细
     */
    MusicChartItemVo queryById(Long id);

    /**
     * 分页查询榜单明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 榜单明细分页列表
     */
    TableDataInfo<MusicChartItemVo> queryPageList(MusicChartItemBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的榜单明细列表
     *
     * @param bo 查询条件
     * @return 榜单明细列表
     */
    List<MusicChartItemVo> queryList(MusicChartItemBo bo);

    /**
     * 新增榜单明细
     *
     * @param bo 榜单明细
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicChartItemBo bo);

    /**
     * 修改榜单明细
     *
     * @param bo 榜单明细
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicChartItemBo bo);

    /**
     * 校验并批量删除榜单明细信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
