package org.dromara.music.service;

import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.vo.MusicNotifyLogVo;

public interface IPortalNotifyService {

    TableDataInfo<MusicNotifyLogVo> queryMyNotifies(PageQuery pageQuery, Long userId);
}
