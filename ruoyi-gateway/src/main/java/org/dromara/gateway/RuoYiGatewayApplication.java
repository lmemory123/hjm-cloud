package org.dromara.gateway;

import com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 网关启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = {
    NacosConfigEndpointAutoConfiguration.class,
})
public class RuoYiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiGatewayApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  网关启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
