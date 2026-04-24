# 前台登录方案分析

## 1. 结论

- 当前项目现有的 `/auth/login`、`/auth/register` **可以直接复用** 给前台。
- 但**不建议**把“后台管理员登录”与“前台用户登录”完全混用为同一套无区分入口。
- 更合适的做法是：
  - 认证核心继续复用现有 `auth` 模块；
  - 前台用户继续使用 `sys_user` 作为基础账号表；
  - 通过 `user_type = app_user` 区分前台用户；
  - 新增前台资料表，例如 `music_user_profile`；
  - 前台单独配置一个 `clientId`；
  - 前台提供独立登录入口，内部仍走现有认证逻辑，但固定为前台用户体系。

## 2. 当前代码现状

### 2.1 已具备的能力

- 认证入口已存在：
  - `POST /auth/login`
  - `POST /auth/register`
  - `POST /auth/logout`
- 框架已经支持多用户类型：
  - `sys_user`
  - `app_user`
- 注册接口已经支持传入 `userType`。
- `sys_user` 表本身已有 `user_type` 字段，可直接承载前台账号。
- 现有前台业务接口通过 `LoginHelper.getUserId()` 取当前登录人，说明接入登录态没有结构性障碍。

### 2.2 当前不足

- 登录查询用户时，当前实现按 `username` 查 `sys_user`，**没有显式按 `user_type` 过滤**。
- 如果未来后台账号和前台账号允许重名，当前实现会有歧义。
- 后台用户管理 `/system/user/*` 当前也是基于 `sys_user`，前台用户进入同一张表后，后台列表可能混在一起。
- `APP_USER` 在权限实现里是预留分支，当前没有单独做角色/菜单体系。
- `openid`/小程序类登录在代码里还是 `todo`，目前只适合账号密码登录。

## 3. 是否要重做一张前台用户表

## 3.1 不建议直接重做“账号主表”

如果现在就再造一张独立前台账号表，然后重写一整套登录、token、鉴权、注册、找回密码、第三方绑定，成本高，且会和现有框架能力重复。

当前更合理的是：

- **账号身份表继续复用 `sys_user`**
  - 登录名
  - 密码
  - 状态
  - 手机号
  - 邮箱
  - 用户类型
- **前台业务资料单独拆表**
  - 昵称展示
  - 头像
  - 个人简介
  - 性别
  - 生日
  - 首页背景
  - 创作者认证信息
  - 粉丝数/关注数/作品数冗余字段
  - 第三方扩展标识

这样可以把“认证”和“业务资料”分开，后续迁移也更容易。

## 3.2 推荐结构

- `sys_user`
  - 作为统一账号表
  - 前台用户写入 `user_type = app_user`
- `music_user_profile`（建议新增）
  - `user_id`
  - `display_name`
  - `avatar`
  - `bio`
  - `gender`
  - `birthday`
  - `cover_url`
  - `verified_status`
  - `verified_remark`
  - `ext_json`

## 4. 登录接口是否要重写

## 4.1 不建议完全重写认证接口

不需要推翻当前 `/auth/login`。

现有认证链路已经包含：

- client 校验
- grantType 校验
- tenant 校验
- 密码校验
- token 签发
- 登录日志

这部分没有必要重复实现。

## 4.2 建议“前台单独入口 + 复用底层认证”

推荐两种方式，优先方案 A。

### 方案 A：新增前台登录入口，底层仍复用现有认证

新增例如：

- `POST /music/open/account/login`
- `POST /music/open/account/register`

由前台接口层做两件事：

- 固定 `userType = app_user`
- 固定使用前台专属 `clientId`

再转调现有认证服务。

优点：

- 前台调用语义清晰；
- 可以对前台参数单独收口；
- 可以避免前端直接感知后台认证细节；
- 以后切短信登录、微信登录时扩展更自然。

### 方案 B：前台直接调用 `/auth/login`

可以用，但只适合短期联调。

前提是：

- 前台专属 `clientId` 已配置；
- 前台用户都写在 `sys_user`；
- 用户名全局唯一；
- 前台暂时只做账号密码登录。

问题是：

- 用户体系边界不够清晰；
- 以后扩展手机号验证码登录、微信登录时还是要再包一层；
- 前台与后台认证参数约定耦合太高。

## 5. 推荐落地方案

## 5.1 P0：先满足前台登录

- 继续复用 `auth` 模块。
- 新增一个前台专属 `clientId`，不要和后台管理端混用。
- 前台用户写入 `sys_user.user_type = app_user`。
- 新增前台登录/注册包装接口：
  - `POST /music/open/account/login`
  - `POST /music/open/account/register`
- 登录成功后，前台业务接口继续使用现有 token。

## 5.2 P1：补齐前台账号资料

- 新增 `music_user_profile`。
- 增加接口：
  - `GET /music/portal/account/profile`
  - `PUT /music/portal/account/profile`
  - `GET /music/portal/account/security`
  - `PUT /music/portal/account/password`

## 5.3 P2：补齐用户体系隔离

- 登录查询用户时增加 `userType` 过滤。
- 后台用户管理接口默认只查 `sys_user`。
- 如需后台管理前台会员，再单独提供 `/system/app-user/*` 或 `/system/music-user/*`。

## 6. 最终建议

最终建议不是“重写整套登录”，而是：

- **认证复用**
- **账号共表**
- **资料拆表**
- **前台单独 client**
- **前台单独入口**

这样改动最小，但后续可扩展性足够。

## 7. 当前是否可以直接开始

可以开始，但建议按下面顺序推进：

1. 新增前台 `clientId`
2. 明确前台用户统一使用 `user_type = app_user`
3. 增加前台登录/注册包装接口
4. 增加前台资料表与资料接口
5. 再补用户查询按 `user_type` 过滤
