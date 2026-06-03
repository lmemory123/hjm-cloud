# 哈基哈米 Docker 一键部署指南

更新时间：2026-05-29

本目录提供哈基哈米全栈项目的本地 Docker 部署方案，涵盖中间件、后端、后台和前台。

## 1. 目录结构

- `backend/`: 后端 Dockerfile 及构建资源。
- `admin/`: 后台 Nginx 配置。
- `frontend/`: 前台 Dockerfile。
- `middleware/`: 中间件持久化数据及初始化配置。
- `docker-compose.yml`: 主编排文件。
- `build.sh`: 一键构建后端 JAR 包脚本。
- `.env.example`: 本地密钥示例，复制为 `.env` 后填写 `JASYPT_ENCRYPTOR_PASSWORD`。

## 2. 快速开始

### 2.1 环境准备
确保已安装：
- Docker & Docker Compose
- JDK 17+ & Maven (仅构建需要)
- Node.js & pnpm (仅构建需要)

### 2.2 构建与启动
1. **准备本地密钥**：
   ```bash
   cp .env.example .env
   # 将 .env 内的 JASYPT_ENCRYPTOR_PASSWORD 改为本机密钥
   ```
2. **构建后端 JAR 包**：
   ```bash
   chmod +x build.sh
   ./build.sh
   ```
3. **启动所有服务**：
   ```bash
   docker compose -f docker-compose.yml up --build -d
   ```

## 3. 访问入口

| 服务 | 访问地址 | 说明 |
| --- | --- | --- |
| **前台 Nuxt** | http://localhost:3000 | 听众访问入口 |
| **后台 Admin** | http://localhost:80 | 管理员运营入口 |
| **Nacos 控制台** | http://localhost:8848/nacos | 配置中心 (nacos/nacos) |
| **RabbitMQ** | http://localhost:15672 | 消息队列监控 (ruoyi/ruoyi123) |
| **后端网关** | http://localhost:8080 | OpenAPI 聚合入口 |

## 4. 配置说明

### 4.1 Nacos 配置导入
首次启动后，需登录 Nacos 将以下配置导入到 `prod` 命名空间或 `DEFAULT_GROUP` 中：
- `application-common.yml`: 通用 Redis/RabbitMQ/Nacos 地址。
- `datasource.yml`: 数据库连接池信息（已预置指向 `hjm-postgres`）。
- `hjm-music-web.yml`: 音乐模块特有配置（如 Valkey 节点）。

示例配置文件见：`docker/middleware/nacos/init_configs/`。

### 4.2 数据库初始化
`hjm-postgres` 容器启动时会自动执行 `hjm-cloud/script/sql/postgres/*.sql` 中的脚本。
包括：
1. 基础架构表及权限。
2. 音乐业务表及初始化数据 (P1)。
3. Nacos 配置表结构。

## 5. 常见问题
- **内存占用**：全栈启动约需 4GB-8GB 内存，请确保 Docker 分配了足够资源。
- **网络延迟**：Nuxt 4 在构建镜像时会抓取远端图标，建议在网络环境良好时执行。
- **服务顺序**：Gateway 和 Auth 依赖 Nacos 完成注册，若启动失败请在 Nacos 稳定后重启应用容器。
