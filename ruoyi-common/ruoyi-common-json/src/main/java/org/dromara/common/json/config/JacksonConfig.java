package org.dromara.common.json.config;

import tools.jackson.databind.*;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * jackson 配置
 *
 * @author Lion Li
 */
@Slf4j
public class JacksonConfig {


    @Bean
//    @Primary
    public ObjectMapper objectMapper() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

// 2. 创建自定义 Module 用于存放你的序列化规则
        SimpleModule customTimeModule = new SimpleModule();
        customTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        customTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        MapperBuilder.findModules();
        return JsonMapper.builder()
            .addModules(customTimeModule)
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    }

}

