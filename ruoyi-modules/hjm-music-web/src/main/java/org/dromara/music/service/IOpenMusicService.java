package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.vo.OpenChartVo;
import org.dromara.music.domain.vo.OpenChartArchiveVo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.OpenSearchPanelVo;
import org.dromara.music.domain.vo.OpenSearchSuggestVo;
import org.dromara.music.domain.vo.OpenUserProfileVo;
import org.dromara.music.domain.vo.MusicVo;

import java.util.List;

public interface IOpenMusicService {

    TableDataInfo<MusicVo> searchPublic(String keyword,
                                        String tag,
                                        String tags,
                                        String style,
                                        String sort,
                                        String isOriginal,
                                        String isAi,
                                        String resourceStatus,
                                        String startDate,
                                        String endDate,
                                        Long playCountMin,
                                        Long playCountMax,
                                        PageQuery pageQuery);

    MusicDetailVo queryPublicDetail(Long id);

    OpenUserProfileVo queryPublicUserProfile(String uid);

    List<MusicVo> queryRandomPublic(Integer limit);

    OpenChartVo queryPublicChart(String chartType, String periodKey, Integer limit);

    List<OpenChartArchiveVo> queryChartArchives(String chartType, Integer limit);

    List<OpenSearchSuggestVo> querySearchSuggestions(String keyword, Integer limit);

    List<String> queryHotKeywords(Integer limit, Integer days);

    OpenSearchPanelVo querySearchPanel(String keyword,
                                       String tag,
                                       String tags,
                                       String style,
                                       String sort,
                                       String isOriginal,
                                       String isAi,
                                       String resourceStatus,
                                       String startDate,
                                       String endDate,
                                       Long playCountMin,
                                       Long playCountMax,
                                       Integer limit);
}
