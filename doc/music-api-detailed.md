# 音乐模块接口文档（详细版）

更新时间：2026-03-23

适用范围：

- 前台公开站点接口
- 前台登录态接口
- 后台音乐管理接口
- 资源直传接口

本文档以**当前项目真实代码实现**为准。

---

## 1. 基础约定

## 1.1 网关入口

- 网关地址：`http://localhost:8080`
- 外部访问统一通过网关：
  - 音乐前台：`/music/**`
  - 后台管理：`/system/**`
  - 资源服务：`/resource/**`

## 1.2 鉴权请求头

需要登录的接口统一携带：

```http
ClientId: e5cd7e4891bf95d1d19206ce24a7b32e
Authorization: Bearer {access_token}
```

公开接口一般无需 `Authorization`。

## 1.3 通用响应结构

### `R<T>`

多数详情 / 操作型接口返回：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### `TableDataInfo<T>`

分页接口返回：

```json
{
  "total": 13,
  "code": 200,
  "msg": "查询成功",
  "rows": []
}
```

## 1.4 分页参数

支持的通用分页参数：

- `pageNum`：页码，默认 `1`
- `pageSize`：每页数量
- `orderByColumn`：排序字段
- `isAsc`：`asc` / `desc`

---

## 2. 公开接口（无需登录）

## 2.1 公开标签

### GET `/music/open/tag/list`

用途：公开标签列表，可按关键字和类型筛选。

请求参数：

- `keyword`：可选，标签关键字
- `type`：可选，标签类型

响应 `data`：

- `id`
- `name`
- `type`
- `tagAlias`
- `parentId`
- `iconUrl`
- `color`
- `description`
- `useCount`
- `sortOrder`
- `isHot`
- `isRecommend`
- `status`

## 2.2 公开歌曲搜索

### GET `/music/open/song/list`

用途：公开歌曲列表、搜索、筛选、排序。

请求参数：

- `keyword`：可选，关键字
- `tag`：可选，标签值
- `sort`：可选，排序方式
- `pageNum`
- `pageSize`

响应：`TableDataInfo<MusicVo>`

`rows` 核心字段：

- `id`
- `title`
- `subtitle`
- `creatorName`
- `creatorLink`
- `duration`
- `bpm`
- `playCount`
- `likeCount`
- `collectCount`
- `commentCount`
- `shareCount`
- `downloadCount`
- `resourceData`
- `tagsSnapshot`
- `auditStatus`
- `isPublic`
- `isOriginal`
- `resourceStatus`
- `copyrightInfo`
- `remark`

### GET `/music/open/song/{id}`

用途：公开歌曲详情。

响应：`R<MusicDetailVo>`

`data` 包含：

- 音乐主信息（继承 `MusicVo`）
- `originals`：原曲关联列表
- `resources`：资源列表
- `tags`：标签列表
- `auditLogs`：审核日志列表（公开场景通常为空）

### GET `/music/open/song/random`

用途：随机推荐歌曲。

请求参数：

- `limit`：可选，数量

响应：`R<List<MusicVo>>`

## 2.3 搜索建议与面板

### GET `/music/open/song/suggest`

用途：搜索建议词。

请求参数：

- `keyword`
- `limit`

响应：`R<List<OpenSearchSuggestVo>>`

字段：

- `musicId`
- `tagId`
- `keyword`
- `title`
- `subtitle`
- `creatorName`
- `suggestType`
- `matchType`
- `matchedText`
- `highlightedText`

### GET `/music/open/song/hot-keywords`

用途：热门搜索词。

请求参数：

- `limit`
- `days`

响应：`R<List<String>>`

### GET `/music/open/song/panel`

用途：搜索面板 / 筛选面板。

请求参数：

- `keyword`
- `tag`
- `sort`
- `limit`

响应：`R<OpenSearchPanelVo>`

字段：

- `keyword`
- `currentSort`
- `recommendedSort`
- `correctedKeyword`
- `searchSummary`
- `synonymKeywords`
- `selectedTags`
- `total`
- `sortOptions`
- `matchedFields`
- `creators`
- `tags`
- `hotKeywords`

其中：

- `sortOptions` 元素字段：`code`, `label`, `description`, `selected`, `recommended`
- `matchedFields` / `creators` / `tags` 元素字段：`type`, `label`, `value`, `description`, `count`, `selected`

## 2.4 榜单

### GET `/music/open/song/chart/{type}`

用途：榜单查询。

路径参数：

- `type`：榜单类型

请求参数：

- `period`：可选，周期标识
- `limit`：可选，数量

响应：`R<OpenChartVo>`

字段：

- `snapshotId`
- `chartType`
- `periodKey`
- `status`
- `items`

`items` 元素字段：

- `id`
- `snapshotId`
- `musicId`
- `rankNo`
- `score`
- `playCount`
- `likeCount`
- `song`（`MusicVo`）

## 2.5 公开评论与互动

### GET `/music/open/song/{id}/comments`

用途：公开评论树。

响应：`R<List<OpenCommentVo>>`

字段：

- `id`
- `musicId`
- `userId`
- `nickName`
- `content`
- `rootId`
- `parentId`
- `createTime`
- `replies`

### POST `/music/open/song/{id}/play`

用途：播放上报。

响应：`R<Void>`

### POST `/music/open/song/{id}/share`

用途：分享上报。

响应：`R<Void>`

### POST `/music/open/song/{id}/download`

用途：下载上报。

响应：`R<Void>`

---

## 3. 登录态接口（前台）

## 3.1 我的作品

### GET `/music/portal/music/my`

用途：我的作品列表。

请求参数：

- `pageNum`
- `pageSize`
- 兼容 `MusicBo` 中部分查询字段，如 `title`、`auditStatus`

响应：`TableDataInfo<MusicVo>`

### GET `/music/portal/music/{id}`

用途：我的作品详情。

响应：`R<MusicDetailVo>`

### PUT `/music/portal/music/{id}`

用途：更新我的作品。

请求体：`PortalMusicUpdateBo`

```json
{
  "title": "作品标题",
  "subtitle": "副标题",
  "isOriginal": "0",
  "copyrightInfo": "版权信息",
  "remark": "备注",
  "resourceIds": [1, 2],
  "tagIds": [11, 12],
  "originalInfoList": [
    {
      "originalTitle": "原曲名",
      "originalAuthor": "原作者",
      "originalAlbum": "专辑",
      "originalLink": "https://example.com",
      "sourceType": "manual",
      "relationType": "cover"
    }
  ],
  "tagProposals": [
    {
      "tagName": "新标签",
      "tagType": "style",
      "description": "标签说明"
    }
  ]
}
```

响应：`R<Void>`

### DELETE `/music/portal/music/{id}`

用途：删除我的作品。

响应：`R<Void>`

## 3.2 草稿

### POST `/music/portal/draft/save`

用途：保存草稿。

请求体：纯文本或 JSON 字符串内容。

响应：`R<Long>`，返回草稿 ID。

### GET `/music/portal/draft/get`

请求参数：

- `id`

响应：`R<?>`

### POST `/music/portal/draft/update`

请求参数：

- `id`

请求体：草稿内容字符串。

响应：`R<Boolean>`

### POST `/music/portal/draft/delete`

请求参数：

- `id`

响应：`R<Boolean>`

### POST `/music/portal/draft/submit`

用途：提交审核。

请求体：`MusicSubmitBo`

```json
{
  "title": "歌曲标题",
  "subtitle": "副标题",
  "isOriginal": "0",
  "copyrightInfo": "版权说明",
  "remark": "备注",
  "resourceIds": [384478638508265472],
  "tagIds": [376935609132019712],
  "originalInfoList": [
    {
      "originalTitle": "Trap Queen",
      "originalAuthor": "Fetty Wap",
      "originalAlbum": "Trap Queen (Single)",
      "originalLink": "https://example.com/original",
      "sourceType": "manual",
      "relationType": "cover"
    }
  ],
  "tagProposals": [
    {
      "tagName": "电子流行",
      "tagType": "style",
      "description": "待审核标签"
    }
  ]
}
```

响应：`R<Long>`，返回音乐 ID。

## 3.3 标签与通知

### GET `/music/portal/tag/list`

用途：登录态标签列表。

请求参数：

- `keyword`
- `type`

响应：`R<List<TagVo>>`

### POST `/music/portal/tag/proposal`

请求体：`PortalTagProposalBo`

```json
{
  "musicId": 384478641838542848,
  "tagName": "新标签",
  "tagType": "style",
  "description": "标签说明"
}
```

响应：`R<Long>`

### GET `/music/portal/tag/proposal/my`

用途：我的标签申请。

响应：`R<List<TagProposalVo>>`

### GET `/music/portal/notify`

用途：我的通知列表。

请求参数：

- `pageNum`
- `pageSize`

响应：`TableDataInfo<MusicNotifyLogVo>`

## 3.4 登录态互动与评论

### POST `/music/portal/song/{id}/like`
- 用途：点赞
- 响应：`R<Boolean>`

### DELETE `/music/portal/song/{id}/like`
- 用途：取消点赞
- 响应：`R<Boolean>`

### GET `/music/portal/song/{id}/liked`
- 用途：是否已点赞
- 响应：`R<Boolean>`

### POST `/music/portal/song/{id}/collect`
- 用途：收藏
- 响应：`R<Boolean>`

### DELETE `/music/portal/song/{id}/collect`
- 用途：取消收藏
- 响应：`R<Boolean>`

### GET `/music/portal/song/{id}/collected`
- 用途：是否已收藏
- 响应：`R<Boolean>`

### POST `/music/portal/song/{id}/comments`

请求体：`PortalCommentSubmitBo`

```json
{
  "content": "评论内容",
  "rootId": 0,
  "parentId": 0
}
```

响应：`R<Long>`，返回评论 ID。

### DELETE `/music/portal/song/{id}/comments/{commentId}`

用途：删除评论。

响应：`R<Boolean>`

---

## 4. 后台管理接口（system）

## 4.1 音乐主数据

### GET `/system/music/list`

用途：音乐分页列表。

常用查询参数：

- `pageNum`
- `pageSize`
- `title`
- `creatorName`
- `auditStatus`
- `isPublic`
- `isOriginal`
- `resourceStatus`

响应：`TableDataInfo<MusicVo>`

### GET `/system/music/{id}`

用途：音乐基础信息。

响应：`R<MusicVo>`

### GET `/system/music/detail/{id}`

用途：音乐详情（基础信息 + 原曲 + 资源 + 标签 + 审核日志）。

响应：`R<MusicDetailVo>`

### GET `/system/music/fullDetail/{id}`

用途：音乐完整聚合详情。

响应：`R<MusicFullDetailVo>`

除 `MusicDetailVo` 内容外，额外包含：

- `stat`：`MusicStatVo`
- `notifyLogs`：`List<MusicNotifyLogVo>`
- `comments`：`List<MusicCommentVo>`

### POST `/system/music`

用途：新增音乐。

请求体：`MusicBo`

常用字段：

- `title`
- `subtitle`
- `creatorName`
- `creatorLink`
- `producerMark`
- `duration`
- `bpm`
- `resourceData`
- `tagsSnapshot`
- `extendData`
- `auditStatus`
- `isPublic`
- `isOriginal`
- `resourceStatus`
- `copyrightInfo`
- `remark`

响应：`R<Void>`

### PUT `/system/music`

用途：修改音乐。

请求体：`MusicBo`，需包含 `id`。

### POST `/system/music/audit`

用途：审核音乐。

请求体：`MusicAuditBo`

常用字段：

- `musicId`
- `action`：`1` 通过，`2` 拒绝，`3` 下架
- `reason`

### POST `/system/music/batchPass/{ids}`

用途：批量通过。

### POST `/system/music/batchOffline`

请求参数：

- `ids`
- `reason`

### DELETE `/system/music/{ids}`

用途：删除音乐。

## 4.2 详情页 5 个 Tab 对应接口

### 基本信息

- `GET /system/music/detail/{id}`
- `PUT /system/music`
- `GET /system/tag/list`
- `GET /system/tagRel/list?musicId={id}`
- `POST /system/tagRel`
- `PUT /system/tagRel`
- `DELETE /system/tagRel/{ids}`

### 原曲关联

- `GET /system/original/list?musicId={id}`
- `POST /system/original`
- `PUT /system/original`
- `DELETE /system/original/{ids}`

### 资源文件

- `GET /system/resource/list?musicId={id}`
- `POST /system/resource`
- `PUT /system/resource`
- `DELETE /system/resource/{ids}`

### 数据统计

- `GET /system/stat/{musicId}`
- `POST /system/stat`
- `PUT /system/stat`

### 通知记录

- `GET /system/notifyLog/list?musicId={id}`
- `GET /system/notifyLog/{id}`
- `POST /system/notifyLog`
- `PUT /system/notifyLog`
- `DELETE /system/notifyLog/{ids}`

---

## 5. OSS 直传接口（resource）

## 5.1 单文件直传

### POST `/resource/oss/portal/presign`

请求体：`SysOssPresignBo`

```json
{
  "fileName": "demo.mp3",
  "fileSize": 5099868,
  "contentType": "audio/mpeg",
  "fileHash": "optional",
  "bizType": "music",
  "service": "rustfs"
}
```

响应：`R<SysOssPresignVo>`

字段：

- `uploadUrl`
- `objectKey`
- `expireAt`
- `method`

### POST `/resource/oss/portal/complete`

请求体：`SysOssCompleteBo`

```json
{
  "objectKey": "music/2026/02/26/xxx.mp3",
  "originalName": "demo.mp3",
  "fileSize": 5099868,
  "contentType": "audio/mpeg",
  "fileHash": "optional",
  "service": "rustfs"
}
```

响应：`R<SysOssUploadVo>`

字段：

- `url`
- `fileName`
- `ossId`

## 5.2 分片上传

### POST `/resource/oss/portal/multipart/init`

请求体：`SysOssMultipartInitBo`

```json
{
  "fileName": "large.mp3",
  "fileSize": 104857600,
  "contentType": "audio/mpeg",
  "partSize": 5242880,
  "partCount": 20,
  "fileHash": "optional",
  "bizType": "music",
  "service": "rustfs"
}
```

响应：`R<SysOssMultipartInitVo>`

字段：

- `uploadId`
- `objectKey`
- `parts`
- `expireAt`

`parts` 元素字段：

- `partNumber`
- `uploadUrl`
- `expireAt`

### POST `/resource/oss/portal/multipart/complete`

请求体：`SysOssMultipartCompleteBo`

```json
{
  "uploadId": "upload-id",
  "objectKey": "music/2026/02/26/large.mp3",
  "originalName": "large.mp3",
  "fileSize": 104857600,
  "contentType": "audio/mpeg",
  "fileHash": "optional",
  "service": "rustfs",
  "parts": [
    {
      "partNumber": 1,
      "eTag": "etag-1"
    }
  ]
}
```

响应：`R<SysOssUploadVo>`

### POST `/resource/oss/portal/multipart/abort`

用途：取消分片上传。

请求体：

```json
{
  "uploadId": "upload-id",
  "objectKey": "music/2026/02/26/large.mp3",
  "service": "rustfs"
}
```

响应：`R<Boolean>`

---

## 6. 推荐联调顺序

1. 登录获取 token：`POST /auth/login`
2. 公开标签与公开歌曲列表：`/music/open/tag/list`、`/music/open/song/list`
3. OSS 直传：`presign -> PUT -> complete`
4. 前台草稿 / 提交审核：`/music/portal/draft/*`
5. 后台音乐详情页 5 个 Tab：`/system/music/detail/{id}` + 各关联接口
6. 登录态互动与评论：`/music/portal/song/*`

---

## 7. 当前联调样例数据

- `musicId`：`384478641838542848`
- `title`：`接口测试上传歌曲-20260226120125`
- `ossId`：`384478638508265472`
- `objectKey`：`music/2026/02/26/afc09068d3504b8483a46d667630e548.mp3`
