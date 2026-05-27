package org.dromara.music.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeveloperApiMetaVo {

    private String apiVersion;

    private String basePath;

    private String authMode;

    private String rateLimit;

    private String apiKeyHeader;

    private String apiKeyConfigKey;

    private String webhookConfigKey;

    private String docsPath;

    private List<Endpoint> endpoints;

    @Data
    public static class Endpoint {

        private String method;

        private String path;

        private String description;

        private Boolean authRequired;

        public static Endpoint of(String method, String path, String description, Boolean authRequired) {
            Endpoint endpoint = new Endpoint();
            endpoint.setMethod(method);
            endpoint.setPath(path);
            endpoint.setDescription(description);
            endpoint.setAuthRequired(authRequired);
            return endpoint;
        }
    }
}
