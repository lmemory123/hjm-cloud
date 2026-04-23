package org.dromara.music.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MusicFullDetailVo extends MusicDetailVo {

    private MusicStatVo stat;

    private List<MusicNotifyLogVo> notifyLogs;

    private List<MusicCommentVo> comments;
}
