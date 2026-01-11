# Spring Boot 4.0 迁移步骤（基于官方指南）

参考：
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide

适用范围：本项目从 Spring Boot 3.5.x -> 4.0.x。

## 步骤 0：迁移前准备

- 升级到最新的 3.5.x，再开始 4.0 升级，先清理 3.x 的废弃 API。
- 对比 3.5.x 与 4.0.x 的依赖管理差异，确认第三方依赖（例如 Spring Cloud）在 4.0 的兼容版本。
- 系统要求：Java 17+、Kotlin 2.2+（如使用）、GraalVM 25+（如使用）、Jakarta EE 11、Servlet 6.1、Spring Framework 7.x。

## 步骤 1：选择迁移策略（模块化影响）

Spring Boot 4.0 模块化拆分较大，推荐两种策略：

- 直接迁移：按需使用新的 starter（`spring-boot-starter-<technology>`）与 test starter。
- 过渡迁移：先使用 Classic Starter 确保可运行，再逐步替换为精细化 starter。

Classic Starter（过渡方案）：
- `spring-boot-starter-classic`
- `spring-boot-starter-test-classic`

完成迁移后应移除 Classic Starter，改为细粒度 starter。

## 步骤 2：处理已删除功能或不再支持的能力

以下功能在 4.0 移除或调整：

- Undertow：4.0 需要 Servlet 6.1，Undertow 当前不兼容。
- Pulsar Reactive：Reactive Pulsar 管理与自动配置移除。
- “可执行” Uber Jar 启动脚本：不再支持。
- Spring Session Hazelcast、Spring Session MongoDB：Boot 不再直接支持。
- Spock 集成：由于 Groovy 5 未支持，Boot 删除集成。

## 步骤 3：升级构建与依赖配置

- 升级 `spring-boot-dependencies` 至 4.0.x。
- Starter 命名变化：
  - `spring-boot-starter-web` -> `spring-boot-starter-webmvc`
  - `spring-boot-starter-web-services` -> `spring-boot-starter-webservices`
  - `spring-boot-starter-aop` -> `spring-boot-starter-aspectj`
  - OAuth2 相关 starter 重命名为 `spring-boot-starter-security-oauth2-*`
- 如使用非 starter 依赖，请参考模块名 `spring-boot-<technology>`。
- Maven Uber Jar 不再包含 optional 依赖，需要时设置 `<includeOptional>true</includeOptional>`。
- 移除 classic loader 配置：`<loaderImplementation>CLASSIC</loaderImplementation>`。
- Spring Retry / Spring Authorization Server 的版本管理移除，需要显式版本或切换到 Spring Framework 新能力。

## 步骤 4：核心 API 与配置变更

- JSpecify：新增 nullability 注解，可能引发 Kotlin 或 null 检查编译问题。
- Logback 默认 Charset：日志文件默认 UTF-8，控制台优先 `Console#charset()`。
- `BootstrapRegistry` 包迁移：`org.springframework.boot` -> `org.springframework.boot.bootstrap`。
- `EnvironmentPostProcessor` 包迁移：`org.springframework.boot.env` -> `org.springframework.boot`（旧包仍暂时可用）。
- `PropertyMapper` 行为变化：默认不再对 `null` 调用映射，需 `always()` 才会传 `null`。
- DevTools Live Reload 默认关闭，需要时配置 `spring.devtools.livereload.enabled=true`。

## 步骤 5：Jackson 3 与 JSON 相关调整

- Boot 4 默认使用 Jackson 3（`com.fasterxml.jackson` -> `tools.jackson`）。
- 类名与注解更名：
  - `@JsonComponent` -> `@JacksonComponent`
  - `@JsonMixin` -> `@JacksonMixin`
  - `Jackson2ObjectMapperBuilderCustomizer` -> `JsonMapperBuilderCustomizer`
- 配置前缀变更：`spring.jackson.read.*` 与 `spring.jackson.write.*` 迁移至 `spring.jackson.json.read.*` / `spring.jackson.json.write.*`。
- 兼容路径：如短期无法迁移 Jackson 3，可使用 `spring-boot-jackson2`，并设置 `spring.jackson.use-jackson2-defaults=true`。

## 步骤 6：Web / Actuator / Data / Messaging / IO 调整

- Actuator：
  - 健康探针默认启用，按需配置 `management.endpoint.health.probes.enabled=false`。
  - `org.springframework.lang.Nullable` 不再支持，改用 `org.jspecify.annotations.Nullable`。
- Web：
  - `HttpMessageConverters` 已弃用，推荐 `ClientHttpMessageConvertersCustomizer` / `ServerHttpMessageConvertersCustomizer`。
  - 静态资源新增 `/fonts/**`，可按需排除。
  - Spring Session Redis/Mongo 属性前缀调整为 `spring.session.data.*`。
  - Jersey 4 不支持 Jackson 3，需 `spring-boot-jackson2`。
- Data：
  - Elasticsearch 使用 `Rest5Client`，相关自定义器改为 `Rest5ClientBuilderCustomizer`。
  - 新增 `spring-boot-persistence` 模块，`@EntityScan` 包名改变。
  - `spring.dao.exceptiontranslation.enabled` -> `spring.persistence.exceptiontranslation.enabled`。
  - Mongo 属性迁移：`spring.data.mongodb.*` -> `spring.mongodb.*`（部分保留）。
  - UUID/BigDecimal 表示需显式配置。
- Messaging：
  - Kafka Streams 自定义器改为 `StreamsBuilderFactoryBeanConfigurer`。
  - Kafka `spring.kafka.retry.topic.backoff.random` -> `spring.kafka.retry.topic.backoff.jitter`。
  - AMQP 重试定制器拆分为 `RabbitTemplateRetrySettingsCustomizer` 和 `RabbitListenerRetrySettingsCustomizer`。
- IO：
  - Spring Batch 默认改为非数据库模式，需数据库时使用 `spring-boot-starter-batch-jdbc`。

## 步骤 7：测试相关调整

- `@SpringBootTest` 不再自动提供 MockMvc / WebTestClient / TestRestTemplate：
  - MockMvc：`@AutoConfigureMockMvc`
  - WebTestClient：`@AutoConfigureWebTestClient`
  - TestRestTemplate：`@AutoConfigureTestRestTemplate`
  - RestTestClient：`@AutoConfigureRestTestClient`
- MockitoTestExecutionListener 已移除，改用 `MockitoExtension`。
- `TestRestTemplate` 包名变化，需要 `spring-boot-resttestclient` 依赖。
- `@PropertyMapping` 包迁移到 `org.springframework.boot.test.context`。
- `@MockBean`/`@SpyBean` 已弃用，迁移到 `@MockitoBean`/`@MockitoSpyBean`。

## 步骤 8：项目内关注点（基于当前仓库）

请按以下路径重点检查：

- Undertow：
  - `ruoyi-visual/ruoyi-monitor/pom.xml`
  - `script/config/nacos/application-common.yml`
  - `README.md` 中关于 Undertow 的说明也需同步更新。
- Starter 重命名：
  - `spring-boot-starter-web` 在 `ruoyi-common/ruoyi-common-web/pom.xml`、`ruoyi-visual/ruoyi-nacos/pom.xml`、`ruoyi-visual/ruoyi-monitor/pom.xml`。
- EnvironmentPostProcessor 包变更：
  - `ruoyi-common/ruoyi-common-elasticsearch/src/main/resources/META-INF/spring.factories`
  - `ruoyi-common/ruoyi-common-loadbalancer/src/main/resources/META-INF/spring.factories`
- Kafka Starter：
  - `ruoyi-example/ruoyi-test-mq/pom.xml` 当前直接使用 `spring-kafka`，建议改为 `spring-boot-starter-kafka`。

## 步骤 9：验证与回归

- 全量编译：确保所有模块可编译通过。
- 单测与集成测试：重点验证 Web、消息、数据库、缓存、鉴权链路。
- 生产相关：健康检查探针、日志编码、静态资源路径、监控端点。

## 输出物

- 依赖变更清单（POM / Gradle）。
- 配置变更清单（YAML / properties）。
- 需要代码修改的 API 清单（包含替代方案）。

