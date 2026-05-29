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

### 接口分组说明

OpenAPI v1 按以下 Swagger/OpenAPI 标签分组：

1. **元信息与统计** (`/meta`, `/stats/overview`)：提供基础配置下发和全站公开统计概览。
2. **歌曲检索** (`/songs`, `/songs/{id}`, `/songs/random`, `/search`)：音乐实体资源的分页、详情和随机推荐。
3. **搜索辅助** (`/search/panel`, `/search/suggest`, `/search/hot-keywords`)：提供聚合搜索面板、输入框建议词和近期热搜榜。
4. **榜单与标签** (`/charts/{type}`, `/charts/{type}/archives`, `/tags`)：周月榜单、历史归档及公用多级标签树。
5. **用户与互动** (`/users/{uid}`, `/songs/{id}/comments`)：公开用户主页资料及歌曲公开评论数据。
6. **Webhook 推送** (`/webhooks/dispatch`)：支持开发者主动触发或模拟服务端事件分发。

### 全局错误码表

| HTTP 状态码 | 业务 Code | 说明 | 常见场景 |
| --- | --- | --- | --- |
| 200 | 200 | 成功 | 请求处理成功 |
| 400 | 500 | 业务错误 | 请求参数缺失或校验未通过 |
| 401 | 401 | 未授权 | 需提供有效 API key 或 token 已停用 |
| 403 | 403 | 访问拒绝 | 权限不足或被封禁 |
| 404 | 404 | 资源未找到 | 请求的记录（如歌曲 ID、uid）不存在 |
| 429 | 429 | 请求过频 | 超出 60次/分钟 的限流阈值 |
| 500 | 500 | 服务器异常 | 内部服务异常或第三方网络超时 |

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

**请求示例**：
```http
GET /music/open/api/v1/songs?keyword=测试&sort=hot&pageNum=1&pageSize=10 HTTP/1.1
Host: api.hajihami.com
X-Hakimi-Api-Key: dev_xxx
```

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

**请求示例**：
```http
GET /music/open/api/v1/charts/week?limit=20 HTTP/1.1
Host: api.hajihami.com
```

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

用途：开发者 Webhook 推送测试/分发，必须提供有效开发者 token。该接口已升级为**异步任务**模式。

**请求示例**：
```http
POST /music/open/api/v1/webhooks/dispatch HTTP/1.1
Host: api.hajihami.com
Content-Type: application/json
X-Hakimi-Api-Key: dev_xxx

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

**重试策略**：
- 如推送失败（非 2xx 响应或网络超时），系统将进行最多 **3 次**自动重试。
- 重试间隔采用指数退避策略（1s, 2s, 4s）。

返回字段：

| 字段 | 说明 |
| --- | --- |
| eventType | 本次事件类型 |
| triggeredBy | 触发的开发者应用名 |
| dispatchedAt | 任务提交时间 |
| message | 状态简讯（如：“Webhook 推送任务已异步提交”） |

> 提示：具体的推送结果、状态码和重试记录，请联系管理员在后台“Webhook 调用日志”中查看。

## 10. 机器人平台适配建议

哈基哈米 Webhook 发送标准 JSON 格式。如需集成到主流机器人平台，可参考以下建议：

### 10.1 QQ 机器人 (官方 OpenAPI)
- **方案**：建议使用中间件（如 Node.js Express 或 Python FastAPI）接收哈基哈米推送，然后调用 QQ 机器人 OpenAPI 发送消息。
- **配置**：在 `hajihami.developer.webhooks` 中配置中间件地址。

### 10.2 Discord
- **方案**：Discord Webhook 需特定格式。可使用 [Discord Webhook Proxy](https://github.com/vaxerr/discord-webhook-proxy) 或简单的网关函数进行转换。
- **Payload 转换示例** (Node.js):
  ```javascript
  const discordPayload = {
    content: `**${body.title}**\n${body.content}\n[查看详情](${body.targetUrl})`
  };
  ```

### 10.3 Telegram
- **方案**：使用 `https://api.telegram.org/bot<token>/sendMessage`。
- **适配**：由于 Telegram 接收 `chat_id` 和 `text` 参数，建议通过网关将哈基哈米的 `content` 映射到 `text`。

## 12. 静态文档导出说明

如需将本 API 契约导出为静态 JSON/YAML 文件（用于导入 Postman、Apifox 或生成客户端 SDK），请参考以下步骤：

1. **在线访问**：启动后端服务后，访问 `http://<gateway-ip>/music/v3/api-docs/music-open` 获取 JSON 原始定义。
2. **Swagger UI**：访问 `http://<gateway-ip>/music/swagger-ui/index.html` 切换到“哈基哈米开放 API v1”分组进行在线调试与导出。
3. **手动导出**：本项目已预置 Swagger 分组，导出的 JSON 文件建议命名为 `hajihami-openapi-v1.json` 并归档至本目录。
