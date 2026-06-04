package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.EmojiBo;
import org.dromara.music.domain.vo.EmojiVo;

import java.util.Collection;
import java.util.List;

/**
 * 表情包Service接口
 */
public interface IEmojiService {

    EmojiVo queryById(Long id);

    TableDataInfo<EmojiVo> queryPageList(EmojiBo bo, PageQuery pageQuery);

    List<EmojiVo> queryList(EmojiBo bo);

    Boolean insertByBo(EmojiBo bo);

    Boolean updateByBo(EmojiBo bo);

    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean approve(Long id);

    Boolean reject(Long id);
    
    List<String> queryCategoryList();
}
