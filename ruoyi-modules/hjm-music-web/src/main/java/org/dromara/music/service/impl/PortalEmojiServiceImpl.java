package org.dromara.music.service.impl;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.music.domain.Emoji;
import org.dromara.music.domain.bo.EmojiBo;
import org.dromara.music.mapper.EmojiMapper;
import org.dromara.music.service.IPortalEmojiService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PortalEmojiServiceImpl implements IPortalEmojiService {

    private final EmojiMapper emojiMapper;

    @Override
    public Boolean submitEmoji(EmojiBo bo) {
        Emoji add = MapstructUtils.convert(bo, Emoji.class);
        return emojiMapper.insert(add) > 0;
    }
}
