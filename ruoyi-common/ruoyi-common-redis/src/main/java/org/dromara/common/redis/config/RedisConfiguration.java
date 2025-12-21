package org.dromara.common.redis.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.esotericsoftware.kryo.serializers.TimeSerializers;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
import org.redisson.config.NameMapper;
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
import tools.jackson.databind.ObjectMapper;
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
            config.setNameMapper(new KeyPrefixHandler(redissonProperties.getKeyPrefix()));

            SimpleModule module = new SimpleModule();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            var typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubTypeIsArray()
                .build();

            ObjectMapper  om = JsonMapper.builder()
                .addModule(module)
                .changeDefaultVisibility(vc -> vc.withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                .defaultTimeZone(TimeZone.getDefault())
                .activateDefaultTypingAsProperty(typeValidator, DefaultTyping.NON_FINAL, "@class")
                .build();


//            ObjectMapper om = new ObjectMapper();
//            om.registerModule(javaTimeModule);
//            om.setTimeZone(TimeZone.getDefault());
//            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//            // 指定序列化输入的类型，类必须是非final修饰的。序列化时将对象全类名一起保存下来
//            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
//            LoggerFactory.useSlf4jLogging(true);
//            FuryCodec furyCodec = new FuryCodec();
//            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, furyCodec, furyCodec);
            TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, om.getClass());
            // 组合序列化 key 使用 String 内容使用通用 json 格式
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
            config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                // 缓存 Lua 脚本 减少网络传输(redisson 大部分的功能都是基于 Lua 脚本实现)
                .setUseScriptCache(true)
                .setCodec(codec);
            if (SpringUtils.isVirtual()) {
                config.setNettyExecutor(new VirtualThreadTaskExecutor("redisson-"));
            }
            RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                // 使用单机模式
                config.useSingleServer()
                    //设置redis key前缀
                    .setTimeout(singleServerConfig.getTimeout())
                    .setClientName(singleServerConfig.getClientName())
                    .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                    .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                    .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                    .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            }
            // 集群配置方式 参考下方注释
            RedissonProperties.ClusterServersConfig clusterServersConfig = redissonProperties.getClusterServersConfig();
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                config.useClusterServers()
                    //设置redis key前缀

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
        System.out.println("使用 Jedis 作为 Redis 连接方式");
        String host = env.getProperty("spring.data.redis.host", "localhost");
        int port = env.getProperty("spring.data.redis.port", Integer.class, 6379);
        String password = env.getProperty("spring.data.redis.password");

        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(host, port);
        if (StrUtil.isNotBlank(password)) {
            conf.setPassword(password);
        }

        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setTestWhileIdle(false);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(30000));
        poolConfig.setNumTestsPerEvictionRun(-1);

        final int timeout = 10000;

        final JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
            .connectTimeout(Duration.ofMillis(timeout)).readTimeout(Duration.ofMillis(timeout)).usePooling()
            .poolConfig(poolConfig).build();
        log.info("Jedis client初始化完成");
        return new JedisConnectionFactory(conf, jedisClientConfiguration);
    }



    /**
     * 异常处理器
     */
    @Bean
    public RedisExceptionHandler redisExceptionHandler() {
        return new RedisExceptionHandler();
    }

    /**
     * redis集群配置 yml
     *
     * --- # redis 集群配置(单机与集群只能开启一个另一个需要注释掉)
     * spring.data:
     *   redis:
     *     cluster:
     *       nodes:
     *         - 192.168.0.100:6379
     *         - 192.168.0.101:6379
     *         - 192.168.0.102:6379
     *     # 密码
     *     password:
     *     # 连接超时时间
     *     timeout: 10s
     *     # 是否开启ssl
     *     ssl.enabled: false
     *
     * redisson:
     *   # 线程池数量
     *   threads: 16
     *   # Netty线程池数量
     *   nettyThreads: 32
     *   # 集群配置
     *   clusterServersConfig:
     *     # 客户端名称
     *     clientName: ${ruoyi.name}
     *     # master最小空闲连接数
     *     masterConnectionMinimumIdleSize: 32
     *     # master连接池大小
     *     masterConnectionPoolSize: 64
     *     # slave最小空闲连接数
     *     slaveConnectionMinimumIdleSize: 32
     *     # slave连接池大小
     *     slaveConnectionPoolSize: 64
     *     # 连接空闲超时，单位：毫秒
     *     idleConnectionTimeout: 10000
     *     # 命令等待超时，单位：毫秒
     *     timeout: 3000
     *     # 发布和订阅连接池大小
     *     subscriptionConnectionPoolSize: 50
     *     # 读取模式
     *     readMode: "SLAVE"
     *     # 订阅模式
     *     subscriptionMode: "MASTER"
     */

}

