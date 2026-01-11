# 音乐模块 API 文档

> 文档版本: v2.0  
> 更新时间: 2025-11-30  
> 基础路径: `/music`

---

## 目录

- [一、概述](#一概述)
- [二、通用说明](#二通用说明)
- [三、一级页面接口](#三一级页面接口)
  - [3.1 音乐管理](#31-音乐管理)
  - [3.2 标签管理](#32-标签管理)
- [四、二级页面/详情接口](#四二级页面详情接口)
  - [4.1 音乐详情](#41-音乐详情)
  - [4.2 原曲关联](#42-原曲关联)
  - [4.3 标签关联](#43-标签关联)
  - [4.4 链接检测日志](#44-链接检测日志)
- [五、数据字典](#五数据字典)
- [六、前端页面规划](#六前端页面规划)

---

## 一、概述

本模块为音乐管理后台系统，主要用于**展示**和**审核**，不包含添加音乐功能（音乐由用户端上传）。

| 功能模块 | 说明 | 页面级别 |
|---------|------|---------|
| 音乐管理 | 曲库列表展示、搜索、筛选、审核、批量操作 | **一级页面** |
| 标签管理 | 标签的完整CRUD管理 | **一级页面** |
| 音乐详情 | 音乐完整信息展示（含原曲、资源、标签、审核记录） | 二级页面 |
| 原曲关联 | 音乐与原曲的关联管理 | 详情子项 |
| 标签关联 | 音乐与标签的关联管理 | 详情子项 |
| 链接检测 | 外链失效检测日志（只读） | 日志查看 |

---

## 二、通用说明

### 2.1 请求格式

- Content-Type: `application/json`
- 认证方式: Sa-Token（Header携带token）

### 2.2 分页参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页条数，默认10 |
| orderByColumn | String | 否 | 排序字段 |
| isAsc | String | 否 | 排序方向 asc/desc |

### 2.3 通用响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### 2.4 分页响应格式

```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [],
  "total": 100
}
```

---

## 三、一级页面接口

### 3.1 音乐管理

> 路由: `/music`  
> 权限前缀: `music:music`

#### 3.1.1 获取音乐列表

**请求方式:** `GET /music/list`  
**权限标识:** `music:music:list`

**请求参数:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| title | String | 否 | 作品标题（模糊搜索） |
| creatorId | Long | 否 | 创作者ID |
| creatorName | String | 否 | 创作者名称（模糊搜索） |
| auditStatus | String | 否 | 审核状态：0待审/1通过/2拒绝/3下架 |
| isPublic | String | 否 | 是否公开 Y/N |
| isOriginal | String | 否 | 是否原创 Y/N |
| resourceStatus | String | 否 | 资源状态 |
| publishTimeStart | Date | 否 | 发布时间-开始 |
| publishTimeEnd | Date | 否 | 发布时间-结束 |
| createTimeStart | Date | 否 | 创建时间-开始 |
| createTimeEnd | Date | 否 | 创建时间-结束 |

**响应数据 - MusicVo:**

```json
{
  "code": 200,
  "rows": [
    {
      "id": 1,
      "title": "纯白",
      "subtitle": "Pure White",
      "originalTitle": "紅蓮華",
      "creatorId": 100,
      "creatorName": "ilem",
      "creatorLink": "https://space.bilibili.com/xxx",
      "producerMark": "全民制作人",
      "duration": 240,
      "bpm": 128,
      "publishTime": "2025-01-15 10:00:00",
      "playCount": 100000,
      "likeCount": 5000,
      "collectCount": 2000,
      "commentCount": 300,
      "shareCount": 150,
      "downloadCount": 800,
      "originalData": [
        {"title": "紅蓮華", "author": "LiSA"}
      ],
      "resourceData": {
        "audio": "https://cdn.example.com/music/xxx.mp3",
        "cover": "https://cdn.example.com/cover/xxx.webp"
      },
      "tagsSnapshot": [
        {"id": 1, "name": "电子", "color": "#FF5722"},
        {"id": 2, "name": "流行", "color": "#2196F3"}
      ],
      "extendData": {},
      "auditStatus": "1",
      "isPublic": "Y",
      "isOriginal": "N",
      "resourceStatus": "0",
      "copyrightInfo": "CC BY-NC",
      "remark": "",
      "createTime": "2025-01-15 09:00:00"
    }
  ],
  "total": 100
}
```

---

#### 3.1.2 获取音乐基本信息

**请求方式:** `GET /music/{id}`  
**权限标识:** `music:music:query`

**路径参数:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| id | Long | 是 | 音乐ID |

**响应数据:** 同MusicVo结构

---

#### 3.1.3 获取音乐详情（含关联数据）

**请求方式:** `GET /music/detail/{id}`  
**权限标识:** `music:music:query`

**路径参数:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| id | Long | 是 | 音乐ID |

**响应数据 - MusicDetailVo:**

```json
{
  "code": 200,
  "data": {
    "id": 1,
    "title": "纯白",
    "subtitle": "Pure White",
    "originalTitle": "紅蓮華",
    "creatorId": 100,
    "creatorName": "ilem",
    "creatorLink": "https://space.bilibili.com/xxx",
    "producerMark": "全民制作人",
    "duration": 240,
    "bpm": 128,
    "publishTime": "2025-01-15 10:00:00",
    "playCount": 100000,
    "likeCount": 5000,
    "collectCount": 2000,
    "commentCount": 300,
    "shareCount": 150,
    "downloadCount": 800,
    "extendData": {},
    "auditStatus": "1",
    "isPublic": "Y",
    "isOriginal": "N",
    "resourceStatus": "0",
    "copyrightInfo": "CC BY-NC",
    "remark": "",
    "createTime": "2025-01-15 09:00:00",
    "updateTime": "2025-01-15 10:00:00",
    
    "originals": [
      {
        "id": 1,
        "musicId": 1,
        "originalTitle": "紅蓮華",
        "originalAuthor": "LiSA",
        "originalAlbum": "紅蓮華 - Single",
        "originalLink": "https://music.163.com/xxx",
        "sourceType": "netease",
        "relationType": "cover",
        "sortOrder": 1,
        "linkStatus": "0",
        "lastCheckTime": "2025-01-15 03:00:00",
        "remark": "",
        "createTime": "2025-01-15 09:00:00"
      }
    ],
    
    "resources": [
      {
        "id": 1,
        "musicId": 1,
        "resType": "audio",
        "qualityTier": "320k",
        "sourceType": "local",
        "sourceUrl": "",
        "sourceId": "",
        "url": "https://cdn.example.com/music/xxx.mp3",
        "filePath": "/music/2025/01/xxx.mp3",
        "fileName": "xxx.mp3",
        "fileFormat": "mp3",
        "fileSize": 8388608,
        "fileHash": "abc123",
        "specInfo": {"bitrate": "320kbps", "sampleRate": 44100},
        "cdnUrl": "https://cdn.example.com/music/xxx.mp3",
        "accessCount": 1000,
        "isPrimary": "Y",
        "status": "0",
        "processStatus": "2",
        "retryCount": 0,
        "failCount": 0,
        "failReason": null,
        "lastCheckTime": "2025-01-15 03:00:00",
        "sortOrder": 1,
        "createTime": "2025-01-15 09:00:00"
      },
      {
        "id": 2,
        "musicId": 1,
        "resType": "cover",
        "qualityTier": "1200",
        "sourceType": "local",
        "url": "https://cdn.example.com/cover/xxx.webp",
        "fileFormat": "webp",
        "fileSize": 102400,
        "isPrimary": "Y",
        "status": "0",
        "processStatus": "2"
      }
    ],
    
    "tags": [
      {
        "id": 1,
        "name": "电子",
        "type": "genre",
        "tagAlias": "Electronic,EDM",
        "parentId": null,
        "iconUrl": "",
        "color": "#FF5722",
        "description": "电子音乐风格",
        "useCount": 1500,
        "sortOrder": 1,
        "isHot": "Y",
        "isRecommend": "Y",
        "status": "0",
        "createTime": "2025-01-01 00:00:00"
      }
    ],
    
    "auditLogs": [
      {
        "id": 1,
        "musicId": 1,
        "musicTitle": "纯白",
        "action": 1,
        "actionName": "通过",
        "oldStatus": 0,
        "newStatus": 1,
        "reason": null,
        "operatorId": 1,
        "operatorName": "admin",
        "createTime": "2025-01-15 10:00:00"
      }
    ]
  }
}
```

---

#### 3.1.4 审核音乐

**请求方式:** `POST /music/audit`  
**权限标识:** `music:music:audit`

**请求参数 - MusicAuditBo:**

```json
{
  "musicId": 1,
  "action": 1,
  "reason": ""
}
```

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| musicId | Long | 是 | 音乐ID |
| action | Integer | 是 | 审核动作：1通过 2拒绝 3下架 |
| reason | String | 条件 | 拒绝/下架原因（action为2或3时必填） |

**响应数据:**

```json
{
  "code": 200,
  "msg": "操作成功"
}
```

---

#### 3.1.5 批量审核通过

**请求方式:** `POST /music/batchPass/{ids}`  
**权限标识:** `music:music:audit`

**路径参数:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| ids | Long[] | 是 | 音乐ID数组，逗号分隔，如 `1,2,3` |

---

#### 3.1.6 批量下架

**请求方式:** `POST /music/batchOffline`  
**权限标识:** `music:music:audit`

**请求参数:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| ids | Long[] | 是 | 音乐ID数组（Query参数） |
| reason | String | 是 | 下架原因（Query参数） |

**示例:** `POST /music/batchOffline?ids=1,2,3&reason=违规内容`

---

#### 3.1.7 删除音乐

**请求方式:** `DELETE /music/{ids}`  
**权限标识:** `music:music:remove`

**路径参数:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| ids | Long[] | 是 | 音乐ID数组，逗号分隔 |

---

#### 3.1.8 获取统计信息

**请求方式:** `GET /music/statistics`  
**权限标识:** `music:music:list`

**响应数据:**

```json
{
  "code": 200,
  "data": {
    "total": 1000,
    "pending": 50,
    "passed": 900,
    "rejected": 30,
    "offline": 20,
    "needArchive": 15
  }
}
```

---

#### 3.1.9 导出音乐列表

**请求方式:** `POST /music/export`  
**权限标识:** `music:music:export`

**请求参数:** 同列表查询参数

**响应:** Excel文件下载

---

### 3.2 标签管理

> 路由: `/tag`  
> 权限前缀: `music:tag`

#### 3.2.1 获取标签列表（分页）

**请求方式:** `GET /tag/list`  
**权限标识:** `music:tag:list`

**请求参数 - TagBo:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| name | String | 否 | 标签名称（模糊搜索） |
| type | String | 否 | 标签类型 |
| parentId | Long | 否 | 父标签ID |
| isHot | String | 否 | 是否热门 Y/N |
| isRecommend | String | 否 | 是否推荐 Y/N |
| status | String | 否 | 状态 0正常 1停用 |

**响应数据 - TagVo:**

```json
{
  "code": 200,
  "rows": [
    {
      "id": 1,
      "name": "电子",
      "type": "genre",
      "tagAlias": "Electronic,EDM",
      "parentId": null,
      "iconUrl": "https://cdn.example.com/icon/electronic.png",
      "color": "#FF5722",
      "description": "电子音乐风格",
      "useCount": 1500,
      "sortOrder": 1,
      "isHot": "Y",
      "isRecommend": "Y",
      "status": "0",
      "createTime": "2025-01-01 00:00:00"
    }
  ],
  "total": 50
}
```

---

#### 3.2.2 获取全部标签（不分页）

**请求方式:** `GET /tag/all`  
**权限标识:** `music:tag:list`

**请求参数:** 同上（用于筛选）

**响应数据:**

```json
{
  "code": 200,
  "data": [
    {"id": 1, "name": "电子", "type": "genre", "color": "#FF5722"},
    {"id": 2, "name": "流行", "type": "genre", "color": "#2196F3"}
  ]
}
```

---

#### 3.2.3 获取标签详情

**请求方式:** `GET /tag/{id}`  
**权限标识:** `music:tag:query`

---

#### 3.2.4 新增标签

**请求方式:** `POST /tag`  
**权限标识:** `music:tag:add`

**请求参数 - TagBo:**

```json
{
  "name": "电子",
  "type": "genre",
  "tagAlias": "Electronic,EDM",
  "parentId": null,
  "iconUrl": "",
  "color": "#FF5722",
  "description": "电子音乐风格",
  "sortOrder": 1,
  "isHot": "N",
  "isRecommend": "N",
  "status": "0"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| name | String | 是 | 标签名称 |
| type | String | 否 | 标签类型 |
| tagAlias | String | 否 | 别名（逗号分隔） |
| parentId | Long | 否 | 父标签ID |
| iconUrl | String | 否 | 图标URL |
| color | String | 否 | 颜色(HEX) |
| description | String | 否 | 描述 |
| sortOrder | Integer | 否 | 排序号 |
| isHot | String | 否 | 是否热门 |
| isRecommend | String | 否 | 是否推荐 |
| status | String | 否 | 状态 |

---

#### 3.2.5 修改标签

**请求方式:** `PUT /tag`  
**权限标识:** `music:tag:edit`

**请求参数:** 同新增，额外需要 `id` 字段

---

#### 3.2.6 删除标签

**请求方式:** `DELETE /tag/{ids}`  
**权限标识:** `music:tag:remove`

---

#### 3.2.7 导出标签

**请求方式:** `POST /tag/export`  
**权限标识:** `music:tag:export`

---

## 四、二级页面/详情接口

### 4.1 音乐详情

见 [3.1.3 获取音乐详情](#313-获取音乐详情含关联数据)

音乐详情页面包含以下Tab：
- **基本信息**: 标题、创作者、时长等
- **原曲信息**: originals 数组
- **资源文件**: resources 数组
- **标签**: tags 数组
- **审核记录**: auditLogs 数组

---

### 4.2 原曲关联

> 路由: `/original`  
> 权限前缀: `music:original`  
> 说明: 用于管理音乐与原曲的关联关系

#### 4.2.1 查询原曲列表

**请求方式:** `GET /original/list`  
**权限标识:** `music:original:list`

**请求参数 - MusicOriginalBo:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| musicId | Long | 否 | 音乐ID |
| originalTitle | String | 否 | 原曲标题 |
| sourceType | String | 否 | 来源平台 |

**响应数据 - MusicOriginalVo:**

```json
{
  "code": 200,
  "rows": [
    {
      "id": 1,
      "musicId": 1,
      "originalTitle": "紅蓮華",
      "originalAuthor": "LiSA",
      "originalAlbum": "紅蓮華 - Single",
      "originalLink": "https://music.163.com/xxx",
      "sourceType": "netease",
      "relationType": "cover",
      "sortOrder": 1,
      "linkStatus": "0",
      "lastCheckTime": "2025-01-15 03:00:00",
      "remark": "",
      "createTime": "2025-01-15 09:00:00"
    }
  ],
  "total": 10
}
```

---

#### 4.2.2 获取原曲详情

**请求方式:** `GET /original/{id}`  
**权限标识:** `music:original:query`

---

#### 4.2.3 新增原曲关联

**请求方式:** `POST /original`  
**权限标识:** `music:original:add`

**请求参数 - MusicOriginalBo:**

```json
{
  "musicId": 1,
  "originalTitle": "紅蓮華",
  "originalAuthor": "LiSA",
  "originalLink": "https://music.163.com/xxx",
  "sourceType": "netease",
  "relationType": "cover",
  "sortOrder": 1,
  "remark": ""
}
```

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| musicId | Long | 是 | 音乐ID |
| originalTitle | String | 是 | 原曲标题 |
| originalAuthor | String | 否 | 原曲作者 |
| originalLink | String | 否 | 原曲外链 |
| sourceType | String | 否 | 来源平台 |
| relationType | String | 否 | 关系类型 |
| sortOrder | Long | 否 | 排序号 |
| remark | String | 否 | 备注 |

---

#### 4.2.4 修改原曲关联

**请求方式:** `PUT /original`  
**权限标识:** `music:original:edit`

---

#### 4.2.5 删除原曲关联

**请求方式:** `DELETE /original/{ids}`  
**权限标识:** `music:original:remove`

---

### 4.3 标签关联

> 路由: `/tagRel`  
> 权限前缀: `music:tagRel`  
> 说明: 用于管理音乐与标签的关联关系

#### 4.3.1 查询标签关联列表

**请求方式:** `GET /tagRel/list`  
**权限标识:** `music:tagRel:list`

**请求参数 - MusicTagRelBo:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| musicId | Long | 否 | 音乐ID |
| tagId | Long | 否 | 标签ID |

---

#### 4.3.2 新增标签关联

**请求方式:** `POST /tagRel`  
**权限标识:** `music:tagRel:add`

**请求参数:**

```json
{
  "musicId": 1,
  "tagId": 1,
  "source": "manual",
  "sortOrder": 1
}
```

---

#### 4.3.3 删除标签关联

**请求方式:** `DELETE /tagRel/{ids}`  
**权限标识:** `music:tagRel:remove`

---

### 4.4 链接检测日志

> 路由: `/linkCheckLog`  
> 权限前缀: `music:linkCheckLog`  
> 说明: **只读**，用于查看外链检测记录

#### 4.4.1 查询检测日志列表

**请求方式:** `GET /linkCheckLog/list`  
**权限标识:** `music:linkCheckLog:list`

**请求参数 - MusicLinkCheckLogBo:**

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|------|
| resourceId | Long | 否 | 资源ID |
| resourceType | String | 否 | 资源类型 audio/cover/original |
| musicId | Long | 否 | 音乐ID |
| checkResult | String | 否 | 检测结果 0正常 1失效 2超时 3异常 |
| checkTimeStart | Date | 否 | 检测时间-开始 |
| checkTimeEnd | Date | 否 | 检测时间-结束 |
| checkBatch | String | 否 | 检测批次号 |

**响应数据 - MusicLinkCheckLogVo:**

```json
{
  "code": 200,
  "rows": [
    {
      "id": 1,
      "resourceId": 100,
      "resourceType": "audio",
      "musicId": 1,
      "checkUrl": "https://example.com/audio.mp3",
      "checkResult": "0",
      "httpStatus": 200,
      "responseTime": 150,
      "errorMessage": null,
      "checkTime": "2025-01-15 03:00:00",
      "checkBatch": "20250115030000"
    }
  ],
  "total": 1000
}
```

---

#### 4.4.2 获取检测日志详情

**请求方式:** `GET /linkCheckLog/{id}`  
**权限标识:** `music:linkCheckLog:query`

---

#### 4.4.3 导出检测日志

**请求方式:** `POST /linkCheckLog/export`  
**权限标识:** `music:linkCheckLog:export`

---

## 五、数据字典

### 5.1 审核状态 (music_audit_status)

| 值 | 标签 | 说明 |
|---|------|-----|
| 0 | 待审核 | 新提交，等待审核 |
| 1 | 已通过 | 审核通过，已上架 |
| 2 | 已拒绝 | 审核不通过 |
| 3 | 已下架 | 主动或被动下架 |

### 5.2 资源状态 (music_resource_status)

| 值 | 标签 | 说明 |
|---|------|-----|
| 0 | 正常 | 资源可用 |
| 1 | 失效 | 外链失效 |
| 2 | 需补档 | 需要重新上传 |
| 3 | 处理中 | 转码/压缩中 |

### 5.3 处理状态 (music_process_status)

| 值 | 标签 | 说明 |
|---|------|-----|
| 0 | 待处理 | 等待处理 |
| 1 | 处理中 | 正在处理 |
| 2 | 已完成 | 处理完成 |
| 3 | 处理失败 | 处理失败 |

### 5.4 检测结果 (music_check_result)

| 值 | 标签 | 说明 |
|---|------|-----|
| 0 | 正常 | 链接有效 |
| 1 | 失效 | 链接失效 |
| 2 | 超时 | 请求超时 |
| 3 | 异常 | 其他异常 |

### 5.5 来源类型 (music_source_type)

| 值 | 标签 |
|---|------|
| local | 本地上传 |
| bilibili | B站 |
| netease | 网易云 |
| soundcloud | SoundCloud |
| youtube | YouTube |

### 5.6 标签类型 (music_tag_type)

| 值 | 标签 |
|---|------|
| genre | 曲风 |
| mood | 情绪 |
| scene | 场景 |
| instrument | 乐器 |
| vocal | 人声 |
| language | 语言 |
| other | 其他 |

### 5.7 关系类型 (music_relation_type)

| 值 | 标签 | 说明 |
|---|------|-----|
| original | 原创 | 完全原创作品 |
| cover | 翻唱 | 翻唱作品 |
| remix | 改编 | 重新编曲 |
| arrange | 编曲 | 重新编曲版本 |
| sample | 采样 | 使用原曲采样 |

---

## 六、前端页面规划

### 6.1 一级页面

| 页面 | 路由 | 功能 |
|-----|------|-----|
| 音乐列表 | /music/list | 搜索、筛选、批量审核、查看详情 |
| 标签管理 | /music/tag | 标签CRUD、树形展示 |

### 6.2 二级页面

| 页面 | 入口 | 功能 |
|-----|------|-----|
| 音乐详情 | 音乐列表→查看 | 完整信息展示、审核操作 |
| 原曲管理 | 音乐详情→Tab | 查看/编辑原曲关联 |
| 资源管理 | 音乐详情→Tab | 查看资源文件状态 |
| 审核记录 | 音乐详情→Tab | 查看审核历史 |
| 检测日志 | 独立入口或音乐详情 | 查看链接检测记录 |

### 6.3 页面功能建议

#### 音乐列表页
- **顶部统计卡片**: 总数、待审核、已通过、资源异常
- **筛选区**: 审核状态、资源状态、时间范围、创作者
- **列表列**: 封面、标题、创作者、时长、标签、状态、操作
- **批量操作**: 批量通过、批量下架

#### 音乐详情页
- **Tab1 基本信息**: 标题、创作者、统计数据、审核按钮
- **Tab2 原曲信息**: 原曲列表、链接状态
- **Tab3 资源文件**: 音频/封面列表、处理状态
- **Tab4 审核记录**: 审核历史时间线

#### 标签管理页
- 标准CRUD表格
- 支持按类型筛选
- 颜色预览
- 使用次数统计

---

## 七、权限标识汇总

| 模块 | 权限标识 | 说明 |
|-----|---------|-----|
| 音乐 | music:music:list | 查看音乐列表 |
| 音乐 | music:music:query | 查看音乐详情 |
| 音乐 | music:music:audit | 审核音乐 |
| 音乐 | music:music:remove | 删除音乐 |
| 音乐 | music:music:export | 导出音乐 |
| 标签 | music:tag:list | 查看标签列表 |
| 标签 | music:tag:query | 查看标签详情 |
| 标签 | music:tag:add | 新增标签 |
| 标签 | music:tag:edit | 修改标签 |
| 标签 | music:tag:remove | 删除标签 |
| 标签 | music:tag:export | 导出标签 |
| 原曲 | music:original:list | 查看原曲列表 |
| 原曲 | music:original:query | 查看原曲详情 |
| 原曲 | music:original:add | 新增原曲 |
| 原曲 | music:original:edit | 修改原曲 |
| 原曲 | music:original:remove | 删除原曲 |
| 标签关联 | music:tagRel:list | 查看标签关联 |
| 标签关联 | music:tagRel:add | 新增标签关联 |
| 标签关联 | music:tagRel:remove | 删除标签关联 |
| 检测日志 | music:linkCheckLog:list | 查看检测日志 |
| 检测日志 | music:linkCheckLog:query | 查看日志详情 |
| 检测日志 | music:linkCheckLog:export | 导出检测日志 |

---

> 文档结束
