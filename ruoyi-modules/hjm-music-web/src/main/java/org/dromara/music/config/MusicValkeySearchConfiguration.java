package org.dromara.music.config;

import com.momao.valkey.autoconfigure.EnableValkeyQuery;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableValkeyQuery(basePackages = "org.dromara.music.search")
public class MusicValkeySearchConfiguration {
}
