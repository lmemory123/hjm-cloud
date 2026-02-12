package org.dromara.music;

import com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointAutoConfiguration;
import com.redis.om.spring.annotations.EnableRedisEnhancedRepositories;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@EnableRedisEnhancedRepositories(basePackages = "org.dromara.*.*")
@SpringBootApplication()
public class MusicWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicWebApplication.class, args);
    }
}
