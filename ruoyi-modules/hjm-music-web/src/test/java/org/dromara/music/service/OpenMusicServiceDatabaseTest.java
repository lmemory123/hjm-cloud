package org.dromara.music.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.mapper.MusicChartItemMapper;
import org.dromara.music.mapper.MusicChartSnapshotMapper;
import org.dromara.music.mapper.MusicMapper;
import org.dromara.music.mapper.MusicOriginalMapper;
import org.dromara.music.mapper.MusicResourceMapper;
import org.dromara.music.mapper.MusicTagRelMapper;
import org.dromara.music.mapper.TagMapper;
import org.dromara.music.service.impl.OpenMusicServiceImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * OpenMusicService 数据库回退链路单元测试 (无需 Spring 上下文)
 */
@Tag("dev")
@ExtendWith(MockitoExtension.class)
public class OpenMusicServiceDatabaseTest {

    @Mock
    private MusicMapper musicMapper;

    @Mock
    private MusicChartSnapshotMapper musicChartSnapshotMapper;

    @Mock
    private MusicChartItemMapper musicChartItemMapper;

    @Mock
    private MusicOriginalMapper musicOriginalMapper;

    @Mock
    private MusicResourceMapper musicResourceMapper;

    @Mock
    private MusicTagRelMapper musicTagRelMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private MusicInteractionService musicInteractionService;

    @InjectMocks
    private OpenMusicServiceImpl openMusicService;

    @Test
    public void testSearchPublicFallback() {
        Page<MusicVo> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(Collections.emptyList());
        when(musicMapper.selectVoPage(any(Page.class), any(QueryWrapper.class))).thenReturn(emptyPage);

        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(1);
        pageQuery.setPageSize(10);

        // 验证全矩阵参数下的逻辑连通性
        TableDataInfo<MusicVo> result = openMusicService.searchPublic(
            null, "Vocaloid", "Electronic", "Pop", "newest", "1", "0", "0", null, null, null, null, pageQuery
        );

        assertNotNull(result, "结果不应为空");
        assertNotNull(result.getRows(), "行数据不应为空");
    }
}
