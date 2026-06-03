# 哈基哈米重构阶段路线图

更新时间：2026-06-03

本文档是 `hjm-web-nuxt-frontend`、`hjm-admin`、`hjm-cloud` 三个项目的统一阶段计划和交接记录。

## 1. 当前状态

当前处于 **S8：技术栈补丁升级专项** 阶段。S0-S7 及去多租户专项已完成当前轮次闭环，S8 聚焦依赖升级和构建门禁复验。

当前覆盖：
- 前台：单站点 MVP 功能完整，支持游客浏览与注册用户投稿互动；SSR 水合与性能优化初步完成。
- 后台：Soybean Admin 管理端已适配单站点模式，移除所有租户管理入口；支持 OpenAPI 与 Webhook 可视化。
- 后端：RuoYi Cloud jdk21 适配完成，移除运行时租户隔离逻辑；Nacos 配置同步与冷启动流程已硬化。
- 质量：Playwright E2E 覆盖核心链路；后端单元测试已补齐数据库回退路径；S8 本轮已完成编译、类型检查、生产构建和 Docker 运行时复验。

## 2. 技术边界

- 前台：Nuxt 4 + Vue 3 + TypeScript + SCSS + Nuxt i18n。
- 后台：Soybean Admin + Vite + Vue 3 + Naive UI。
- 后端：RuoYi Cloud JDK 21 (Postgres + Redis/Valkey + Nacos + RabbitMQ)。
- 模式：单站点模式（已移除 SaaS 多租户）。

## 3. 阶段路线图

### S0 - S5：MVP 业务全量闭环
状态：已完成 (2026-04-28)。
涵盖：公开浏览、搜索、播放、投稿审核、社交互动、激励流水、Dashboard 运营。

### S6：开放平台、健壮性与去租户化
状态：已完成 (2026-06-02)。
涵盖：
- 开放 API v1 与 异步 Webhook 引擎。
- 自动化质量证据闭环（Playwright 4/4 passed）。
- 去多租户专项：业务层完全回归单站点模式。

### S7：内测上线准备
涵盖：
- S7.0 文档校准与清理：同步各端计划，清理残留代码。
- S7.1 生产安全与部署门禁：Secret 加密、限频实测、日志脱敏、发布环境演练。
- S7.2 业务健壮性：云端草稿、举报处理台、关注/粉丝列表、异常态处理。
- S7.3 媒体处理：音频哈希查重、转码重试、补档通知。
- S7.4 正式验收：1000 人并发压测、72 小时稳定性测试、上线清单确认。

### S8：技术栈补丁升级专项
状态：已完成当前轮次 (2026-06-03)。
涵盖：
- 后端 Spring Boot `4.0.5` 升级到 `4.0.6`，继续通过 BOM 管理依赖，不在业务模块直接引入 `spring-boot`。
- 移除根 POM 中手工钉死的 `junit-jupiter-api 5.11.4`，让 Spring Boot 4.0.6 统一管理 JUnit 6 测试依赖。
- 前台 Nuxt 4 小版本升级：Nuxt、Vue、Nuxt UI、Nuxt i18n、VueUse、Iconify、Sass、vue-tsc。
- 后台 Admin 小版本升级：Vue、Naive UI、VueUse、UnoCSS、ECharts、vue-tsc、Sass 等；暂不升级 Vite 8、Vue Router 5、TypeScript 6、ESLint 10。
- 修复后台包装组件 `useAttrs()` 透传类型断言，以及 `file-upload` TSX 中 `NP` 组件显式导入。
- 修复后台 Docker 构建：复制 `.npmrc` 进入构建层并使用 npmmirror 安装 pnpm，新增 `.dockerignore`，避免 Docker 内依赖下载长尾和上下文噪音。

## 4. 当前完成记录

| 阶段 | 状态 | 完成日期 | 说明 |
| --- | --- | --- | --- |
| S0 - S5 业务闭环 | 已完成 | 2026-04-28 | 业务全量 MVP 交付 |
| S6.1 - S6.4 开放平台 | 已完成 | 2026-05-29 | OpenAPI、Webhook 及压测脚本交付 |
| S6.6 冷启动加固 | 已完成 | 2026-06-02 | 修复健康检查、SQL 初始化及网关登录 |
| MT 去多租户专项 | 已完成 | 2026-06-02 | 业务层完全去租户化，单站点冷启动复验通过 |
| S7.0 文档校准与清理 | 已完成 | 2026-06-02 | 同步 S7 路线图，清理 stale 文档，移除 TenantConstants 残留，停止跟踪 transient 文件 |
| S7.1 生产安全与门禁 | 已完成 | 2026-06-02 | 落实 Jasypt 秘钥加密、OpenAPI 60次/min 限频、日志脱敏及部署演练 |
| S8 技术栈补丁升级 | 已完成 | 2026-06-03 | Spring Boot 4.0.6、Nuxt 4.4.7、Vue 3.5.35、Admin 依赖小版本升级；编译、类型检查、构建、Docker 运行时和 E2E 通过 |

## 4.1 S8 验证记录

- 后端：`mvn -U clean compile -DskipTests` 通过，62/62 模块成功。
- 后端：`mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests=false -Dmaven.test.skip=false test` 通过，实际执行 `EncryptTest` 和 `OpenMusicServiceDatabaseTest`。
- 前台：`pnpm exec vue-tsc --noEmit --skipLibCheck` 通过。
- 前台：`pnpm build` 通过；仍存在 Tailwind/VueUse sourcemap/PURE 注释类非阻断警告。
- Docker 运行时：`RESET_DATA=1 ./cold_start.sh` 在 Docker 可用环境复验；后端、中间件、前台、后台核心 10 个容器均为 healthy。
- Nacos 服务注册：`ruoyi-auth`、`ruoyi-system`、`hjm-music-web` 3/3 注册并存在 healthy 实例。
- 网关 smoke：`/auth/code`、`/music/open/song/list?pageNum=1&pageSize=1`、`/music/open/user/1` 返回 200；前台 `http://localhost:3000/` 和后台 `http://localhost:81/` 返回 200。
- 前台 E2E：完整运行环境启动后 `pnpm test:e2e` 通过，6/6 passed，覆盖首页、搜索、注册登录无 `tenantId`、歌曲详情跳转和后台登录。
- 后台：`pnpm typecheck` 通过。
- 后台：`pnpm build` 通过。
- 后台 Docker：`docker compose build --no-cache hjm-admin` 通过，已确认升级后的后台镜像可生产构建并启动健康。

## 5. 后续交接区 (S7.2 - S7.4)

### S7.2：核心业务健壮性 (已完成)
1. **云端草稿**：支持跨设备保存投稿草稿。
2. **互动列表**：补齐关注、粉丝列表页，增加举报处理控制台。
3. **鲁棒性处理**：投稿文件异常、接口超时、外链失效的 UI 反馈增强。

### S7.1：生产安全与部署门禁 (已完成)
1. **Secret 安全**：Nacos 数据库密码加密，落实 OSS 凭证环境变量注入。
2. **限频加固**：匿名接口 60 次/min 限频实测，防止恶意抓取。
3. **日志脱敏**：手机号、邮箱、Token 在后端日志中必须掩码处理。
4. **发布演练**：执行一次完整无缓存构建与回滚演练。

## 6. 已知风险

- 后台 `gen/soy` 目录仍被主 typecheck 排除，生成产物需独立验收。
- 前台 Nuxt build 仍有 Tailwind/VueUse sourcemap/PURE 注释类非阻断警告，后续可作为构建噪声专项处理。
- 后台 pnpm 安装提示部分构建脚本被忽略，当前不影响 typecheck/build，生产 CI 可按组织策略决定是否执行 `pnpm approve-builds`。
- 机器人平台接入目前仅为方案验证，需真实 API 凭证进行 S7.4 最终验收。
