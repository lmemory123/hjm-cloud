package org.dromara.common.redis.config;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.core.util.ObjUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.redis.config.properties.RedissonProperties;
import org.dromara.common.redis.handler.KeyPrefixHandler;
import org.dromara.common.redis.handler.RedisExceptionHandler;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * redis配置
 *
 * @author Lion Li
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisConfiguration {

    @Autowired
    private RedissonProperties redissonProperties;

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return config -> {
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            ObjectMapper om = new ObjectMapper();
            om.registerModule(javaTimeModule);
            om.setTimeZone(TimeZone.getDefault());
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
            TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, om);
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
            KeyPrefixHandler nameMapper = new KeyPrefixHandler(redissonProperties.getKeyPrefix());
            config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                .setUseScriptCache(true)
                .setCodec(codec)
                .setNameMapper(nameMapper);
            if (SpringUtils.isVirtual()) {
                config.setNettyExecutor(new VirtualThreadTaskExecutor("redisson-"));
            }
            RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
            if (ObjUtil.isNotNull(singleServerConfig)) {
                config.useSingleServer()
                    .setTimeout(singleServerConfig.getTimeout())
                    .setClientName(singleServerConfig.getClientName())
                    .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                    .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                    .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                    .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            }
            RedissonProperties.ClusterServersConfig clusterServersConfig = redissonProperties.getClusterServersConfig();
            if (ObjUtil.isNotNull(clusterServersConfig)) {
                config.useClusterServers()
                    .setTimeout(clusterServersConfig.getTimeout())
                    .setClientName(clusterServersConfig.getClientName())
                    .setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())
                    .setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())
                    .setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())
                    .setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())
                    .setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())
                    .setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())
                    .setReadMode(clusterServersConfig.getReadMode())
                    .setSubscriptionMode(clusterServersConfig.getSubscriptionMode());
            }
            log.info("初始化 redis 配置");
        };
    }

    @Resource
    private Environment env;

    @Bean
    @Primary
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        String host = env.getProperty("spring.data.redis.host", "localhost");
        int port = env.getProperty("spring.data.redis.port", Integer.class, 6379);
        String password = env.getProperty("spring.data.redis.password");

        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(host, port);
        if (StrUtil.isNotBlank(password)) {
            conf.setPassword(password);
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestWhileIdle(false);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));
        poolConfig.setNumTestsPerEvictionRun(-1);

        int timeout = 10000;
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
            .connectTimeout(Duration.ofMillis(timeout)).readTimeout(Duration.ofMillis(timeout)).usePooling()
            .poolConfig(poolConfig).build();
        log.info("Jedis client初始化完成");
        return new JedisConnectionFactory(conf, jedisClientConfiguration);
    }

    @Bean
    public RedisExceptionHandler redisExceptionHandler() {
        return new RedisExceptionHandler();
    }
}
