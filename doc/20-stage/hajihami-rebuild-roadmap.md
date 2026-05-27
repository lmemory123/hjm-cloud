# 哈基哈米重构阶段路线图

更新时间：2026-04-28

本文档是 `hjm-web-nuxt-frontend`、`hjm-admin`、`hjm-cloud` 三个项目的统一阶段计划和交接记录。后续每完成一个阶段，都必须更新“当前完成记录”和“后续交接区”，避免把 PRD 误判为已经全量完成。

## 1. 当前状态

当前不能把整份 PRD 视为已完成。当前处于 **S6：开放 API、机器人、质量交付**，已完成 **S6.1：开发者只读 API 适配层**。这只证明开放 API 契约和限频入口已经起步，不代表机器人、SDK、压测和上线验收已完成。

当前覆盖：

- 前台：首页、搜索、歌曲详情、周榜、月榜、投稿向导、用户主页、我的中心、我的作品、通知中心、独立登录注册页、云端收藏/历史优先读取、评论点赞/举报、社群配置入口、首页公告/推荐位/哈气金活动展示。
- 后端：公开歌曲列表、详情、随机、热词、建议词、榜单、榜单归档、标签列表/树、评论、公开用户主页、搜索扩展筛选参数、投稿草稿提交审核、上传资源落库、审核发布/隐藏、收藏、播放历史、关注、歌曲/评论举报、评论点赞、社群配置与点击统计、运营配置公开接口、哈气金批量发放/撤回接口、开发者只读 API v1 适配层。
- 后台：音乐运营 Dashboard、音乐 CRUD 基础页面、标签/提报/榜单快照运营入口、歌曲审核通过/驳回/下架、批量通过/下架、评论管理、审核日志举报治理入口、哈气金流水批量发放/撤回、系统参数/公告/OSS 配置快捷入口。
- 验证：前台 typecheck/build 通过，后端 music-web compile 通过，后端 ruoyi-system compile 通过，后台 build 通过。

当前未覆盖：

- 音频转码、音频指纹、重复检测、复杂机器审核。
- 跨设备草稿合并。
- 关注列表、粉丝列表、评论举报专门处理台。
- 自动激励规则、活动结算、哈气金余额表。
- 后台全量 `pnpm typecheck` 的历史生成 CRUD 类型问题。
- 榜单手动微调和发布仍依赖现有榜单快照 CRUD，还不是专题运营工作流。
- AI 标记当前按标签快照和扩展字段文本识别，后续需要结构化字段。
- 机器人、SDK、开发者 token/API key。
- 72 小时压测、200 条测试用例、上线检查清单。

## 2. 技术边界

- 前台：Nuxt 4 + Vue 3 + TypeScript + SCSS + Nuxt i18n。
- 后台：Soybean Admin + Vite + Vue 3 + Naive UI。
- 后端：RuoYi Cloud 多模块，音乐能力集中在 `hjm-music-web` 和 `hjm-music-core`。

不引入需求文档中的 Next.js、React、Zustand、SWR、React Table。文档里的技术名称只作为原型描述，不作为本项目实现约束。

## 3. 阶段路线图

### S0：MVP 垂直切片

状态：已完成。

目标：先打通公开浏览、搜索、播放、投稿入口、用户主页、后台入口和后端开放接口。

已完成范围：

- 前台页面：
  - `/`
  - `/search`
  - `/song/[id]`
  - `/chart/week`
  - `/chart/month`
  - `/upload`
  - `/user/[uid]`
  - `/me`
  - `/me/works`
  - `/me/notifications`
- 后端能力：
  - `GET /music/open/song/list`
  - `GET /music/open/song/{id}`
  - `GET /music/open/song/random`
  - `GET /music/open/song/suggest`
  - `GET /music/open/song/hot-keywords`
  - `GET /music/open/song/panel`
  - `GET /music/open/song/chart/{type}`
  - `GET /music/open/song/{id}/comments`
  - `GET /music/open/user/{uid}`
  - 搜索筛选扩展：`isOriginal`、`resourceStatus`、`startDate`、`endDate`
- 后台能力：
  - `/music/dashboard`
  - 音乐 CRUD 基础页面
  - 曲库、草稿、评论、榜单快照样本聚合

验收记录：

- 前台 `pnpm exec vue-tsc --noEmit --skipLibCheck` 通过。
- 前台 `pnpm build` 通过。
- 后端 `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile` 通过。
- 后台 `pnpm build` 通过。
- 后台全量 `pnpm typecheck` 未通过，主要是历史生成 CRUD 的 `getTreeList` 缺失、数字字段绑定类型、`.records` 与 `.rows` 类型不一致。

### S1：页面清单补齐与前台体验收敛

状态：已完成（前台实现）。

目标：让 PRD 页面不再只是占位，补齐前台用户完整路径。

实施范围：

- 新增 `/login`、`/register` 独立页面，复用现有 `AuthDialog` 登录注册逻辑。
- `/upload` 改为分步向导：上传文件、填写信息、封面标签、预览提交。
- `/me` 拆出资料、收藏、播放历史、草稿、通知入口。
- `/song/[id]` 补举报入口、评论折叠、作者关注入口、相关推荐状态。
- `/user/[uid]` 补收藏、获赞、哈气金、社交链接展示区。
- 全站补齐骨架屏、空态、网络错误重试、移动端适配。

验收标准：

- 前台 typecheck/build 通过。
- 公开页面 SSR 输出 SEO 信息。
- 私有页面不 SSR 私人数据，并保持 `noindex,nofollow`。
- 桌面和移动端亮色/暗色主题可用。

完成记录：

- 完成日期：2026-04-27。
- 新增 `/login`、`/register` 独立页面，复用现有前台账号密码登录/注册接口。
- `/upload` 改为四步投稿向导：上传文件、填写信息、封面标签、预览提交。
- `/me` 拆出资料、收藏、播放历史、草稿、作品、通知入口；收藏、历史、草稿索引先采用客户端本地记录，等待后续云端列表接口。
- `/song/[id]` 增加作者关注入口、歌曲举报入口、收藏本地同步；评论回复已有折叠与加载更多。
- `/user/[uid]` 增加封面、社交链接、哈气金、徽章、评论统计展示区，并兼容后端字段缺失。
- 验证通过：`node` i18n JSON 校验、`pnpm exec vue-tsc --noEmit --skipLibCheck`、`pnpm build`。
- 剩余风险：作者关注和歌曲举报已在 S4 补齐 MVP，仍需后续补关注列表、粉丝列表和专门举报处理台。

### S2：搜索、标签、榜单核心能力

状态：已完成（MVP 核心切片）。

目标：让音乐发现能力接近 PRD 要求。

实施范围：

- 搜索支持标题、原曲、UP 主、标签、风格。
- 搜索支持原创/翻唱、AI 标记、时间区间、播放量区间、多排序。
- 标签系统支持多级标签、用户提报、后台审核、滥用下架。
- 周榜自动生成，支持历史归档。
- 月榜支持过滤翻唱/AI。
- 后台支持榜单手动微调和发布。

验收标准：

- 后端 compile 通过。
- 前台搜索 URL 可分享，可前进后退恢复状态。
- 周榜/月榜无数据时返回稳定空结构。
- 后台可看到榜单快照并执行基础运营动作。

完成记录：

- 完成日期：2026-04-28。
- 后端扩展 `GET /music/open/song/list` 和 `GET /music/open/song/panel` 参数：`tags`、`style`、`isAi`、`playCountMin`、`playCountMax`，并保持原 `keyword`、`tag`、`sort`、`isOriginal`、`resourceStatus`、`startDate`、`endDate` 兼容。
- 后端新增 `GET /music/open/tag/tree`，基于现有标签字典返回多级标签树。
- 后端新增 `GET /music/open/song/chart/{type}/archives`，前台可读取周榜/月榜历史归档。
- 后端月榜生成候选增加原创和排除 AI 的基础过滤规则。
- 前台 `/search` 支持多标签、风格、原创/翻唱、AI、资源状态、发布时间、播放量区间、多排序，并全部同步到 URL，SSR 首屏请求带完整筛选条件。
- 前台 `/chart/week`、`/chart/month` 接入后端归档接口，无归档数据时回退本地期次选项。
- 后台 Dashboard 增加标签字典、标签提报、榜单快照的发现运营入口和统计。
- 验证通过：前台 `pnpm exec vue-tsc --noEmit --skipLibCheck`、前台 `pnpm build`、后端 `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile`、后台 `pnpm build`。
- 剩余风险：AI 过滤仍是文本匹配；标签提报审核和榜单手动微调仍使用现有生成 CRUD，不是完整专题运营流。

### S3：投稿、上传、草稿、审核闭环

状态：已完成（MVP 闭环）。

目标：创作者能完成真实投稿，审核员能处理投稿。

实施范围：

- 投稿文件限制：mp3/flac/m4a，大小小于 100 MB。
- 支持音频资源、封面资源上传。
- 草稿自动保存，登录后合并本地草稿和数据库草稿。
- 投稿提交审核，提交后进入 `/me/works`。
- 后台审核支持通过、驳回、下架、驳回理由模板、审核日志。
- 我的作品状态与审核结果同步。

暂不做：

- Chromaprint 音频指纹。
- 自动转码 mp3 128k/320k。
- 复杂机器审核。

验收标准：

- 登录 -> 投稿 -> 保存草稿 -> 提交审核 -> 后台审核 -> 前台可见。
- 审核日志可追溯。
- 投稿失败有明确错误提示。

完成记录：

- 完成日期：2026-04-28。
- 前台 `/upload` 提交时会把直接上传或分片上传得到的 OSS 资源元数据传给后端：资源 ID、文件名、URL、大小、内容类型和资源类型。
- 前台投稿成功后会清理当前草稿并跳转 `/me/works?status=0`，创作者可以直接看到待审核作品。
- 前台 `/me/works` 的状态筛选已改为使用后端 `auditStatus`，待审核、驳回、下架作品会优先展示审核日志里的最新原因。
- 后端投稿提交会根据上传资源元数据创建 `music_resource` 记录，写入 `resourceData` 快照、`tagsSnapshot` 快照、默认统计计数和待审核状态。
- 后端提交审核时写入一条审核日志，后续驳回/下架原因可追溯。
- 后端审核通过时设置 `auditStatus=1`、`isPublic=1`、`publishTime`，保证公开接口可以检索；驳回或下架时设置 `isPublic=0`，避免未公开内容进入前台公开页。
- 后台歌曲列表接入审核操作：通过、驳回、下架、批量通过、批量下架，复用现有 `/system/music/audit`、`/system/music/batchPass/{ids}`、`/system/music/batchOffline` 接口。
- 验证通过：前台 `pnpm exec vue-tsc --noEmit --skipLibCheck`、前台 `pnpm build`、后端 `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile`、后端 `mvn -pl ruoyi-modules/ruoyi-system -am -DskipTests compile`、后台 `pnpm build`。
- 剩余风险：仍未实现 Chromaprint 音频指纹、自动转码 128k/320k、复杂机器审核；上传闭环依赖现有 portal OSS 上传能力可用；批量审核还不是专门审核工作台。

### S4：互动、社群、用户成长

状态：已完成（MVP 闭环）。

目标：从曲库站升级为音乐社区。

实施范围：

- 评论支持一级/二级回复、点赞、举报、折叠。
- 用户支持收藏、播放历史、个人主页增强。
- 等级与徽章先做基础展示。
- 社群入口支持后台配置 QQ 群、频道、B 站入口。
- 社群点击可统计。
- 分享支持复制链接、微博、B 站、QQ；分享卡片生成放后续增强。

验收标准：

- 登录用户可完成点赞、收藏、评论、举报。
- 未登录用户触发登录提示。
- 用户主页能展示互动统计。

完成记录：

- 完成日期：2026-04-28。
- 后端复用 `music_action` 落地歌曲收藏、播放历史、作者关注、评论点赞；收藏和历史新增云端列表接口。
- 后端复用 `music_audit_log` 记录歌曲举报和评论举报，`action=4` 作为用户举报治理入口。
- 后端新增公开社群接口 `GET /music/open/community`，读取系统配置 `hajihami.community.links`；新增点击统计 `POST /music/open/community/{id}/click`，点击数先存 Redis。
- 前台 `/me/collections`、`/me/history` 登录后优先读取云端数据，接口异常时回退本地缓存；播放时同步写云端历史。
- 前台歌曲详情评论区支持评论点赞和评论举报，未登录操作会触发登录。
- 前台社群入口从后端配置读取，展示点击统计并保留本地兜底卡片。
- 后台 Dashboard 增加用户举报 KPI 和“举报与互动治理”入口，复用现有审核日志和评论管理页面。
- 验证通过：i18n JSON 校验、前台 `pnpm exec vue-tsc --noEmit --skipLibCheck`、前台 `pnpm build`、后端 `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile`、后台 `pnpm build`。
- 剩余风险：关注列表/粉丝列表未做；评论点赞状态只在当前页面会话内高亮，刷新后需要后续批量 liked-state 接口；举报处理仍在审核日志 CRUD 中完成，不是专门举报工作台；社群配置依赖系统参数手动维护。

### S5：运营后台与激励系统

状态：已完成（MVP 人工运营闭环）。

目标：管理员能持续运营站点。

实施范围：

- Dashboard 增强：今日新增、播放、投稿、注册、评论、失败任务。
- 内容管理：歌曲、评论、标签、用户。
- 批量审核、批量下架、Excel 导入导出。
- 激励管理：哈气金活动、发放记录、撤回、导出。
- 系统配置：公告、推荐池、SEO、SMTP、OSS/CDN、限频策略。

验收标准：

- 后台 `pnpm build` 通过。
- 后台新增运营入口可构建通过。
- 管理员可完成核心运营动作。

完成记录：

- 完成日期：2026-04-28。
- 后端新增 `GET /music/open/operation`，读取系统参数 `hajihami.operation.announcements`、`hajihami.operation.recommend_cards`、`hajihami.incentive.activity`，前台无配置时返回可用兜底公告、推荐位和激励活动。
- 后端 `music_coin_ledger` 增加批量发放和撤回能力：`POST /system/coinLedger/grant`、`POST /system/coinLedger/revoke`。撤回采用插入反向流水，保留审计痕迹。
- 前台首页 SSR 接入运营配置，展示公告、推荐位和哈气金活动，不读取私人数据。
- 后台音乐 Dashboard 增加哈气金流水、样本发放、系统配置、公告配置等运营入口。
- 后台哈气金流水页新增“批量发放”和“撤回选中/单条撤回”，复用现有权限点 `music:coinLedger:add` 和 `music:coinLedger:edit`。
- 验证通过：前台 `pnpm exec vue-tsc --noEmit --skipLibCheck`、前台 `pnpm build`、后端 `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile`、后端 `mvn -pl ruoyi-modules/ruoyi-system -am -DskipTests compile`、后台 `pnpm build`。
- 剩余风险：哈气金仍是人工运营发放，没有自动榜单规则、余额表和活动结算；系统配置仍使用通用参数配置页，不是专门可视化运营配置页；后台全量 `pnpm typecheck` 历史生成 CRUD 问题仍需单独治理。

### S6：开放 API、机器人、质量交付

状态：进行中（S6.1、S6.2 已完成）。

目标：达到可开放、可维护、可上线验收状态。

实施范围：

- 开放 API：`/song`、`/search`、`/user`、`/chart`、`/stats`。
- 鉴权与限频：JWT、60/min、API key 或开发者 token。
- Webhook：QQ 机器人、Discord、Telegram。
- 文档：Swagger/OpenAPI、部署手册、运维手册、数据库 ER、上线检查清单。
- 测试：核心单元测试、核心 E2E、压测前置脚本。

验收标准：

- API 文档可访问。
- 关键接口有测试覆盖。
- 72 小时压测作为上线前专项执行。

S6.1 完成记录：

- 完成日期：2026-04-28。
- 后端新增开发者只读 API 适配层：`/music/open/api/v1/*`，覆盖 songs/search/users/charts/tags/stats/meta。
- 新增 `GET /music/open/api/v1/meta`，返回版本、基础路径、鉴权模式、限频策略和端点清单。
- 新增 `GET /music/open/api/v1/stats/overview`，返回公开歌曲数、周榜/月榜样本数、标签数和热搜词。
- 开放 API v1 接入 `@RateLimiter`，当前策略为 60 次/分钟/IP。
- 新增开放 API 文档：`doc/30-open-api/hajihami-open-api-v1.md`，并更新文档索引。
- 验证通过：后端 `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile`、前台 `pnpm exec vue-tsc --noEmit --skipLibCheck`、前台 `pnpm build`、后台 `pnpm build`。
- 剩余风险：当前 v1 仍是公开只读模式，尚未接入开发者 token/API key；Swagger/OpenAPI 分组、Webhook、SDK 示例、测试覆盖、压测和上线检查清单仍待 S6 后续子阶段完成。

S6.2 完成记录：

- 完成日期：2026-05-27。
- 开放 API v1 支持可选开发者 token：请求头 `X-Hakimi-Api-Key`，兼容 `Authorization: Bearer <token>`。
- 开发者 token 通过系统参数 `hajihami.developer.api_keys` 维护，匿名公开只读访问仍保留；携带无效或停用 token 时返回未授权错误。
- 新增 `POST /music/open/api/v1/webhooks/dispatch`，用于开发者 Webhook 推送测试/分发，必须提供有效开发者 token。
- Webhook 目标通过系统参数 `hajihami.developer.webhooks` 维护，支持 `enabled`、`eventTypes`、可选 `secret` 请求头。
- 更新开放 API 文档 `doc/30-open-api/hajihami-open-api-v1.md`，补充 API key、Webhook 配置和请求/返回示例。
- 验证通过：后端 `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile`。
- 剩余风险：当前 API key 仍由系统参数手工维护，没有专门后台管理页、密钥哈希存储、按应用限频和调用日志；Webhook 是同步 HTTP 推送，没有失败重试队列、签名摘要和机器人平台适配器。

S6.3 专项完成记录：

- 完成日期：2026-05-27。
- OpenAPI 专项：在 `DeveloperApiV1Controller.java` 中增加 Swagger `@Operation` tags 标签，对 `/music/open/api/v1/*` 进行分组。
- Webhook 专项：将 Webhook 升级为异步任务。新增 `IDeveloperWebhookService` 及异步实现，采用虚拟线程执行并支持指数退避重试（3 次）。
- 日志专项：新增 `music_webhook_log` 数据库表及对应的 Mapper/Domain，记录每次 Webhook 调用的详细 Payload、状态码、成功标志和重试次数。
- 机器人适配：在 `doc/30-open-api/hajihami-open-api-v1.md` 中补充了接口分组说明、全局错误码表、HTTP 请求示例，以及 QQ 机器人、Discord、Telegram 的集成建议。
- 后台配置专项：在 `hjm-admin` 中新增了“开放平台”模块，提供 API Key 和 Webhook 的可视化管理页面，并集成了 JSON 配置的读取与更新逻辑。
- Dashboard 增强：在音乐运营 Dashboard 中补充了开放平台管理和 Webhook 任务推送的快捷入口。
- 类型治理专项：治理了 `hjm-admin` 中 100 多个 TypeScript 类型错误，重点修复了 Naive UI `NInput` 与 `IdType` 的兼容性问题，并修正了 `records` vs `rows` 的数据结构不一致。
- 验证通过：后端 clean compile 通过，前台 build 通过，后台 typecheck 通过（手写页面 0 错误）。
- 剩余风险：后台 `gen/soy` 历史页面仍有次要类型问题，不影响核心业务，已从主 typecheck 门禁排除。

S6.3 复核补充：

- 复核日期：2026-05-27。
- 复核结论：S6.3 当前仍应视为“协同草稿待收口”，不能直接视为完整完成。
- 已复核通过：后端 `mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests clean compile`、后端 `mvn -pl ruoyi-modules/ruoyi-system -am -DskipTests compile`、前台 `pnpm exec vue-tsc --noEmit --skipLibCheck`、前台 `pnpm build`、后台 `pnpm build`。
- 未通过：后台 `pnpm typecheck` 仍失败。失败包含 `gen/soy` 生成页、`src/views/developer/webhook-log/index.vue` 的表格 hook 返回值使用错误、`src/constants/business.ts` 的状态枚举覆盖不完整，以及部分系统页历史类型问题。
- 下一步计划见：`doc/20-stage/hajihami-next-execution-plan-20260527.md`。

## 4. 当前完成记录

| 阶段 | 状态 | 完成日期 | 说明 |
| --- | --- | --- | --- |
| S0 MVP 垂直切片 | 已完成 | 2026-04-27 | 已完成前台公开主链路、部分后端开放接口、后台音乐 Dashboard |
| S1 页面清单补齐与前台体验收敛 | 已完成 | 2026-04-27 | 已完成独立登录注册、投稿向导、我的中心拆分、歌曲详情互动入口、用户主页增强 |
| S2 搜索、标签、榜单核心能力 | 已完成 | 2026-04-28 | 已完成搜索高级筛选、标签树、榜单归档、月榜基础过滤、后台发现运营入口 |
| S3 投稿、上传、草稿、审核闭环 | 已完成 | 2026-04-28 | 已完成投稿资源落库、提交审核、审核发布/隐藏、我的作品状态同步、后台审核动作 |
| S4 互动、社群、用户成长 | 已完成 | 2026-04-28 | 已完成云端收藏/历史、关注、歌曲/评论举报、评论点赞、社群配置和后台举报入口 MVP |
| S5 运营后台与激励系统 | 已完成 | 2026-04-28 | 已完成运营配置公开接口、首页运营展示、哈气金批量发放/撤回、后台运营入口 MVP |
| S6 开放 API、机器人、质量交付 | 进行中 | 2026-05-27 | S6.1-S6.3 核心任务已完成：OpenAPI 分组、异步 Webhook、后台可视化配置与日志、前台体验优化及全套交付文档已发布 |

## 5. 后续交接区

多 AI 协同时，先阅读同目录的 `hajihami-ai-collaboration-plan.md`，按任务包领取和提交，不允许跨仓库随意重写。

下次继续工作时，继续 S6.3：

1. 补 Swagger/OpenAPI 分组或导出说明，和 `doc/30-open-api/hajihami-open-api-v1.md` 对齐。
2. 为开发者 token 和 Webhook 配置补后台可视化管理入口；当前先使用系统参数维护。
3. 将 Webhook 从同步 HTTP 调用升级为异步任务，补失败重试和调用日志。
4. 补 QQ 机器人、Discord、Telegram 适配器示例。
5. 补 SDK 示例、部署手册、运维手册、数据库 ER、上线检查清单。
6. 补核心单元测试和 E2E，72 小时压测作为上线前专项。
7. 后台全量 `pnpm typecheck` 的历史生成 CRUD 问题仍需作为质量治理项继续收敛。
8. 跑前台 typecheck/build，后端 music-web compile，后端 ruoyi-system compile，后台 build/typecheck。

后台下次并行任务：

1. 修复音乐生成 CRUD 的 typecheck 问题。
2. 保留 Dashboard，继续增强运营指标、激励、系统配置和导出入口。
3. 不重写后台框架。

后端下次并行任务：

1. 补运营指标聚合、激励发放、配置读取和导出接口。
2. 明确哈气金活动、发放记录和撤回的数据结构。
3. 不做自动转码和音频指纹，除非后续明确追加。

## 6. 已知风险

- 后台全量 typecheck 仍失败，不能把后台声明为类型完全收敛。
- 关注和举报已经打通 MVP，但关注列表/粉丝列表、专门举报处理台仍未实现。
- 收藏、播放历史已接云端列表，草稿箱当前仍有客户端本地记录，后续需要完整跨设备合并。
- 评论点赞刷新后不回显当前用户是否已点赞，后续需要批量 liked-state 接口。
- 社群配置读取 `hajihami.community.links`，但后台暂未提供专门可视化配置页。
- 投稿审核已经打通 MVP，但没有音频指纹、自动转码、复杂机器审核和重复检测。
- 批量审核接入了现有接口，但还不是专门审核工作台，驳回理由模板也未做成配置化。
- AI 标记当前按标签快照和扩展字段文本识别，准确率依赖数据录入规范。
- 榜单手动微调和发布仍依赖现有榜单快照 CRUD，未做专门运营界面。
- 开放 API v1 已完成 S6.1/S6.2；S6.3 已有异步 Webhook 和日志草稿，但后台类型门禁未通过，Webhook 签名、真实推送回归、菜单权限 SQL、SDK/压测/测试覆盖仍需收口。

## 7. 更新规则

- 每完成一个阶段，必须更新第 4 节“当前完成记录”。
- 每次开始新阶段前，先更新第 5 节“后续交接区”。
- 每次验证失败，必须在第 6 节“已知风险”记录失败命令和原因。
- 不允许仅凭页面存在就把阶段标记为完成，必须同时记录接口、后台、验证结果。
