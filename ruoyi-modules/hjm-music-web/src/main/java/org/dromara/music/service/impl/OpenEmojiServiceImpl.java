package org.dromara.music.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.EmojiBo;
import org.dromara.music.domain.vo.EmojiVo;
import org.dromara.music.mapper.EmojiMapper;
import org.dromara.music.service.IOpenEmojiService;
import org.springframework.stereotype.Service;
import org.dromara.common.core.utils.StringUtils;

import java.util.List;

import static org.dromara.music.domain.table.EmojiTableDef.EMOJI;

@RequiredArgsConstructor
@Service
public class OpenEmojiServiceImpl implements IOpenEmojiService {

    private final EmojiMapper emojiMapper;

    @Override
    public TableDataInfo<EmojiVo> queryPageList(EmojiBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(EMOJI.NAME.like(bo.getName(), StringUtils.isNotBlank(bo.getName())))
                .and(EMOJI.CATEGORY.eq(bo.getCategory(), StringUtils.isNotBlank(bo.getCategory())))
                .and(EMOJI.STATUS.eq(bo.getStatus(), bo.getStatus() != null));

        Page<EmojiVo> result = emojiMapper.selectVoPage(pageQuery.build(), wrapper);
        return TableDataInfo.build(result);
    }

    @Override
    public List<String> queryCategoryList() {
        QueryWrapper wrapper = QueryWrapper.create()
                .select(EMOJI.CATEGORY)
                .from(EMOJI)
                .where(EMOJI.STATUS.eq(1))
                .groupBy(EMOJI.CATEGORY);
        return emojiMapper.selectObjectListByQueryAs(wrapper, String.class);
    }
}
