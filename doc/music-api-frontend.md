# 音乐模块前台接口文档

更新时间：2026-03-24

适用范围：
- 前台认证
- 公开站点接口
- 登录态前台接口
- 前台上传接口

## 1. 基础约定

### 1.1 网关入口

- 网关地址：`http://localhost:8080`
- 前台认证：`/auth/front/**`
- 音乐前台：`/music/**`
- 资源服务：`/resource/**`

### 1.2 前台登录态

前台登录、注册入口：
- `POST /auth/front/login`
- `POST /auth/front/register`

前台专用 `ClientId`：
- `7f57f2e3c3f14d15a7c4dd8b7e69a241`

登录成功后，登录态接口统一携带：

```http
ClientId: 7f57f2e3c3f14d15a7c4dd8b7e69a241
Authorization: Bearer {access_token}
```

说明：
- 公开接口无需 `Authorization`
- 当前网关已放行 `/auth/front/**`、`/music/open/**`
- `/resource/oss/portal/**` 当前实现建议携带登录态访问

### 1.3 通用响应结构

`R<T>`：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

`TableDataInfo<T>`：

```json
{
  "total": 13,
  "code": 200,
  "msg": "查询成功",
  "rows": []
}
```

### 1.4 分页参数

列表接口通用分页参数：
- `pageNum`
- `pageSize`
- `orderByColumn`
- `isAsc`

---

## 2. 前台认证接口

### 2.1 前台注册

`POST /auth/front/register`

请求体：

```json
{
  "tenantId": "000000",
  "username": "front_test_001",
  "password": "123456"
}
```

字段说明：
- `tenantId`：可选，默认租户可传 `000000`
- `username`：必填，长度 2-30
- `password`：必填，长度 5-30
- `code`：验证码开启时传
- `uuid`：验证码开启时传

响应：`R<Void>`

### 2.2 前台登录

`POST /auth/front/login`

请求体：

```json
{
  "tenantId": "000000",
  "username": "front_test_001",
  "password": "123456"
}
```

响应：`R<LoginVo>`

返回核心字段：
- `access_token`
- `refresh_token`
- `expire_in`
- `refresh_expire_in`
- `client_id`
- `scope`
- `openid`

说明：
- 当前实现为前台密码登录
- 前台账号写入 `sys_user`，但固定 `user_type=app_user`
- 不影响后台原有 `/auth/login`

---

## 3. 公开接口（无需登录）

### 3.1 公开标签

#### GET `/music/open/tag/list`

用途：公开标签列表。

请求参数：
- `keyword`
- `type`

响应：`R<List<TagVo>>`

核心字段：
- `id`
- `name`
- `type`
- `tagAlias`
- `color`
- `description`
- `status`

### 3.2 公开歌曲列表与详情

#### GET `/music/open/song/list`

用途：公开歌曲列表、搜索结果页。

请求参数：
- `keyword`
- `tag`
- `sort`
- `pageNum`
- `pageSize`

响应：`TableDataInfo<MusicVo>`

列表核心字段：
- `id`
- `title`
- `subtitle`
- `creatorName`
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

#### GET `/music/open/song/{id}`

用途：公开歌曲详情。

响应：`R<MusicDetailVo>`

`data` 主要包含：
- 音乐主信息
- `originals`
- `resources`
- `tags`
- `auditLogs`

#### GET `/music/open/song/random`

用途：随机推荐。

请求参数：
- `limit`

响应：`R<List<MusicVo>>`

### 3.3 搜索建议与搜索面板

#### GET `/music/open/song/suggest`

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

#### GET `/music/open/song/hot-keywords`

请求参数：
- `limit`
- `days`

响应：`R<List<String>>`

#### GET `/music/open/song/panel`

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

### 3.4 榜单与互动曝光

#### GET `/music/open/song/chart/{type}`

请求参数：
- `period`
- `limit`

响应：`R<OpenChartVo>`

#### GET `/music/open/song/{id}/comments`

用途：公开评论树。

响应：`R<List<OpenCommentVo>>`

#### POST `/music/open/song/{id}/play`
#### POST `/music/open/song/{id}/share`
#### POST `/music/open/song/{id}/download`

用途：播放、分享、下载上报。

响应：`R<Void>`

---

## 4. 登录态前台接口

说明：
- `portal/music` 用于创作者自己的作品管理
- `portal/song` 用于面向单曲的互动行为
- 这两个前缀当前同时存在，前端按实际路由调用即可

### 4.1 我的作品

#### GET `/music/portal/music/my`

用途：我的作品分页列表。

请求参数：
- `pageNum`
- `pageSize`
- `title`
- `auditStatus`
- `resourceStatus`

响应：`TableDataInfo<MusicVo>`

#### GET `/music/portal/music/{id}`

用途：我的作品详情。

响应：`R<MusicDetailVo>`

#### PUT `/music/portal/music/{id}`

用途：修改自己的作品。

请求体：`PortalMusicUpdateBo`

常用字段：
- `title`
- `subtitle`
- `creatorName`
- `creatorLink`
- `duration`
- `bpm`
- `coverUrl`
- `resourceData`
- `tagsSnapshot`
- `extendData`
- `copyrightInfo`
- `remark`

#### DELETE `/music/portal/music/{id}`

用途：删除自己的作品。

响应：`R<Void>`

### 4.2 草稿与投稿

#### POST `/music/portal/draft/save`

用途：保存草稿。

请求体：纯文本或 JSON 字符串内容。

响应：`R<Long>`，返回草稿 ID。

#### GET `/music/portal/draft/get?id={id}`

用途：读取草稿。

#### POST `/music/portal/draft/update?id={id}`

用途：更新草稿。

请求体：纯文本或 JSON 字符串内容。

#### POST `/music/portal/draft/delete?id={id}`

用途：删除草稿。

#### POST `/music/portal/draft/submit`

用途：提交审核。

请求体：`MusicSubmitBo`

建议至少包含：
- 基础音乐信息
- 标签信息
- 资源信息
- 原曲关联信息

响应：`R<Long>`，返回音乐 ID。

### 4.3 标签提案

#### GET `/music/portal/tag/list`

用途：登录态标签列表，通常用于投稿表单标签选择。

#### POST `/music/portal/tag/proposal`

用途：提交新标签提案。

请求体：`PortalTagProposalBo`

常用字段：
- `tagName`
- `tagType`
- `reason`
- `remark`

#### GET `/music/portal/tag/proposal/my`

用途：查询我的标签提案。

响应：`R<List<TagProposalVo>>`

### 4.4 通知中心

#### GET `/music/portal/notify`

用途：我的通知列表。

请求参数：
- `pageNum`
- `pageSize`

响应：`TableDataInfo<MusicNotifyLogVo>`

### 4.5 点赞、收藏、评论

#### POST `/music/portal/song/{id}/like`
#### DELETE `/music/portal/song/{id}/like`
#### GET `/music/portal/song/{id}/liked`

用途：点赞、取消点赞、查询是否已点赞。

#### POST `/music/portal/song/{id}/collect`
#### DELETE `/music/portal/song/{id}/collect`
#### GET `/music/portal/song/{id}/collected`

用途：收藏、取消收藏、查询是否已收藏。

#### POST `/music/portal/song/{id}/comments`

用途：发表评论。

请求体：`PortalCommentSubmitBo`

常用字段：
- `content`
- `rootId`
- `parentId`

#### DELETE `/music/portal/song/{id}/comments/{commentId}`

用途：删除自己的评论。

---

## 5. 前台上传接口

当前实现支持两种模式：
- 单文件直传
- 分片上传

上传完成后，前台再把 `ossId / url / filePath / fileName / fileSize` 写入投稿数据或资源表单。

### 5.1 单文件直传

#### 步骤 1：申请预签名

`POST /resource/oss/portal/presign`

请求体：

```json
{
  "fileName": "demo.mp3",
  "fileSize": 5099868,
  "contentType": "audio/mpeg",
  "fileHash": "optional-md5-or-sha256",
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

#### 步骤 2：前端直传到对象存储

使用步骤 1 返回的 `uploadUrl` 发 `PUT` 请求上传文件内容。

#### 步骤 3：回调完成上传

`POST /resource/oss/portal/complete`

请求体：

```json
{
  "objectKey": "music/2026/02/26/afc09068d3504b8483a46d667630e548.mp3",
  "originalName": "demo.mp3",
  "fileSize": 5099868,
  "contentType": "audio/mpeg",
  "fileHash": "optional-md5-or-sha256",
  "service": "rustfs"
}
```

响应：`R<SysOssUploadVo>`

字段：
- `url`
- `fileName`
- `ossId`

### 5.2 分片上传

#### 步骤 1：初始化分片

`POST /resource/oss/portal/multipart/init`

请求体：

```json
{
  "fileName": "big-demo.flac",
  "fileSize": 104857600,
  "contentType": "audio/flac",
  "partSize": 5242880,
  "partCount": 20,
  "fileHash": "optional-md5-or-sha256",
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

其中 `parts` 每项通常包含：
- `partNumber`
- `uploadUrl`
- `method`

#### 步骤 2：逐片上传

前端对 `parts` 中每个 `uploadUrl` 发 `PUT` 请求。

#### 步骤 3：完成分片

`POST /resource/oss/portal/multipart/complete`

请求体示例：

```json
{
  "uploadId": "upload-id",
  "objectKey": "music/2026/03/24/big-demo.flac",
  "originalName": "big-demo.flac",
  "fileSize": 104857600,
  "contentType": "audio/flac",
  "fileHash": "optional-md5-or-sha256",
  "service": "rustfs",
  "parts": [
    { "partNumber": 1, "etag": "etag-1" },
    { "partNumber": 2, "etag": "etag-2" }
  ]
}
```

响应：`R<SysOssUploadVo>`

#### 步骤 4：取消分片

`POST /resource/oss/portal/multipart/abort`

请求体：

```json
{
  "uploadId": "upload-id",
  "objectKey": "music/2026/03/24/big-demo.flac",
  "service": "rustfs"
}
```

---

## 6. 前端页面调用建议

### 6.1 首页 / 搜索页

并行加载：
- `GET /music/open/tag/list`
- `GET /music/open/song/list`
- `GET /music/open/song/hot-keywords`

输入关键字时：
- `GET /music/open/song/suggest`
- `GET /music/open/song/panel`

### 6.2 作品详情页

首屏：
- `GET /music/open/song/{id}`
- `GET /music/open/song/{id}/comments`

用户点击播放、分享、下载时：
- `POST /music/open/song/{id}/play`
- `POST /music/open/song/{id}/share`
- `POST /music/open/song/{id}/download`

### 6.3 投稿页

建议顺序：
1. 登录：`POST /auth/front/login`
2. 标签候选：`GET /music/portal/tag/list`
3. 上传资源：`/resource/oss/portal/*`
4. 保存草稿：`POST /music/portal/draft/save`
5. 提交审核：`POST /music/portal/draft/submit`

---

## 7. 当前联调说明

已联通并实测通过的关键链路：
- `POST /auth/front/register`
- `POST /auth/front/login`
- `GET /music/open/tag/list`
- `GET /music/portal/music/my`

说明：
- 当前 `valkey-search` 已保留，但全文索引能力仍需单独验证
- 不影响前台基础浏览、投稿、审核、互动接口联调
