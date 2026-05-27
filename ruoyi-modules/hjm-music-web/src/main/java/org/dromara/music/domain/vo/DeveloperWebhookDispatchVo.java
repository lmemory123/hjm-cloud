package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeveloperWebhookDispatchVo {

    private String eventType;

    private String triggeredBy;

    private Integer attempted;

    private Integer succeeded;

    private Integer failed;

    private String dispatchedAt;

    private String message;

    private List<Result> results;

    @Data
    public static class Result {

        private String id;

        private String name;

        private String url;

        private Boolean success;

        private Integer statusCode;

        private String message;
    }
}
