package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.MusicNotifyLogBo;
import org.dromara.music.domain.vo.MusicNotifyLogVo;

import java.util.Collection;
import java.util.List;

/**
 * 音乐通知日志Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicNotifyLogService {

    /**
     * 查询音乐通知日志
     *
     * @param id 主键
     * @return 音乐通知日志
     */
    MusicNotifyLogVo queryById(Long id);

    /**
     * 分页查询音乐通知日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐通知日志分页列表
     */
    TableDataInfo<MusicNotifyLogVo> queryPageList(MusicNotifyLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐通知日志列表
     *
     * @param bo 查询条件
     * @return 音乐通知日志列表
     */
    List<MusicNotifyLogVo> queryList(MusicNotifyLogBo bo);

    /**
     * 新增音乐通知日志
     *
     * @param bo 音乐通知日志
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicNotifyLogBo bo);

    /**
     * 修改音乐通知日志
     *
     * @param bo 音乐通知日志
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicNotifyLogBo bo);

    /**
     * 校验并批量删除音乐通知日志信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
