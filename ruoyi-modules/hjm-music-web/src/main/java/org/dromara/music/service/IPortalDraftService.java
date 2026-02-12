package org.dromara.music.service;

import org.dromara.music.domain.bo.MusicSubmitBo;
import org.dromara.music.domain.vo.MusicDraftVo;

public interface IPortalDraftService {
    Long saveDraft(Long userId, String content);

    MusicDraftVo getDraft(Long id, Long userId);

    Boolean updateDraft(Long id, Long userId, String content);

    Boolean deleteDraft(Long id, Long userId);

    Long submitForAudit(MusicSubmitBo bo);
}
