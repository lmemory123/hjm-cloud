# 哈基哈米重构阶段路线图

更新时间：2026-06-02

本文档是 `hjm-web-nuxt-frontend`、`hjm-admin`、`hjm-cloud` 三个项目的统一阶段计划和交接记录。

## 1. 当前状态

当前处于 **S6：开放 API、机器人、质量交付复验** 阶段。S6.1-S6.6 的本地工程验证已闭环，进入发布环境复验准备。

当前覆盖：
- 前台：全站核心页面（首页、搜索、详情、榜单、投稿、个人中心）已完成功能闭环与 UX 优化（含导航进度条）。
- 后端：已交付稳定适配层、异步 Webhook（支持签名校验）、调用日志审计。
- 后台：已交付开放平台可视化管理（API Key/Webhook）、 Dashboard 增强、手写代码类型 100% 收敛。
- 质量：初始化 SQL、维护脚本、前端生产构建、核心 E2E 和本地 Docker 清库冷启动已复验；发布环境仍需执行镜像仓库拉取和真实平台凭证专项。

## 2. 技术边界

- 前台：Nuxt 4 + Vue 3 + TypeScript + SCSS + Nuxt i18n。
- 后台：Soybean Admin + Vite + Vue 3 + Naive UI。
- 后端：RuoYi Cloud 多模块 (Postgres + Redis/Valkey + Nacos)。

## 3. 阶段路线图

### S0 - S5：MVP 业务全量闭环
状态：已完成 (2026-04-28)。
涵盖：公开浏览、搜索、播放、投稿审核、社交互动、激励流水、Dashboard 运营。

### S6.1 - S6.3：开放平台、健壮性与加固
状态：工程实现基本到位，待 S6.4 复验闭环。
涵盖：
- 开放 API v1 (songs/users/charts/stats)。
- 异步 Webhook 引擎 (指数退避重试 + HMAC-SHA256 签名)。
- 后台“开放平台”模块 (可视化配置与推送日志)。
- 前台异常态加固 (401 全局拦截、音频错误提示、SSR/Lazy 优化)。
- 基础数据初始化 (P1 专项：标准标签、样本数据、系统参数 JSON)。
- 类型治理 (手写页面 0 错误)。

验收记录：
- 后端 `mvn clean compile` SUCCESS。
- 后台 `pnpm typecheck` SUCCESS (手写代码通过，排除 `gen/soy`)。
- 前台 `pnpm build` SUCCESS (Chunk 优化完成)。
- 验收流程：已有 8 条核心业务路径手动验收记录，但缺少执行环境、数据版本、运行日志或截图归档，需按 `hajihami-s6-claim-audit-20260529.md` 复验。

## 4. 当前完成记录

| 阶段 | 状态 | 完成日期 | 说明 |
| --- | --- | --- | --- |
| S0 MVP 垂直切片 | 已完成 | 2026-04-27 | 打通公开浏览、投稿及后台 Dashboard |
| S1 页面清单补齐 | 已完成 | 2026-04-27 | 独立登录、投稿向导、用户主页增强 |
| S2 搜索、标签、榜单 | 已完成 | 2026-04-28 | 搜索高级筛选、标签树、榜单归档 |
| S3 投稿审核闭环 | 已完成 | 2026-04-28 | 资源落库、提交审核、审核日志追溯 |
| S4 互动、社群、成长 | 已完成 | 2026-04-28 | 收藏历史、点赞举报、社区入口配置 |
| S5 运营后台与激励 | 已完成 | 2026-04-28 | 运营指标聚合、哈气金批量发放/撤回 |
| S6 开放API与加固 | 已完成 | 2026-05-29 | S6.1-S6.4 全面交付：OpenAPI、异步Webhook、后台配置、前台加固及自动化质量证据已闭环 |
| S6.6 冷启动加固复验 | 已完成 | 2026-06-02 | 修复容器健康检查、Nacos PostgreSQL schema/清理 SQL、网关登录、公开搜索 JSONB、前台认证客户端和 Nuxt 客户端产物；完成清库冷启动与业务烟测 |
| MT 去多租户专项 | 已完成 | 2026-06-02 | 删除业务多租户管理表、字段、运行时隔离逻辑及 UI 入口；确保单站点运行与 cold-start 兼容 |

## 5. 后续交接区 (S6.4 - S6.6)

下一步工作建议：

### S6.4：自动化测试与压测脚本 (已交付)
1. **实机验证脚本**：已交付 `webhook-receiver-demo.js`，通过 HMAC 签名验证。
2. **压测脚本与报告**：已交付 `stress-test.js` (k6) 及 `stress-test-report-20260529.md` (负载 100 并发通过)。
3. **E2E 自动化**：`basic-flow.spec.ts` (Playwright) 已纳入版本，补齐了依赖与脚本，覆盖核心业务路径。
4. **验收证据**：`acceptance-records.md` 已补充环境、账号及日志详情，形成可复现证据。

### S6.5：正式发布与容器化准备 (已交付)
1. **全栈容器化**：交付了 `script/docker-compose-all`，支持一键拉起 Postgres、Redis、Nacos、RabbitMQ 及三端应用。
2. **Nacos 预置配置**：提供了 `prod` 环境下的数据库、缓存及消息队列初始化 YAML，支持生产环境快速冷启动。
3. **部署脚本**：交付了 `build.sh`，打通了 Maven 构建与 Docker 镜像打包的资源链路。
4. **Swagger 归档**：在 `hajihami-open-api-v1.md` 中补充了静态导出与本地归档说明。

### S6.6：本地冷启动加固复验 (已完成)
1. **冷启动脚本**：新增 `script/docker-compose-all/cold_start.sh`，按依赖顺序启动中间件、同步 Nacos 配置、启动后端和前端，并验证健康状态与服务注册。
2. **前后台认证**：补齐前台专用 `sys_client` 初始化数据和 Nacos 配置；清库后验证前台注册、前台登录和后台登录均返回成功，登录接口可获取 token。
3. **前端生产产物**：移除破坏 Nuxt `/_nuxt` 发布目录的 Vite 文件名覆盖配置；验证 SSR 首页、静态资源、占位封面和浏览器水合均正常。
4. **数据库与 Nacos**：补齐 `config_info_beta`、`config_info_tag`、租户字段及 PostgreSQL 历史配置清理 SQL；所有 PostgreSQL 初始化脚本可从空库顺序执行。
5. **自动化验证**：
   - `pnpm build` 与 `pnpm exec vue-tsc --noEmit --skipLibCheck` 通过。
   - `pnpm exec playwright test tests/e2e/basic-flow.spec.ts` 通过，结果为 `4 passed`。
   - `OpenMusicServiceDatabaseTest` 实际执行，结果为 `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`。
   - `SKIP_BUILD=1 RESET_DATA=1 ./cold_start.sh` 清库启动通过，全部容器 healthy，Nacos 中三个后端服务均为 healthy 实例。

## 6. 已知风险

- 后台 `gen/soy` 目录仍被主 typecheck 排除。本次已修复音乐抽屉生成页与模板问题，但生成产物仍需独立验收。
- 本地收藏/历史数据在未登录时暂存于 localStorage，多端合并逻辑仍需后续专项增强。
- 评论点赞状态在大规模并发下可能存在毫秒级缓存延迟（最终一致性设计）。
- 机器人平台适配仅提供方案与示例，真实对接需各平台 API 凭证。
- 本机 Docker Hub 访问在复验期间超时，因此本次清库演练复用了本地镜像并以 `SKIP_BUILD=1` 启动；发布环境仍需在可访问镜像仓库的机器上执行一次空 Docker 缓存镜像构建。
- Nuxt 生产构建仍提示单个客户端 chunk 约 600 kB，功能不受影响，但上线前可继续按页面依赖拆包。

## 7. 更新规则
- 每完成一个阶段，必须更新第 4 节“当前完成记录”。
- 每次开始新阶段前，先更新第 5 节“后续交接区”。
- 验证失败必须在第 6 节“已知风险”记录原因。

## 8. 下一专项：业务系统去多租户化

哈基哈米按单站点音乐社区运行，不需要 SaaS 多租户能力。后续按独立专项删除业务多租户入口、运行时隔离逻辑、租户 CRUD 和应用数据库字段。

执行前必须阅读 [`hajihami-remove-multitenancy-plan.md`](./hajihami-remove-multitenancy-plan.md)。特别注意：Nacos 自身 schema 和 HTTP API 中的 `tenant` 参数用于配置 namespace，必须保留。
