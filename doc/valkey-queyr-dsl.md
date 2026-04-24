# Valkey QueryDSL

Valkey QueryDSL 是一个面向 Java 17 的多模块 Maven 项目，用来为 Valkey Search 提供编译期生成的类型安全查询能力。

它的目标不是再包一层字符串 API，而是把下面几件事一起做掉：

- 通过注解和 APT 生成强类型 Query 类
- 通过 Repository 抽象统一 JSON / HASH 存储访问
- 在 Spring Boot 中自动装配 GlideClient 与索引初始化能力
- 在运行时尽量少做反射和动态解析，把复杂度前移到编译期

当前版本既支持本地源码安装，也支持直接从 Maven Central 引用已发布版本。

## 1. 模块说明

项目采用多模块结构：

- `valkey-query-annotations`
  说明：定义 `@ValkeyDocument`、`@ValkeyId`、`@ValkeySearchable`、`@ValkeyIndexed` 等注解。
- `valkey-query-core`
  说明：定义 `SearchCondition`、`ValkeyRepository`、`ValkeyQueryChain`、元数据模型等核心抽象。
- `valkey-query-processor`
  说明：APT 处理器，在编译期根据实体生成 Query 类和元数据。
- `valkey-query-glide-adapter`
  说明：基于 `valkey-glide` 的 Repository 实现和索引创建逻辑。
- `valkey-query-spring-boot-starter`
  说明：Spring Boot 自动配置、属性绑定、索引自动初始化。
- `valkey-query-test-example`
  说明：示例工程，包含 JSON、HASH、嵌套对象、分页、排序、测试样例。

## 2. 核心能力

- 支持编译期生成 Query 类，默认类名后缀为 `Query`
- 支持 `JSON` 和 `HASH` 两种存储模式
- 支持文本字段、标签字段、数值字段建模
- 支持嵌套对象索引展开
- 支持 `list`、`page`、`one`、`count` 查询
- 支持链式查询拼装
- 支持启动时自动建索引
- 支持远端索引定义与本地元数据不一致时自动重建

## 3. 快速开始

### 3.1 本地安装

如果你要在其他项目里直接使用当前版本，先在本项目根目录执行：

```bash
mvn clean install
```

执行后会发生两件事：

- 每个模块会在各自的 `target` 目录下生成 jar
- 产物会安装到本机 Maven 本地仓库 `~/.m2/repository/com/momao/`

如果不依赖本地快照，优先直接使用 Maven Central 上的正式发布坐标。

### 3.2 外部项目依赖

在你的其他项目里，至少需要引入 Starter：

```xml
<dependency>
    <groupId>io.github.lmemory123</groupId>
    <artifactId>valkey-query-spring-boot-starter</artifactId>
    <version>1.1.0-RC3</version>
</dependency>
```

如果你要让实体在编译期生成 `XXXQuery` 类，还需要把处理器加到 `maven-compiler-plugin`：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>io.github.lmemory123</groupId>
                        <artifactId>valkey-query-processor</artifactId>
                        <version>1.1.0-RC3</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 3.3 启用扫描

在 Spring Boot 启动类或配置类上添加：

```java
@EnableValkeyQuery(basePackages = "com.example.demo")
@SpringBootApplication
public class DemoApplication {
}
```

### 3.4 基础配置

示例配置如下，请替换成你自己的地址和凭证，不要直接照抄示例工程中的敏感值：

```yaml
valkey:
  query:
    enabled: true
    mode: standalone
    read-preference: primary
    use-tls: false
    request-timeout: 5000
    connection-timeout: 5000
    username: your-username
    password: your-password
    index-management:
      mode: none
    standalone:
      nodes:
        - host: 127.0.0.1
          port: 6379
```

## 4. 入门示例

### 4.1 定义 JSON 实体

```java
@ValkeyDocument
public class Sku {

    @ValkeyId
    private String id;

    @ValkeySearchable(weight = 2.5d, noStem = true)
    private String title;

    @ValkeyIndexed(sortable = true)
    private Integer price;

    @ValkeyIndexed
    private List<String> tags;

    @ValkeyIndexed
    private Merchant merchant;
}
```

对应嵌套对象：

```java
public class Merchant {

    @ValkeyIndexed
    private String name;

    private String level;
}
```

编译后会生成 `SkuQuery`，你可以直接使用：

```java
SkuQuery q = new SkuQuery();
SearchCondition condition = q.title.eq("iPhone")
    .and(q.price.between(1000, 5000))
    .and(q.merchant.name.eq("Apple Flagship"))
    .sortBy("price", true);
```

### 4.2 定义 HASH 实体

`@ValkeyDocument` 默认存储类型是 `JSON`。如果你要走 HASH，必须显式指定：

```java
@ValkeyDocument(
    value = "student",
    indexName = "idx:student",
    prefixes = {"student:"},
    storageType = StorageType.HASH
)
public class Student {

    @ValkeyId
    private Long id;

    @ValkeySearchable(weight = 1.5d)
    private String name;

    @ValkeyIndexed(sortable = true)
    private Integer age;

    @ValkeyIndexed(sortable = true)
    private Double score;

    @ValkeySearchable("class_name")
    private String className;

    @ValkeyIndexed
    private String department;
}
```

## 5. Repository 使用方式

推荐做法是让具体仓库继承 `BaseValkeyRepository<T>`：

```java
@Repository
public class SkuRepository extends BaseValkeyRepository<Sku> {

    public SkuRepository(ValkeyClientRouting clientRouting, ObjectMapper objectMapper) {
        super(SkuQuery.METADATA, clientRouting, Sku.class, objectMapper);
    }
}
```

`SkuQuery.METADATA` 是处理器生成的索引元数据，包含：

- indexName
- prefix / prefixes
- storageType
- fields

### 5.1 常用方法

`ValkeyRepository<T>` 当前提供这些核心方法：

- `checkAndCreateIndex()`
- `save(String id, T entity)`
- `search(SearchCondition condition)`
- `list(SearchCondition condition)`
- `page(SearchCondition condition, int offset, int limit)`
- `one(SearchCondition condition)`
- `count(SearchCondition condition)`
- `queryChain()`

## 6. 查询方式

### 6.1 直接使用 SearchCondition

```java
SkuQuery q = new SkuQuery();
SearchCondition condition = q.tags.contains("HOT")
    .and(q.price.between(1000, 3000))
    .sortBy("price", true);

Page<Sku> page = skuRepository.page(condition, 0, 20);
```

### 6.2 使用 QueryChain

```java
SkuQuery q = new SkuQuery();

Sku matched = skuRepository.queryChain()
    .where(q.title.eq("IntegrationPro"))
    .and(q.merchant.name.eq("Apple Flagship"))
    .one();

Page<Sku> result = skuRepository.queryChain()
    .where(q.tags.contains("HOT"))
    .orderByAsc("price")
    .page(0, 10);

long total = skuRepository.queryChain()
    .where(q.tags.contains("HOT"))
    .count();
```

### 6.3 条件拼接能力

当前查询链支持：

- `where(...)`
- `and(...)`
- `or(...)`
- `andIf(boolean, Supplier<SearchCondition>)`
- `orIf(boolean, Supplier<SearchCondition>)`
- `orderByAsc(...)`
- `orderByDesc(...)`
- `list()`
- `page(offset, limit)`
- `one()`
- `count()`

## 7. 注解说明

### 7.1 `@ValkeyDocument`

作用：标记一个实体需要生成 Query 类型和索引元数据。

参数说明：

- `value`
  说明：实体逻辑名，可选。
- `indexName`
  说明：索引名；为空时按默认规则生成。
- `prefix`
  说明：旧字段，已废弃。
- `prefixes`
  说明：索引前缀集合。
- `storageType`
  说明：存储类型，默认 `StorageType.JSON`。
- `querySuffix`
  说明：生成类后缀，默认是 `Query`。

建议：

- 新代码优先使用 `prefixes`，不要继续使用已废弃的 `prefix`
- 如果要用 HASH，必须显式设置 `storageType = StorageType.HASH`

### 7.2 `@ValkeyId`

作用：标记主键字段。

说明：

- 用于实体主键映射
- 生成的 Query 中通常对应 TAG 语义
- 保存时通常会拼接到 `prefix + id`

### 7.3 `@ValkeySearchable`

作用：标记文本检索字段。

参数说明：

- `value`
  说明：别名或路径映射，可选
- `weight`
  说明：文本权重，默认 `1.0`
- `noStem`
  说明：是否禁用词干处理，默认 `false`
- `sortable`
  说明：是否支持排序，默认 `false`

适用场景：

- 标题
- 名称
- 关键词描述
- 需要全文匹配的文本内容

### 7.4 `@ValkeyIndexed`

作用：标记普通索引字段。

参数说明：

- `value`
  说明：别名或路径映射，可选
- `sortable`
  说明：是否支持排序，默认 `false`

适用场景：

- 数值字段，例如价格、年龄、分数
- 标签集合，例如 `List<String>`
- 枚举风格字符串
- 嵌套对象字段展开

## 8. 嵌套对象与别名规则

### 8.1 嵌套对象

对于下面这种结构：

```java
@ValkeyIndexed
private Merchant merchant;
```

如果 `Merchant.name` 也被索引，则会生成：

- 扁平别名，例如 `merchant_name`
- 嵌套访问器，例如 `q.merchant.name`

这意味着：

```java
q.merchant.name.eq("Apple Flagship")
```

可以直接参与条件构建。

### 8.2 字段别名

例如：

```java
@ValkeySearchable("class_name")
private String className;
```

这里 Java 字段名是 `className`，索引别名则会映射为 `class_name`。

## 9. 从入门到进阶的建议路径

### 9.1 入门阶段

建议先掌握下面四件事：

- 给实体加 `@ValkeyDocument`
- 给主键加 `@ValkeyId`
- 给文本字段加 `@ValkeySearchable`
- 给数值或标签字段加 `@ValkeyIndexed`

### 9.2 进阶阶段

接着再使用：

- `prefixes` 自定义键前缀
- `StorageType.HASH` / `StorageType.JSON`
- 嵌套对象字段索引
- `sortable = true` 与排序查询
- `queryChain()` 做链式查询

### 9.3 高阶阶段

最后再关注：

- 启动时自动建索引
- 索引不一致自动重建
- 多前缀索引管理
- 外部项目集成与本地 Maven 仓库复用

## 10. 自动建索引机制

Starter 中会基于 `@EnableValkeyQuery(basePackages = ...)` 扫描包路径，并在存在 `ValkeyQueryPackages` Bean 时创建 `ValkeyIndexAutoCreator`。

Repository 执行 `checkAndCreateIndex()` 时：

- 如果索引不存在，则创建
- 如果索引已存在但远端 schema 与本地生成 metadata 不一致，则会删除后重建

这能避免“索引名存在，但字段定义已经变了”导致的查询异常。

## 11. 在其他项目中验证可用性

如果你要在别的项目里快速验证，推荐这样做：

1. 在当前项目根目录执行 `mvn clean install`
2. 在目标项目里添加 Starter 依赖
3. 在目标项目的编译插件中添加 `valkey-query-processor`
4. 编写实体并加上注解
5. 执行一次 Maven 编译，确认 `XXXQuery` 已生成
6. 启动 Spring Boot 项目，检查索引自动创建是否成功

## 12. 当前版本注意事项

- 当前推荐使用 Spring Boot 集成方式
- `@ValkeyDocument.prefix()` 已废弃，建议使用 `prefixes()`
- `ValkeyQueryChain.select(...)` 目前还是占位 API，暂时不要依赖它做字段裁剪
- 本项目当前未发布到 Maven Central，推荐先通过本地 `install` 进行集成测试

## 13. 产物说明

执行 `mvn clean install` 后，通常会得到这些可复用产物：

- `valkey-query-annotations/target/valkey-query-annotations-1.0.0-SNAPSHOT.jar`
- `valkey-query-core/target/valkey-query-core-1.0.0-SNAPSHOT.jar`
- `valkey-query-processor/target/valkey-query-processor-1.0.0-SNAPSHOT.jar`
- `valkey-query-glide-adapter/target/valkey-query-glide-adapter-1.0.0-SNAPSHOT.jar`
- `valkey-query-spring-boot-starter/target/valkey-query-spring-boot-starter-1.0.0-SNAPSHOT.jar`

如果你的其他项目走 Maven，本地仓库里的坐标通常更方便直接使用。
