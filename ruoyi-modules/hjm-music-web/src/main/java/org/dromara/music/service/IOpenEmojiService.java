package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.EmojiBo;
import org.dromara.music.domain.vo.EmojiVo;

import java.util.List;

public interface IOpenEmojiService {

    TableDataInfo<EmojiVo> queryPageList(EmojiBo bo, PageQuery pageQuery);

    List<String> queryCategoryList();
}
