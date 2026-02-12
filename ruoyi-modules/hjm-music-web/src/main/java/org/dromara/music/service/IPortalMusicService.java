package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.bo.PortalMusicUpdateBo;
import org.dromara.music.domain.bo.MusicBo;
import org.dromara.music.domain.vo.MusicDetailVo;
import org.dromara.music.domain.vo.MusicVo;

public interface IPortalMusicService {

    TableDataInfo<MusicVo> queryMyPage(MusicBo bo, PageQuery pageQuery, Long userId);

    MusicDetailVo queryDetail(Long id, Long userId);

    Boolean updateMusic(PortalMusicUpdateBo bo, Long userId);

    Boolean deleteMusic(Long id, Long userId);
}
