package org.dromara.system;

import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: TokyoMomao
 * DateTime: 2025-12-22 10:23
 */
@EnableRedisEnhancedRepositories(basePackages = "org.dromara.*.repository")
@SpringBootApplication()
public class MusicApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MusicApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
