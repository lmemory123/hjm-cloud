# 音乐模块需求基线（改进版）

本文基于以下两份文档整合修正：

- `doc/ai.md`（偏未来架构建议）
- `doc/music-detail-tabs-api.md`（当前项目可落地接口）

目标：给后续需求编写与前端开发提供一份**不冲突、可直接执行**的基线。

---

## 1. 结论先行（冲突校正）

`doc/ai.md` 中有价值的方向是对的（高并发、检索优化、异步化），但和当前项目存在明显不一致。当前开发应按下面规则执行：

1. **接口路径以现有网关路由为准，不使用 `/api/songs` 体系。**
   - 当前有效：`/system/*`、`/music/portal/*`、`/resource/oss/portal/*`
2. **详情页 5 个 Tab 以现有 Controller 为准，优先复用，不先造新聚合接口。**
3. **上传链路以“预签名 URL 直传 + complete 回调”实现，不是 STS Token 模式。**
4. **ValkeySearch、`@ApiVersion`、`@ConcurrencyLimit` 作为演进项，不作为当前需求前置条件。**
5. **当前需求文档和页面联调按 P0 执行，架构升级放入 P1/P2。**

---

## 2. 当前可执行范围（P0）

## 2.1 页面与业务目标

后台音乐详情页包含 5 个 Tab：

1. 基本信息
2. 原曲关联
3. 资源文件
4. 数据统计
5. 通知记录

要求：可查、可改、可删、可补数据，支持上传音频并绑定到该音乐。

## 2.2 鉴权与网关

- 网关入口：`http://localhost:8080`
- 请求头：
  - `ClientId: e5cd7e4891bf95d1d19206ce24a7b32e`
  - `Authorization: Bearer {token}`

## 2.3 核心接口基线（P0）

### A. 基本信息 Tab

- 首屏详情：`GET /system/music/detail/{id}`
- 基本信息保存：`PUT /system/music`
- 标签候选：`GET /system/tag/list`
- 标签关联：`GET /system/tagRel/list?musicId={id}`
- 标签增删改：`POST/PUT/DELETE /system/tagRel`

说明：标签保存采用差量提交（新增/修改/删除）。

### B. 原曲关联 Tab

- 列表：`GET /system/original/list?musicId={id}`
- 增删改：`POST/PUT/DELETE /system/original`

### C. 资源文件 Tab

- 列表：`GET /system/resource/list?musicId={id}`
- 上传流程：
  1. `POST /resource/oss/portal/presign`
  2. 客户端 `PUT uploadUrl`
  3. `POST /resource/oss/portal/complete`
  4. `POST /system/resource` 写入资源业务记录
- 资源增删改：`POST/PUT/DELETE /system/resource`

### D. 数据统计 Tab

- 单曲统计：`GET /system/stat/{musicId}`
- 保存统计：`PUT /system/stat`（无记录时 `POST /system/stat`）

说明：详情页不要依赖 `/system/stat/list` 做单曲读取。

### E. 通知记录 Tab

- 列表：`GET /system/notifyLog/list?musicId={id}`
- 详情：`GET /system/notifyLog/{id}`
- 管理端维护：`POST/PUT/DELETE /system/notifyLog`

---

## 3. 前后台职责划分（当前）

## 3.1 前台（music-web）

- 草稿：`/music/portal/draft/*`
- 投稿：`POST /music/portal/draft/submit`
- 我的作品：`GET /music/portal/music/my`
- 作品详情/编辑/删除：`GET/PUT/DELETE /music/portal/music/{id}`
- 标签（公开）：`GET /music/open/tag/list`
- 标签提案：`/music/portal/tag/*`
- 我的通知：`GET /music/portal/notify`

## 3.2 后台（system）

- 音乐主数据审核与管理：`/system/music/*`
- 原曲、资源、标签关联、统计、通知日志：`/system/original|resource|tagRel|stat|notifyLog/*`

## 3.3 资源服务（resource）

- OSS 直传接口：`/resource/oss/portal/*`
- 存储后端：rustfs（已验证可上传并落库）

---

## 4. 来自 ai.md 的可采纳项（演进，不阻塞当前）

以下建议保留，但按阶段推进：

1. **检索层升级**：引入 ValkeySearch 做高并发搜索（P1）
2. **高频计数写入优化**：播放/点赞先写缓存，异步批量落库（P1）
3. **投稿后异步处理**：审核前增加异步工作流（P1）
4. **统一聚合详情接口**：`/system/music/fullDetail/{id}`（P1）
5. **版本化 API 策略**：评估 `@ApiVersion` 统一版本治理（P2）

---

## 5. 非功能需求（当前可验收）

1. 详情页首屏在同网段下响应可控（建议目标 < 1s）
2. 上传流程可重复执行，失败可重试，不产生脏记录
3. 单曲详情可完整返回：主信息 + 原曲 + 资源 + 标签 + 审核日志
4. 各 Tab 保存后刷新可见，数据一致

---

## 6. 验收清单（需求评审用）

1. 是否明确使用现有网关路径（非 `/api/songs`）  
2. 是否确认 5 个 Tab 的首屏与保存接口  
3. 是否确认上传链路为 `presign -> PUT -> complete -> system/resource`  
4. 是否确认标签采用差量提交  
5. 是否确认统计读取用 `/system/stat/{musicId}`  
6. 是否将 ValkeySearch 等升级项标注为演进而非当前阻塞  

---

## 7. 联调样例数据（已可用）

- musicId：`384478641838542848`
- title：`接口测试上传歌曲-20260226120125`
- ossId：`384478638508265472`
- 文件路径：`music/2026/02/26/afc09068d3504b8483a46d667630e548.mp3`

可直接用于详情页联调与 5 个 Tab 数据展示验证。
