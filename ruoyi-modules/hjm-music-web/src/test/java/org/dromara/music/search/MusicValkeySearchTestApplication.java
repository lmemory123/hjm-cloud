package org.dromara.music.search;

import com.momao.valkey.autoconfigure.EnableValkeyQuery;
import com.momao.valkey.autoconfigure.ValkeyQueryAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableValkeyQuery(basePackages = "org.dromara.music.search")
@ComponentScan(basePackageClasses = MusicSearchRepository.class)
@ImportAutoConfiguration({
    JacksonAutoConfiguration.class,
    ValkeyQueryAutoConfiguration.class
})
public class MusicValkeySearchTestApplication {
}
