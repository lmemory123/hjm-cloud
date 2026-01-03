package org.dromara.system.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.MusicStatBo;
import org.dromara.system.domain.vo.MusicStatVo;
import org.dromara.system.domain.MusicStat;
import org.dromara.system.mapper.MusicStatMapper;
import org.dromara.system.service.IMusicStatService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

import static org.dromara.system.domain.table.MusicStatTableDef.MUSIC_STAT;

/**
 * 音乐统计(高频读写)Service业务层处理
 *
 * @author momao
 * @date 2025-12-30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MusicStatServiceImpl implements IMusicStatService {

    private final MusicStatMapper baseMapper;

    /**
     * 查询音乐统计(高频读写)
     *
     * @param musicId 主键
     * @return 音乐统计(高频读写)
     */
    @Override
    public MusicStatVo queryById(Long musicId){
        return baseMapper.selectVoById(musicId);
    }

    /**
     * 分页查询音乐统计(高频读写)列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐统计(高频读写)分页列表
     */
    @Override
    public TableDataInfo<MusicStatVo> queryPageList(MusicStatBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<MusicStatVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的音乐统计(高频读写)列表
     *
     * @param bo 查询条件
     * @return 音乐统计(高频读写)列表
     */
    @Override
    public List<MusicStatVo> queryList(MusicStatBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(MusicStatBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create()
            .where(
                MUSIC_STAT.PLAY_COUNT.eq(bo.getPlayCount())
                .and(MUSIC_STAT.LIKE_COUNT.eq(bo.getLikeCount()))
                .and(MUSIC_STAT.COLLECT_COUNT.eq(bo.getCollectCount()))
                .and(MUSIC_STAT.COMMENT_COUNT.eq(bo.getCommentCount()))
                .and(MUSIC_STAT.SHARE_COUNT.eq(bo.getShareCount()))
                .and(MUSIC_STAT.DOWNLOAD_COUNT.eq(bo.getDownloadCount()))
                .and(MUSIC_STAT.SCORE.eq(bo.getScore()))
            )
            .orderBy(MUSIC_STAT.MUSIC_ID.asc());
        return queryWrapper;
    }

    /**
     * 新增音乐统计(高频读写)
     *
     * @param bo 音乐统计(高频读写)
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MusicStatBo bo) {
        MusicStat add = MapstructUtils.convert(bo, MusicStat.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMusicId(add.getMusicId());
        }
        return flag;
    }

    /**
     * 修改音乐统计(高频读写)
     *
     * @param bo 音乐统计(高频读写)
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MusicStatBo bo) {
        MusicStat update = MapstructUtils.convert(bo, MusicStat.class);
        validEntityBeforeSave(update);
        return baseMapper.update(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MusicStat entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除音乐统计(高频读写)信息
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
