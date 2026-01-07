package org.dromara.system.service.music;

import org.dromara.system.domain.vo.MusicAuditLogVo;
import org.dromara.system.domain.bo.MusicAuditLogBo;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 音乐审核流水日志Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicAuditLogService {

    /**
     * 查询音乐审核流水日志
     *
     * @param id 主键
     * @return 音乐审核流水日志
     */
    MusicAuditLogVo queryById(Long id);

    /**
     * 分页查询音乐审核流水日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐审核流水日志分页列表
     */
    TableDataInfo<MusicAuditLogVo> queryPageList(MusicAuditLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐审核流水日志列表
     *
     * @param bo 查询条件
     * @return 音乐审核流水日志列表
     */
    List<MusicAuditLogVo> queryList(MusicAuditLogBo bo);

    /**
     * 新增音乐审核流水日志
     *
     * @param bo 音乐审核流水日志
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicAuditLogBo bo);

    /**
     * 修改音乐审核流水日志
     *
     * @param bo 音乐审核流水日志
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicAuditLogBo bo);

    /**
     * 校验并批量删除音乐审核流水日志信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
