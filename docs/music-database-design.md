# 音乐数据库设计文档 V2.0

> 基于需求文档重新设计，优化表结构

## 一、需求分析

### 核心需求
1. **曲库管理**：ID、标题、原曲名、UP主、全民制作人、风格标签、时长、BPM、封面图、音频文件、发布时间、播放量、点赞量、收藏量、审核状态、备注
2. **原曲关联**：一首歌可关联 0-N 个原曲条目（标题、作者、外链、类型）
3. **多源音频**：本地上传 + 外链（B站、网易云、SoundCloud等），支持转码 mp3 128k/320k
4. **封面图**：自动压缩 WebP，多分辨率 200/600/1200
5. **失效检测**：每日定时检测外链，标记"需补档"并通知UP

---

## 二、ER图（实体关系）

```
┌─────────────────┐       ┌─────────────────┐
│     music       │───────│  music_original │
│   (音乐主表)     │ 1:N   │   (原曲关联)     │
└────────┬────────┘       └─────────────────┘
         │
         │ 1:N
         ▼
┌─────────────────┐       ┌─────────────────┐
│music_resource   │───────│music_link_check │
│    (资源文件)    │ 1:N   │    (检测日志)    │
└────────┬────────┘       └─────────────────┘
         │
         │ N:N
         ▼
┌─────────────────┐       ┌─────────────────┐
│  music_tag_rel  │───────│      tag        │
│  (音乐标签关联)  │ N:1   │    (标签表)      │
└─────────────────┘       └─────────────────┘
```

---

## 三、表结构设计

### 1. music（音乐主表）

```sql
DROP TABLE IF EXISTS `music`;
CREATE TABLE `music` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(200) NOT NULL COMMENT '作品标题',
  `subtitle` VARCHAR(200) DEFAULT NULL COMMENT '副标题/别名',
  `original_title` VARCHAR(200) DEFAULT NULL COMMENT '原曲名（冗余，方便搜索）',
  
  -- UP主/创作者信息
  `creator_id` BIGINT DEFAULT NULL COMMENT 'UP主/创作者ID（关联用户表）',
  `creator_name` VARCHAR(100) DEFAULT NULL COMMENT 'UP主/创作者名称（冗余）',
  `producer_mark` VARCHAR(500) DEFAULT NULL COMMENT '全民制作人标签（JSON数组或逗号分隔）',
  
  -- 音乐属性
  `duration` INT UNSIGNED DEFAULT 0 COMMENT '时长（秒）',
  `bpm` SMALLINT UNSIGNED DEFAULT NULL COMMENT '节拍数(BPM)',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  
  -- 统计数据（定期同步或实时更新）
  `play_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '播放量',
  `like_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '点赞量',
  `collect_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '收藏量',
  `comment_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '评论数',
  `share_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '分享数',
  `download_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '下载数',
  
  -- 状态字段
  `audit_status` CHAR(1) DEFAULT '0' COMMENT '审核状态：0-待审 1-通过 2-拒绝 3-下架',
  `is_public` CHAR(1) DEFAULT '1' COMMENT '是否公开：0-否 1-是',
  `is_original` CHAR(1) DEFAULT '0' COMMENT '是否原创：0-否 1-是',
  `resource_status` CHAR(1) DEFAULT '0' COMMENT '资源状态：0-正常 1-部分失效 2-全部失效',
  
  -- 扩展字段
  `copyright_info` VARCHAR(500) DEFAULT NULL COMMENT '版权信息',
  `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
  `extend_data` JSON DEFAULT NULL COMMENT '扩展数据（JSON格式）',
  
  -- 系统字段
  `tenant_id` VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
  `create_dept` BIGINT DEFAULT NULL COMMENT '创建部门',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志：0-存在 1-删除',
  
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_creator_id` (`creator_id`),
  KEY `idx_creator_name` (`creator_name`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_resource_status` (`resource_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐主表';
```

**字段变更说明：**
- `duration` 改为 `INT UNSIGNED`，更合理（秒）
- `bpm` 改为 `SMALLINT UNSIGNED`，BPM通常60-200
- 新增 `resource_status` 资源状态字段，方便快速筛选失效资源
- 统计字段改为 `BIGINT UNSIGNED`，避免负数
- `extend_data` 改为 `JSON` 类型（MySQL 5.7+支持）

---

### 2. music_original（原曲关联表）

```sql
DROP TABLE IF EXISTS `music_original`;
CREATE TABLE `music_original` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `music_id` BIGINT NOT NULL COMMENT '关联的音乐ID',
  
  -- 原曲信息
  `original_title` VARCHAR(200) NOT NULL COMMENT '原曲标题',
  `original_author` VARCHAR(100) DEFAULT NULL COMMENT '原曲作者/艺术家',
  `original_album` VARCHAR(200) DEFAULT NULL COMMENT '原曲专辑',
  `original_link` VARCHAR(500) DEFAULT NULL COMMENT '原曲外链地址',
  
  -- 来源与关系
  `source_type` VARCHAR(20) DEFAULT 'unknown' COMMENT '来源平台：bilibili/netease/youtube/spotify/other',
  `relation_type` VARCHAR(20) DEFAULT 'cover' COMMENT '关系类型：original-原创/cover-翻唱/remix-混音/arrange-改编/sample-采样',
  
  -- 排序与状态
  `sort_order` INT DEFAULT 0 COMMENT '排序号（多个原曲时的显示顺序）',
  `link_status` CHAR(1) DEFAULT '0' COMMENT '链接状态：0-正常 1-失效 2-未检测',
  `last_check_time` DATETIME DEFAULT NULL COMMENT '最后检测时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  
  -- 系统字段
  `tenant_id` VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志：0-存在 1-删除',
  
  PRIMARY KEY (`id`),
  KEY `idx_music_id` (`music_id`),
  KEY `idx_source_type` (`source_type`),
  KEY `idx_link_status` (`link_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐原曲关联表';
```

**字段变更说明：**
- 新增 `original_album` 原曲专辑字段
- 新增 `link_status` 链接状态字段，方便失效检测
- `relation_type` 枚举值更规范：original/cover/remix/arrange/sample

---

### 3. music_resource（资源文件表）- 重命名优化

```sql
DROP TABLE IF EXISTS `music_resource`;
CREATE TABLE `music_resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `music_id` BIGINT NOT NULL COMMENT '关联的音乐ID',
  
  -- 资源类型
  `resource_type` VARCHAR(20) NOT NULL COMMENT '资源类型：audio-音频/cover-封面图',
  `quality_level` VARCHAR(20) DEFAULT 'standard' COMMENT '质量等级：audio(128k/320k/flac) cover(200/600/1200)',
  
  -- 来源信息
  `source_type` VARCHAR(20) DEFAULT 'local' COMMENT '来源类型：local-本地上传/bilibili/netease/soundcloud/youtube',
  `source_url` VARCHAR(1000) DEFAULT NULL COMMENT '原始来源地址（外链）',
  `source_id` VARCHAR(100) DEFAULT NULL COMMENT '来源平台的资源ID（如BV号）',
  
  -- 文件信息
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '处理后文件路径（本地/OSS）',
  `file_name` VARCHAR(200) DEFAULT NULL COMMENT '文件名',
  `file_format` VARCHAR(20) DEFAULT NULL COMMENT '文件格式：mp3/flac/webp/jpeg/png',
  `file_size` BIGINT UNSIGNED DEFAULT 0 COMMENT '文件大小（字节）',
  `file_hash` VARCHAR(64) DEFAULT NULL COMMENT '文件MD5/SHA256哈希（去重用）',
  
  -- 规格信息（JSON）
  `spec_info` JSON DEFAULT NULL COMMENT '规格信息：音频{"bitrate":"320k","sample_rate":44100} 图片{"width":1200,"height":1200}',
  
  -- CDN与访问
  `cdn_url` VARCHAR(500) DEFAULT NULL COMMENT 'CDN加速地址',
  `access_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '访问次数',
  
  -- 状态管理
  `is_primary` CHAR(1) DEFAULT '0' COMMENT '是否主版本：0-否 1-是（每种类型只有一个主版本）',
  `status` CHAR(1) DEFAULT '0' COMMENT '资源状态：0-正常 1-失效 2-需补档 3-处理中',
  `process_status` CHAR(1) DEFAULT '0' COMMENT '处理状态：0-待处理 1-处理中 2-已完成 3-失败',
  `retry_count` TINYINT UNSIGNED DEFAULT 0 COMMENT '重试次数',
  `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
  `last_check_time` DATETIME DEFAULT NULL COMMENT '最后检测时间',
  
  -- 排序
  `sort_order` INT DEFAULT 0 COMMENT '排序号',
  
  -- 系统字段
  `tenant_id` VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志：0-存在 1-删除',
  
  PRIMARY KEY (`id`),
  KEY `idx_music_id` (`music_id`),
  KEY `idx_resource_type` (`resource_type`),
  KEY `idx_source_type` (`source_type`),
  KEY `idx_status` (`status`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_file_hash` (`file_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐资源文件表';
```

**字段变更说明：**
- 表名从 `music_resource_file` 简化为 `music_resource`
- 新增 `quality_level` 质量等级字段（128k/320k/flac 或 200/600/1200）
- 新增 `source_id` 来源平台资源ID（如B站BV号）
- 新增 `file_name` 文件名
- 新增 `file_hash` 文件哈希（用于去重）
- 新增 `access_count` 访问次数统计
- `spec_info` 改为 JSON 类型

---

### 4. tag（标签表）

```sql
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  
  -- 标签基本信息
  `tag_name` VARCHAR(50) NOT NULL COMMENT '标签名称（唯一）',
  `tag_type` VARCHAR(20) NOT NULL COMMENT '标签类型：style-风格/mood-情绪/scene-场景/language-语言/instrument-乐器/era-年代/theme-主题',
  `tag_alias` VARCHAR(200) DEFAULT NULL COMMENT '标签别名/同义词（逗号分隔，用于搜索）',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父标签ID（支持层级）',
  
  -- 展示信息
  `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '标签图标URL',
  `color_hex` VARCHAR(7) DEFAULT NULL COMMENT '标签颜色（十六进制，如#FF5733）',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '标签描述',
  
  -- 统计与排序
  `use_count` BIGINT UNSIGNED DEFAULT 0 COMMENT '使用次数统计',
  `sort_order` INT DEFAULT 0 COMMENT '排序号',
  
  -- 状态
  `is_hot` CHAR(1) DEFAULT '0' COMMENT '是否热门：0-否 1-是',
  `is_recommend` CHAR(1) DEFAULT '0' COMMENT '是否推荐：0-否 1-是',
  `status` CHAR(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  
  -- 系统字段
  `tenant_id` VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志：0-存在 1-删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name_type` (`tag_name`, `tag_type`),
  KEY `idx_tag_type` (`tag_type`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';
```

**字段变更说明：**
- 新增 `parent_id` 父标签ID，支持标签层级（如：风格 > 摇滚 > 硬摇滚）
- 新增 `is_recommend` 是否推荐字段
- 唯一索引改为 `(tag_name, tag_type)` 组合，同名标签可以属于不同类型

---

### 5. music_tag_rel（音乐标签关联表）

```sql
DROP TABLE IF EXISTS `music_tag_rel`;
CREATE TABLE `music_tag_rel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `music_id` BIGINT NOT NULL COMMENT '音乐ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  
  -- 关联属性
  `tag_weight` TINYINT UNSIGNED DEFAULT 50 COMMENT '标签权重（0-100，数值越大越重要）',
  `is_primary` CHAR(1) DEFAULT '0' COMMENT '是否主标签：0-否 1-是（每首歌可有多个主标签）',
  `source` VARCHAR(20) DEFAULT 'manual' COMMENT '标签来源：manual-人工/auto-自动识别/user-用户投票',
  
  -- 系统字段
  `tenant_id` VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_music_tag` (`music_id`, `tag_id`),
  KEY `idx_music_id` (`music_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐标签关联表';
```

**字段变更说明：**
- `tag_weight` 改为 `TINYINT UNSIGNED`，范围0-100更合理
- 新增 `source` 标签来源字段（人工/自动/用户投票）
- 添加唯一索引防止重复关联

---

### 6. music_link_check_log（链接检测日志表）

```sql
DROP TABLE IF EXISTS `music_link_check_log`;
CREATE TABLE `music_link_check_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  
  -- 关联信息
  `resource_id` BIGINT NOT NULL COMMENT '资源ID（music_resource.id 或 music_original.id）',
  `resource_type` VARCHAR(20) NOT NULL COMMENT '资源类型：audio-音频/cover-封面/original-原曲',
  `music_id` BIGINT DEFAULT NULL COMMENT '关联音乐ID（冗余，方便查询）',
  
  -- 检测信息
  `check_url` VARCHAR(1000) NOT NULL COMMENT '检测的URL地址',
  `check_result` CHAR(1) NOT NULL COMMENT '检测结果：0-正常 1-失效 2-超时 3-异常',
  `http_status` SMALLINT DEFAULT NULL COMMENT 'HTTP状态码',
  `response_time` INT UNSIGNED DEFAULT NULL COMMENT '响应时间（毫秒）',
  `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
  
  -- 检测时间
  `check_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '检测时间',
  `check_batch` VARCHAR(50) DEFAULT NULL COMMENT '检测批次号（同一次定时任务）',
  
  -- 系统字段
  `tenant_id` VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
  
  PRIMARY KEY (`id`),
  KEY `idx_resource` (`resource_id`, `resource_type`),
  KEY `idx_music_id` (`music_id`),
  KEY `idx_check_result` (`check_result`),
  KEY `idx_check_time` (`check_time`),
  KEY `idx_check_batch` (`check_batch`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='链接检测日志表';
```

**字段变更说明：**
- 新增 `music_id` 关联音乐ID（冗余字段，方便查询）
- 新增 `check_batch` 检测批次号（同一次定时任务的标识）
- `http_status` 改为 `SMALLINT`，状态码通常3位数
- `response_time` 改为 `INT UNSIGNED`

---

### 7. music_notify_log（通知日志表）- 新增

```sql
DROP TABLE IF EXISTS `music_notify_log`;
CREATE TABLE `music_notify_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  
  -- 关联信息
  `music_id` BIGINT NOT NULL COMMENT '音乐ID',
  `user_id` BIGINT NOT NULL COMMENT '接收用户ID（UP主）',
  
  -- 通知内容
  `notify_type` VARCHAR(20) NOT NULL COMMENT '通知类型：link_invalid-链接失效/audit_result-审核结果/resource_update-资源更新',
  `notify_title` VARCHAR(200) NOT NULL COMMENT '通知标题',
  `notify_content` TEXT COMMENT '通知内容',
  
  -- 发送状态
  `send_channel` VARCHAR(20) DEFAULT 'system' COMMENT '发送渠道：system-站内信/email-邮件/sms-短信/wechat-微信',
  `send_status` CHAR(1) DEFAULT '0' COMMENT '发送状态：0-待发送 1-已发送 2-发送失败',
  `send_time` DATETIME DEFAULT NULL COMMENT '发送时间',
  `read_status` CHAR(1) DEFAULT '0' COMMENT '阅读状态：0-未读 1-已读',
  `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
  
  -- 系统字段
  `tenant_id` VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  PRIMARY KEY (`id`),
  KEY `idx_music_id` (`music_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_notify_type` (`notify_type`),
  KEY `idx_send_status` (`send_status`),
  KEY `idx_read_status` (`read_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐通知日志表';
```

**新增说明：**
- 用于记录向UP主发送的通知（失效提醒、审核结果等）
- 支持多渠道发送：站内信、邮件、短信、微信

---

## 四、字段枚举值汇总

### 审核状态 (audit_status)
| 值 | 说明 |
|----|------|
| 0 | 待审核 |
| 1 | 审核通过 |
| 2 | 审核拒绝 |
| 3 | 已下架 |

### 资源状态 (status/resource_status)
| 值 | 说明 |
|----|------|
| 0 | 正常 |
| 1 | 失效/部分失效 |
| 2 | 需补档/全部失效 |
| 3 | 处理中 |

### 处理状态 (process_status)
| 值 | 说明 |
|----|------|
| 0 | 待处理 |
| 1 | 处理中 |
| 2 | 已完成 |
| 3 | 处理失败 |

### 链接检测结果 (check_result)
| 值 | 说明 |
|----|------|
| 0 | 正常 |
| 1 | 失效（404等） |
| 2 | 超时 |
| 3 | 异常（网络错误等） |

### 来源平台 (source_type)
| 值 | 说明 |
|----|------|
| local | 本地上传 |
| bilibili | B站 |
| netease | 网易云 |
| youtube | YouTube |
| soundcloud | SoundCloud |
| spotify | Spotify |
| other | 其他 |

### 关系类型 (relation_type)
| 值 | 说明 |
|----|------|
| original | 原创 |
| cover | 翻唱 |
| remix | 混音 |
| arrange | 改编 |
| sample | 采样 |

### 标签类型 (tag_type)
| 值 | 说明 |
|----|------|
| style | 风格（摇滚、流行、古风等） |
| mood | 情绪（欢快、伤感、燃等） |
| scene | 场景（运动、学习、睡前等） |
| language | 语言（中文、日文、英文等） |
| instrument | 乐器（钢琴、吉他、电子等） |
| era | 年代（80s、90s、现代等） |
| theme | 主题（动漫、游戏、影视等） |

### 资源质量等级 (quality_level)
| 资源类型 | 可选值 |
|---------|-------|
| audio | 128k / 320k / flac |
| cover | 200 / 600 / 1200 |

---

## 五、索引设计说明

### 主要查询场景与索引

1. **按标题搜索** → `idx_title`
2. **按创作者查询** → `idx_creator_id`, `idx_creator_name`
3. **按审核状态筛选** → `idx_audit_status`
4. **按发布时间排序** → `idx_publish_time`
5. **按资源状态筛选失效资源** → `idx_resource_status`
6. **按音乐ID查询关联数据** → 各子表的 `idx_music_id`
7. **标签搜索** → `idx_tag_type`, `uk_tag_name_type`

---

## 六、数据流转示意

### 1. 音频上传流程
```
用户上传/填写外链
       ↓
创建 music 记录（audit_status=0）
       ↓
创建 music_resource 记录（process_status=0）
       ↓
异步任务：下载外链/转码
       ↓
更新 music_resource（file_path, cdn_url, process_status=2）
       ↓
管理员审核 → 更新 audit_status
```

### 2. 失效检测流程
```
每日定时任务启动
       ↓
查询所有 source_type != 'local' 的资源
       ↓
HTTP HEAD 请求检测链接
       ↓
写入 music_link_check_log
       ↓
更新 music_resource.status（失效=1）
       ↓
更新 music.resource_status（汇总）
       ↓
创建 music_notify_log 通知UP主
```

---

## 七、与现有表结构对比

| 现有表名 | 新表名 | 变更说明 |
|---------|-------|---------|
| music | music | 优化字段类型，新增 resource_status |
| music_original | music_original | 新增 original_album, link_status |
| music_resource_file | music_resource | 重命名，新增 quality_level, source_id, file_hash |
| tag | tag | 新增 parent_id, is_recommend |
| music_tag_rel | music_tag_rel | 新增 source 字段 |
| music_link_check_log | music_link_check_log | 新增 music_id, check_batch |
| - | music_notify_log | **新增表**，用于通知UP主 |
