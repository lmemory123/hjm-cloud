# 哈基哈米前台功能后端支撑总控计划

## 目标

前台已经暴露了音乐播放、喜欢、关注、搜索多 Tab、表情包、投稿上传、音质选择、视频/PV 入口等交互。后端需要按模块补齐真实数据闭环，避免前台长期依赖本地缓存、空态或假数据。

本文档给后端协作者和 AI 使用。实现时继续使用当前 `hjm-cloud` 技术栈，不迁移框架，不硬编码密钥。

## 密钥与 OSS 规则

### 必须遵守

- 不允许把 AccessKey ID / Secret 明文提交到 Git。
- 不允许在日志中输出 AccessKey、签名 URL 的完整 query、Authorization、Token。
- 本地开发使用 `.env` 或 Nacos 本地配置注入。
- 正式环境使用 Nacos + Jasypt 加密后的配置。
- 当前对外暴露过的 AccessKey Secret 建议上线前在阿里云控制台轮换一次。

### OSS 配置契约

Bucket：

```text
hjm-oss
```

配置键建议：

```yaml
hajihami:
  oss:
    provider: aliyun
    bucket: ${HJM_OSS_BUCKET:hjm-oss}
    endpoint: ${HJM_OSS_ENDPOINT:}
    region: ${HJM_OSS_REGION:}
    access-key-id: ${HJM_OSS_ACCESS_KEY_ID:}
    access-key-secret: ${HJM_OSS_ACCESS_KEY_SECRET:}
    public-base-url: ${HJM_OSS_PUBLIC_BASE_URL:}
    callback-enabled: false
    presign-expire-seconds: 900
```

缺口：

- 仍需确认阿里云 OSS `endpoint` / `region`。
- Bucket 读策略需要确定：公开读 CDN，或私有读 + 后端签名 GET。
- 上传目录建议按资源类型隔离：`music/audio/`、`music/video/`、`music/cover/`、`emoji/`、`temp/`。

### Docker 本地建议

在 `hjm-cloud/script/docker-compose-all/.env` 写入，文件不得提交：

```text
HJM_OSS_BUCKET=hjm-oss
HJM_OSS_ENDPOINT=<aliyun-oss-endpoint>
HJM_OSS_REGION=<aliyun-region>
HJM_OSS_ACCESS_KEY_ID=<local-secret>
HJM_OSS_ACCESS_KEY_SECRET=<local-secret>
HJM_OSS_PUBLIC_BASE_URL=<cdn-or-oss-public-domain>
```

## 当前前台依赖清单

| 前台能力 | 当前状态 | 后端需要补齐 |
| --- | --- | --- |
| 歌曲卡片喜欢 | 前台本地缓存 + 调用 portal like | 幂等点赞接口、我的喜欢列表、计数一致性 |
| 歌曲详情点赞/收藏 | 已接 portal 接口 | 状态查询、列表查询、取消、并发去重 |
| 作者关注 | 已接 portal follow | 关注/取关、关注状态、粉丝/关注列表、用户排序 |
| 底部播放器音质显示 | 前台可消费资源字段 | 多资源真实 URL、码率、预加载、失败降级 |
| 搜索音频/视频/用户/歌手 Tab | 前台入口已做 | 聚合搜索接口或分类型搜索接口 |
| 表情包专区 | 前台页面已做 | 表情包列表、分类、搜索、投稿、审核 |
| 投稿上传 | 前台向导已有 | OSS 预签名、上传确认、资源入库、审核流 |
| Footer 最近音乐 | SSR 拉最新音乐 | 最新公开歌曲稳定返回 |

## B1：OSS 上传与资源入库

### 目标

让投稿、封面、表情包、视频/PV 都可以通过 OSS 直传进入后端资源表，再由审核流决定是否公开。

### 接口

#### POST `/resource/oss/portal/presign`

鉴权：前台登录。

请求：

```json
{
  "fileName": "demo.mp3",
  "contentType": "audio/mpeg",
  "size": 12345678,
  "resourceType": "audio",
  "checksum": "sha256-or-md5-optional"
}
```

返回：

```json
{
  "uploadId": "uuid",
  "objectKey": "music/audio/2026/06/uuid.mp3",
  "uploadUrl": "https://...",
  "headers": {
    "Content-Type": "audio/mpeg"
  },
  "expireAt": "2026-06-05T12:00:00+08:00"
}
```

#### POST `/resource/oss/portal/complete`

鉴权：前台登录。

请求：

```json
{
  "uploadId": "uuid",
  "objectKey": "music/audio/2026/06/uuid.mp3",
  "resourceType": "audio",
  "fileName": "demo.mp3",
  "contentType": "audio/mpeg",
  "size": 12345678,
  "checksum": "sha256-or-md5-optional",
  "duration": 123.4
}
```

返回：资源 VO，至少包含：

```json
{
  "id": "resource-id",
  "url": "https://cdn-or-signed-url",
  "objectKey": "music/audio/2026/06/uuid.mp3",
  "mediaType": "audio",
  "status": "uploaded"
}
```

### 验收

- 上传 URL 过期时间可控。
- 只允许白名单扩展名：`mp3`、`flac`、`m4a`、`ogg`、`mp4`、`webm`、`jpg`、`png`、`webp`、`gif`。
- 文件大小限制按类型区分：音频 100MB、视频 300MB、封面/表情 20MB。
- `complete` 必须校验 object 是否存在，不能只信前端。

## B2：音乐资源模型与播放闭环

### 目标

公开歌曲详情和列表返回结构化 `resources[]`，支持音频/视频/外链、多码率、码率选择和失败降级。

### 数据模型建议

新增或规范 `music_resource`：

| 字段 | 说明 |
| --- | --- |
| id | 资源 ID |
| music_id | 歌曲 ID |
| media_type | `audio` / `video` / `cover` / `external` / `waveform` |
| quality_tier | `128k` / `320k` / `lossless` / `source` |
| bitrate | 码率 |
| codec | 编码 |
| container | 容器，如 mp3/flac/mp4 |
| sample_rate | 采样率 |
| duration | 时长 |
| size | 文件大小 |
| object_key | OSS object key |
| url | 外链或公开 URL |
| resource_status | `normal` / `partial` / `missing` / `transcoding` |
| sort_order | 展示排序 |

### 公开 VO

`GET /music/open/song/list` 和 `GET /music/open/song/{id}` 必须返回：

```json
{
  "id": "1001",
  "title": "Call of Silence",
  "mediaType": "audio",
  "hasVideo": false,
  "resources": [
    {
      "id": "r1",
      "url": "https://...",
      "mediaType": "audio",
      "qualityTier": "320k",
      "bitrate": 320000,
      "codec": "mp3",
      "container": "mp3",
      "sampleRate": 44100,
      "size": 12345678,
      "duration": 123.4,
      "posterUrl": null
    }
  ]
}
```

### 验收

- 前台 `/song/{id}` 能列出可播放音质。
- 所有返回 URL 必须真实可访问；没有真实 128k 文件时不要伪造 128k URL。
- Range/206 能用于音频和视频。
- 资源失效时接口返回 `resourceStatus=missing`，前台可降级或提示补档。

## B3：喜欢、收藏、我的喜欢

### 目标

替换前台本地喜欢缓存为云端列表，形成“喜欢 -> 我的喜欢 -> 计数 -> 状态查询”闭环。

### 接口

```text
POST   /music/portal/song/{id}/like
DELETE /music/portal/song/{id}/like
GET    /music/portal/song/{id}/liked
GET    /music/portal/me/likes?pageNum=&pageSize=
```

返回列表使用公开歌曲卡片 VO：

```json
{
  "rows": [],
  "total": 0
}
```

### 规则

- `like` 幂等，多次请求不重复计数。
- `unlike` 幂等，不存在时也返回成功。
- 计数更新建议使用事件或缓存增量，避免高频写主表。
- 用户删除或歌曲下架后，列表过滤不可公开内容。

### 验收

- 登录用户点击歌曲卡片喜欢后，刷新页面仍保持喜欢状态。
- `/me/collections` 或后续 `/me/likes` 能读取云端喜欢列表。
- 未登录请求返回 401，不返回 500。

## B4：关注、粉丝、用户搜索

### 目标

支撑搜索页“用户”Tab、歌曲详情“关注作者”、用户主页关注/粉丝列表。

### 接口

```text
POST   /music/portal/user/{uid}/follow
DELETE /music/portal/user/{uid}/follow
GET    /music/portal/user/{uid}/followed
GET    /music/open/user/{uid}/follows?pageNum=&pageSize=
GET    /music/open/user/{uid}/fans?pageNum=&pageSize=
GET    /music/open/search/user?q=&sort=&pageNum=&pageSize=
```

用户搜索排序：

```text
sort=
  comprehensive
  followers_desc
  level_desc
  active_desc
```

用户搜索 VO：

```json
{
  "uid": "10001",
  "displayName": "front_demo",
  "avatarUrl": "",
  "bio": "",
  "songCount": 0,
  "followerCount": 0,
  "level": 1,
  "followed": false
}
```

### 验收

- 搜索页用户 Tab 可显示真实用户结果。
- 登录用户关注后，按钮状态能刷新。
- 关注自己返回业务错误码，前台可提示。

## B5：搜索聚合与 Valkey Search

### 目标

搜索页不再只有音乐 DB 兜底，而是支持音频、视频、用户、歌手/原作者的独立结果。

### 推荐接口

保留现有：

```text
GET /music/open/song/list
GET /music/open/search/panel
```

新增：

```text
GET /music/open/search/all?q=&type=&sort=&pageNum=&pageSize=
GET /music/open/search/video?q=&sort=&duration=&publishRange=&pageNum=&pageSize=
GET /music/open/search/user?q=&sort=&pageNum=&pageSize=
GET /music/open/search/artist?q=&sort=&pageNum=&pageSize=
```

如果先不做聚合接口，也可以前台按 Tab 调用各独立接口。

### Valkey Search 索引

建议索引：

```text
idx:music:public
idx:music:video
idx:user:public
idx:artist:public
idx:emoji:public
```

中文搜索要求：

- 标题、原曲、UP、标签支持短词，如 `曼波`、`哈基`。
- 搜索失败或索引为空时必须 DB 兜底。
- 搜索接口返回空列表，不抛异常给前台。

### 验收

- `/search?q=曼波` 可命中真实数据。
- `type=user` 返回用户结果。
- 视频和歌手没有数据时返回空列表和 `total=0`。

## B6：表情包专区与审核

### 目标

让 `/emoji` 从前台页面壳变成真实可运营模块。

### 数据模型建议

`music_emoji`：

| 字段 | 说明 |
| --- | --- |
| id | 表情 ID |
| title | 标题 |
| category | 分类 |
| image_url | 图片/GIF URL |
| thumb_url | 缩略图 |
| related_music_id | 关联歌曲 |
| submitter_id | 投稿用户 |
| audit_status | `pending` / `approved` / `rejected` / `offline` |
| use_count | 使用次数 |
| copy_count | 复制次数 |

### 接口

```text
GET  /music/open/emoji/list?q=&category=&pageNum=&pageSize=
GET  /music/open/emoji/category
POST /music/portal/emoji/submit
POST /music/portal/comment/{commentId}/emoji/{emojiId}
```

后台：

```text
GET   /music/emoji/list
PATCH /music/emoji/{id}/audit
PATCH /music/emoji/{id}/offline
```

### 验收

- `/emoji` SSR 首屏可以读接口。
- 分类筛选和关键词搜索可用。
- 管理端可以审核通过、驳回、下架。

## B7：投稿审核闭环

### 目标

投稿页四步向导提交后，审核员可在后台完成通过/驳回/下架，前台我的作品状态同步。

### 关键接口

```text
POST /music/portal/draft
PUT  /music/portal/draft/{id}
POST /music/portal/draft/{id}/submit
GET  /music/portal/me/works
GET  /music/portal/me/drafts
```

后台：

```text
GET   /music/admin/submission/list
PATCH /music/admin/submission/{id}/approve
PATCH /music/admin/submission/{id}/reject
PATCH /music/admin/song/{id}/offline
```

### 验收

- 登录 -> 上传 OSS -> 保存草稿 -> 提交审核 -> 后台通过 -> 前台公开可见。
- 驳回必须有原因，前台我的作品能看到。
- 重复提交有防重，返回明确业务错误。

## 协作者提示词

### 后端 OSS 上传

```text
你负责哈基哈米 OSS 上传和资源入库。只在 hjm-cloud 中修改后端，不提交任何 AccessKey 明文。Bucket 使用 hjm-oss，AccessKey 从环境变量或 Nacos/Jasypt 读取。实现前台登录用户的 OSS 预签名直传：presign、complete、资源入库、文件存在性校验、大小/类型限制。完成后运行 hjm-music-web/resource 相关模块编译，并用 curl 验证 presign 返回 uploadUrl，complete 能生成资源记录。
```

### 后端互动闭环

```text
你负责哈基哈米喜欢、收藏、关注、我的喜欢列表。只改 hjm-cloud 后端互动相关模块，接口必须幂等，未登录返回 401，不允许 500。实现 /music/portal/song/{id}/like、/liked、/music/portal/me/likes、/music/portal/user/{uid}/follow、/followed、公开 fans/follows。完成后编译 hjm-music-web，并提供 curl 验证：登录后喜欢歌曲、查询状态、我的喜欢列表、关注用户、查询粉丝关注列表。
```

### 后端搜索聚合

```text
你负责哈基哈米搜索聚合。基于当前 Valkey Search，补音频、视频、用户、歌手/原作者搜索入口。搜索接口必须支持空结果稳定返回，索引失败时 DB 兜底。不要破坏现有 /music/open/song/list。完成后验证 /search?q=曼波 对应接口能返回真实歌曲，type=user 能返回用户，type=video 和 artist 即使无数据也返回 total=0。
```

### 表情包模块

```text
你负责哈基哈米表情包模块。实现公开列表、分类、搜索、投稿、后台审核。图片资源走 OSS，不写本地静态假数据。前台 /emoji 首屏需要可 SSR 读取接口。后台审核状态包括 pending、approved、rejected、offline。完成后运行后端 compile、后台 build，并用 curl 验证公开接口和审核接口。
```

## 总控验收清单

每个后端任务完成后，必须提供：

- 分支名、提交 hash。
- 修改文件列表。
- 数据库 migration / 初始化 SQL 变更说明。
- 新增接口 curl 样例。
- 编译命令和结果。
- 是否影响 Nacos 配置。
- 是否需要前台同步改 `useMusicApi`。

总控复验命令：

```bash
mvn -pl ruoyi-modules/hjm-music-web -am -DskipTests compile
curl -s 'http://localhost:8080/music/open/song/list?pageNum=1&pageSize=1' | python3 -m json.tool
curl -s 'http://localhost:8080/music/open/search/panel?q=曼波' | python3 -m json.tool
curl -s 'http://localhost:8080/music/open/emoji/list?pageNum=1&pageSize=10' | python3 -m json.tool
```

前台联调复验：

```bash
cd ../hjm-web-nuxt-frontend
pnpm exec vue-tsc --noEmit --skipLibCheck
pnpm build
FRONTEND_URL=http://localhost:3000 ADMIN_URL=http://localhost:81 pnpm test:e2e
```

## 当前状态

- 2026-06-05：创建本文档。
- 前台已完成紧凑化、喜欢入口、搜索 Tab、用户排序入口、底部播放器喜欢按钮。
- 后端仍需补云端喜欢列表、用户搜索、OSS 真实上传、表情包真实 API、视频/PV 资源。
- OSS Bucket 已定为 `hjm-oss`，endpoint/region/public domain 待确认。
