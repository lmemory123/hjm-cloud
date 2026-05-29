# 哈基哈米上线检查清单 (Pre-launch Checklist)

更新时间：2026-05-27

在项目进入 S6 质量交付阶段后，正式上线生产环境前需逐项核对。

## 1. 基础环境核对 (Infrastructure)
- [x] Redis/Valkey 已开启 AOF 持久化。
- [x] Postgres 数据库已配置自动备份脚本（建议每日凌晨）。
- [x] OSS Bucket 权限已设置为公共读（针对音频/封面）且私有写。
- [x] Nginx 已配置 Gzip 压缩及 HSTS 安全头。

## 2. 功能验证 (Functionality)
- [x] 注册与登录：支持密码登录，JWT Token 续装期正常。
- [x] 投稿闭环：上传 -> 待审 -> 后台通过 -> 前台展示 链路通畅。
- [x] 搜索与筛选：支持多标签、风格、原创性联合筛选。
- [x] 开放 API：`/music/open/api/v1/meta` 返回正常，API Key 校验有效。
- [x] Webhook 推送：异步重试机制已激活，日志可追溯。

## 3. 性能与压力测试 (Performance)
- [x] 单接口压测：核心 `GET /songs/{id}` QPS 需达 500+。
- [x] 全链路模拟：支持 1000 人同时在线播放不出现卡顿。
- [x] 慢 SQL 治理：`p6spy`监控中无超过 500ms 的执行记录。

**压测脚本说明**：
- 建议使用 **JMeter** 或 **k6**。
- 压测前需通过 `POST /music/open/api/v1/webhooks/dispatch` 预热虚拟线程池。
- 测试数据需覆盖不同 `audit_status` 的歌曲。

## 4. 安全核对 (Security)
- [ ] 配置脱敏：Nacos 中数据库密码、OSS Secret 已加密。
- [ ] 限频策略：匿名访问 `/music/open/api/*` 触发 60次/min 拦截。
- [ ] 日志合规：脱敏存储用户手机号、邮箱等敏感信息。

## 5. 运营准备 (Operations)
- [ ] 系统公告：已预置一条“欢迎来到哈基哈米”上线公告。
- [ ] 推荐位：首页推荐池已填充至少 10 首精选作品。
- [ ] 帮助文档：开放平台文档 `hajihami-open-api-v1.md` 已发布。
