# 项目技术说明（hjm-cloud）

更新时间：2026-03-14

## 1. 技术栈与职责

### 1.1 运行时与框架

- Java 21：当前构建链路按 release 21 编译。
- Spring Boot：微服务主框架。
- Spring Cloud Gateway：统一网关入口、路由与鉴权边界。
- Dubbo：服务间 RPC 调用。

### 1.2 数据与持久化

- PostgreSQL（当前可接受目标数据库形态）。
- MyBatis-Flex：用于音乐域查询、分页、聚合。
- Redis/Valkey + Redisson：缓存、计数器、有序集合热词、状态集。
- ValkeySearch：公开搜索的索引与查询。

### 1.3 安全与基础能力

- Sa-Token：登录态、鉴权、会话相关能力。
- 统一异步执行器：taskExecutor（支持虚拟线程）。
- 统一调度执行器：scheduledExecutorService（虚拟线程模式下启用虚拟线程工厂）。

## 2. 模块划分（与业务对应）

### 2.1 ruoyi-system

- 后台管理能力
- 系统日志、角色用户、权限管理
- 音乐后台管理接口承载

### 2.2 hjm-music-web

- 音乐公开接口（open）
- 门户投稿与用户侧业务
- 搜索、榜单、评论、互动链路核心实现

### 2.3 ruoyi-common

- 基础设施组件
- 并发模型配置、Redis 封装、租户、幂等、日志切面

### 2.4 ruoyi-resource

- 上传链路：presign -> 客户端直传 -> complete

## 3. 关键实现链路（写到点上）

### 3.1 公开搜索链路

1. 入口参数：keyword/tag/sort/page。
2. 搜索策略：ValkeySearch 优先，失败或不命中回退 DB。
3. 索引预热：DB 回退结果会触发索引回填。
4. 搜索增强：
   - 多标签过滤
   - 字段化匹配（title/subtitle/original_title/creator_name/tags）
   - 高亮字段返回
   - 建议词、热词、面板聚合
   - 纠错与同义词扩展
   - 综合相关性排序

### 3.2 高频互动链路

1. 播放/点赞/收藏/评论等高频写入优先落缓存。
2. 定时任务批量刷盘到 music 与 music_stat。
3. 读链路合并未刷盘增量，避免显示落后。

### 3.3 榜单链路

1. 定时生成周榜/月榜快照。
2. 公开端读取 published 状态快照。
3. 榜单条目与公开歌曲数据聚合返回。

## 4. 并发与虚拟线程现状

### 4.1 已落地

- spring.threads.virtual.enabled = true
- Dubbo threadpool = virtual
- taskExecutor 在虚拟线程模式下使用 VirtualThreadTaskExecutor
- 已发现 @Async 入口统一绑定 taskExecutor

### 4.2 已做兼容收敛

- 清理部分 parallelStream，避免绕过统一执行模型落到公共 ForkJoinPool。
- ThreadLocal 清理细节修复：
  - RepeatSubmitAspect
  - TenantHelper
  - DataPermissionHelper

### 4.3 当前边界

- 主干执行模型已统一。
- 全仓 ThreadLocal 深审和第三方 SDK 上下文传播评估仍需继续。

## 5. 搜索词库治理现状

### 5.1 已完成

- 关键词标准化
- 词项首尾噪音标点清洗
- 停用词过滤
- 内置同义词词典扩展
- 配置化同义词扩展（music.search.synonyms）
- 标签别名同义词扩展

配置格式示例：

- music.search.synonyms=国风:古风|中国风;电子:电音|edm|electronic
- 分组使用 ; 分隔，键值使用 : 分隔，同义词列表使用 | 分隔。

### 5.2 待完成

- 可配置词库（DB/配置中心）
- 词库热更新
- 词库运营闭环（版本、灰度、效果评估）

## 6. 已知风险与注意事项

1. ValkeyQueryProcessor 仍存在 source 17 警告（不影响当前编译通过）。
2. 搜索治理目前是“规则 + 内置词典”，还不是“可运营词库平台”。
3. 并发治理已覆盖关键入口，但尚未完成全仓逐模块审计。
