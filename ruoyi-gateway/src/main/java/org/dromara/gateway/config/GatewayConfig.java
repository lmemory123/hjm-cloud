package org.dromara.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关路由配置
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("music-all", r -> r.path("/music/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://hjm-music-web:9212"))
            .route("auth-all", r -> r.path("/auth/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://ruoyi-auth:9210"))
            .route("system-all", r -> r.path("/system/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://ruoyi-system:9201"))
            .build();
    }
}
