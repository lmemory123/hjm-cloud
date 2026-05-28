# 哈基哈米演示账号与系统配置手册 (C1 阶段)

更新时间：2026-05-28

本文档提供 `hjm-cloud` 所需的业务演示账号体系说明，以及各核心模块依赖的 Nacos/Postgres 系统参数 (`sys_config`) 标准 JSON 配置参考，供一键初始化环境或在后台手动配置使用。

## 1. 演示账号体系

在初始化环境后，内置以下业务账号供演示和测试使用（密码默认均为 `123456`，基于原 RuoYi 体系加密方式）：

| 账号 | 角色 | 权限范围 | 测试场景 |
| --- | --- | --- | --- |
| `admin` | 超级管理员 | 拥有所有系统及业务权限 | 基础配置、开放平台管理、API Key 申请 |
| `auditor` | 审核员 | 仅限曲库、评论、举报的处理权限 | 曲库审核、批量下架、处理被举报评论 |
| `creator` | 创作者 | 前台发布作品，查看自己的草稿与流水 | 投稿流程、查看作品审核状态、提报新标签 |
| `listener` | 普通听众 | 前台浏览，基础互动（点赞、收藏、评论）| 歌曲浏览、互动、查看主页与个人中心 |

*注：若环境无自动生成脚本，请由 `admin` 账号在“用户管理”页面手动新建并绑定对应角色。*

## 2. 核心系统参数 (sys_config) 示例

请将以下内容作为 `config_value` 填入对应的系统配置项。

### 2.1 社区入口配置
- **参数键名 (Config Key)**: `hajihami.community.links`
- **参数说明**: 控制前台歌曲详情页底部的社区、群组导流入口。
- **示例 JSON**:
```json
[
  {
    "id": "qq",
    "name": "哈基哈米 QQ 群",
    "description": "投稿讨论、周榜围观和补档反馈入口。",
    "icon": "simple-icons:tencentqq",
    "url": "https://jq.qq.com/?_wv=1027&k=xxxxxx",
    "btnText": "加入群聊"
  },
  {
    "id": "bilibili",
    "name": "官方 B站 账号",
    "description": "关注最新榜单视频和社区精选填词翻唱。",
    "icon": "simple-icons:bilibili",
    "url": "https://space.bilibili.com/xxxxxx",
    "btnText": "去关注"
  }
]
```

### 2.2 首页公告配置
- **参数键名 (Config Key)**: `hajihami.operation.announcements`
- **参数说明**: 控制前台首页顶部的跑马灯或静态公告。
- **示例 JSON**:
```json
[
  {
    "id": "1",
    "title": "🎉 欢迎来到哈基哈米！重构 S6 版本现已上线上线！",
    "url": "/about",
    "level": "info"
  },
  {
    "id": "2",
    "title": "⚠️ 关于近期部分老音频资源失效的补档说明",
    "url": "/chart/week",
    "level": "warning"
  }
]
```

### 2.3 首页推荐位配置
- **参数键名 (Config Key)**: `hajihami.operation.recommend_cards`
- **参数说明**: 控制前台首页“编辑推荐”区域的大图卡片。
- **示例 JSON**:
```json
[
  {
    "id": "rec-001",
    "title": "本周鬼畜全明星",
    "subtitle": "经典永不过时",
    "coverUrl": "https://oss.hajihami.com/recommend/guichu_week.jpg",
    "targetUrl": "/search?tag=鬼畜",
    "type": "tag"
  },
  {
    "id": "rec-002",
    "title": "AI 翻唱特辑",
    "subtitle": "以假乱真的惊艳之作",
    "coverUrl": "https://oss.hajihami.com/recommend/ai_cover.jpg",
    "targetUrl": "/search?tag=AI&style=翻唱",
    "type": "search"
  }
]
```

### 2.4 激励活动配置
- **参数键名 (Config Key)**: `hajihami.incentive.activity`
- **参数说明**: 控制前台展示的哈气金获取规则或当前活动。
- **示例 JSON**:
```json
{
  "title": "首发投稿奖励活动",
  "description": "本月内在平台首次发布【原教旨】标签作品，过审即送 50 哈气金！",
  "coinAmount": 50,
  "isActive": true,
  "endDate": "2026-06-30T23:59:59Z"
}
```

### 2.5 开发者 API Key 配置
- **参数键名 (Config Key)**: `hajihami.developer.api_keys`
- **参数说明**: 开放平台应用凭证，控制 `/music/open/api/*` 部分接口访问权限。现已支持后台“开放平台”模块可视化管理。
- **示例 JSON**:
```json
[
  {
    "appId": "qq-bot",
    "name": "哈基官方 QQ Bot",
    "token": "dev_hjm_auth_token_example_123",
    "enabled": true,
    "createTime": "2026-05-27T10:00:00Z"
  }
]
```

### 2.6 开发者 Webhook 配置
- **参数键名 (Config Key)**: `hajihami.developer.webhooks`
- **参数说明**: Webhook 异步推送目标配置。现已支持后台“开放平台”模块可视化管理。
- **示例 JSON**:
```json
[
  {
    "id": "wh-001",
    "name": "主群审核通过通知",
    "url": "https://bot.hajihami.com/webhook/dispatch",
    "secret": "hjm_webhook_secret_key_888",
    "enabled": true,
    "eventTypes": [
      "music.published",
      "chart.week.published"
    ]
  }
]
```
