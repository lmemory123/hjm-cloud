package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.MusicAuditBo;
import org.dromara.music.domain.bo.MusicBo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicFullDetailVo;
import org.dromara.music.domain.vo.MusicVo;

import java.util.Collection;
import java.util.List;

/**
 * 音乐曲库主Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicService {

    /**
     * 查询音乐曲库主
     *
     * @param id 主键
     * @return 音乐曲库主
     */
    MusicVo queryById(Long id);

    /**
     * 查询音乐详情(含关联数据)
     *
     * @param id 主键
     * @return 音乐详情
     */
    MusicDetailVo queryDetailById(Long id);

    /**
     * 查询音乐完整聚合详情
     *
     * @param id 主键
     * @return 音乐完整详情
     */
    MusicFullDetailVo queryFullDetailById(Long id);

    /**
     * 分页查询音乐曲库主列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐曲库主分页列表
     */
    TableDataInfo<MusicVo> queryPageList(MusicBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐曲库主列表
     *
     * @param bo 查询条件
     * @return 音乐曲库主列表
     */
    List<MusicVo> queryList(MusicBo bo);

    /**
     * 新增音乐曲库主
     *
     * @param bo 音乐曲库主
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicBo bo);

    /**
     * 修改音乐曲库主
     *
     * @param bo 音乐曲库主
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicBo bo);

    /**
     * 校验并批量删除音乐曲库主信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 审核音乐
     *
     * @param bo         审核参数
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    Boolean audit(MusicAuditBo bo, Long operatorId);

    /**
     * 批量通过
     *
     * @param ids        音乐ID集合
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    Boolean batchPass(Collection<Long> ids, Long operatorId);

    /**
     * 批量下架
     *
     * @param ids        音乐ID集合
     * @param reason     下架原因
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    Boolean batchOffline(Collection<Long> ids, String reason, Long operatorId);
}
