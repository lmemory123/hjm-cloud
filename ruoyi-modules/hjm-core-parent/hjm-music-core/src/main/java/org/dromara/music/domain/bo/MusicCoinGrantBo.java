package org.dromara.music.domain.bo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MusicCoinGrantBo {

    @NotEmpty(message = "用户ID不能为空")
    private List<Long> userIds;

    @NotNull(message = "发放金额不能为空")
    private Long amount;

    private String reasonCode;
}
