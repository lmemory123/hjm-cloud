package org.dromara;

import com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointAutoConfiguration;
import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 系统模块
 *
 * @author ruoyi
 */
@EnableDubbo
@EnableRedisEnhancedRepositories(basePackages = "org.dromara.*.repo")
@SpringBootApplication()
public class RuoYiSystemApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiSystemApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
