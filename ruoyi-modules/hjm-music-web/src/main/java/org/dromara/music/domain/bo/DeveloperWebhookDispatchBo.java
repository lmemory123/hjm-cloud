package org.dromara.music.domain.bo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class DeveloperWebhookDispatchBo {

    @NotBlank(message = "事件类型不能为空")
    private String eventType;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String targetUrl;

    private Map<String, Object> data;
}
