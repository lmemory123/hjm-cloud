package org.dromara.system.service;

import org.dromara.system.domain.MusicLinkCheckLog;
import org.dromara.system.domain.vo.MusicLinkCheckLogVo;
import org.dromara.system.domain.bo.MusicLinkCheckLogBo;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 链接检测日志Service接口
 *
 * @author momao
 * @date 2025-12-30
 */
public interface IMusicLinkCheckLogService {

    /**
     * 查询链接检测日志
     *
     * @param id 主键
     * @return 链接检测日志
     */
    MusicLinkCheckLogVo queryById(Long id);

    /**
     * 分页查询链接检测日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 链接检测日志分页列表
     */
    TableDataInfo<MusicLinkCheckLogVo> queryPageList(MusicLinkCheckLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的链接检测日志列表
     *
     * @param bo 查询条件
     * @return 链接检测日志列表
     */
    List<MusicLinkCheckLogVo> queryList(MusicLinkCheckLogBo bo);

    /**
     * 新增链接检测日志
     *
     * @param bo 链接检测日志
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicLinkCheckLogBo bo);

    /**
     * 修改链接检测日志
     *
     * @param bo 链接检测日志
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicLinkCheckLogBo bo);

    /**
     * 校验并批量删除链接检测日志信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
