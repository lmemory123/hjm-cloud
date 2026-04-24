# 音乐模块后台接口汇总

更新时间：2026-04-05

## 1. 结论

后台管理接口已经覆盖音乐管理主流程，可以支持：
- 音乐主数据管理
- 审核流
- 音乐详情页 5 个 Tab
- 标签、评论、草稿、通知、统计等配套模块

后台当前没有明显缺口，主要工作应转为页面联调和回归测试。

## 2. 已验证情况

### 2.1 已验证通过
- `GET /system/music/list`
- `GET /system/music/{id}`
- `GET /system/music/detail/{id}`
- `POST /system/music`
- `PUT /system/music`
- `POST /system/music/audit`
- `POST /system/music/batchPass/{ids}`
- `POST /system/music/batchOffline`
- `POST /system/original`
- `POST /system/resource`
- `POST /system/tagRel`
- `GET /system/tagProposal/list`
- `POST /system/tagProposal/audit`

### 2.2 已实现待继续回归
- `GET /system/music/fullDetail/{id}`
- `DELETE /system/music/{ids}`
- 各类 `/system/comment/*`、`/system/notifyLog/*`、`/system/stat/*`、`/system/action/*`

## 3. 后台核心接口

### 3.1 音乐主数据
- `GET /system/music/list`
- `GET /system/music/{id}`
- `GET /system/music/detail/{id}`
- `GET /system/music/fullDetail/{id}`
- `POST /system/music`
- `PUT /system/music`
- `DELETE /system/music/{ids}`

### 3.2 审核
- `POST /system/music/audit`
- `POST /system/music/batchPass/{ids}`
- `POST /system/music/batchOffline`
- `GET /system/auditLog/list`
- `GET /system/auditLog/{id}`

## 4. 详情页 5 个 Tab

### 4.1 基本信息
- `GET /system/music/detail/{id}`
- `PUT /system/music`
- `GET /system/tag/list`
- `GET /system/tagRel/list?musicId={id}`
- `POST /system/tagRel`
- `PUT /system/tagRel`
- `DELETE /system/tagRel/{ids}`

### 4.2 原曲关联
- `GET /system/original/list?musicId={id}`
- `POST /system/original`
- `PUT /system/original`
- `DELETE /system/original/{ids}`

### 4.3 资源文件
- `GET /system/resource/list?musicId={id}`
- `POST /system/resource`
- `PUT /system/resource`
- `DELETE /system/resource/{ids}`
- 可复用 `/resource/oss/upload` 或前台直传链路完成上传后再入库

### 4.4 数据统计
- `GET /system/stat/{musicId}`
- `POST /system/stat`
- `PUT /system/stat`
- `DELETE /system/stat/{musicIds}`

### 4.5 通知记录
- `GET /system/notifyLog/list?musicId={id}`
- `GET /system/notifyLog/{id}`
- `POST /system/notifyLog`
- `PUT /system/notifyLog`
- `DELETE /system/notifyLog/{ids}`

## 5. 后台配套接口

### 5.1 标签与标签提案
- `GET /system/tag/list`
- `GET /system/tag/{id}`
- `POST /system/tag`
- `PUT /system/tag`
- `DELETE /system/tag/{ids}`
- `GET /system/tagProposal/list`
- `GET /system/tagProposal/{id}`
- `POST /system/tagProposal/audit`

### 5.2 评论与互动行为
- `GET /system/comment/list`
- `GET /system/comment/{id}`
- `POST /system/comment`
- `PUT /system/comment`
- `DELETE /system/comment/{ids}`
- `GET /system/action/list`
- `GET /system/action/{id}`

### 5.3 草稿、榜单、通知、统计
- `/system/draft/*`
- `/system/chartSnapshot/*`
- `/system/chartItem/*`
- `/system/notifyLog/*`
- `/system/stat/*`
- `/system/coinLedger/*`
- `/system/linkCheckLog/*`

## 6. 建议

后台接口已经足够支撑页面开发。下一步优先做：
- 后台详情页 5 个 Tab 联调
- 审核工作台联调
- 配套接口回归测试
- 后续再决定是否统一 `portal/music` 与 `portal/song` 命名策略
