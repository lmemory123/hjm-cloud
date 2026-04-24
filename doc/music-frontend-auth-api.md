# 前台登录接口文档

## 1. 目标

前台登录不再直接暴露后台登录参数细节，而是提供前台专用认证入口。

当前已新增接口：

- `POST /auth/front/login`
- `POST /auth/front/register`

这两个接口底层仍复用现有认证体系，但会固定走前台用户体系。

## 2. 前置条件

使用前需要先完成两项配置。

### 2.1 配置前台专用 clientId

认证服务配置项：

```yaml
security:
  front-auth:
    client-id: "你的前台clientId"
    grant-type: password
    register-enabled: true
```

对应位置：

- `/Users/momao/dm/java/hjm/hjm-cloud/script/config/nacos/ruoyi-auth.yml`

说明：

- 该 `clientId` 需要先在 `sys_client` 表中存在；
- 建议不要与后台管理端共用同一个 `clientId`；
- 前台后续访问登录态接口时，Header 里的 `ClientId` 也要传这个值。
- `register-enabled` 为前台注册独立开关，与后台注册开关无关。

### 2.2 网关白名单

网关需要放行：

- `/auth/front/login`
- `/auth/front/register`

对应示例配置已更新：

- `/Users/momao/dm/java/hjm/hjm-cloud/script/config/nacos/ruoyi-gateway.yml`

## 3. 用户体系说明

前台账号当前仍落在 `sys_user` 表，但注册时会固定写入：

```text
user_type = app_user
```

因此：

- 后台管理员仍是 `sys_user`
- 前台用户是 `app_user`

当前新增接口会拒绝非 `app_user` 用户通过前台登录入口登录。

## 4. 接口一：前台登录

### 4.1 请求地址

```http
POST /auth/front/login
```

网关完整地址示例：

```http
POST http://localhost:8080/auth/front/login
```

### 4.2 请求头

```http
Content-Type: application/json
```

如果你的网关/前端沿用现有加解密链路，可继续带：

```http
isEncrypt: true
```

### 4.3 请求参数

```json
{
  "tenantId": "000000",
  "username": "front_test_001",
  "password": "123456",
  "code": "",
  "uuid": ""
}
```

字段说明：

| 字段 | 是否必填 | 说明 |
|---|---|---|
| tenantId | 是 | 租户 ID，多租户场景建议必传 |
| username | 是 | 用户名 |
| password | 是 | 密码 |
| code | 否 | 验证码开启时必传 |
| uuid | 否 | 验证码开启时必传 |

### 4.4 成功响应

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "expireIn": 7200,
    "clientId": "front-web-client-id"
  }
}
```

### 4.5 失败场景

常见失败包括：

- 前台 `clientId` 未配置；
- `sys_client` 中未创建前台客户端；
- 用户不是 `app_user`；
- 用户名或密码错误；
- 验证码错误；
- 注册功能未开启时尝试注册。

## 5. 接口二：前台注册

### 5.1 请求地址

```http
POST /auth/front/register
```

网关完整地址示例：

```http
POST http://localhost:8080/auth/front/register
```

### 5.2 请求头

```http
Content-Type: application/json
```

### 5.3 请求参数

```json
{
  "tenantId": "000000",
  "username": "front_test_001",
  "password": "123456",
  "code": "",
  "uuid": ""
}
```

字段说明与登录一致。

### 5.4 成功响应

```json
{
  "code": 200,
  "msg": "操作成功"
}
```

### 5.5 注册行为

注册成功后，系统会：

- 将用户写入 `sys_user`
- 自动写入 `user_type = app_user`
- 密码按现有系统规则加密存储

## 6. 登录后前台如何调用其他接口

登录成功后，前台请求受保护接口时，至少带两个 Header：

```http
ClientId: front-web-client-id
Authorization: Bearer {accessToken}
```

例如：

```http
GET /music/portal/music/my
ClientId: front-web-client-id
Authorization: Bearer eyJ...
```

说明：

- `ClientId` 必须与登录时签发 token 对应的客户端一致；
- 否则网关会判定 `ClientId` 与 Token 不匹配。

## 7. IntelliJ HTTP Client 示例

```http
### 前台注册
POST http://localhost:8080/auth/front/register
Content-Type: application/json

{
  "tenantId": "000000",
  "username": "front_test_001",
  "password": "123456"
}

### 前台登录
POST http://localhost:8080/auth/front/login
Content-Type: application/json

{
  "tenantId": "000000",
  "username": "front_test_001",
  "password": "123456"
}
```

## 8. 对现有后台登录的影响评估

结论：**当前改动不会影响现有后台登录接口。**

原因如下：

- 后台原有接口 `POST /auth/login` 没有改；
- 后台原有接口 `POST /auth/register` 没有改；
- 新增的是独立入口：
  - `POST /auth/front/login`
  - `POST /auth/front/register`
- 后台继续使用原有 `clientId`、原有登录页面、原有 token 体系；
- 前台只是新增了一条旁路，不会覆盖后台逻辑。

## 9. 当前仍需注意的边界

虽然不会影响后台登录，但当前还有两个体系边界问题需要后续补齐：

### 9.1 后台用户管理与前台用户共表

当前前台用户仍在 `sys_user` 表，后台用户管理如果不加过滤，理论上会看到前台用户。

建议后续：

- 后台用户管理默认只查 `user_type = sys_user`
- 前台会员管理单独做 `/system/app-user/*` 或 `/system/music-user/*`

### 9.2 前台资料仍未拆表

当前只是把认证入口补齐了，前台用户资料还没有独立表。

建议下一步补：

- `music_user_profile`
- 前台个人中心资料接口
- 修改密码/绑定手机/头像等接口
