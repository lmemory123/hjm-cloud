package org.dromara.music.config;

import com.momao.valkey.autoconfigure.EnableValkeyQuery;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(proxyBeanMethods = false)
@EnableValkeyQuery(basePackages = "org.dromara.music.search")
@EnableScheduling
@ConditionalOnProperty(prefix = "valkey.query", name = "enabled", havingValue = "true")
public class MusicValkeySearchConfiguration {
}
