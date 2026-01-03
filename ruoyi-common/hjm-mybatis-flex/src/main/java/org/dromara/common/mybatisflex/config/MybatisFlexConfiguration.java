package org.dromara.common.mybatisflex.config;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.dromara.common.mybatisflex.aspect.DataPermissionPointcutAdvisor;
import org.dromara.common.mybatisflex.core.domain.BaseEntity;
import org.dromara.common.mybatisflex.dialect.AuthPostgreDialect;
import org.dromara.common.mybatisflex.handler.InjectionMetaObjectHandler;
import org.dromara.common.mybatisflex.handler.MybatisExceptionHandler;
import org.dromara.common.mybatisflex.service.SysDataScopeService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Role;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mybatis-flex配置类
 *
 * @author Lion Li
 */
@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("${mybatis-flex.mapper-package:${mybatis-plus.mapperPackage:org.dromara.**.mapper}}")
@PropertySource(value = "classpath:common-mybatis-flex.yml", factory = YmlPropertySourceFactory.class)
public class MybatisFlexConfiguration {


    /**
     * 数据权限切面处理器
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataPermissionPointcutAdvisor dataPermissionPointcutAdvisor() {
        return new DataPermissionPointcutAdvisor();
    }

    /**
     * 元对象字段填充监听器
     */
    @Bean
    public InjectionMetaObjectHandler injectionMetaObjectHandler() {
        return new InjectionMetaObjectHandler();
    }

    /**
     * 全局配置补充
     */
    @Bean
    public MyBatisFlexCustomizer myBatisFlexCustomizer(InjectionMetaObjectHandler handler) {
        // 使用内置规则自动忽略 null 和 空字符串
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_EMPTY);
        // 使用内置规则自动忽略 null 和 空白字符串
        QueryColumnBehavior.setIgnoreFunction(QueryColumnBehavior.IGNORE_BLANK);
        // 智能将 IN 转换为 EQ
        QueryColumnBehavior.setSmartConvertInToEquals(true);
        // 其他自定义规则
//        QueryColumnBehavior.setIgnoreFunction(o -> {...});
        return globalConfig -> {
            globalConfig.registerInsertListener(handler, BaseEntity.class);
            globalConfig.registerUpdateListener(handler, BaseEntity.class);
            // 注册数据权限方言（遵循 MyBatis-Flex 官方数据权限方案）
//            DialectFactory.registerDialect(DbType.MYSQL, new AuthDialectImpl());
//            DialectFactory.registerDialect(DbType.ORACLE, new AuthOracleDialect());
            DialectFactory.registerDialect(DbType.POSTGRE_SQL, new AuthPostgreDialect());
//            DialectFactory.registerDialect(DbType.SQLSERVER, new AuthSqlServerDialect());
        };
    }

    /**
     * 异常处理器
     */
    @Bean
    public MybatisExceptionHandler mybatisExceptionHandler() {
        return new MybatisExceptionHandler();
    }

    /**
     * 数据权限处理实现
     */
    @Bean("sdss")
    public SysDataScopeService sysDataScopeService() {
        return new SysDataScopeService();
    }
}
