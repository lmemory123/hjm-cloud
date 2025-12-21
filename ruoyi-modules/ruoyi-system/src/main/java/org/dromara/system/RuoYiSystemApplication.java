package org.dromara.system;

import com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointAutoConfiguration;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.core.env.Environment;

/**
 * 系统模块
 *
 * @author ruoyi
 */
@EnableDubbo
@EnableRedisEnhancedRepositories(basePackages = "org.dromara.*.*")
@SpringBootApplication(exclude = {
    NacosConfigEndpointAutoConfiguration.class, // 解决 Nacos 启动报错
})
public class RuoYiSystemApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiSystemApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
