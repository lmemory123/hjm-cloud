# 哈基哈米媒体产品重设计划 S9

## 背景判断

上一轮前台优化只处理了视觉表层，仍缺少音乐/视频社区的核心媒体能力：

- 前台数据仍是占位歌曲，未接入真实音频。
- 音乐作品可能包含纯音频、视频/PV、外链搬运、二创剪辑等多种媒体形态。
- 播放器只有基础播放，缺少码率选择、预加载策略、资源失效兜底。
- 表情包入口只是组件，不是完整专区。
- 页面风格偏通用 SaaS/demo，没有形成类似 B 站内容社区的密度、UP 信息、互动与媒体消费路径。

## 参考方向

本阶段参考的是产品结构，不复制视觉皮肤：

- B 站：https://www.bilibili.com/v/music 。参考视频/音频内容社区的信息流结构，重点是封面、标题、UP、播放/互动数据和分区入口。
- 网易云音乐：https://music.163.com/ 。参考播放队列、音质、评论、歌单/榜单、歌曲详情页的信息组织。
- YouTube Music：https://music.youtube.com/ 。参考同一消费场景下歌曲、视频和播放器的切换路径。
- SoundCloud：https://soundcloud.com/ 。参考波形/时间轴评论，后续可作为音乐二创评论增强方向。

## S9 目标

把哈基哈米从“有几张卡片的音乐页”推进到“可真实播放、可扩展视频、可运营二创内容”的媒体社区基础版本。

## S9.0 本地真实音频样本接入

状态：已完成。

范围：

- 使用 `/Users/momao/Music/网易云音乐/` 下 5 个本地音频作为开发样本。
- Docker 前台容器将该目录挂载到 Nuxt 静态资源目录。
- 后端初始化 SQL 改为 5 首真实样本歌曲。
- `music.resource_data` 写入 `audioUrl`、`qualityTier`、`bitrate`、`container`、`sampleRate` 等可被前台消费的信息。
- 当前数据库同步更新，用户无需冷启动即可在前台验证。

验收：

- `GET /music/open/song/list?pageNum=1&pageSize=10` 返回 5 首真实歌曲。
- `GET /music/open/song/list?keyword=Call&pageNum=1&pageSize=5` 返回 `Call of Silence`。
- `GET /music/open/song/chart/week` 和 `GET /music/open/song/chart/month` 返回 `published` 榜单。
- `/media/local/...` 支持 Range 请求，返回 `206 Partial Content`。
- 前台首页、搜索、详情页展示真实标题/作者，并可拉取真实音频。

## S9.1 媒体模型补全

状态：未开始。

范围：

- 前端类型扩展：
  - `mediaType: audio | video | external`
  - `resources[]` 支持 `audio`、`video`、`cover`、`subtitle`、`waveform`
  - `qualityTier`、`bitrate`、`container`、`codec`、`sampleRate`
- 后端开放 VO 输出资源列表，而不是只依赖 `resource_data` 快照。
- 详情页显示“音频 / 视频 / 外链”媒体标签。

验收：

- 一首歌可同时拥有音频、视频、封面和外链资源。
- 前台能按资源类型选择播放器。

## S9.2 播放器能力

状态：未开始。

范围：

- 码率选择：128k、320k、flac、source。
- 音频预加载：
  - 首屏卡片不预加载音频。
  - 当前播放项加载 metadata。
  - 用户点击播放后预取当前资源，并预取下一首 metadata。
  - 后端支持 Range/206 后，再推进 30 秒分段预加载。
- 播放失败降级：
  - 当前码率失败时切换到低码率。
  - 全部失败时标记资源需补档。
- 播放器 UI：
  - 当前音质显示。
  - 队列。
  - 歌词/评论入口。

## S9.3 视频/PV 能力

状态：未开始。

范围：

- 歌曲详情页播放器区域支持 `<video>`。
- 首页与搜索卡片展示视频角标。
- 投稿页支持视频/PV 链接或文件资源。
- 后台音乐资源管理增加视频资源类型。

验收：

- 视频作品可播放、可全屏、可回退到音频。

## S9.4 表情包专区

状态：未开始。

范围：

- 新增 `/emoji` 表情包专区。
- 列表、分类、搜索、复制/下载。
- 评论区插入表情。
- 后台审核表情包。

验收：

- 首页有清晰入口。
- 用户可浏览、搜索、复制表情包。

## S9.5 前台信息架构重设

状态：未开始。

范围：

- 首页改成内容社区布局：
  - 顶部推荐位。
  - 热门视频/音频二创。
  - 周榜/月榜。
  - 最新投稿。
  - 表情包专区入口。
  - 社群入口。
- 搜索结果支持音频/视频/表情包 Tab。
- 歌曲详情页强化 UP、原曲、评论、推荐、媒体资源选择。
- 移动端以信息流和底部播放器优先。

## 当前完成记录

- 2026-06-04：创建 S9 媒体产品重设计划，开始 S9.0 本地真实音频样本接入。
- 2026-06-04：完成 S9.0。
  - Docker 前台挂载 `/Users/momao/Music/网易云音乐/` 到 `/app/local-media`。
  - Nuxt 增加 `/media/local/**` 服务端流式路由，支持 mp3/flac/ogg/m4a/mp4/webm 与 Range/206。
  - PostgreSQL 初始化数据替换为 5 首本地真实样本：`Call of Silence`、`玉门关`、`Trap Queen`、`Prayer X`、`醒`。
  - 样本数据补齐 `audioUrl`、`mediaType`、`qualityTier`、`bitrate`、`container`、`codec`、`sampleRate`、`duration`。
  - 榜单快照状态统一为服务端使用的 `published`。
  - 当前运行库已同步更新，并重启 `hjm-music-web` 触发 Valkey 搜索索引同步。

## 下一步执行顺序

1. S9.1：后端开放 VO 输出 `resources[]`，不要继续让前台解析 `resource_data` 字符串兜底。
2. S9.2：播放器增加音质选择、metadata 预加载、下一首预取、失败降级和资源补档提示。
3. S9.3：详情页媒体区支持视频/PV，卡片展示视频角标，投稿页支持视频资源。
4. S9.4：新增 `/emoji` 表情包专区，并打通评论区插入入口。
5. S9.5：按内容社区重做首页、搜索和详情页信息架构，降低 AI 风格的装饰化卡片，提升内容密度与消费路径。

## 协作约束

- 不再只做视觉表层调整。涉及播放体验时必须同时检查数据、接口、播放器和异常态。
- 不提交本地音频二进制文件，开发环境通过 Docker volume 读取 `/Users/momao/Music/网易云音乐/`。
- 所有媒体资源必须有明确类型：`audio`、`video`、`external`、`cover`、`waveform`。
- 音频播放链路必须支持 Range/206；做预加载、拖动、码率切换前先验证该能力。
- 每完成 S9 子阶段，必须更新本文件的状态、验证命令和剩余风险。
