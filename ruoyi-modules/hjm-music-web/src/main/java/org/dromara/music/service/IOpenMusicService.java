package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.vo.OpenChartVo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.OpenSearchPanelVo;
import org.dromara.music.domain.vo.OpenSearchSuggestVo;
import org.dromara.music.domain.vo.MusicVo;

import java.util.List;

public interface IOpenMusicService {

    TableDataInfo<MusicVo> searchPublic(String keyword, String tag, String sort, PageQuery pageQuery);

    MusicDetailVo queryPublicDetail(Long id);

    List<MusicVo> queryRandomPublic(Integer limit);

    OpenChartVo queryPublicChart(String chartType, String periodKey, Integer limit);

    List<OpenSearchSuggestVo> querySearchSuggestions(String keyword, Integer limit);

    List<String> queryHotKeywords(Integer limit, Integer days);

    OpenSearchPanelVo querySearchPanel(String keyword, String tag, String sort, Integer limit);
}
