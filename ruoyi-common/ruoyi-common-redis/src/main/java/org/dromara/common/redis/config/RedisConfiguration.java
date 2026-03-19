package org.dromara.common.redis.config;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.core.util.ObjUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.redis.config.properties.RedissonProperties;
import org.dromara.common.redis.handler.KeyPrefixHandler;
import org.dromara.common.redis.handler.RedisExceptionHandler;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJackson3Codec;
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
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;

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
            SimpleModule simpleModule = new SimpleModule();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            JsonMapper jsonMapper = JsonMapper.builder()
                .addModules(simpleModule)
                .defaultTimeZone(TimeZone.getDefault())
                .changeDefaultVisibility(visibilityChecker -> visibilityChecker.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY))
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().allowIfSubType((ctxt, clazz) -> true).build(), DefaultTyping.NON_FINAL)
                .build();
            TypedJsonJackson3Codec jsonCodec = new TypedJsonJackson3Codec(Object.class, jsonMapper);
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
