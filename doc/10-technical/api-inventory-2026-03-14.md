# 接口清单（音乐域，2026-03-14）

本文档聚焦当前已实现并可落地的音乐域接口，按 open / portal 分组整理。

## 1. 分组说明

- open：公开访问接口（多数为 SaIgnore）。
- portal：登录态用户接口（投稿、互动、评论、通知等）。
- system：后台管理接口不在 hjm-music-web 控制器内维护，当前以既有管理模块路由为准。

## 2. Open 公开接口

### 2.1 歌曲公开接口（前缀 /open/song）

1. GET /open/song/list
- 用途：公开歌曲检索与分页
- 关键参数：keyword, tag, sort, pageQuery

2. GET /open/song/{id}
- 用途：公开详情
- 关键参数：id

3. GET /open/song/random
- 用途：随机歌曲
- 关键参数：limit

4. GET /open/song/suggest
- 用途：搜索建议词
- 关键参数：keyword, limit

5. GET /open/song/hot-keywords
- 用途：热门搜索词
- 关键参数：limit, days

6. GET /open/song/panel
- 用途：搜索聚合面板
- 关键参数：keyword, tag, sort, limit
- 返回要点：currentSort, recommendedSort, sortOptions, matchedFields, searchSummary

7. GET /open/song/chart/{type}
- 用途：榜单查询
- 关键参数：type, period, limit

8. GET /open/song/{id}/comments
- 用途：公开评论树
- 关键参数：id

9. POST /open/song/{id}/play
- 用途：记录播放
- 关键参数：id

10. POST /open/song/{id}/share
- 用途：记录分享
- 关键参数：id

11. POST /open/song/{id}/download
- 用途：记录下载
- 关键参数：id

### 2.2 公开标签接口（前缀 /open/tag）

1. GET /open/tag/list
- 用途：标签列表
- 关键参数：keyword, type

## 3. Portal 登录态接口

### 3.1 我的作品（前缀 /portal/music）

1. GET /portal/music/my
- 用途：我的作品分页
- 关键参数：MusicBo, pageQuery

2. GET /portal/music/{id}
- 用途：我的作品详情
- 关键参数：id

3. PUT /portal/music/{id}
- 用途：更新作品
- 关键参数：id, PortalMusicUpdateBo

4. DELETE /portal/music/{id}
- 用途：删除作品
- 关键参数：id

### 3.2 草稿箱（前缀 /portal/draft）

1. POST /portal/draft/save
- 用途：保存草稿
- 关键参数：content

2. GET /portal/draft/get
- 用途：获取草稿
- 关键参数：id

3. POST /portal/draft/update
- 用途：更新草稿
- 关键参数：id, content

4. POST /portal/draft/delete
- 用途：删除草稿
- 关键参数：id

5. POST /portal/draft/submit
- 用途：提交审核
- 关键参数：MusicSubmitBo

### 3.3 互动接口（前缀 /portal/song）

1. POST /portal/song/{id}/like
2. DELETE /portal/song/{id}/like
3. GET /portal/song/{id}/liked
4. POST /portal/song/{id}/collect
5. DELETE /portal/song/{id}/collect
6. GET /portal/song/{id}/collected
- 用途：点赞/收藏状态与操作
- 关键参数：id

### 3.4 评论接口（前缀 /portal/song）

1. POST /portal/song/{id}/comments
- 用途：提交评论
- 关键参数：id, PortalCommentSubmitBo

2. DELETE /portal/song/{id}/comments/{commentId}
- 用途：删除评论
- 关键参数：id, commentId

### 3.5 标签提案（前缀 /portal/tag）

1. GET /portal/tag/list
- 用途：标签列表
- 关键参数：keyword, type

2. POST /portal/tag/proposal
- 用途：提交标签提案
- 关键参数：PortalTagProposalBo

3. GET /portal/tag/proposal/my
- 用途：我的标签提案

### 3.6 通知（前缀 /portal/notify）

1. GET /portal/notify
- 用途：我的通知分页
- 关键参数：pageQuery

## 4. 路由维护建议

1. 公开检索相关改动优先同步维护 /open/song/list、/open/song/panel、/open/song/suggest。
2. 用户互动改动优先维护 /portal/song 下 like/collect/comments 三类接口语义一致性。
3. 新增接口时必须在本清单中补充：路径、方法、用途、关键参数。
