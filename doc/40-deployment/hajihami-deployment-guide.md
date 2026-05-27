# 哈基哈米部署手册

更新时间：2026-05-27

本文档介绍哈基哈米三端项目（hjm-cloud, hjm-admin, hjm-web-nuxt-frontend）的生产环境部署流程。

## 1. 基础环境要求

| 组件 | 版本要求 | 说明 |
| --- | --- | --- |
| JDK | 17 或 21 | 后端运行环境 |
| Node.js | 20+ | 前端构建与运行 (Nuxt 4) |
| PNPM | 9+ | 前端包管理 |
| Postgres | 15+ | 核心关系数据库 |
| Redis | 6+ | 缓存与限频 (支持 Valkey) |
| Nacos | 2.3+ | 注册中心与配置中心 |
| OSS | Minio/Aliyun | 资源存储 |
| Nginx | 1.24+ | 静态资源与反向代理 |

## 2. 后端部署 (hjm-cloud)

### 2.1 数据库初始化
1. 执行 `hjm-cloud/script/sql/postgres/postgres_music.sql` 初始化音乐模块表。
2. 执行 `ruoyi-system` 相关的系统表 SQL。

### 2.2 配置中心设置
1. 登录 Nacos 控制台。
2. 修改 `hajihami.developer.api_keys` 和 `hajihami.developer.webhooks` 初始参数。
3. 检查 `ruoyi-music-dev.yml` 中的数据库和 Redis 连接信息。

### 2.3 编译打包
```bash
cd hjm-cloud
mvn clean package -DskipTests
```

### 2.4 服务启动清单
按顺序启动以下核心模块：
1. `ruoyi-gateway.jar` (网关)
2. `ruoyi-auth.jar` (鉴权)
3. `ruoyi-system.jar` (系统)
4. `hjm-music-web.jar` (音乐业务)

## 3. 后台部署 (hjm-admin)

### 3.1 环境变量
修改 `.env.prod` 中的 `VITE_SERVICE_BASE_URL` 为后端网关地址。

### 3.2 编译构建
```bash
cd hjm-admin
pnpm install
pnpm build
```

### 3.3 Nginx 配置
将 `dist` 目录上传至服务器，Nginx 配置示例：
```nginx
location /admin/ {
    alias /var/www/hjm-admin/dist/;
    try_files $uri $uri/ /admin/index.html;
}
```

## 4. 前台部署 (hjm-web-nuxt-frontend)

### 4.1 构建
```bash
cd hjm-web-nuxt-frontend
pnpm install
pnpm build
```

### 4.2 运行
Nuxt 4 建议使用 Node.js 进程管理器 (如 PM2) 运行：
```bash
pm2 start .output/server/index.mjs --name hjm-frontend
```

## 5. 运维监控
- **日志查询**：所有后端日志均通过 `ruoyi-system` 的统一日志接口或 ELK 收集。
- **Webhook 监控**：通过 `hjm-admin` 开放平台的“调用日志”查看异步任务执行情况。
- **限频监控**：观察 Redis 中的 `rate_limit:` 键值对。
