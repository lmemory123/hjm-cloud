


# 哈基哈米后端架构与接口设计文档

版本：v3.0  
适用仓库：hjm-cloud / new-boot4-copy  
文档定位：本文件既是后端实现规范，也是后续交给 AI 编码助手的高质量上下文。

---

## 一、文档目标

本文解决两个问题：

1. 给当前仓库提供一份可直接执行的音乐模块后端基线，避免需求、接口、实现三者脱节。
2. 给未来的 Spring Boot 4 + JDK 21/25 + PostgreSQL + Valkey/ValkeySearch 架构升级提供清晰的演进路线，而不是把演进目标误写成当前事实。

结论先行：

- 当前仓库已经具备音乐后台、投稿前台、OSS 直传三条主链路，短期应该优先复用现有接口。
- Spring Boot 4、原生 API 版本治理、ValkeySearch、高频写入异步化，属于本项目下一阶段的架构升级目标。
- 本文所有设计都以“当前可落地优先，未来演进不冲突”为原则。

---

## 二、总体架构分层

### 2.1 当前运行时形态

- 网关入口：统一通过网关暴露服务。
- 后台管理：system 模块承接音乐主数据、审核、统计、原曲、资源、通知日志等管理能力。
- 前台门户：music-web 模块承接用户投稿、草稿、我的作品、标签提案、通知中心等前台能力。
- 资源服务：resource 模块承接 OSS 预签名直传、回调确认、分片上传。
- 持久化：当前主存储仍以关系型数据库为核心。

### 2.2 目标技术架构

- 运行环境：JDK 21 起步，优先兼容 JDK 25。
- 核心框架：Spring Boot 4 + Spring Framework 7。
- 数据库：PostgreSQL 16+。
- 缓存与检索：Valkey + ValkeySearch。
- ORM：MyBatis-Flex。
- 对象存储：阿里云 OSS / 腾讯云 COS / 兼容 S3 存储。
- 并发模型：虚拟线程承担高并发 I/O 任务，缓存写入与异步 Worker 解耦高频写压力。

### 2.3 分阶段落地原则

- P0：复用现有网关路径、现有 Controller、现有上传链路，先保证业务可用。
- P1：将高频读写迁移到 Valkey，补齐异步任务、事件流、聚合详情接口。
- P2：引入 ValkeySearch、API 版本治理、空安全、统一可观测性规范。

---

## 三、仓库内模块职责映射

### 3.1 后台管理侧

主要由 ruoyi-system 承担：

- 音乐主数据管理与审核：/system/music/*
- 原曲关联：/system/original/*
- 资源文件：/system/resource/*
- 标签关联：/system/tagRel/*
- 数据统计：/system/stat/*
- 通知记录：/system/notifyLog/*
- 审核日志、评论、榜单、行为流水等扩展能力：按现有 controller 拆分维护

### 3.2 前台门户侧

主要由 hjm-music-web 承担：

- 我的作品：/music/portal/music/*
- 草稿：/music/portal/draft/*
- 标签提案：/music/portal/tag/*
- 我的通知：/music/portal/notify

### 3.3 资源上传侧

主要由 ruoyi-resource 承担：

- 直传预签名：/resource/oss/portal/presign
- 上传完成回调：/resource/oss/portal/complete
- 分片上传初始化：/resource/oss/portal/multipart/init
- 分片上传完成：/resource/oss/portal/multipart/complete
- 分片上传取消：/resource/oss/portal/multipart/abort

说明：服务内部 Controller 路由是 /oss/portal/*，网关对外暴露路径为 /resource/oss/portal/*。文档和联调说明以网关路径为准。

---

## 四、架构设计铁律

### 4.1 当前阶段必须遵守

1. 不凭空新造与现有系统并行的一套接口体系，优先复用当前网关路由。
2. 上传必须走预签名 URL 直传加 complete 回调，不把大文件流量压到业务服务。
3. 详情页首屏优先复用现有聚合查询能力，不先为“设计上的优雅”牺牲交付效率。
4. 高频统计不能长期停留在同步直写数据库的模式，哪怕 P0 先保留，也必须在文档中明确其 P1 改造方向。
5. 演进项要写清楚是“目标架构”还是“当前事实”，不得混写。

### 4.2 目标阶段必须遵守

1. API 版本治理统一收口，禁止在业务代码里到处硬编码版本路径。
2. 搜索、筛选、排序优先走 ValkeySearch，而不是把 PostgreSQL 当搜索引擎硬顶。
3. 高频计数先入缓存，再异步批量落盘，避免数据库热行锁竞争。
4. 复杂 I/O 使用虚拟线程或异步 Worker 处理，不阻塞主请求线程。
5. 外部 HTTP 调用统一收口到声明式客户端，不继续扩散旧式模板调用。

---

## 五、核心领域模型

围绕音乐业务，推荐稳定维护以下领域对象：

- Music：音乐主实体，承载标题、创作者、审核状态、展示属性。
- MusicOriginal：原曲关联，一首音乐可以挂多个原曲来源。
- MusicResource：资源文件，描述音频、封面、外链、转码结果、处理状态。
- MusicStat：聚合统计，承接播放、点赞、收藏、评论、分享、下载等指标。
- MusicTagRel：音乐与标签的关联。
- MusicNotifyLog：审核通知、补档通知、资源失效通知等记录。
- MusicAuditLog：审核动作留痕。
- MusicAction：点赞、收藏、投币、播放上报等行为流水。
- MusicChartSnapshot / MusicChartItem：榜单快照与榜单条目。

建议把“主数据”和“高频统计”明确分层，不再让一张表承担全部职责。

---

## 六、数据库设计建议

### 6.1 PostgreSQL 主表建议

#### 1. sys_user

补齐用户主表或至少补齐音乐业务依赖的用户域信息：

- id
- openid / unionId / 第三方登录标识
- nickname
- avatar
- role
- status
- exp
- createTime

#### 2. music

音乐主表建议保留低频、稳定字段：

- id
- title
- subtitle
- originalTitle
- creatorId
- creatorName
- producerMark
- duration
- bpm
- coverOssId
- publishTime
- auditStatus
- resourceStatus
- isOriginal
- isPublic
- tagsSnapshot JSONB
- extendData JSONB
- remark
- createTime / updateTime / delFlag

不要把 playCount、likeCount 这类超高频变更字段长期放在主表上实时更新。

#### 3. music_stat

统计表独立维护：

- musicId
- playCount
- likeCount
- collectCount
- commentCount
- shareCount
- downloadCount
- hotScore
- lastAggregateTime

#### 4. music_resource

资源表建议保留：

- id
- musicId
- resourceType
- qualityLevel
- sourceType
- sourceUrl
- sourceId
- ossId
- filePath
- fileName
- fileFormat
- fileSize
- fileHash
- specInfo JSONB
- cdnUrl
- status
- processStatus
- retryCount
- failReason
- lastCheckTime
- isPrimary

#### 5. music_original

原曲关联表建议保留：

- musicId
- originalTitle
- originalAuthor
- originalAlbum
- originalLink
- sourceType
- relationType
- sortOrder
- linkStatus
- lastCheckTime

### 6.2 关键索引建议

- music(title)
- music(creator_id)
- music(audit_status, publish_time)
- music(resource_status)
- music_stat(music_id)
- music_resource(music_id, resource_type)
- music_resource(file_hash) 唯一索引，配合 del_flag = 0 实现去重/秒传基础
- music_original(music_id)
- music.tags_snapshot 使用 GIN
- music.extend_data 使用 GIN

### 6.3 建模原则

- 主表只存稳定主信息。
- 高频值单独聚合。
- 搜索快照与业务主表分离。
- 外链状态可追踪、可补档、可审计。

---

## 七、Valkey 与 ValkeySearch 设计

### 7.1 Valkey 角色定位

- 热点详情缓存
- 高频计数累加器
- 随机推荐与榜单缓存
- 点赞/收藏等去重集合
- 异步事件流
- 幂等与限流辅助

### 7.2 推荐缓存键规范

建议统一命名，避免后续键空间失控：

- music:detail:{musicId}
- music:stat:{musicId}
- music:tags:{musicId}
- music:random:pool
- music:chart:{type}:{period}
- music:like:users:{musicId}
- music:collect:users:{musicId}
- music:play:delta
- music:event:uploaded
- music:event:resource-check
- music:idempotent:{bizType}:{bizKey}

### 7.3 ValkeySearch 索引建议

对审核通过的音乐建立 idx:music 检索索引，字段建议如下：

- id: TAG 或 NUMERIC
- title: TEXT，权重最高
- original_title: TEXT
- creator_name: TEXT
- tags: TAG
- audit_status: TAG
- play_count: NUMERIC，可排序
- hot_score: NUMERIC，可排序
- publish_time: NUMERIC，可排序
- resource_status: TAG

### 7.4 文档同步原则

- 只有 audit_status = 1 的音乐进入搜索索引。
- 审核通过、下架、删除、标签变更、统计回刷都要触发索引更新。
- 搜索文档使用展示快照，不在搜索结果阶段实时拼接多张表。

---

## 八、接口设计

## 8.1 当前仓库可直接执行的接口基线

### A. 后台音乐详情页

1. 基本信息 Tab

- GET /system/music/detail/{id}
- PUT /system/music
- GET /system/tag/list
- GET /system/tagRel/list?musicId={id}
- POST /system/tagRel
- PUT /system/tagRel
- DELETE /system/tagRel/{ids}

2. 原曲关联 Tab

- GET /system/original/list?musicId={id}
- POST /system/original
- PUT /system/original
- DELETE /system/original/{ids}

3. 资源文件 Tab

- GET /system/resource/list?musicId={id}
- POST /resource/oss/portal/presign
- 客户端 PUT uploadUrl
- POST /resource/oss/portal/complete
- POST /system/resource
- PUT /system/resource
- DELETE /system/resource/{ids}

4. 数据统计 Tab

- GET /system/stat/{musicId}
- POST /system/stat
- PUT /system/stat

说明：详情页单曲统计读取优先使用 GET /system/stat/{musicId}，不要依赖未按单曲维度过滤的列表查询。

5. 通知记录 Tab

- GET /system/notifyLog/list?musicId={id}
- GET /system/notifyLog/{id}
- POST /system/notifyLog
- PUT /system/notifyLog
- DELETE /system/notifyLog/{ids}

### B. 前台投稿链路

- POST /music/portal/draft/save
- GET /music/portal/draft/get
- POST /music/portal/draft/update
- POST /music/portal/draft/delete
- POST /music/portal/draft/submit
- GET /music/portal/music/my
- GET /music/portal/music/{id}
- PUT /music/portal/music/{id}
- DELETE /music/portal/music/{id}
- GET /music/portal/tag/list
- POST /music/portal/tag/proposal
- GET /music/portal/tag/proposal/my
- GET /music/portal/notify

### C. 审核管理链路

- GET /system/music/list
- GET /system/music/{id}
- GET /system/music/detail/{id}
- POST /system/music/audit
- POST /system/music/batchPass/{ids}
- POST /system/music/batchOffline

## 8.2 目标态逻辑 API 设计

当项目完成 Boot 4 架构升级后，可在不破坏当前网关兼容性的前提下，抽象出统一逻辑 API：

### 检索与展示

- GET /api/songs
- GET /api/songs/{id}
- GET /api/songs/random
- GET /api/charts/{type}

### 高频交互

- POST /api/songs/{id}/play
- POST /api/songs/{id}/like
- POST /api/songs/{id}/collect
- GET /api/songs/{id}/comments

### 投稿与资源处理

- GET /api/upload/check
- GET /api/upload/presigned
- POST /api/songs
- PATCH /api/admin/songs/{id}/audit
- GET /api/utils/fetch-ext

说明：这一层是目标态逻辑接口，不要求立即替换当前 system、portal、resource 三套路径，而是为后续 API 版本治理提供统一抽象。

---

## 九、核心业务流转

### 9.1 高并发搜索

业务目标：用户搜索“曼波”，筛选“原教旨、原创”，按播放量倒序，响应时间目标小于 50ms。

目标链路：

1. 客户端请求统一搜索接口。
2. 网关完成鉴权、路由、基础限流。
3. 应用层构造 ValkeySearch 查询语句。
4. 搜索引擎直接返回命中文档快照。
5. 服务层只做轻量组装，不回表做重型拼装。

示例查询：

FT.SEARCH idx:music "@title|original_title:(曼波) @tags:{原教旨|原创}" SORTBY play_count DESC LIMIT 0 20

### 9.2 高频播放与点赞写入

业务目标：热点歌曲在高并发场景下不把数据库打穿。

目标链路：

1. 客户端在播放达到阈值后上报播放事件。
2. 应用层做幂等、防刷、轻量限流。
3. Valkey 执行 HINCRBY 或计数脚本累加。
4. 点赞/收藏使用 Set 或 Bitmap 做去重判断。
5. 定时任务或流式消费者批量落库到 music_stat 与行为流水。
6. 落库后刷新榜单缓存与搜索排序字段。

### 9.3 投稿与审核异步处理

业务目标：上传和审核链路可扩展，不让长耗时处理阻塞主请求。

目标链路：

1. 客户端先通过预签名接口直传 OSS。
2. complete 回调返回 ossId、path、etag 等存储结果。
3. 用户提交音乐元数据和资源关联。
4. 服务层写入 music、music_resource、tag_rel、audit_log 初始记录。
5. 事务提交后写入事件流：song.uploaded 或 song.submitted。
6. 异步 Worker 执行时长提取、格式校验、封面压缩、敏感词检测、外链元数据抓取。
7. 管理员审核通过后，写入 ValkeySearch 搜索索引并刷新详情缓存。

### 9.4 资源失效检测与补档

业务目标：对外链资源和第三方来源建立持续健康检查机制。

建议流程：

1. 定时任务扫描 sourceType 为第三方平台的资源。
2. 使用 HEAD、探测接口或平台元数据接口检查可访问性。
3. 失败达到阈值后，将 music_resource.status 标记为需补档。
4. 写入 music_link_check_log。
5. 推送通知给投稿人和后台管理端。

---

## 十、异步任务与调度设计

建议至少保留以下任务：

- 播放量回刷任务：每 1 到 3 分钟聚合落库一次。
- 点赞/收藏回刷任务：按分钟或按批次落库。
- 搜索索引同步任务：兜底修复增量失败的数据。
- 外链有效性巡检任务：按天执行。
- 榜单快照生成任务：按周、月生成。
- 资源处理失败重试任务：按退避策略执行。

执行原则：

- I/O 密集型任务优先虚拟线程。
- 有外部依赖的任务必须带超时、重试、熔断保护。
- 所有异步任务都要具备可观测日志与死信补偿思路。

---

## 十一、可观测性与风控

### 11.1 监控指标

- 搜索 QPS、P95、P99
- 详情接口响应时间
- 播放上报吞吐
- Valkey 命中率
- 索引同步延迟
- 资源处理成功率
- 审核积压量

### 11.2 风控要求

- 播放、点赞、收藏、评论写入必须支持幂等。
- 接口层要有基础限流与防刷策略。
- 上传回调必须校验存储返回值，避免伪造完成。
- 审核、下架、批量操作必须保留审计日志。

---

## 十二、编码规范建议

以下规范适用于开始进入 Boot 4 改造后的新代码：

1. API 版本治理统一收口，不再手写 /v1 形式路径分叉。
2. DTO 优先使用 Record，减少样板代码。
3. 空安全显式化，重要边界引入 JSpecify 注解。
4. 外部 HTTP 接口统一使用声明式客户端。
5. 高频写入逻辑不直接耦合数据库更新语句。
6. 搜索接口返回对象以展示快照为核心，而不是暴露数据库内部结构。

---

## 十三、实施优先级

### P0：当前立刻执行

- 统一以后端现有网关路径作为联调标准。
- 完成后台详情页 5 个 Tab 的查改删。
- 固化 presign -> 上传 -> complete -> 业务入库 的上传流程。
- 修正文档与实现不一致的部分，避免前后端对错接口。

### P1：下一阶段优化

- 引入统一聚合详情接口，例如 /system/music/fullDetail/{id}。
- 将播放、点赞、收藏迁移为缓存先写、数据库异步落盘。
- 引入资源异步处理与巡检任务。
- 逐步用 PostgreSQL JSONB + GIN 增强回表检索能力。

### P2：目标架构升级

- 升级到 Spring Boot 4 + Spring Framework 7。
- 落地 ValkeySearch 搜索索引与回刷机制。
- 统一 API 版本治理。
- 虚拟线程承接外部 I/O 密集型链路。

---

## 十四、给 AI 编码助手的标准 Prompt

下面这段提示词可以直接提供给 Cursor、Copilot、Cline 等工具，用于生成符合本项目语境的代码。

```markdown
# 角色与上下文
你是本项目的资深 Java 后端工程师，任务是在 hjm-cloud 仓库内实现音乐模块需求。

# 当前仓库事实
1. 网关路径以现有接口为准，主要包括：
   - /system/*
   - /music/portal/*
   - /resource/oss/portal/*
2. 上传链路必须使用 presign -> 客户端直传 -> complete -> 业务入库。
3. 后台音乐详情页以现有 5 个 Tab 为准：基本信息、原曲关联、资源文件、数据统计、通知记录。
4. 单曲统计优先读取 GET /system/stat/{musicId}。
5. 当前阶段优先复用现有 Controller、Service、Mapper，不凭空重建一套并行接口。

# 目标架构方向
1. 后续将演进到 Spring Boot 4 + JDK 21+ + PostgreSQL + Valkey + ValkeySearch。
2. 高频统计应逐步改为缓存先写、异步落库。
3. 搜索场景后续优先走 ValkeySearch。

# 编码要求
1. 先阅读现有模块实现，再补代码，不要脱离仓库现状。
2. 优先在现有接口和领域模型上扩展，不做无必要重构。
3. 如果修改上传、审核、统计链路，必须说明事务边界与幂等处理。
4. 如果新增异步逻辑，必须说明失败补偿、重试与日志方案。
5. 输出代码时请同时给出 Controller、Service、Mapper 或 Repository、VO/BO、以及必要的 SQL 或索引建议。

请先确认你理解以上约束，然后开始实现：[在这里填写具体接口或功能名称]
```

---

## 十五、最终结论

这份文档的核心不是“把所有新技术名词堆上去”，而是明确三件事：

- 当前仓库今天就能怎么做。
- 下一阶段应该先优化什么。
- 最终目标架构应该演进到哪里。

只要后续开发严格按照这三个层次推进，就不会再出现“文档很先进，代码接不上，前后端联不动”的问题。