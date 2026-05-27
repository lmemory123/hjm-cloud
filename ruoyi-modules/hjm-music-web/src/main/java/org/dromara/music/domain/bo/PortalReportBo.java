package org.dromara.music.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PortalReportBo {

    @NotBlank(message = "举报原因不能为空")
    private String reason;

    private String description;
}
