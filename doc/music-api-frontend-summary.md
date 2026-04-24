# 音乐模块前台接口汇总

更新时间：2026-04-05

## 1. 结论

前台主流程接口已经齐备，可以支持：
- 前台注册、登录
- 公开标签、公开歌曲列表与详情
- 投稿上传、草稿保存、提交审核
- 我的作品、通知、点赞、收藏、评论

当前唯一未完全验通的是 `valkey-search` 的全文检索索引能力；它不影响前台主流程，搜索接口可继续走数据库兜底。

## 2. 已验证情况

### 2.1 已验证通过
- `POST /auth/front/register`
- `POST /auth/front/login`
- `GET /music/open/tag/list`
- `GET /music/portal/music/my`
- `/resource/oss/portal/*` 直传与分片主链路

### 2.2 当前注意项
- Valkey 服务 `8.148.70.157:15051` 仍然返回：`FT.CREATE ... TEXT -> Unknown argument TEXT`
- 说明当前 Search 模块对 `TEXT` 字段仍不兼容
- 因此前台搜索相关接口应视为“可用但建议数据库兜底”

## 3. 前台认证接口

### 3.1 注册
- `POST /auth/front/register`

请求体：
```json
{
  "tenantId": "000000",
  "username": "front_test_001",
  "password": "123456"
}
```

### 3.2 登录
- `POST /auth/front/login`

请求体：
```json
{
  "tenantId": "000000",
  "username": "front_test_001",
  "password": "123456"
}
```

登录态请求头：
```http
ClientId: 7f57f2e3c3f14d15a7c4dd8b7e69a241
Authorization: Bearer {access_token}
```

## 4. 公开接口

### 4.1 标签
- `GET /music/open/tag/list`

### 4.2 歌曲展示与搜索
- `GET /music/open/song/list`
- `GET /music/open/song/{id}`
- `GET /music/open/song/random`
- `GET /music/open/song/suggest`
- `GET /music/open/song/hot-keywords`
- `GET /music/open/song/panel`
- `GET /music/open/song/chart/{type}`

### 4.3 公开互动
- `GET /music/open/song/{id}/comments`
- `POST /music/open/song/{id}/play`
- `POST /music/open/song/{id}/share`
- `POST /music/open/song/{id}/download`

## 5. 登录态前台接口

### 5.1 我的作品
- `GET /music/portal/music/my`
- `GET /music/portal/music/{id}`
- `PUT /music/portal/music/{id}`
- `DELETE /music/portal/music/{id}`

### 5.2 草稿与投稿
- `POST /music/portal/draft/save`
- `GET /music/portal/draft/get`
- `POST /music/portal/draft/update`
- `POST /music/portal/draft/delete`
- `POST /music/portal/draft/submit`

### 5.3 标签提案
- `GET /music/portal/tag/list`
- `POST /music/portal/tag/proposal`
- `GET /music/portal/tag/proposal/my`

### 5.4 通知
- `GET /music/portal/notify`

### 5.5 互动与评论
- `POST /music/portal/song/{id}/like`
- `DELETE /music/portal/song/{id}/like`
- `GET /music/portal/song/{id}/liked`
- `POST /music/portal/song/{id}/collect`
- `DELETE /music/portal/song/{id}/collect`
- `GET /music/portal/song/{id}/collected`
- `POST /music/portal/song/{id}/comments`
- `DELETE /music/portal/song/{id}/comments/{commentId}`

## 6. 上传接口

### 6.1 单文件直传
- `POST /resource/oss/portal/presign`
- 对返回 `uploadUrl` 发 `PUT`
- `POST /resource/oss/portal/complete`

### 6.2 分片上传
- `POST /resource/oss/portal/multipart/init`
- 对每个分片 URL 发 `PUT`
- `POST /resource/oss/portal/multipart/complete`
- `POST /resource/oss/portal/multipart/abort`

## 7. 建议

前台现在可以直接做页面联调。
搜索接口如果依赖全文检索排序，先不要绑定 Valkey Search 结果，优先按数据库结果联调。
