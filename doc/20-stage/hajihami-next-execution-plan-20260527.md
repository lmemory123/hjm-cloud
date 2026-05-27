# 哈基哈米下一步执行计划

更新时间：2026-05-27 14:50

本文档记录当前协同分支的最新检查结果和下一步计划。当前分支仍有未提交改动，后续 AI 不应覆盖这些改动。

## 1. 当前检查结果

三个项目当前均在分支：

```text
collab/hajihami-s6-coordination-20260527
```

当前工作区状态：

- `hjm-cloud`：有 S6.3 相关未提交改动，包含异步 Webhook、Webhook 日志表、开放 API 文档和交付文档。
- `hjm-web-nuxt-frontend`：有 `nuxt.config.ts` 未提交改动，主要是全局 head、description、preconnect。
- `hjm-admin`：有开放平台配置页、Dashboard 入口和部分类型修复未提交改动。

已执行验证：

| 项目 | 命令 | 结果 |
| --- | --- | --- |
| `hjm-cloud` | `git diff --check` | 通过，已修复开放 API 文档末尾空白行 |
| `hjm-cloud` | `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests clean compile` | 通过 |
| `hjm-cloud` | `mvn -pl ruoyi-modules/ruoyi-system -am -DskipTests compile` | 通过 |
| `hjm-web-nuxt-frontend` | `git diff --check` | 通过 |
| `hjm-web-nuxt-frontend` | `pnpm exec vue-tsc --noEmit --skipLibCheck` | 通过 |
| `hjm-web-nuxt-frontend` | `pnpm build` | 通过，有 chunk 大小和 sourcemap 警告 |
| `hjm-admin` | `git diff --check` | 通过，已修复多个行尾空格 |
| `hjm-admin` | `pnpm build` | 通过 |
| `hjm-admin` | `pnpm typecheck` | 未通过，失败包含 `gen/soy` 生成页、新增 `src/views/developer/webhook-log/index.vue`、`src/constants/business.ts` 和部分系统页历史类型 |

结论：

- S6.3 后端异步 Webhook 和日志表代码具备编译通过条件。
- 前台当前改动风险较低，但 `entry` chunk 偏大，后续要拆包优化。
- 后台可构建，但不能声明 typecheck 通过。路线图中的 S6.3 完成结论需要等后台类型治理和新增开放平台页面错误都收敛后再正式确认。

## 2. 立即冻结规则

在下列动作完成前，不建议继续大面积开发：

1. 先把当前未提交改动按项目分别提交成 checkpoint。
2. 后台 typecheck 失败必须明确处理策略：
   - 要么修复 `gen/soy` 生成目录；
   - 要么将 `gen/soy` 标记为生成产物并从 typecheck 门禁中拆出去；
   - 不能继续写“typecheck 已通过”。
3. S6.3 的“完成记录”必须与实际验证结果一致。

## 3. 下一步任务顺序

### P0：保存当前协同改动

目标：防止其他 AI 覆盖当前 S6.3 草稿。

建议提交：

```bash
# hjm-cloud
git add -A
git commit -m "feat(open-api): draft async webhook and delivery docs"

# hjm-web-nuxt-frontend
git add -A
git commit -m "chore(frontend): add global seo head defaults"

# hjm-admin
git add -A
git commit -m "feat(admin): draft developer platform settings"
```

后台若 pre-commit 仍被 `pnpm typecheck` 拦截，不要直接宣称通过；可先使用 `--no-verify` 做 checkpoint，但必须在提交说明或路线图风险区记录原因。

### P1：S6.3 后端收口

目标：把异步 Webhook 从“草稿可编译”收敛为“可运营”。

任务：

- 检查 `DeveloperApiV1Controller` 的同步分发逻辑是否已完全迁移到 `IDeveloperWebhookService`。
- 确认 `music_webhook_log` 建表 SQL 覆盖当前部署数据库类型，至少 Postgres 和项目常用数据库脚本要一致。
- 增加 Webhook 日志查询接口，供后台查看推送结果。
- 为 Webhook payload 增加签名摘要字段，避免只依赖明文 secret header。
- 明确重试次数语义：`retryCount=0` 表示首次成功，失败后最多重试 3 次。

验证：

```bash
mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests clean compile
```

### P2：后台开放平台模块收口

目标：让 API Key、Webhook 配置和日志可在后台完成日常运营。

任务：

- 开放平台页面拆为：
  - API Key 管理
  - Webhook 目标配置
  - Webhook 调用日志
- 先修新增页面自身类型错误：`src/views/developer/webhook-log/index.vue` 当前错误为解构了不存在的 `reload`。
- 修复或隔离 `src/constants/business.ts` 的状态枚举扩展问题，避免 `EnableStatus`、`YesOrNoStatus` 扩展到 `Y/N` 后破坏旧映射。
- 配置页需要处理系统参数不存在的情况：允许创建或提示管理员先建参数。
- Token 页面必须避免明文长期展示，至少默认遮罩，支持复制和重新生成。
- Dashboard 入口确认路由存在、菜单可达、权限点清晰。
- 确定 `gen/soy` 类型治理策略，不把生成目录问题混入手写页面质量。

验证：

```bash
pnpm build
pnpm typecheck
```

如果短期不能通过全量 `typecheck`，必须提供替代命令，例如只检查 `src/views/developer` 和手写音乐页面，不能笼统写“后台 typecheck 通过”。

### P3：前台体验专项

目标：解决用户反馈的导航点击后延迟感。

任务：

- 给顶部导航增加即时激活态或页面切换 loading，不让用户误以为点击无效。
- 检查 `useAsyncData` 的 lazy 策略和 SSR 页面首屏请求，确保导航不被慢接口阻塞。
- 对 `/search`、`/chart/*`、`/song/[id]` 保留骨架屏和错误重试态。
- 检查 Nuxt `preconnect` 是否会在 SSR 环境中稳定读取 `NUXT_PUBLIC_API_BASE`。
- 针对 build 警告，评估图标包和入口 chunk 拆分。

验证：

```bash
pnpm exec vue-tsc --noEmit --skipLibCheck
pnpm build
```

### P4：质量交付文档

目标：让 S6 能进入上线准备，而不是只停留在功能草稿。

任务：

- 完成部署手册。
- 完成运维手册。
- 完成数据库 ER 或至少核心音乐表关系说明。
- 完成上线检查清单。
- 完成 SDK 示例文档。
- 完成压测前置脚本说明。

验证：

```bash
git diff --check
```

## 4. 推荐分工

- AI-A：执行 P3，只改 `hjm-web-nuxt-frontend`。
- AI-B：执行 P1，只改 `hjm-cloud` 的后端代码和 SQL。
- AI-C：执行 P2，只改 `hjm-admin`。
- AI-D：执行 P4，只改 `hjm-cloud/doc`。

任何 AI 如果需要跨项目修改，必须先说明原因和涉及文件。

## 5. 阶段完成门槛

S6.3 可以标记完成的最低标准：

1. 后端 clean compile 通过。
2. 后台 build 通过。
3. 后台 typecheck 策略明确，不能留下“声称已通过但实际失败”的记录。
4. Webhook 日志可查询。
5. 开放平台配置页可用，且系统参数缺失时有明确处理。
6. 路线图更新真实验证结果。

S6.4 启动条件：

1. S6.3 checkpoint 已提交。
2. API 文档与实际接口一致。
3. 后台类型风险有明确处置计划。
