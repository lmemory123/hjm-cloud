# 音乐模块当前实现状态评估

评估时间：2026-03-23

评估依据：

- 当前代码实现
- `doc/music-requirements-improved.md`
- `doc/music-detail-tabs-api.md`

---

## 1. 结论

当前音乐模块已经**明显超过最初 P0 范围**。

如果目标是：

- 完成后台音乐详情页
- 完成前台投稿 / 上传 / 审核流
- 支持基础公开展示

那么**已经有必要先停下继续扩展，转入接口联调、页面落地和回归测试**。

当前不建议继续无边界扩功能，原因不是“功能太少”，而是“已经进入第二阶段能力建设，但验证还没跟上”。

---

## 2. 已实现能力

## 2.1 后台管理（system）

已具备：

- 音乐主表 CRUD、审核、批量通过、批量下架
- 详情页 5 个核心 Tab 所需接口：
  - `/system/music/detail/{id}`
  - `/system/original/*`
  - `/system/resource/*`
  - `/system/stat/*`
  - `/system/notifyLog/*`
  - `/system/tagRel/*`
  - `/system/tag/*`
- 新增了聚合详情接口：
  - `/system/music/fullDetail/{id}`

说明：这部分已经足够支撑后台详情页开发。

## 2.2 前台用户（music-web）

已具备：

- 草稿保存 / 获取 / 更新 / 删除 / 提交审核
- 我的作品列表 / 详情 / 编辑 / 删除
- 标签公开列表、标签提案、我的标签提案
- 我的通知

## 2.3 资源上传（resource）

已具备：

- 预签名直传
- 上传完成回调
- 分片初始化 / 完成 / 取消

说明：这一块已经满足“前台上传，后台不直接吃文件流”的原始要求。

## 2.4 新增的扩展能力（超出原始 P0）

新增了以下能力：

- 公开歌曲检索与详情：
  - `/music/open/song/list`
  - `/music/open/song/{id}`
  - `/music/open/song/random`
  - `/music/open/song/suggest`
  - `/music/open/song/hot-keywords`
  - `/music/open/song/panel`
  - `/music/open/song/chart/{type}`
- 公开互动上报：
  - `/music/open/song/{id}/play`
  - `/music/open/song/{id}/share`
  - `/music/open/song/{id}/download`
- 登录态互动：
  - `/music/portal/song/{id}/like`
  - `/music/portal/song/{id}/collect`
- 评论：
  - `/music/open/song/{id}/comments`
  - `/music/portal/song/{id}/comments`

说明：这些已经进入“公开站点 / 社区互动 / 搜索推荐”阶段。

---

## 3. 与原需求基线的偏离点

## 3.1 已经偏离但不是坏事

- 增加了 `/system/music/fullDetail/{id}`
- 增加了公开搜索、随机推荐、榜单、建议词
- 增加了播放/点赞/收藏/评论的互动能力
- 引入了缓存增量刷盘、搜索索引同步、Dubbo 搜索服务

这些都属于“合理前进”，但已经不是最初的最小闭环。

## 3.2 目前存在的不一致

1. 路径命名不统一  
   - 原有主体是 `/portal/music/*`
   - 新增互动/评论用了 `/portal/song/*`
   - 公开接口用了 `/open/song/*`

2. 状态文档过时  
   - `requirements-document/interface-status.md` 仍停留在早期接口，不包含新增搜索/互动/评论/聚合详情。

3. 验证不足  
   - 当前能编译通过
   - 但自动化测试非常少，`music-web` 只有 ValkeySearch 相关测试
   - `ruoyi-system` 几乎没有覆盖音乐业务的测试

4. 业务校验仍比较薄  
   - 多个 service 里仍有 `TODO` 的唯一性校验 / 删除校验

---

## 4. 当前是否还有必要继续写

结论：**有必要继续，但不是继续无差别加功能。**

建议改为下面顺序：

1. 先收口  
   - 以 `doc/music-requirements-improved.md` 为准
   - 锁定当前 P0/P1 边界

2. 先联调页面  
   - 后台详情页 5 个 Tab
   - 前台投稿 / 我的作品 / 通知

3. 再补验证  
   - 更新接口状态文档
   - 增加关键接口测试
   - 验证 Redis/Valkey 缓存增量与搜索索引同步

4. 最后再决定是否继续扩  
   - 如果你的下一阶段真要做公开站点，就继续保留 `/open/song/*`
   - 如果近期只做后台与投稿流，那就不要再继续扩搜索推荐能力了

---

## 5. 建议动作

当前最合理的动作不是继续加接口，而是：

- 更新接口完成度文档
- 把“已实现但未验证”的接口列出来
- 对 `portal/song` 与 `portal/music` 路径命名做一次统一决策
- 以页面联调结果决定是否继续推进公开搜索/互动

---

## 6. 判断

如果你现在问“还有必要继续吗”，我的判断是：

- **对 P0 需求：没必要继续扩接口，应该转联调和验证**
- **对 P1 公开站点：可以继续，但要先统一边界和命名**
