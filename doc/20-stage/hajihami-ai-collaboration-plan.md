# 哈基哈米多 AI 协同开发计划

更新时间：2026-05-27

本文档用于多个 AI 或开发者并行参与哈基哈米重构。所有参与者必须先阅读本文件和 `hajihami-rebuild-roadmap.md`，再开始修改代码。

## 1. 当前基线

当前三项目已完成 S0-S5 MVP，并进入 S6 开放 API、机器人、质量交付阶段。

- 前台：`hjm-web-nuxt-frontend`
  - 当前主线是 Nuxt 4 + Vue 3 + TypeScript + SCSS。
  - 已完成首页、搜索、歌曲详情、榜单、投稿、用户主页、我的中心等 MVP 页面。
  - SSR/CSR 边界已定义：公开 SEO 页面走 SSR，登录态和副作用走 CSR。
- 后台：`hjm-admin`
  - 保留 Soybean Admin + Naive UI。
  - 已有音乐 Dashboard、音乐 CRUD、哈气金流水和运营入口。
  - 全量 `pnpm typecheck` 仍存在历史生成 CRUD 类型问题，不可宣称完全收敛。
- 后端：`hjm-cloud`
  - 音乐能力集中在 `hjm-music-web`、`hjm-music-core` 和 `ruoyi-system` 的音乐管理模块。
  - 已完成开放 API v1：`/music/open/api/v1/*`。
  - S6.2 已支持可选开发者 token 和 Webhook 同步分发最小闭环。

当前保存分支建议：`collab/hajihami-s6-coordination-20260527`。

## 2. 协同原则

1. 不重写现有技术栈：
   - 前台不迁入 Next.js/React/Zustand/SWR。
   - 后台不重写 Soybean Admin。
   - 后端继续使用当前 RuoYi Cloud 模块结构。
2. 不删除别人已完成的页面、接口、文档、配置，除非有明确替代实现并记录原因。
3. 每个 AI 只负责一个清晰任务包，避免同时改同一个文件。
4. 所有阶段完成后都必须更新：
   - `hjm-cloud/doc/20-stage/hajihami-rebuild-roadmap.md`
   - 如影响开放 API，同步更新 `hjm-cloud/doc/30-open-api/hajihami-open-api-v1.md`
   - 如影响前台，更新 `hjm-web-nuxt-frontend/docs/hajihami-current-stack-plan.md`
   - 如影响后台，更新 `hjm-admin/docs/README.md`
5. 不允许只因为页面存在就标记完成。必须记录接口、页面、后台入口和验证命令。
6. 提交前至少跑对应项目的最小验证命令。

## 3. 分工建议

### AI-A：前台体验与 SEO

负责仓库：`hjm-web-nuxt-frontend`

任务范围：

- 优化页面跳转感知延迟：导航预取、懒加载状态、骨架屏、错误态。
- 检查公开页面 SSR 输出：`/`、`/search`、`/song/[id]`、`/chart/week`、`/chart/month`、`/user/[uid]`。
- 补移动端布局、主题切换一致性、按钮 hover 和空态体验。
- 不改后台和后端业务逻辑。

验证命令：

```bash
pnpm exec vue-tsc --noEmit --skipLibCheck
pnpm build
```

### AI-B：后端开放 API 与机器人

负责仓库：`hjm-cloud`

任务范围：

- S6.3：补 Swagger/OpenAPI 分组或导出说明。
- 将 Webhook 从同步 HTTP 调用升级为异步任务，补失败重试和调用日志。
- 增加 QQ 机器人、Discord、Telegram 的适配器示例或配置说明。
- 保持 `/music/open/*` 和 `/music/open/api/v1/*` 向后兼容。

验证命令：

```bash
mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile
mvn -pl ruoyi-modules/ruoyi-system -am -DskipTests compile
```

### AI-C：后台运营配置与类型治理

负责仓库：`hjm-admin`

任务范围：

- 给开发者 token 和 Webhook 配置增加后台可视化入口。
- 增强音乐 Dashboard 的开放 API、Webhook、机器人配置入口。
- 收敛历史生成 CRUD 的类型问题，最终恢复全量 `pnpm typecheck`。
- 不重写路由、请求封装或 UI 框架。

验证命令：

```bash
pnpm build
pnpm typecheck
```

### AI-D：质量与交付文档

负责仓库：优先 `hjm-cloud/doc`

任务范围：

- 补部署手册、运维手册、数据库 ER、上线检查清单。
- 补开放 API 示例、SDK 示例和压测前置脚本说明。
- 汇总每个阶段的验证记录和剩余风险。

验证命令：

```bash
git diff --check
```

## 4. 任务领取格式

每个 AI 开工前，在自己的上下文中声明：

```text
我领取任务：<AI-A/AI-B/AI-C/AI-D>
目标：<一句话说明>
涉及仓库：<仓库名>
预计修改文件：<路径列表>
不会修改：<明确排除的模块>
验证命令：<命令列表>
```

如果预计会改到其他 AI 的文件，先暂停并说明冲突点。

## 5. 提交规范

分支命名：

```text
collab/hajihami-<stage-or-topic>-<yyyymmdd>
```

提交信息格式：

```text
feat(scope): 简短说明
fix(scope): 简短说明
docs(scope): 简短说明
chore(scope): 简短说明
```

示例：

```text
feat(open-api): add developer webhook dispatch
docs(collab): add ai collaboration plan
fix(frontend): improve route navigation feedback
```

提交前检查：

- `git status --short` 确认只包含本任务相关文件。
- `git diff --check` 必须通过。
- 验证失败必须写入路线图风险区，不允许静默跳过。

## 6. 给其他 AI 的通用提示词

将下面提示词发给参与协同的 AI：

```text
你正在参与哈基哈米音乐社区重构。请先阅读：
1. hjm-cloud/doc/20-stage/hajihami-ai-collaboration-plan.md
2. hjm-cloud/doc/20-stage/hajihami-rebuild-roadmap.md
3.与你任务相关的项目文档

硬性规则：
- 不迁移技术栈。前台是 Nuxt/Vue，后台是 Soybean Admin/Naive UI，后端是 RuoYi Cloud。
- 不删除或回滚他人改动。
- 一次只做一个清晰任务包。
- 修改前先说明涉及文件和验证命令。
- 完成后必须更新路线图的完成记录、验证结果和剩余风险。
- 提交前运行 git diff --check 和对应项目验证命令。

输出要求：
- 先给改动摘要。
- 再给验证结果。
- 最后列出剩余风险。
```

## 7. 当前可并行队列

1. 前台导航延迟专项：
   - 检查导航点击后迟滞来源。
   - 优先处理预取、loading 状态、骨架屏和页面级异步数据策略。
2. OpenAPI 专项：
   - 输出 `/music/open/api/v1/*` 的接口分组说明。
   - 补示例请求和错误码表。
3. Webhook 专项：
   - 将同步分发改为异步队列。
   - 增加失败重试、调用日志和签名摘要。
4. 后台配置专项：
   - 给 `hajihami.developer.api_keys` 和 `hajihami.developer.webhooks` 增加可视化配置入口。
5. 类型治理专项：
   - 先定位后台 typecheck 历史错误。
   - 分批修复生成 CRUD 类型。

## 8. 当前禁止事项

- 禁止把 Next.js 文档技术栈迁入前台。
- 禁止删除 Nuxt 前台已有页面来重做。
- 禁止重写后台框架或替换请求层。
- 禁止修改已有公开接口语义导致前台 SSR 断链。
- 禁止未经记录就把阶段状态从“进行中”改为“已完成”。

## 9. 接手检查清单

接手前执行：

```bash
git branch --show-current
git status --short
```

开发前阅读：

- `hjm-cloud/doc/20-stage/hajihami-rebuild-roadmap.md`
- `hjm-cloud/doc/30-open-api/hajihami-open-api-v1.md`
- `hjm-web-nuxt-frontend/docs/hajihami-current-stack-plan.md`
- `hjm-admin/docs/README.md`

交付前执行：

```bash
git diff --check
```

并按任务范围执行对应项目验证命令。
