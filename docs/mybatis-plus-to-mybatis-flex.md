# MyBatis-Plus -> MyBatis-Flex 迁移文档（Boot 4）

## 目标与约束
- 目标：将项目中的 MyBatis-Plus 全量迁移到 MyBatis-Flex，并对齐 Spring Boot 4。
- 约束：保持现有功能（分页、逻辑删除、乐观锁、多租户、数据权限、自动填充、批量操作、代码生成）不变。
- 说明：本文不展开 Flex 的具体 API 用法，仅给出初步替换方向与操作步骤。

## 现有 MyBatis-Plus 使用点清单（必须全部处理）

### 依赖/版本
- `pom.xml`：`mybatis-plus.version`、`mybatis-plus-spring-boot4-starter`、`mybatis-plus-jsqlparser`、`mybatis-plus-annotation`、`warm-flow-mybatis-plus-sb3-starter`
- `ruoyi-common/ruoyi-common-mybatis/pom.xml`：`mybatis-plus-spring-boot4-starter`、`mybatis-plus-jsqlparser`
- `ruoyi-common/ruoyi-common-encrypt/pom.xml`：`mybatis-plus-spring-boot4-starter`（optional）
- `ruoyi-modules/ruoyi-workflow/pom.xml`：`warm-flow-mybatis-plus-sb3-starter`
- 生成文件（无需手工改，构建时再生成）：`.flattened-pom.xml`、`ruoyi-common/.../.flattened-pom.xml`、`ruoyi-modules/ruoyi-workflow/.flattened-pom.xml`

### 配置
- `ruoyi-common/ruoyi-common-mybatis/src/main/resources/common-mybatis.yml`：`mybatis-plus` 全量配置
- `script/config/nacos/application-common.yml`：`mybatis-plus` 全量配置
- `ruoyi-visual/ruoyi-snailjob-server/src/main/resources/application.yml`：`mybatis-plus` 配置
- `ruoyi-common/ruoyi-common-mybatis/src/main/resources/spy.properties`：MP 专用 P6Spy 日志工厂

### 通用基础设施（ruoyi-common）
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/config/MybatisPlusConfiguration.java`
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/interceptor/PlusDataPermissionInterceptor.java`
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/handler/InjectionMetaObjectHandler.java`
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/handler/PlusPostInitTableInfoHandler.java`
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/core/mapper/BaseMapperPlus.java`
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/core/page/PageQuery.java`
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/core/page/TableDataInfo.java`
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/core/domain/BaseEntity.java`
- `ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/helper/DataPermissionHelper.java`
- `ruoyi-common/ruoyi-common-encrypt/src/main/java/org/dromara/common/encrypt/config/EncryptorAutoConfiguration.java`
- `ruoyi-common/ruoyi-common-tenant/src/main/java/org/dromara/common/tenant/config/TenantConfiguration.java`
- `ruoyi-common/ruoyi-common-tenant/src/main/java/org/dromara/common/tenant/handle/PlusTenantLineHandler.java`
- `ruoyi-common/ruoyi-common-tenant/src/main/java/org/dromara/common/tenant/helper/TenantHelper.java`
- `ruoyi-common/ruoyi-common-tenant/src/main/java/org/dromara/common/tenant/manager/TenantSpringCacheManager.java`
- `ruoyi-common/ruoyi-common-tenant/src/main/java/org/dromara/common/tenant/handle/TenantKeyPrefixHandler.java`

### 业务模块（Java 代码）
- `ruoyi-modules/ruoyi-system/src/main/java/org/dromara/system/domain/`：
  `SysUserRole.java`、`SysUserPost.java`、`SysUser.java`、`SysTenantPackage.java`、`SysTenant.java`、`SysSocial.java`、`SysRoleMenu.java`、`SysRoleDept.java`、`SysRole.java`、`SysPost.java`、`SysOperLog.java`、`SysNotice.java`、`SysMenu.java`、`SysLogininfor.java`、`SysDictType.java`、`SysDictData.java`、`SysDept.java`、`SysConfig.java`、`SysClient.java`
- `ruoyi-modules/ruoyi-system/src/main/java/org/dromara/system/mapper/`：
  `SysDictDataMapper.java`、`SysDeptMapper.java`、`SysPostMapper.java`、`SysMenuMapper.java`、`SysUserRoleMapper.java`、`SysRoleMenuMapper.java`、`SysRoleMapper.java`、`SysUserMapper.java`
- `ruoyi-modules/ruoyi-system/src/main/java/org/dromara/system/service/impl/`：
  `SysUserServiceImpl.java`、`SysTenantServiceImpl.java`、`SysTenantPackageServiceImpl.java`、`SysSocialServiceImpl.java`、`SysRoleServiceImpl.java`、`SysPostServiceImpl.java`、`SysOperLogServiceImpl.java`、`SysNoticeServiceImpl.java`、`SysMenuServiceImpl.java`、`SysConfigServiceImpl.java`、`SysLogininforServiceImpl.java`、`SysClientServiceImpl.java`、`SysDictTypeServiceImpl.java`、`SysDeptServiceImpl.java`、`SysDictDataServiceImpl.java`
- `ruoyi-modules/ruoyi-system/src/main/java/org/dromara/system/dubbo/`：
  `RemoteUserServiceImpl.java`、`RemoteRoleServiceImpl.java`、`RemoteDeptServiceImpl.java`、`RemoteDataScopeServiceImpl.java`、`RemotePostServiceImpl.java`
- `ruoyi-modules/ruoyi-resource/src/main/java/org/dromara/resource/`：
  `service/impl/SysOssServiceImpl.java`、`service/impl/SysOssConfigServiceImpl.java`、`domain/SysOss.java`、`domain/SysOssConfig.java`
- `ruoyi-modules/ruoyi-gen/src/main/java/org/dromara/gen/`：
  `domain/GenTable.java`、`domain/GenTableColumn.java`、`mapper/GenTableMapper.java`、`mapper/GenTableColumnMapper.java`、`service/GenTableServiceImpl.java`
- `ruoyi-modules/ruoyi-workflow/src/main/java/org/dromara/workflow/`：
  `domain/FlowInstanceBizExt.java`、`domain/TestLeave.java`、`domain/FlowCategory.java`、`domain/FlowSpel.java`、
  `mapper/FlwTaskMapper.java`、`mapper/FlwCategoryMapper.java`、`mapper/FlwInstanceMapper.java`、`mapper/FlwInstanceBizExtMapper.java`、
  `service/impl/FlwCategoryServiceImpl.java`、`service/impl/FlwInstanceServiceImpl.java`、`service/impl/FlwDefinitionServiceImpl.java`、
  `service/impl/TestLeaveServiceImpl.java`、`service/impl/FlwChartExtServiceImpl.java`、`service/impl/FlwSpelServiceImpl.java`、`service/impl/FlwTaskServiceImpl.java`
- `ruoyi-modules/hjm-music/src/main/java/org/dromara/system/domain/`：
  `MusicResource.java`、`MusicAuditLog.java`
- `ruoyi-visual/ruoyi-snailjob-server/src/main/java/com/aizuda/snailjob/server/common/register/ServerRegister.java`

### 示例/测试（Java 代码）
- `ruoyi-example/ruoyi-demo/src/main/java/org/dromara/demo/`：
  `domain/ShardingOrder.java`、`domain/ShardingOrderItem.java`、`domain/TestDemo.java`、`domain/TestDemoEncrypt.java`、`domain/TestTree.java`、
  `mapper/ShardingOrderMapper.java`、`mapper/ShardingOrderItemMapper.java`、`mapper/TestDemoMapper.java`、
  `service/impl/TestTreeServiceImpl.java`、`service/impl/TestDemoServiceImpl.java`、
  `controller/TestBatchController.java`、`controller/TestShardingController.java`
- `ruoyi-example/ruoyi-demo/src/test/java/org/dromara/demo/TOrderTest.java`

### 代码生成模板
- `ruoyi-modules/ruoyi-gen/src/main/resources/vm/java/domain.java.vm`
- `ruoyi-modules/ruoyi-gen/src/main/resources/vm/java/serviceImpl.java.vm`

### 文档引用
- `README.md`：涉及 MyBatis-Plus 的能力描述（迁移后需同步）

## 迁移步骤（每步详细）

### 1. 冻结基线并确认范围
- 记录当前 MyBatis-Plus 版本、插件清单、SQL 行为（分页、逻辑删除、租户、数据权限）。
- 以本文“使用点清单”为迁移范围基线，任何遗漏都必须补充到清单中。
- 对关键模块（`ruoyi-common-mybatis`、`ruoyi-system`、`ruoyi-workflow`、`ruoyi-gen`）标注负责人，避免并行改造冲突。

### 2. Maven 依赖替换到 MyBatis-Flex Boot 4
- 新增 `mybatis-flex.version` 属性，并引入 `mybatis-flex-spring-boot4-starter`（或对应 Boot4 Starter）。
- 从 `pom.xml` 与子模块移除 `mybatis-plus-*` 依赖（含 `mybatis-plus-jsqlparser`、`mybatis-plus-annotation`）。
- `ruoyi-common/ruoyi-common-encrypt` 的 MP 依赖替换为 Flex Starter（保持 optional 语义）。
- 处理第三方依赖：`warm-flow-mybatis-plus-sb3-starter` 必须替换为 Flex/Boot4 对应版本或上游替代方案；没有替代方案时需单独评估是否保留 MP。

### 3. 配置层迁移（YAML 与 Nacos）
- 将 `mybatis-plus` 配置块迁移到 `mybatis`/`mybatis-flex` 的配置键下（按 Flex 官方配置项调整）。
- 重点迁移项：`mapperPackage`、`mapperLocations`、`typeAliasesPackage`、`global-config/dbConfig`、`logic-delete`、`idType`、`logImpl`。
- 更新 `ruoyi-visual/ruoyi-snailjob-server/src/main/resources/application.yml` 中 `mybatis-plus` 配置，使其与 Flex 统一。
- `spy.properties` 中 MP 日志工厂替换为通用 P6Spy 或 Flex 对应日志配置。

### 4. 核心配置类替换（ruoyi-common-mybatis）
- 将 `MybatisPlusConfiguration` 改为 Flex 配置类：替换 `MybatisPlusInterceptor` 与内置拦截器为 Flex 的分页、租户、乐观锁、数据权限拦截器。
- `@MapperScan` 的属性由 `${mybatis-plus.mapperPackage}` 改为 Flex 对应配置键。
- `IdentifierGenerator`、`MetaObjectHandler`、`PostInitTableInfoHandler` 改为 Flex 的等价扩展点或自定义实现。

### 5. 实体注解与自动填充迁移
- `@TableName`、`@TableId`、`@TableField`、`@TableLogic`、`@Version`、`@FieldFill` 替换为 Flex 对应注解。
- `BaseEntity` 中的填充策略迁移到 Flex 的填充/审计机制，`InjectionMetaObjectHandler` 同步调整。
- `MusicResource.java` 中的 `JacksonTypeHandler` 替换为 Flex 可识别的 JSON 类型处理器。

### 6. Mapper/BaseMapperPlus 迁移
- `BaseMapperPlus` 改为基于 Flex `BaseMapper` 的实现，替换 `Db.saveBatch/updateBatchById/saveOrUpdateBatch` 等批量方法调用。
- 所有 `BaseMapperPlus<T,V>` 的继承关系保持不变，但底层实现迁移到 Flex 的 `BaseMapper`/`QueryWrapper`。
- `ShardingOrderMapper`、`ShardingOrderItemMapper` 等直接继承 MP `BaseMapper` 的类改为 Flex `BaseMapper`。

### 7. Wrapper/Query API 替换
- `LambdaQueryWrapper`、`QueryWrapper`、`LambdaUpdateWrapper`、`Wrappers` 全量替换为 Flex 查询/更新构建器。
- `Constants.WRAPPER` 的 `@Param` 传参方式替换为 Flex 需要的参数名/类型，确保 XML/注解 SQL 兼容。
- 受影响文件包含所有 service/mapper 中的 wrapper 构建逻辑（详见清单）。

### 8. 分页对象迁移
- 将 `PageQuery` 与 `TableDataInfo` 的分页对象从 MP `Page/IPage` 切换为 Flex `Page` 或其分页接口。
- `PageQuery.build()` 中的 `OrderItem` 构建逻辑替换为 Flex 的排序结构。
- 所有 `Page<*>` 返回值、`selectVoPage` 调用链同步调整。

### 9. 多租户与数据权限机制迁移
- `TenantLineInnerInterceptor` 与 `PlusTenantLineHandler` 替换为 Flex 租户插件与处理器。
- `InterceptorIgnoreHelper/IgnoreStrategy` 不再可用，需要定义 Flex 版的“忽略租户/数据权限”上下文与工具方法（改造 `TenantHelper`、`TenantSpringCacheManager`、`TenantKeyPrefixHandler`、`DataPermissionHelper`）。
- `PlusDataPermissionInterceptor` 迁移为 Flex 官方 `IDialect#prepareAuth` 方案，自定义方言并通过 `DialectFactory` 注册。

### 10. 加密/自动配置与属性迁移
- `EncryptorAutoConfiguration` 从 `MybatisPlusAutoConfiguration/MybatisPlusProperties` 替换为 Flex 对应的自动配置/属性对象。
- 保持 `EncryptorManager` 的别名包扫描逻辑与配置项一致。

### 11. 代码生成模板调整
- `domain.java.vm` 的 MP 注解替换为 Flex 注解。
- `serviceImpl.java.vm` 中的 `LambdaQueryWrapper`、`Wrappers`、`Page` 等替换为 Flex 查询与分页类型。

### 12. 业务模块与第三方模块兼容
- 对 `ruoyi-workflow` 的 `warm-flow-mybatis-plus-sb3-starter` 进行替换或升级；如果上游未提供 Flex/Boot4 版本，需要评估独立保留 MP 的风险。
- `ruoyi-visual/ruoyi-snailjob-server` 中的 MP 查询与配置迁移到 Flex，确保其依赖库兼容。
- 示例模块（`ruoyi-example`）同步迁移，保证编译通过并可作为迁移验证样例。

### 13. 文档同步与清理
- `README.md` 中 MyBatis-Plus 的描述与能力对比更新为 MyBatis-Flex。
- 删除未使用的 MP 配置与依赖后，确保无残留 `com.baomidou.mybatisplus` 引用。

### 14. 验证与回归
- 全量编译 + 单测 + 关键接口功能回归（分页、逻辑删除、租户隔离、数据权限、批量操作、加解密）。
- 对比迁移前后 SQL 输出与性能指标，确认无行为回归。

## 常见类型/注解初步替换对照（示意）
- `com.baomidou.mybatisplus.annotation.TableName` → Flex `@Table`（表名声明）
- `com.baomidou.mybatisplus.annotation.TableId` → Flex `@Id`（主键声明）
- `com.baomidou.mybatisplus.annotation.TableField` → Flex `@Column`（字段映射/忽略）
- `com.baomidou.mybatisplus.annotation.TableLogic` → Flex `@LogicDelete`
- `com.baomidou.mybatisplus.annotation.Version` → Flex `@Version`
- `com.baomidou.mybatisplus.core.conditions.*Wrapper` → Flex 查询/更新构造器
- `com.baomidou.mybatisplus.extension.plugins.pagination.Page/IPage` → Flex `Page`/分页接口
- `MybatisPlusInterceptor` → Flex 插件链
- `MetaObjectHandler` → Flex 填充/审计扩展点

> 以上对照仅为替换方向，具体包名与配置项需以当前使用的 MyBatis-Flex Boot 4 版本为准。 
