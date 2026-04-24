# 音乐模块后台接口文档

更新时间：2026-03-24

适用范围：
- 后台管理端接口
- 音乐详情页 5 个 Tab
- 审核与配套管理接口

## 1. 基础约定

### 1.1 网关入口

- 网关地址：`http://localhost:8080`
- 后台统一前缀：`/system/**`

### 1.2 管理端鉴权

后台管理接口统一携带：

```http
ClientId: e5cd7e4891bf95d1d19206ce24a7b32e
Authorization: Bearer {access_token}
```

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

---

## 2. 后台音乐主数据接口

### 2.1 音乐列表

#### GET `/system/music/list`

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

列表核心字段：
- `id`
- `title`
- `subtitle`
- `creatorName`
- `auditStatus`
- `isPublic`
- `isOriginal`
- `resourceStatus`
- `playCount`
- `likeCount`
- `collectCount`
- `commentCount`
- `shareCount`
- `downloadCount`
- `createTime`

#### POST `/system/music/export`

用途：导出列表。

### 2.2 音乐详情

#### GET `/system/music/{id}`

用途：基础信息详情。

#### GET `/system/music/detail/{id}`

用途：标准详情。

`data` 主要包含：
- 主信息 `MusicVo`
- `originals`
- `resources`
- `tags`
- `auditLogs`

#### GET `/system/music/fullDetail/{id}`

用途：完整聚合详情，适合后台详情页并行请求较少的场景。

`data` 主要包含：
- 主信息
- `originals`
- `resources`
- `tags`
- `auditLogs`
- `stat`
- `notifyLogs`
- `comments`

### 2.3 新增、编辑、删除

#### POST `/system/music`

请求体：`MusicBo`

常用字段：
- `title`
- `subtitle`
- `creatorName`
- `creatorLink`
- `producerMark`
- `duration`
- `bpm`
- `coverUrl`
- `resourceData`
- `tagsSnapshot`
- `extendData`
- `auditStatus`
- `isPublic`
- `isOriginal`
- `resourceStatus`
- `copyrightInfo`
- `remark`

#### PUT `/system/music`

请求体：`MusicBo`，必须带 `id`。

#### DELETE `/system/music/{ids}`

用途：删除音乐。

---

## 3. 审核接口

### 3.1 单条审核

#### POST `/system/music/audit`

请求体：

```json
{
  "musicId": 384478641838542848,
  "action": 1,
  "reason": ""
}
```

规则：
- `action=1`：通过
- `action=2`：拒绝
- `action=3`：下架

### 3.2 批量操作

#### POST `/system/music/batchPass/{ids}`

用途：批量通过。

#### POST `/system/music/batchOffline`

请求参数：
- `ids`
- `reason`

用途：批量下架。

### 3.3 审核日志

#### GET `/system/auditLog/list`
#### GET `/system/auditLog/{id}`
#### POST `/system/auditLog/export`

用途：审核流水列表、详情、导出。

---

## 4. 音乐详情页 5 个 Tab 设计

### 4.1 Tab 一：基本信息

推荐读取接口：
- `GET /system/music/detail/{id}`

这个 Tab 展示：
- 标题、副标题
- 作者名、作者链接
- BPM、时长、封面
- 公开状态、原创状态、资源状态、审核状态
- 标签列表
- 扩展 JSON、版权信息、备注

编辑保存：
- `PUT /system/music`

标签候选：
- `GET /system/tag/list?pageNum=1&pageSize=200&status=0`

当前标签关联：
- `GET /system/tagRel/list?pageNum=1&pageSize=200&musicId={id}`

标签增删改：
- `POST /system/tagRel`
- `PUT /system/tagRel`
- `DELETE /system/tagRel/{ids}`

### 4.2 Tab 二：原曲关联

列表：
- `GET /system/original/list?pageNum=1&pageSize=20&musicId={id}`

新增：
- `POST /system/original`

示例：

```json
{
  "musicId": 384478641838542848,
  "originalTitle": "Trap Queen",
  "originalAuthor": "Fetty Wap",
  "originalAlbum": "Trap Queen (Single)",
  "originalLink": "https://example.com/original/trap-queen",
  "sourceType": "manual",
  "relationType": "cover",
  "sortOrder": 1,
  "linkStatus": "0",
  "remark": "后台补充原曲信息"
}
```

编辑 / 删除：
- `PUT /system/original`
- `DELETE /system/original/{ids}`

### 4.3 Tab 三：资源文件

列表：
- `GET /system/resource/list?pageNum=1&pageSize=20&musicId={id}`

新增：
- `POST /system/resource`

示例：

```json
{
  "musicId": 384478641838542848,
  "resType": "audio",
  "qualityTier": "standard",
  "sourceType": "upload",
  "url": "http://8.148.70.157:9000/hjm/music/2026/02/26/afc09068d3504b8483a46d667630e548.mp3",
  "filePath": "music/2026/02/26/afc09068d3504b8483a46d667630e548.mp3",
  "fileName": "MKJEightfold - Trap Queen.mp3",
  "fileFormat": "mp3",
  "fileSize": 5099868,
  "isPrimary": "1",
  "status": "0",
  "processStatus": "1",
  "sortOrder": 1
}
```

编辑 / 删除：
- `PUT /system/resource`
- `DELETE /system/resource/{ids}`

补充：
- 后台也可以直接复用资源服务上传接口
- 上传完成后，把返回的 `ossId/url/fileName` 写入资源表

### 4.4 Tab 四：数据统计

推荐读取：
- `GET /system/stat/{musicId}`

说明：
- 详情页不建议用 `/system/stat/list` 做单曲查询
- `GET /system/stat/{musicId}` 更明确

新增 / 修改：
- `POST /system/stat`
- `PUT /system/stat`

常用字段：
- `musicId`
- `playCount`
- `likeCount`
- `collectCount`
- `commentCount`
- `shareCount`
- `downloadCount`
- `score`

### 4.5 Tab 五：通知记录

列表：
- `GET /system/notifyLog/list?pageNum=1&pageSize=20&musicId={id}`

详情：
- `GET /system/notifyLog/{id}`

新增 / 编辑 / 删除：
- `POST /system/notifyLog`
- `PUT /system/notifyLog`
- `DELETE /system/notifyLog/{ids}`

适用场景：
- 审核结果通知
- 资源异常通知
- 标签提案审核通知
- 系统消息补录

---

## 5. 后台配套管理接口

### 5.1 标签字典

- `GET /system/tag/list`
- `GET /system/tag/{id}`
- `POST /system/tag`
- `PUT /system/tag`
- `DELETE /system/tag/{ids}`
- `POST /system/tag/export`

### 5.2 标签提案审核

- `GET /system/tagProposal/list`
- `GET /system/tagProposal/{id}`
- `POST /system/tagProposal/audit`

### 5.3 评论管理

- `GET /system/comment/list`
- `GET /system/comment/{id}`
- `POST /system/comment`
- `PUT /system/comment`
- `DELETE /system/comment/{ids}`
- `POST /system/comment/export`

### 5.4 互动行为流水

- `GET /system/action/list`
- `GET /system/action/{id}`
- `POST /system/action/export`

说明：
- 适合后台查看点赞、收藏、播放、分享、下载等动作明细

### 5.5 草稿管理

- `GET /system/draft/list`
- `GET /system/draft/{id}`
- `POST /system/draft`
- `PUT /system/draft`
- `DELETE /system/draft/{ids}`
- `POST /system/draft/export`

### 5.6 链路检查日志

- `GET /system/linkCheckLog/list`
- `GET /system/linkCheckLog/{id}`
- `POST /system/linkCheckLog`
- `PUT /system/linkCheckLog`
- `DELETE /system/linkCheckLog/{ids}`
- `POST /system/linkCheckLog/export`

### 5.7 榜单管理

- `GET /system/chartSnapshot/list`
- `GET /system/chartSnapshot/{id}`
- `POST /system/chartSnapshot`
- `PUT /system/chartSnapshot`
- `DELETE /system/chartSnapshot/{ids}`

- `GET /system/chartItem/list`
- `GET /system/chartItem/{id}`
- `POST /system/chartItem`
- `PUT /system/chartItem`
- `DELETE /system/chartItem/{ids}`

### 5.8 虚拟币流水

- `GET /system/coinLedger/list`
- `GET /system/coinLedger/{id}`
- `POST /system/coinLedger`
- `PUT /system/coinLedger`
- `DELETE /system/coinLedger/{ids}`

---

## 6. 后台上传与资源入库

### 6.1 管理端传统上传

#### POST `/resource/oss/upload`

用途：后台管理端直接上传文件。

请求方式：`multipart/form-data`

字段：
- `file`

响应：`R<SysOssUploadVo>`

字段：
- `url`
- `fileName`
- `ossId`

### 6.2 管理端 OSS 查询

- `GET /resource/oss/list`
- `GET /resource/oss/listByIds/{ossIds}`
- `GET /resource/oss/download/{ossId}`
- `DELETE /resource/oss/{ossIds}`

### 6.3 OSS 配置管理

- `GET /resource/oss/config/list`
- `GET /resource/oss/config/{ossConfigId}`
- `POST /resource/oss/config`
- `PUT /resource/oss/config`
- `DELETE /resource/oss/config/{ossConfigIds}`
- `PUT /resource/oss/config/changeStatus`

---

## 7. 后台页面调用建议

### 7.1 音乐列表页

首屏：
- `GET /system/music/list`

批量审核场景：
- `POST /system/music/batchPass/{ids}`
- `POST /system/music/batchOffline`

### 7.2 音乐详情页

推荐方案一：
- 直接用 `GET /system/music/fullDetail/{id}`
- 适合详情页一次性取全量数据

推荐方案二：按 Tab 懒加载
- 基本信息：`GET /system/music/detail/{id}`
- 原曲：`GET /system/original/list?musicId={id}`
- 资源：`GET /system/resource/list?musicId={id}`
- 统计：`GET /system/stat/{id}`
- 通知：`GET /system/notifyLog/list?musicId={id}`

### 7.3 审核工作台

建议并行读取：
- `GET /system/music/list?auditStatus=0`
- `GET /system/tagProposal/list`
- `GET /system/auditLog/list`

---

## 8. 当前实现说明

当前后台接口已经覆盖：
- 音乐主数据增删改查
- 审核流
- 详情页 5 个 Tab
- 标签字典与标签提案审核
- 评论、互动、草稿、榜单、通知、统计等配套模块

当前可直接用于后台页面联调的核心入口：
- `GET /system/music/list`
- `GET /system/music/detail/{id}`
- `GET /system/music/fullDetail/{id}`
- `POST /system/music/audit`
