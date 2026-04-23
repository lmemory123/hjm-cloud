package org.dromara.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 前台认证配置
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.front-auth")
public class FrontAuthProperties {

    /**
     * 前台专用 clientId
     */
    private String clientId;

    /**
     * 授权类型
     */
    private String grantType = "password";

    /**
     * 是否开启前台注册
     */
    private Boolean registerEnabled = true;

}
