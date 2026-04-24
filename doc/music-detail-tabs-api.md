# 音乐详情页（多 Tab）接口设计说明

## 1. 目标与范围

本说明用于后台管理端「音乐详情页」对接，页面包含 5 个 Tab：

1. 基本信息
2. 原曲关联
3. 资源文件
4. 数据统计
5. 通知记录

当前以**复用现有接口**为主，仅在文末给出可选优化项。

---

## 2. 页面结构与数据分配

| Tab | 主要展示内容 | 主接口 |
|---|---|---|
| 基本信息 | 音乐主信息、标签、审核日志（可放在右侧时间线） | `GET /system/music/detail/{id}` |
| 原曲关联 | 原曲列表（标题/作者/专辑/外链/关系类型） | `GET /system/original/list?musicId={id}` |
| 资源文件 | 音频资源列表（格式/大小/主版本/状态）+ 上传替换 | `GET /system/resource/list?musicId={id}` |
| 数据统计 | 播放、点赞、收藏、评论、分享、下载、分数 | `GET /system/stat/{musicId}` |
| 通知记录 | 通知历史（类型、标题、渠道、发送状态、阅读状态） | `GET /system/notifyLog/list?musicId={id}` |

---

## 3. 鉴权与网关约定

- 统一通过网关访问：`http://localhost:8080`
- 业务路径前缀：
  - 系统后台：`/system/**`
  - 资源服务：`/resource/**`
- 请求头：
  - `ClientId: e5cd7e4891bf95d1d19206ce24a7b32e`
  - `Authorization: Bearer {access_token}`

---

## 4. 各 Tab 接口设计（复用现有）

## 4.1 基本信息 Tab

### 4.1.1 首屏读取

- `GET /system/music/detail/{id}`
- 用途：一次返回音乐主信息 + 标签 + 原曲 + 资源 + 审核日志
- 前端建议：
  - 基本信息表单字段来自 `data` 主体
  - 标签展示优先使用 `data.tags`
  - 审核日志可用 `data.auditLogs` 做时间线

### 4.1.2 编辑基本信息

- `PUT /system/music`
- Body（示例）：

```json
{
  "id": 384478641838542848,
  "title": "接口测试上传歌曲-20260226120125",
  "subtitle": "接口测试样本",
  "creatorName": "接口测试用户",
  "creatorLink": "https://example.com/creator/test",
  "duration": 233,
  "bpm": 95,
  "isPublic": "1",
  "isOriginal": "0",
  "auditStatus": "0",
  "resourceStatus": "0",
  "copyrightInfo": "接口测试版权",
  "remark": "后台编辑保存"
}
```

### 4.1.3 标签操作（在基本信息 Tab 内）

- 标签候选：
  - `GET /system/tag/list?pageNum=1&pageSize=200&status=0`
- 当前关联：
  - `GET /system/tagRel/list?pageNum=1&pageSize=200&musicId={id}`
- 新增关联：
  - `POST /system/tagRel`
- 修改关联（权重/主标签）：
  - `PUT /system/tagRel`
- 删除关联：
  - `DELETE /system/tagRel/{ids}`

> 说明：当前无“整包覆盖标签”接口，前端保存时建议采用“差量提交（新增/修改/删除）”。

---

## 4.2 原曲关联 Tab

### 4.2.1 列表

- `GET /system/original/list?pageNum=1&pageSize=20&musicId={id}`

### 4.2.2 新增 / 编辑 / 删除

- `POST /system/original`
- `PUT /system/original`
- `DELETE /system/original/{ids}`

### 4.2.3 字段建议

- 展示：`originalTitle`, `originalAuthor`, `originalAlbum`, `relationType`, `sourceType`, `linkStatus`
- 编辑：额外支持 `originalLink`, `sortOrder`, `remark`

---

## 4.3 资源文件 Tab

本 Tab 分两段：先上传到 OSS，再写业务资源记录。

### 4.3.1 资源列表

- `GET /system/resource/list?pageNum=1&pageSize=20&musicId={id}`

### 4.3.2 上传流程（直传）

1. 获取预签名  
   `POST /resource/oss/portal/presign`
2. 前端用返回的 `uploadUrl` 直接 `PUT` 文件到存储
3. 上传完成回调  
   `POST /resource/oss/portal/complete`  
   返回 `ossId/url/fileName`
4. 写入资源业务表  
   `POST /system/resource`

### 4.3.3 新增资源记录（示例）

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

### 4.3.4 编辑 / 删除

- `PUT /system/resource`
- `DELETE /system/resource/{ids}`

---

## 4.4 数据统计 Tab

### 4.4.1 读取

- 推荐：`GET /system/stat/{musicId}`（单曲详情最直接）
- 返回字段：`playCount`, `likeCount`, `collectCount`, `commentCount`, `shareCount`, `downloadCount`, `score`

### 4.4.2 编辑

- `PUT /system/stat`
- 若无记录，先 `POST /system/stat` 创建后再改

> 注意：当前 `GET /system/stat/list` 的查询实现未按 `musicId` 过滤，详情页请优先使用 `GET /system/stat/{musicId}`。

---

## 4.5 通知记录 Tab

### 4.5.1 列表

- `GET /system/notifyLog/list?pageNum=1&pageSize=20&musicId={id}`

### 4.5.2 详情

- `GET /system/notifyLog/{id}`

### 4.5.3 维护（可选，按权限开放）

- `POST /system/notifyLog`
- `PUT /system/notifyLog`
- `DELETE /system/notifyLog/{ids}`

---

## 5. 前端加载时序建议

## 5.1 详情页打开

并行请求：

1. `GET /system/music/detail/{id}`（基本信息 + 标签 + 原曲 + 资源 + 审核日志）
2. `GET /system/stat/{id}`（统计）

懒加载请求：

1. 切到通知记录 Tab 时再调  
   `GET /system/notifyLog/list?musicId={id}&pageNum=1&pageSize=20`

## 5.2 保存策略

- 基本信息：`PUT /system/music`
- 标签：`tagRel` 做差量（新增/修改/删除）
- 原曲：`/system/original` CRUD
- 资源：先 OSS 上传，再 `/system/resource` CRUD
- 统计：`PUT /system/stat`

---

## 6. 权限点（后端已有）

- 基本信息：`music:music:query`, `music:music:edit`
- 原曲关联：`music:original:list|add|edit|remove`
- 资源文件：`music:resource:list|add|edit|remove`
- 数据统计：`music:stat:query|add|edit`
- 通知记录：`music:notifyLog:list|query|add|edit|remove`
- 标签关联：`music:tagRel:list|add|edit|remove`
- 标签字典：`music:tag:list`

---

## 7. 可选优化（后续）

如果后续想减少详情页请求次数，可新增聚合接口：

- `GET /system/music/fullDetail/{id}`

建议返回：

```json
{
  "basic": {},
  "tags": [],
  "originals": [],
  "resources": [],
  "stat": {},
  "notifyPage": { "rows": [], "total": 0 },
  "auditLogs": []
}
```

该优化不是必需，当前接口已能支持页面落地。
