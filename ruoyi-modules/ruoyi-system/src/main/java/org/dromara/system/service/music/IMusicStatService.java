package org.dromara.system.service.music;

import org.dromara.system.domain.vo.MusicStatVo;
import org.dromara.system.domain.bo.MusicStatBo;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 音乐统计(高频读写)Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicStatService {

    /**
     * 查询音乐统计(高频读写)
     *
     * @param musicId 主键
     * @return 音乐统计(高频读写)
     */
    MusicStatVo queryById(Long musicId);

    /**
     * 分页查询音乐统计(高频读写)列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐统计(高频读写)分页列表
     */
    TableDataInfo<MusicStatVo> queryPageList(MusicStatBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐统计(高频读写)列表
     *
     * @param bo 查询条件
     * @return 音乐统计(高频读写)列表
     */
    List<MusicStatVo> queryList(MusicStatBo bo);

    /**
     * 新增音乐统计(高频读写)
     *
     * @param bo 音乐统计(高频读写)
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicStatBo bo);

    /**
     * 修改音乐统计(高频读写)
     *
     * @param bo 音乐统计(高频读写)
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicStatBo bo);

    /**
     * 校验并批量删除音乐统计(高频读写)信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
