package org.dromara.music.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.Emoji;
import org.dromara.music.domain.bo.EmojiBo;
import org.dromara.music.domain.vo.EmojiVo;
import org.dromara.music.mapper.EmojiMapper;
import org.dromara.music.service.IEmojiService;
import org.springframework.stereotype.Service;
import org.dromara.common.core.utils.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.dromara.music.domain.table.EmojiTableDef.EMOJI;

/**
 * 表情包Service业务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EmojiServiceImpl implements IEmojiService {

    private final EmojiMapper baseMapper;

    @Override
    public EmojiVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public TableDataInfo<EmojiVo> queryPageList(EmojiBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<EmojiVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    @Override
    public List<EmojiVo> queryList(EmojiBo bo) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        return baseMapper.selectVoList(wrapper);
    }

    private QueryWrapper buildQueryWrapper(EmojiBo bo) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(EMOJI.NAME.like(bo.getName(), StringUtils.isNotBlank(bo.getName())))
                .and(EMOJI.CATEGORY.eq(bo.getCategory(), StringUtils.isNotBlank(bo.getCategory())))
                .and(EMOJI.STATUS.eq(bo.getStatus(), bo.getStatus() != null));
        return wrapper;
    }

    @Override
    public Boolean insertByBo(EmojiBo bo) {
        Emoji add = MapstructUtils.convert(bo, Emoji.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    @Override
    public Boolean updateByBo(EmojiBo bo) {
        Emoji update = MapstructUtils.convert(bo, Emoji.class);
        return baseMapper.update(update) > 0;
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        return baseMapper.deleteBatchByIds(ids) > 0;
    }

    @Override
    public Boolean approve(Long id) {
        Emoji emoji = new Emoji();
        emoji.setId(id);
        emoji.setStatus(1);
        return baseMapper.update(emoji) > 0;
    }

    @Override
    public Boolean reject(Long id) {
        Emoji emoji = new Emoji();
        emoji.setId(id);
        emoji.setStatus(2);
        return baseMapper.update(emoji) > 0;
    }

    @Override
    public List<String> queryCategoryList() {
        QueryWrapper wrapper = QueryWrapper.create()
                .select(EMOJI.CATEGORY)
                .from(EMOJI)
                .where(EMOJI.STATUS.eq(1))
                .groupBy(EMOJI.CATEGORY);
        return baseMapper.selectObjectListByQueryAs(wrapper, String.class);
    }
}
