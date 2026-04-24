# 数据库表结构概览

## music - 音乐曲库主表
- id | int8 | 主键ID (雪花算法)
- title | varchar(255) | 作品标题
- subtitle | varchar(255) | 副标题/别名
- original_title | varchar(255) | 原曲名（冗余，方便搜索）
- creator_id | int8 | UP主/创作者ID（关联用户表）
- creator_name | varchar(100) | 外部原作者名称
- creator_link | varchar(500) | 外部原作者主页链接
- producer_mark | varchar(500) | 全民制作人标签（JSON数组或逗号分隔）
- duration | int4 | 时长(秒)
- bpm | int4 | 节拍数(BPM)
- publish_time | timestamp | 发布时间(过审时间)
- play_count | int8 | 播放量(缓存)
- like_count | int8 | 点赞量(缓存)
- collect_count | int8 | 收藏量(缓存)
- comment_count | int8 | 评论数(缓存)
- share_count | int8 | 分享数(缓存)
- download_count | int8 | 下载数(缓存)
- original_data | jsonb | 原曲关联信息快照(JSON数组)
- resource_data | jsonb | 资源展示快照(JSON): 封面和音频最佳链接
- tags_snapshot | jsonb | 标签展示快照(JSON): 标签名和颜色
- extend_data | jsonb | 扩展字段(JSON): 歌词、备注、PV链接等
- audit_status | char(1) | 审核状态: 0待审 1通过 2拒绝 3下架
- is_public | char(1) | 是否公开: 0否 1是
- is_original | char(1) | 是否原创: 0否 1是
- resource_status | char(1) | 资源状态: 0正常 1部分失效 2全部失效
- copyright_info | varchar(500) | 版权信息
- remark | varchar(1000) | 备注
- create_by | int8 | 创建人ID
- create_time | timestamp | 创建时间
- update_by | int8 | 更新人ID
- update_time | timestamp | 更新时间
- del_flag | char(1) | 删除标志: 0存在 1删除

---

## music_stat - 音乐统计表(高频读写)
- music_id | int8 | 音乐ID
- play_count | int8 | 播放量
- like_count | int8 | 点赞量
- collect_count | int8 | 收藏量
- comment_count | int8 | 评论数
- share_count | int8 | 分享数
- download_count | int8 | 下载数
- score | float8 | 综合热度分
- create_time | timestamp | 创建时间
- update_time | timestamp | 更新时间

---

## music_original - 音乐原曲关联表
- id | int8 | 主键
- music_id | int8 | 关联的音乐ID
- original_title | varchar(200) | 原曲标题
- original_author | varchar(100) | 原曲作者/艺术家
- original_album | varchar(200) | 原曲专辑
- original_link | varchar(500) | 原曲外链地址
- source_type | varchar(20) | 来源平台: bilibili/netease/youtube/spotify/other
- relation_type | varchar(20) | 关系类型: original/cover/remix/arrange/sample
- sort_order | int4 | 排序号
- link_status | char(1) | 链接状态: 0正常 1失效 2未检测
- last_check_time | timestamp | 最后检测时间
- remark | varchar(500) | 备注
- create_by | int8 | 创建者
- create_time | timestamp | 创建时间
- update_by | int8 | 更新者
- update_time | timestamp | 更新时间
- del_flag | char(1) | 删除标志: 0存在 1删除

---

## music_resource - 音乐资源文件表
- id | int8 | 主键ID
- music_id | int8 | 关联音乐ID
- res_type | varchar(20) | 资源类型: audio(音频)/cover(封面)
- quality_tier | varchar(20) | 质量等级: audio(128k/320k/flac) cover(200/600/1200)
- source_type | varchar(20) | 来源类型: local/bilibili/netease/soundcloud/youtube
- source_url | varchar(1000) | 原始来源地址（外链）
- source_id | varchar(100) | 来源平台资源ID（如BV号）
- url | varchar(1000) | 资源实际链接 (OSS或外链)
- file_path | varchar(500) | 处理后文件路径（本地/OSS）
- file_name | varchar(200) | 文件名
- file_format | varchar(20) | 文件格式: mp3/flac/webp/jpeg/png
- file_size | int8 | 文件大小（字节）
- file_hash | varchar(64) | 文件哈希（去重用）
- spec_info | jsonb | 规格信息(JSON): 音频`{"bitrate":"320k"}` 图片`{"width":1200}`
- cdn_url | varchar(500) | CDN加速地址
- access_count | int8 | 访问次数
- is_primary | char(1) | 是否主版本: 0否 1是
- status | char(1) | 资源状态: 0正常 1失效 2需补档 3处理中
- process_status | char(1) | 处理状态: 0待处理 1处理中 2已完成 3失败
- retry_count | int4 | 重试次数
- fail_count | int4 | 连续检测失败次数
- fail_reason | varchar(500) | 失败原因
- last_check_time | timestamp | 最后检测时间
- sort_order | int4 | 排序号
- create_by | int8 | 创建者
- create_time | timestamp | 创建时间
- update_by | int8 | 更新者
- update_time | timestamp | 更新时间
- del_flag | char(1) | 删除标志: 0存在 1删除

---

## tag - 标签字典表
- id | int8 | 主键
- name | varchar(50) | 标签名称
- type | varchar(20) | 标签类型: style/mood/scene/language/instrument/era/theme
- tag_alias | varchar(200) | 标签别名/同义词（逗号分隔）
- parent_id | int8 | 父标签ID（支持层级）
- icon_url | varchar(500) | 标签图标URL
- color | varchar(20) | 标签颜色(HEX)
- description | varchar(500) | 标签描述
- use_count | int8 | 使用次数统计
- sort_order | int4 | 排序号
- is_hot | char(1) | 是否热门: 0否 1是
- is_recommend | char(1) | 是否推荐: 0否 1是
- status | char(1) | 状态: 0禁用 1启用
- create_by | int8 | 创建者
- create_time | timestamp | 创建时间
- update_by | int8 | 更新者
- update_time | timestamp | 更新时间
- del_flag | char(1) | 删除标志: 0存在 1删除

---

## music_tag_rel - 音乐标签关联表
- id | int8 | 主键
- music_id | int8 | 音乐ID
- tag_id | int8 | 标签ID
- tag_weight | int2 | 标签权重（0-100）
- is_primary | char(1) | 是否主标签: 0否 1是
- source | varchar(20) | 标签来源: manual/auto/user
- create_by | int8 | 创建者
- create_time | timestamp | 创建时间

---

## music_comment - 音乐评论表
- id | int8 | 主键
- music_id | int8 | 音乐ID
- user_id | int8 | 用户ID
- content | text | 评论内容
- root_id | int8 | 顶级评论ID(0表示自身为顶级)
- parent_id | int8 | 父评论ID(0表示直接回复音乐)
- create_by | int8 | 创建者
- create_time | timestamp | 创建时间
- update_by | int8 | 更新者
- update_time | timestamp | 更新时间
- del_flag | char(1) | 删除标志: 0存在 1删除

---

## music_action - 音乐互动动作表
- id | int8 | 主键
- user_id | int8 | 用户ID
- target_id | int8 | 目标对象ID
- target_type | varchar(20) | 目标类型: song/comment
- action | varchar(20) | 动作: like/dislike
- create_time | timestamp | 创建时间

---

## music_audit_log - 音乐审核流水日志表
- id | int8 | 主键
- music_id | int8 | 音乐ID
- target_type | varchar(20) | 目标类型: music/comment
- action | int2 | 审核动作: 1通过 2拒绝 3下架
- old_status | int2 | 修改前状态
- new_status | int2 | 修改后状态
- reason | varchar(500) | 拒绝或下架原因
- snapshot | jsonb | 下架/审核快照(JSONB)
- operator_id | int8 | 操作人ID (后台管理员ID)
- create_time | timestamp | 操作时间

---

## music_link_check_log - 链接检测日志表
- id | int8 | 主键
- resource_id | int8 | 资源ID
- resource_type | varchar(20) | 资源类型: audio/cover/original
- music_id | int8 | 关联音乐ID
- check_url | varchar(1000) | 检测的URL地址
- check_result | char(1) | 检测结果: 0正常 1失效 2超时 3异常
- http_status | int2 | HTTP状态码
- response_time | int4 | 响应时间（毫秒）
- error_message | varchar(500) | 错误信息
- check_time | timestamp | 检测时间
- check_batch | varchar(50) | 检测批次号

---

## music_notify_log - 音乐通知日志表
- id | int8 | 主键
- music_id | int8 | 音乐ID
- user_id | int8 | 接收用户ID（UP主）
- notify_type | varchar(20) | 通知类型: link_invalid/audit_result/resource_update
- notify_title | varchar(200) | 通知标题
- notify_content | text | 通知内容
- send_channel | varchar(20) | 发送渠道: system/email/sms/wechat
- send_status | char(1) | 发送状态: 0待发送 1已发送 2发送失败
- send_time | timestamp | 发送时间
- read_status | char(1) | 阅读状态: 0未读 1已读
- read_time | timestamp | 阅读时间
- create_time | timestamp | 创建时间

---

## music_coin_ledger - 哈气金流水表
- id | int8 | 主键
- user_id | int8 | 用户ID
- amount | int4 | 变动金额(+/-)
- reason_code | varchar(50) | 变动原因(upload_reward/system_grant)
- balance_after | int4 | 变动后余额
- create_time | timestamp | 创建时间

---

## music_chart_snapshot - 榜单快照表
- id | int8 | 主键
- chart_type | varchar(20) | 榜单类型: week/month
- period_key | varchar(20) | 周期标识(如 2025-W04)
- status | varchar(20) | 状态: calculating/published
- create_by | int8 | 创建者
- create_time | timestamp | 创建时间
- update_by | int8 | 更新者
- update_time | timestamp | 更新时间

---

## music_chart_item - 榜单明细表
- id | int8 | 主键
- snapshot_id | int8 | 榜单快照ID
- music_id | int8 | 音乐ID
- rank_no | int4 | 排名
- score | float8 | 综合热度分
- play_count | int8 | 播放量
- like_count | int8 | 点赞量
- create_time | timestamp | 创建时间

---

## music_draft - 投稿草稿表
- id | int8 | 主键
- user_id | int8 | 用户ID
- content | jsonb | 表单草稿(JSONB)
- create_time | timestamp | 创建时间
- update_time | timestamp | 更新时间

---

## tag_proposal - 标签提报申请表
- id | int8 | 主键
- user_id | int8 | 提报用户ID
- music_id | int8 | 关联的音乐ID
- tag_name | varchar(50) | 标签名称
- tag_type | varchar(20) | 标签类型
- description | varchar(500) | 标签描述/申请理由
- status | char(1) | 审核状态: 0待审核 1通过 2拒绝
- auditor_id | int8 | 审核人ID
- audit_time | timestamp | 审核时间
- audit_remark | varchar(500) | 审核备注/拒绝原因
- tag_id | int8 | 通过后关联的正式标签ID
- create_by | int8 | 创建者
- create_time | timestamp | 创建时间
- update_by | int8 | 更新者
- update_time | timestamp | 更新时间
- del_flag | char(1) | 删除标志: 0存在 1删除
