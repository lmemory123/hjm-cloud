package org.dromara.auth;

import com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointAutoConfiguration;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 认证授权中心
 *
 * @author ruoyi
 */
@EnableDubbo
@SpringBootApplication(exclude = {
    NacosConfigEndpointAutoConfiguration.class,
})
public class RuoYiAuthApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiAuthApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  认证授权中心启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
