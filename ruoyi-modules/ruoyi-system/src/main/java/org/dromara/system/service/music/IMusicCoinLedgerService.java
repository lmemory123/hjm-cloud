package org.dromara.system.service.music;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.system.domain.bo.MusicCoinLedgerBo;
import org.dromara.system.domain.vo.MusicCoinLedgerVo;

import java.util.Collection;
import java.util.List;

/**
 * 哈气金流水Service接口
 *
 * @author momao
 * @date 2026-01-07
 */
public interface IMusicCoinLedgerService {

    /**
     * 查询哈气金流水
     *
     * @param id 主键
     * @return 哈气金流水
     */
    MusicCoinLedgerVo queryById(Long id);

    /**
     * 分页查询哈气金流水列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 哈气金流水分页列表
     */
    TableDataInfo<MusicCoinLedgerVo> queryPageList(MusicCoinLedgerBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的哈气金流水列表
     *
     * @param bo 查询条件
     * @return 哈气金流水列表
     */
    List<MusicCoinLedgerVo> queryList(MusicCoinLedgerBo bo);

    /**
     * 新增哈气金流水
     *
     * @param bo 哈气金流水
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicCoinLedgerBo bo);

    /**
     * 修改哈气金流水
     *
     * @param bo 哈气金流水
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicCoinLedgerBo bo);

    /**
     * 校验并批量删除哈气金流水信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
