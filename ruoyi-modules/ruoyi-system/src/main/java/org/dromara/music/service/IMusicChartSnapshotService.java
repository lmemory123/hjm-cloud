package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.MusicChartSnapshotBo;
import org.dromara.music.domain.vo.MusicChartSnapshotVo;

import java.util.Collection;
import java.util.List;

/**
 * 榜单快照Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicChartSnapshotService {

    /**
     * 查询榜单快照
     *
     * @param id 主键
     * @return 榜单快照
     */
    MusicChartSnapshotVo queryById(Long id);

    /**
     * 分页查询榜单快照列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 榜单快照分页列表
     */
    TableDataInfo<MusicChartSnapshotVo> queryPageList(MusicChartSnapshotBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的榜单快照列表
     *
     * @param bo 查询条件
     * @return 榜单快照列表
     */
    List<MusicChartSnapshotVo> queryList(MusicChartSnapshotBo bo);

    /**
     * 新增榜单快照
     *
     * @param bo 榜单快照
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicChartSnapshotBo bo);

    /**
     * 修改榜单快照
     *
     * @param bo 榜单快照
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicChartSnapshotBo bo);

    /**
     * 校验并批量删除榜单快照信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
