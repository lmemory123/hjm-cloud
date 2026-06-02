# 哈基哈米去多租户专项计划

更新时间：2026-06-02

## 1. 目标

哈基哈米是单站点音乐社区，不需要 SaaS 多租户能力。本专项删除业务系统中的多租户入口、运行时隔离逻辑、租户管理 CRUD 和应用数据库字段，降低认证、缓存、SQL 和运维复杂度。

本专项基于以下稳定基线开始：

- 后端：`c0f13d25f fix(s6): harden cold start and service integration`
- Nuxt 前台：`aaa2f42 fix(frontend): restore hydration and verified navigation flows`
- 后台：`1c14422e feat(deployment): add Dockerfile and nginx config for admin production build`

## 2. 删除边界

### 2.1 必须删除

- 业务登录、注册、社交绑定请求中的 `tenantId`。
- `/auth/tenant/list`、租户切换和租户套餐接口。
- `sys_tenant`、`sys_tenant_package` 对应 Controller、Service、Mapper、Dubbo API、后台页面和菜单。
- `ruoyi-common-tenant` 运行时模块及其 MyBatis 租户拦截器、Redis key 前缀、Spring Cache 和 Sa-Token 租户包装。
- `LoginUser`、token extra、用户查询、操作日志、登录日志中的业务租户传播。
- 系统实体、资源实体、工作流实体、代码生成模板中的业务 `tenantId` 字段。
- 应用数据库中的 `sys_tenant`、`sys_tenant_package` 表、租户菜单和业务表 `tenant_id` 列。

### 2.2 必须保留

- Nacos 自身数据库 schema 中的 `tenant_id`、`tenant_info`、`tenant_capacity`。
- `push_nacos_all.sh` 和 `fetch_nacos.sh` 中传给 Nacos HTTP API 的 `tenant` 参数。
- Nacos 配置中的 namespace 概念。它用于隔离 `prod` 等环境，不属于哈基哈米业务多租户。
- 普通 RBAC：用户、角色、部门、菜单、数据权限。删除多租户不等于删除权限系统。

## 3. 当前审计结论

多租户当前配置虽然已经是 `tenant.enable: false`，但仍然存在深层耦合：

| 区域 | 现状 | 风险 |
| --- | --- | --- |
| 后端 | 约 162 个文件包含租户模块、字段或调用链 | 直接删列会导致认证、用户查询和工作流启动失败 |
| 后台 | 约 35 个文件包含租户页面、路由、API、类型和 Header 切换器 | 只删后端会导致后台请求 404 和类型错误 |
| Nuxt 前台 | 登录和注册固定发送 `tenantId: "000000"` | 后端契约删除后必须同步调整 |
| 数据库 | 系统表、日志表、资源表和工作流表包含 `tenant_id` | 需要提供存量迁移脚本，不能只改初始化 SQL |
| Nacos | 内部 schema 也使用 `tenant_id` | 不得误删，否则 Nacos 冷启动失败 |

## 4. 执行阶段

### MT0：数据盘点和保护线

目标：确认线上数据确实可以收敛为单站点数据。

实施：

1. 备份 PostgreSQL 数据库。
2. 执行盘点 SQL，列出业务表中的非默认租户数据。
3. 确认 `sys_tenant` 中除 `000000` 之外没有需要保留的租户。
4. 建立迁移前烟测基线：后台登录、前台注册登录、歌曲列表、搜索、投稿、审核、评论、收藏。

阻断条件：

- 任一业务表存在需要保留的非 `000000` 数据时，不执行删列。
- 必须先制定数据归并规则，明确用户重名、角色重名和资源归属冲突处理方式。

验收：

- 输出盘点报告和数据库备份路径。
- 当前冷启动与核心 E2E 保持通过。

### MT1：外部接口和界面去租户化

目标：先移除用户可见租户概念，同时暂时保留数据库列，降低变更爆炸半径。

后端：

- 登录、注册、社交绑定不再要求客户端提供 `tenantId`。
- 删除 `/auth/tenant/list`。
- 删除系统租户管理、租户套餐和动态切换租户接口。
- 过渡期内部仍可使用单一默认上下文，但不得暴露给客户端。

Nuxt 前台：

- 删除 `FRONT_AUTH_TENANT_ID`。
- 登录、注册请求不再发送 `tenantId`。
- 更新认证接口文档。

后台：

- 删除登录页租户下拉框、Header `TenantSelect` 和相关组件。
- 删除租户管理、租户套餐页面、路由、API、类型和国际化文案。
- 清理登录记忆数据中的 `tenantId`。

验收：

- Nuxt `pnpm exec vue-tsc --noEmit --skipLibCheck` 和 `pnpm build` 通过。
- 后台 `pnpm typecheck` 和 `pnpm build` 通过。
- 前台、后台登录请求体均不再出现 `tenantId`。

### MT2：后端运行时去租户化

目标：删除应用运行时的租户隔离基础设施。

实施：

- 删除 `ruoyi-common/ruoyi-common-tenant` 模块及所有 Maven 依赖。
- 从 MyBatis 配置中删除 `TenantLineInnerInterceptor` 注册逻辑。
- 删除 `TenantHelper` 动态切换、忽略租户和缓存前缀调用。
- 从 `LoginUser`、`LoginHelper`、Sa-Token extra、日志事件和 Dubbo 用户查询契约中删除 `tenantId`。
- 删除 `RemoteTenantService`、`RemoteTenantVo` 和 `ruoyi-system` 中整套租户 CRUD。
- 将系统实体从 `SystemTenantEntity` 迁移到普通基础实体。
- 删除资源、工作流、代码生成模板中的租户传播逻辑。
- 保留数据权限拦截器、RBAC 和普通缓存能力。

验收：

- `rg` 确认应用 Java 和 Maven 模块中不存在 `TenantHelper`、`TenantEntity`、`RemoteTenantService`、`ruoyi-common-tenant`。
- `mvn clean compile` 通过。
- 后台登录、前台注册登录、字典、用户、资源、工作流基础接口烟测通过。

### MT3：数据库和初始化脚本去租户化

目标：在代码已经不再读取租户字段后，删除业务数据库遗留结构。

实施：

- 新增 PostgreSQL 存量迁移脚本，删除租户菜单、`sys_tenant`、`sys_tenant_package` 和业务 `tenant_id` 列。
- 同步修改 PostgreSQL、MySQL、Oracle 初始化脚本和工作流 SQL。
- 删除 S6 为兼容旧框架追加的 `tenant_id` 补列逻辑。
- 保留 Nacos 初始化 SQL 中的租户字段和表。

部署顺序：

1. 先部署 MT1、MT2 代码。
2. 验证代码不再读写业务 `tenant_id`。
3. 再执行 MT3 存量迁移 SQL。
4. 最后用空库初始化 SQL 做一次冷启动。

验收：

- PostgreSQL 空库脚本按顺序执行成功。
- 存量迁移脚本支持重复执行。
- 应用业务 schema 中不再存在租户管理表和业务 `tenant_id` 列。
- Nacos 健康检查和配置推送仍然通过。

### MT4：文档、测试和发布复验

目标：形成可交付的单站点系统。

实施：

- 更新前台认证文档、部署文档、路线图和后台 README。
- 增加不带 `tenantId` 的前台注册、前台登录和后台登录测试。
- 执行完整清库冷启动、Playwright E2E 和接口烟测。
- 检查日志中无 `tenant` 相关异常、SQL 缺列异常和 Dubbo 契约不匹配异常。

验收：

- `RESET_DATA=1 ./script/docker-compose-all/cold_start.sh` 通过。
- 全部容器 healthy，Nacos 三个后端服务均注册成功。
- Playwright 核心流程通过。
- 完成记录回写到统一路线图。

## 5. 数据盘点 SQL

执行 MT0 时至少检查：

```sql
SELECT tenant_id, COUNT(*) FROM sys_user GROUP BY tenant_id ORDER BY tenant_id;
SELECT tenant_id, COUNT(*) FROM sys_role GROUP BY tenant_id ORDER BY tenant_id;
SELECT tenant_id, COUNT(*) FROM sys_dept GROUP BY tenant_id ORDER BY tenant_id;
SELECT tenant_id, COUNT(*) FROM sys_config GROUP BY tenant_id ORDER BY tenant_id;
SELECT tenant_id, COUNT(*) FROM sys_dict_type GROUP BY tenant_id ORDER BY tenant_id;
SELECT tenant_id, COUNT(*) FROM sys_oss GROUP BY tenant_id ORDER BY tenant_id;
SELECT tenant_id, COUNT(*) FROM flow_definition GROUP BY tenant_id ORDER BY tenant_id;
SELECT tenant_id, company_name, status FROM sys_tenant ORDER BY tenant_id;
```

盘点 SQL 需要按实际 schema 校准：部分表可能由不同模块初始化，执行前先检查列是否存在。

## 6. 回归矩阵

| 能力 | MT1 | MT2 | MT3 | MT4 |
| --- | --- | --- | --- | --- |
| 后台登录 | 必测 | 必测 | 必测 | 必测 |
| 前台注册、登录 | 必测 | 必测 | 必测 | 必测 |
| 歌曲列表、搜索、详情 | 必测 | 必测 | 必测 | 必测 |
| 投稿、审核、上下架 | - | 必测 | 必测 | 必测 |
| 评论、点赞、收藏、历史 | - | 必测 | 必测 | 必测 |
| 用户、角色、部门、字典 | 必测 | 必测 | 必测 | 必测 |
| OSS 和资源访问 | - | 必测 | 必测 | 必测 |
| 工作流基础接口 | - | 必测 | 必测 | 必测 |
| Nacos 配置推送和注册 | 必测 | 必测 | 必测 | 必测 |
| 空库冷启动 | - | - | 必测 | 必测 |

## 7. 协作 AI 作业规范

每个协作 AI 开始前必须阅读本文档，并遵守：

1. 每次只执行一个阶段，不跨 MT1、MT2、MT3 混改。
2. 禁止删除 Nacos schema 的 `tenant_id`、`tenant_info`、`tenant_capacity`。
3. 禁止把删除多租户扩大为删除 RBAC、部门、角色或数据权限。
4. 删除文件前先用 `rg` 列出引用，修改后再次用 `rg` 做残留检查。
5. 每个阶段独立提交，提交信息使用 `refactor(single-site): ...`。
6. 每个提交必须附带执行过的构建、测试和失败项，不得只报告“已完成”。
7. 不覆盖其他协作 AI 的未提交修改；发现工作区脏数据时先记录并停止混合提交。
8. 完成阶段后更新本文档和 `hajihami-rebuild-roadmap.md`。

建议分工：

| 执行者 | 范围 |
| --- | --- |
| AI-A | MT1 后端认证契约与 Nuxt 前台 |
| AI-B | MT1 后台 UI、路由、API 和类型 |
| AI-C | MT2 common tenant、system、auth、Dubbo、workflow、resource |
| AI-D | MT3 数据盘点、迁移 SQL、初始化 SQL |
| 主审 | 每阶段合并前构建、冷启动、业务烟测和残留扫描 |

## 8. 完成记录

| 阶段 | 状态 | 日期 | 说明 |
| --- | --- | --- | --- |
| 审计与规划 | 已完成 | 2026-06-02 | 已确认删除边界、Nacos 保留边界、执行顺序和回归门禁 |
| MT0 数据盘点 | 待执行 | - | 需要先确认是否存在非默认租户数据 |
| MT1 外部接口和界面 | 待执行 | - | - |
| MT2 后端运行时 | 待执行 | - | - |
| MT3 数据库和初始化脚本 | 待执行 | - | - |
| MT4 发布复验 | 待执行 | - | - |
