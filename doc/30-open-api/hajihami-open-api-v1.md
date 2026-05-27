# 哈基哈米开放 API v1

更新时间：2026-05-27

本文档记录 S6 开放 API v1 契约。当前接口是对既有 `/music/open/*` 能力的稳定适配层，不替换前台正在消费的接口。

## 1. 基础约定

- 网关前缀：`/music`
- v1 基础路径：`/music/open/api/v1`
- 返回格式：沿用 RuoYi `R<T>` 或 `TableDataInfo<T>`
- 鉴权：公开只读接口允许匿名访问；如请求头携带 token，会校验开发者身份
- API key 请求头：`X-Hakimi-Api-Key`
- Bearer 兼容：`Authorization: Bearer <token>`
- 限频：60 次/分钟/IP，已通过 `@RateLimiter(limitType = IP)` 接入
- 写操作：只开放 Webhook 分发测试/推送接口，必须提供有效开发者 token；播放、分享、下载上报仍保留在现有前台接口中

## 2. 开发者 Token 配置

开发者 token 当前通过系统参数维护，不新增表结构。

配置 key：`hajihami.developer.api_keys`

示例：

```json
[
  {
    "appId": "qq-bot",
    "name": "QQ Bot",
    "token": "dev_xxx",
    "status": "enabled"
  }
]
```

字段说明：

| 字段 | 说明 |
| --- | --- |
| appId | 应用标识，用于日志和 Webhook payload |
| name | 应用展示名 |
| token | 开发者 token，接口只做精确匹配 |
| status/enabled | `enabled`、`true`、`1` 视为启用；其他值视为停用 |

未携带 token 时，公开只读接口仍按匿名访问处理。携带无效或停用 token 时，返回未授权错误。

## 3. 元信息

### GET `/music/open/api/v1/meta`

返回 API 版本、基础路径、鉴权模式、限频策略、配置键和端点清单。

## 4. 歌曲

### GET `/music/open/api/v1/songs`

公开歌曲搜索和分页列表。

参数：

| 参数 | 说明 |
| --- | --- |
| keyword | 关键词，匹配标题、原曲、UP 主等 |
| tag | 单标签 |
| tags | 多标签，逗号、空格等分隔 |
| style | 风格 |
| sort | 排序，如 `hot`、`latest`、`play_count_desc` |
| isOriginal | `1` 原创，`0` 改编/翻唱 |
| isAi | `1` 只看 AI，`0` 排除 AI |
| resourceStatus | 资源状态 |
| startDate/endDate | 发布时间范围 |
| playCountMin/playCountMax | 播放量范围 |
| pageNum/pageSize | 分页 |

### GET `/music/open/api/v1/search`

`/songs` 的别名，方便开发者按搜索语义接入。

### GET `/music/open/api/v1/songs/{id}`

公开歌曲详情。

### GET `/music/open/api/v1/songs/random`

随机推荐歌曲。

参数：

| 参数 | 说明 |
| --- | --- |
| limit | 返回数量 |

### GET `/music/open/api/v1/songs/{id}/comments`

公开评论树。

## 5. 搜索辅助

### GET `/music/open/api/v1/search/panel`

返回搜索筛选面板，包括排序、命中字段、创作者、标签、风格、活跃筛选和热搜词。

### GET `/music/open/api/v1/search/suggest`

搜索建议词。

参数：`keyword`、`limit`。

### GET `/music/open/api/v1/search/hot-keywords`

热搜词。

参数：`limit`、`days`。

## 6. 榜单

### GET `/music/open/api/v1/charts/{type}`

榜单详情。

参数：

| 参数 | 说明 |
| --- | --- |
| type | `week` 或 `month` |
| period | 可选期次 |
| limit | 返回数量 |

### GET `/music/open/api/v1/charts/{type}/archives`

榜单历史归档。

## 7. 用户与标签

### GET `/music/open/api/v1/users/{uid}`

公开用户主页，`uid` 支持数字 creatorId，也支持 creatorName fallback。

### GET `/music/open/api/v1/tags`

公开标签列表。

参数：`keyword`、`type`。

## 8. 统计

### GET `/music/open/api/v1/stats/overview`

开放数据概览。

返回：

| 字段 | 说明 |
| --- | --- |
| publicSongCount | 当前公开歌曲总数 |
| weekChartItemCount | 当前周榜样本条数 |
| monthChartItemCount | 当前月榜样本条数 |
| tagCount | 标签数量 |
| hotKeywords | 最近热搜词 |
| generatedAt | 生成时间 |

## 9. Webhook

### 配置

配置 key：`hajihami.developer.webhooks`

示例：

```json
[
  {
    "id": "qq-main",
    "name": "QQ Bot Main",
    "url": "https://example.com/webhook",
    "secret": "optional-secret",
    "enabled": true,
    "eventTypes": ["test", "announcement.published", "chart.week.published"]
  }
]
```

字段说明：

| 字段 | 说明 |
| --- | --- |
| id/name | Webhook 目标标识和展示名 |
| url | 接收端 HTTP 地址 |
| secret | 可选，配置后会作为 `X-Hakimi-Webhook-Secret` 请求头发送 |
| enabled | 是否启用 |
| eventTypes | 允许事件类型；为空或包含 `*` 表示接收全部 |

### POST `/music/open/api/v1/webhooks/dispatch`

用途：开发者 Webhook 推送测试/分发，必须提供有效开发者 token。

请求体：

```json
{
  "eventType": "test",
  "title": "哈基哈米 Webhook 测试",
  "content": "这是一条测试推送",
  "targetUrl": "https://hajihami.com",
  "data": {
    "source": "open-api"
  }
}
```

接收端收到的 payload 会包含 `eventType`、`title`、`content`、`targetUrl`、`data`、`clientId`、`timestamp`。

返回字段：

| 字段 | 说明 |
| --- | --- |
| eventType | 本次事件类型 |
| triggeredBy | 触发的开发者应用名 |
| attempted/succeeded/failed | 尝试、成功、失败数量 |
| results | 每个 Webhook 目标的状态码和错误信息 |

## 10. 后续计划

- S6.3：补 Swagger/OpenAPI 分组或导出说明。
- S6.4：补 SDK 示例、部署手册、运维手册、数据库 ER 和上线检查清单。
- S6.5：补核心测试、E2E 和压测前置脚本。
