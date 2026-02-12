package org.dromara.common.encrypt.config;

import com.mybatisflex.spring.boot.v4.MybatisFlexAutoConfiguration;import com.mybatisflex.spring.boot.v4.MybatisFlexProperties;import jakarta.annotation.Resource;import org.dromara.common.encrypt.core.EncryptorManager;
import org.dromara.common.encrypt.interceptor.MybatisDecryptInterceptor;
import org.dromara.common.encrypt.interceptor.MybatisEncryptInterceptor;
import org.dromara.common.encrypt.properties.EncryptorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 加解密配置
 *
 * @author 老马
 * @version 4.6.0
 */
@AutoConfiguration(after = MybatisFlexAutoConfiguration.class)
@EnableConfigurationProperties({EncryptorProperties.class, MybatisFlexProperties.class})
@ConditionalOnClass(MybatisFlexAutoConfiguration.class)
@ConditionalOnProperty(value = "mybatis-encryptor.enable", havingValue = "true")
public class EncryptorAutoConfiguration {

    @Resource
    private EncryptorProperties properties;

    @Bean
    public EncryptorManager encryptorManager(MybatisFlexProperties mybatisPlusProperties) {
        return new EncryptorManager(mybatisPlusProperties.getTypeAliasesPackage());
    }

    @Bean
    public MybatisEncryptInterceptor mybatisEncryptInterceptor(EncryptorManager encryptorManager) {
        return new MybatisEncryptInterceptor(encryptorManager, properties);
    }

    @Bean
    public MybatisDecryptInterceptor mybatisDecryptInterceptor(EncryptorManager encryptorManager) {
        return new MybatisDecryptInterceptor(encryptorManager, properties);
    }
}
