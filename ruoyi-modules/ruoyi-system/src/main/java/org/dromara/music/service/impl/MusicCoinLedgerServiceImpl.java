package org.dromara.music.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.helper.DataBaseHelper;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.MusicCoinGrantBo;
import org.dromara.music.domain.MusicCoinLedger;
import org.dromara.music.domain.bo.MusicCoinLedgerBo;
import org.dromara.music.domain.vo.MusicCoinLedgerVo;
import org.dromara.music.mapper.MusicCoinLedgerMapper;
import org.dromara.music.service.IMusicCoinLedgerService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.dromara.music.domain.table.MusicCoinLedgerTableDef.MUSIC_COIN_LEDGER;

/**
 * 哈气金流水Service业务层处理
 *
 * @author momao
 * @date 2026-01-07
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicCoinLedgerServiceImpl implements IMusicCoinLedgerService {

    private static final String REASON_SYSTEM_GRANT = "system_grant";
    private static final String REASON_REVOKE = "system_revoke";

    private final MusicCoinLedgerMapper baseMapper;

    /**
     * 查询哈气金流水
     *
     * @param id 主键
     * @return 哈气金流水
     */
    @Override
    public MusicCoinLedgerVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询哈气金流水列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 哈气金流水分页列表
     */
    @Override
    public TableDataInfo<MusicCoinLedgerVo> queryPageList(MusicCoinLedgerBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicCoinLedgerVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的哈气金流水列表
     *
     * @param bo 查询条件
     * @return 哈气金流水列表
     */
    @Override
    public List<MusicCoinLedgerVo> queryList(MusicCoinLedgerBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicCoinLedgerBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_COIN_LEDGER.USER_ID.eq(bo.getUserId())
                .and(MUSIC_COIN_LEDGER.AMOUNT.eq(bo.getAmount()))
                .and(MUSIC_COIN_LEDGER.REASON_CODE.eq(bo.getReasonCode()))
                .and(MUSIC_COIN_LEDGER.BALANCE_AFTER.eq(bo.getBalanceAfter()))
            )
            .orderBy(MUSIC_COIN_LEDGER.ID.asc());
        return queryWrapper;
    }

    /**
     * 新增哈气金流水
     *
     * @param bo 哈气金流水
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicCoinLedgerBo bo) {
        MusicCoinLedger add = MapstructUtils.convert(bo, MusicCoinLedger.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改哈气金流水
     *
     * @param bo 哈气金流水
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicCoinLedgerBo bo) {
        MusicCoinLedger update = MapstructUtils.convert(bo, MusicCoinLedger.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    @Override
    public Integer batchGrant(MusicCoinGrantBo bo) {
        if (bo.getAmount() == null || bo.getAmount() <= 0) {
            throw new ServiceException("发放金额必须大于 0");
        }
        String reasonCode = StringUtils.isBlank(bo.getReasonCode()) ? REASON_SYSTEM_GRANT : bo.getReasonCode().trim();
        Date now = new Date();
        int count = 0;
        for (Long userId : bo.getUserIds()) {
            if (userId == null) {
                continue;
            }
            MusicCoinLedger ledger = newLedger(userId, bo.getAmount(), reasonCode, currentBalance(userId) + bo.getAmount(), now);
            count += baseMapper.insert(ledger) > 0 ? 1 : 0;
        }
        return count;
    }

    @Override
    public Integer batchRevoke(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<MusicCoinLedger> ledgers = baseMapper.selectListByIds(ids).stream()
            .filter(Objects::nonNull)
            .filter(item -> item.getAmount() != null && item.getAmount() > 0)
            .toList();
        Date now = new Date();
        int count = 0;
        for (MusicCoinLedger source : ledgers) {
            Long userId = source.getUserId();
            Long amount = -source.getAmount();
            MusicCoinLedger revoke = newLedger(userId, amount, REASON_REVOKE, currentBalance(userId) + amount, now);
            count += baseMapper.insert(revoke) > 0 ? 1 : 0;
        }
        return count;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicCoinLedger entity){
        //TODO 做一些数据校验,如唯一约束
    }

    private MusicCoinLedger newLedger(Long userId, Long amount, String reasonCode, Long balanceAfter, Date now) {
        MusicCoinLedger ledger = new MusicCoinLedger();
        ledger.setId(DataBaseHelper.nextId());
        ledger.setUserId(userId);
        ledger.setAmount(amount);
        ledger.setReasonCode(reasonCode);
        ledger.setBalanceAfter(Math.max(0L, balanceAfter));
        ledger.setCreateTime(now);
        return ledger;
    }

    private long currentBalance(Long userId) {
        List<MusicCoinLedger> rows = baseMapper.selectListByQuery(QueryWrapper.create()
            .where(MUSIC_COIN_LEDGER.USER_ID.eq(userId)));
        return rows.stream().map(MusicCoinLedger::getAmount).filter(Objects::nonNull).mapToLong(Long::longValue).sum();
    }

    /**
     * 校验并批量删除哈气金流水信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchByIds(ids) > 0;
    }
}
