-- =====================================================
-- 音乐模块数据库脚本 (PostgreSQL)
-- 包含所有音乐相关表的创建和字典数据
-- 适用于 RuoYi-Cloud-Plus 框架
-- =====================================================

-- =====================================================
-- 1. music 音乐主表
-- =====================================================
DROP TABLE IF EXISTS music CASCADE;
CREATE TABLE IF NOT EXISTS music
(
    id                int8            NOT NULL,

    -- 核心展示字段
    title             varchar(255)    NOT NULL,
    subtitle          varchar(255),
    original_title    varchar(255),

    -- 外部原作者信息 (B站UP主/网易云歌手)
    creator_id        int8,
    creator_name      varchar(100)    NOT NULL,
    creator_link      varchar(500),
    producer_mark     varchar(500),

    -- 音乐属性
    duration          int4            DEFAULT 0,
    bpm               int4,
    publish_time      timestamp,

    -- 统计数据
    play_count        int8            DEFAULT 0,
    like_count        int8            DEFAULT 0,
    collect_count     int8            DEFAULT 0,
    comment_count     int8            DEFAULT 0,
    share_count       int8            DEFAULT 0,
    download_count    int8            DEFAULT 0,

    -- 业务数据快照 (JSONB) - 用于列表页快速渲染
    original_data     jsonb           DEFAULT '[]'::jsonb,
    resource_data     jsonb           DEFAULT '{}'::jsonb,
    tags_snapshot     jsonb           DEFAULT '[]'::jsonb,
    extend_data       jsonb           DEFAULT '{}'::jsonb,

    -- 状态字段
    audit_status      char(1)         DEFAULT '0',
    is_public         char(1)         DEFAULT '1',
    is_original       char(1)         DEFAULT '0',
    resource_status   char(1)         DEFAULT '0',

    -- 扩展字段
    copyright_info    varchar(500),
    remark            varchar(1000),

    -- 系统审计字段
    tenant_id         varchar(20)     DEFAULT '000000',
    create_dept       int8,
    create_by         int8,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_by         int8,
    update_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    del_flag          char(1)         DEFAULT '0',

    CONSTRAINT pk_music PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_music_title ON music(title);
CREATE INDEX idx_music_creator_name ON music(creator_name);
CREATE INDEX idx_music_creator_id ON music(creator_id);
CREATE INDEX idx_music_status ON music(audit_status, publish_time DESC);
CREATE INDEX idx_music_create_by ON music(create_by);
CREATE INDEX idx_music_resource_status ON music(resource_status);
CREATE INDEX idx_music_is_public ON music(is_public);
CREATE INDEX idx_music_tenant ON music(tenant_id);

-- 注释
COMMENT ON TABLE music IS '音乐曲库主表';
COMMENT ON COLUMN music.id IS '主键ID (雪花算法)';
COMMENT ON COLUMN music.title IS '作品标题';
COMMENT ON COLUMN music.subtitle IS '副标题/别名';
COMMENT ON COLUMN music.original_title IS '原曲名（冗余，方便搜索）';
COMMENT ON COLUMN music.creator_id IS 'UP主/创作者ID（关联用户表）';
COMMENT ON COLUMN music.creator_name IS '外部原作者名称 (如: ilem)';
COMMENT ON COLUMN music.creator_link IS '外部原作者主页链接';
COMMENT ON COLUMN music.producer_mark IS '全民制作人标签（JSON数组或逗号分隔）';
COMMENT ON COLUMN music.duration IS '时长(秒)';
COMMENT ON COLUMN music.bpm IS '节拍数(BPM)';
COMMENT ON COLUMN music.publish_time IS '发布时间(过审时间)';
COMMENT ON COLUMN music.play_count IS '播放量';
COMMENT ON COLUMN music.like_count IS '点赞量';
COMMENT ON COLUMN music.collect_count IS '收藏量';
COMMENT ON COLUMN music.comment_count IS '评论数';
COMMENT ON COLUMN music.share_count IS '分享数';
COMMENT ON COLUMN music.download_count IS '下载数';
COMMENT ON COLUMN music.original_data IS '原曲关联信息快照(JSON数组)';
COMMENT ON COLUMN music.resource_data IS '资源展示快照(JSON对象): 封面和音频的最佳链接';
COMMENT ON COLUMN music.tags_snapshot IS '标签展示快照(JSON数组): 标签名和颜色';
COMMENT ON COLUMN music.extend_data IS '扩展字段(JSON对象): 歌词、备注、PV链接等';
COMMENT ON COLUMN music.audit_status IS '审核状态: 0待审 1通过 2拒绝 3下架';
COMMENT ON COLUMN music.is_public IS '是否公开: 0否 1是';
COMMENT ON COLUMN music.is_original IS '是否原创: 0否 1是';
COMMENT ON COLUMN music.resource_status IS '资源状态: 0正常 1部分失效 2全部失效';
COMMENT ON COLUMN music.copyright_info IS '版权信息';
COMMENT ON COLUMN music.remark IS '备注';
COMMENT ON COLUMN music.tenant_id IS '租户编号';
COMMENT ON COLUMN music.create_dept IS '创建部门';
COMMENT ON COLUMN music.create_by IS '创建人ID';
COMMENT ON COLUMN music.create_time IS '创建时间';
COMMENT ON COLUMN music.update_by IS '更新人ID';
COMMENT ON COLUMN music.update_time IS '更新时间';
COMMENT ON COLUMN music.del_flag IS '删除标志: 0存在 1删除';

-- =====================================================
-- 2. music_original 原曲关联表
-- =====================================================
DROP TABLE IF EXISTS music_original CASCADE;
CREATE TABLE IF NOT EXISTS music_original
(
    id                int8            NOT NULL,
    music_id          int8            NOT NULL,

    -- 原曲信息
    original_title    varchar(200)    NOT NULL,
    original_author   varchar(100),
    original_album    varchar(200),
    original_link     varchar(500),

    -- 来源与关系
    source_type       varchar(20)     DEFAULT 'unknown',
    relation_type     varchar(20)     DEFAULT 'cover',

    -- 排序与状态
    sort_order        int4            DEFAULT 0,
    link_status       char(1)         DEFAULT '0',
    last_check_time   timestamp,
    remark            varchar(500),

    -- 系统字段
    tenant_id         varchar(20)     DEFAULT '000000',
    create_by         int8,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_by         int8,
    update_time       timestamp,
    del_flag          char(1)         DEFAULT '0',

    CONSTRAINT pk_music_original PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_original_music_id ON music_original(music_id);
CREATE INDEX idx_original_source_type ON music_original(source_type);
CREATE INDEX idx_original_link_status ON music_original(link_status);

-- 注释
COMMENT ON TABLE music_original IS '音乐原曲关联表';
COMMENT ON COLUMN music_original.id IS '主键';
COMMENT ON COLUMN music_original.music_id IS '关联的音乐ID';
COMMENT ON COLUMN music_original.original_title IS '原曲标题';
COMMENT ON COLUMN music_original.original_author IS '原曲作者/艺术家';
COMMENT ON COLUMN music_original.original_album IS '原曲专辑';
COMMENT ON COLUMN music_original.original_link IS '原曲外链地址';
COMMENT ON COLUMN music_original.source_type IS '来源平台: bilibili/netease/youtube/spotify/other';
COMMENT ON COLUMN music_original.relation_type IS '关系类型: original/cover/remix/arrange/sample';
COMMENT ON COLUMN music_original.sort_order IS '排序号';
COMMENT ON COLUMN music_original.link_status IS '链接状态: 0正常 1失效 2未检测';
COMMENT ON COLUMN music_original.last_check_time IS '最后检测时间';
COMMENT ON COLUMN music_original.remark IS '备注';
COMMENT ON COLUMN music_original.tenant_id IS '租户编号';
COMMENT ON COLUMN music_original.create_by IS '创建者';
COMMENT ON COLUMN music_original.create_time IS '创建时间';
COMMENT ON COLUMN music_original.update_by IS '更新者';
COMMENT ON COLUMN music_original.update_time IS '更新时间';
COMMENT ON COLUMN music_original.del_flag IS '删除标志: 0存在 1删除';

-- =====================================================
-- 3. music_resource 资源文件表
-- =====================================================
DROP TABLE IF EXISTS music_resource CASCADE;
CREATE TABLE IF NOT EXISTS music_resource
(
    id                int8            NOT NULL,
    music_id          int8            NOT NULL,

    -- 资源类型
    res_type          varchar(20)     NOT NULL,
    quality_tier      varchar(20),

    -- 来源信息
    source_type       varchar(20)     DEFAULT 'local',
    source_url        varchar(1000),
    source_id         varchar(100),
    url               varchar(1000)   NOT NULL,

    -- 文件信息
    file_path         varchar(500),
    file_name         varchar(200),
    file_format       varchar(20),
    file_size         int8            DEFAULT 0,
    file_hash         varchar(64),

    -- 规格信息
    spec_info         jsonb           DEFAULT '{}'::jsonb,

    -- CDN与访问
    cdn_url           varchar(500),
    access_count      int8            DEFAULT 0,

    -- 状态管理
    is_primary        char(1)         DEFAULT '0',
    status            char(1)         DEFAULT '0',
    process_status    char(1)         DEFAULT '0',
    retry_count       int4            DEFAULT 0,
    fail_count        int4            DEFAULT 0,
    fail_reason       varchar(500),
    last_check_time   timestamp,

    -- 排序
    sort_order        int4            DEFAULT 0,

    -- 系统字段
    tenant_id         varchar(20)     DEFAULT '000000',
    create_by         int8,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_by         int8,
    update_time       timestamp,
    del_flag          char(1)         DEFAULT '0',

    CONSTRAINT pk_music_resource PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_res_music_id ON music_resource(music_id);
CREATE INDEX idx_res_type ON music_resource(res_type);
CREATE INDEX idx_res_source_type ON music_resource(source_type);
CREATE INDEX idx_res_status ON music_resource(status);
CREATE INDEX idx_res_process_status ON music_resource(process_status);
CREATE INDEX idx_res_is_primary ON music_resource(is_primary);
CREATE INDEX idx_res_file_hash ON music_resource(file_hash);
CREATE INDEX idx_res_check ON music_resource(status, source_type);

-- 注释
COMMENT ON TABLE music_resource IS '音乐资源文件表';
COMMENT ON COLUMN music_resource.id IS '主键ID';
COMMENT ON COLUMN music_resource.music_id IS '关联音乐ID';
COMMENT ON COLUMN music_resource.res_type IS '资源类型: audio(音频)/cover(封面)';
COMMENT ON COLUMN music_resource.quality_tier IS '质量等级: audio(128k/320k/flac) cover(200/600/1200)';
COMMENT ON COLUMN music_resource.source_type IS '来源类型: local/bilibili/netease/soundcloud/youtube';
COMMENT ON COLUMN music_resource.source_url IS '原始来源地址（外链）';
COMMENT ON COLUMN music_resource.source_id IS '来源平台资源ID（如BV号）';
COMMENT ON COLUMN music_resource.url IS '资源实际链接 (OSS或外链)';
COMMENT ON COLUMN music_resource.file_path IS '处理后文件路径（本地/OSS）';
COMMENT ON COLUMN music_resource.file_name IS '文件名';
COMMENT ON COLUMN music_resource.file_format IS '文件格式: mp3/flac/webp/jpeg/png';
COMMENT ON COLUMN music_resource.file_size IS '文件大小（字节）';
COMMENT ON COLUMN music_resource.file_hash IS '文件哈希（去重用）';
COMMENT ON COLUMN music_resource.spec_info IS '规格信息(JSON): 音频{"bitrate":"320k"} 图片{"width":1200}';
COMMENT ON COLUMN music_resource.cdn_url IS 'CDN加速地址';
COMMENT ON COLUMN music_resource.access_count IS '访问次数';
COMMENT ON COLUMN music_resource.is_primary IS '是否主版本: 0否 1是';
COMMENT ON COLUMN music_resource.status IS '资源状态: 0正常 1失效 2需补档 3处理中';
COMMENT ON COLUMN music_resource.process_status IS '处理状态: 0待处理 1处理中 2已完成 3失败';
COMMENT ON COLUMN music_resource.retry_count IS '重试次数';
COMMENT ON COLUMN music_resource.fail_count IS '连续检测失败次数';
COMMENT ON COLUMN music_resource.fail_reason IS '失败原因';
COMMENT ON COLUMN music_resource.last_check_time IS '最后检测时间';
COMMENT ON COLUMN music_resource.sort_order IS '排序号';
COMMENT ON COLUMN music_resource.tenant_id IS '租户编号';
COMMENT ON COLUMN music_resource.create_by IS '创建者';
COMMENT ON COLUMN music_resource.create_time IS '创建时间';
COMMENT ON COLUMN music_resource.update_by IS '更新者';
COMMENT ON COLUMN music_resource.update_time IS '更新时间';
COMMENT ON COLUMN music_resource.del_flag IS '删除标志: 0存在 1删除';

-- =====================================================
-- 4. tag 标签表
-- =====================================================
DROP TABLE IF EXISTS tag CASCADE;
CREATE TABLE IF NOT EXISTS tag
(
    id                int8            NOT NULL,

    -- 标签基本信息
    name              varchar(50)     NOT NULL,
    type              varchar(20),
    tag_alias         varchar(200),
    parent_id         int8            DEFAULT 0,

    -- 展示信息
    icon_url          varchar(500),
    color             varchar(20),
    description       varchar(500),

    -- 统计与排序
    use_count         int8            DEFAULT 0,
    sort_order        int4            DEFAULT 0,

    -- 状态
    is_hot            char(1)         DEFAULT '0',
    is_recommend      char(1)         DEFAULT '0',
    status            char(1)         DEFAULT '1',

    -- 系统字段
    tenant_id         varchar(20)     DEFAULT '000000',
    create_by         int8,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_by         int8,
    update_time       timestamp,
    del_flag          char(1)         DEFAULT '0',

    CONSTRAINT pk_tag PRIMARY KEY (id),
    CONSTRAINT uk_tag_name UNIQUE (name)
);

-- 索引
CREATE INDEX idx_tag_type ON tag(type);
CREATE INDEX idx_tag_parent_id ON tag(parent_id);
CREATE INDEX idx_tag_is_hot ON tag(is_hot);
CREATE INDEX idx_tag_status ON tag(status);

-- 注释
COMMENT ON TABLE tag IS '标签字典表';
COMMENT ON COLUMN tag.id IS '主键';
COMMENT ON COLUMN tag.name IS '标签名称';
COMMENT ON COLUMN tag.type IS '标签类型: style/mood/scene/language/instrument/era/theme';
COMMENT ON COLUMN tag.tag_alias IS '标签别名/同义词（逗号分隔）';
COMMENT ON COLUMN tag.parent_id IS '父标签ID（支持层级）';
COMMENT ON COLUMN tag.icon_url IS '标签图标URL';
COMMENT ON COLUMN tag.color IS '标签颜色(HEX)';
COMMENT ON COLUMN tag.description IS '标签描述';
COMMENT ON COLUMN tag.use_count IS '使用次数统计';
COMMENT ON COLUMN tag.sort_order IS '排序号';
COMMENT ON COLUMN tag.is_hot IS '是否热门: 0否 1是';
COMMENT ON COLUMN tag.is_recommend IS '是否推荐: 0否 1是';
COMMENT ON COLUMN tag.status IS '状态: 0禁用 1启用';
COMMENT ON COLUMN tag.tenant_id IS '租户编号';
COMMENT ON COLUMN tag.create_by IS '创建者';
COMMENT ON COLUMN tag.create_time IS '创建时间';
COMMENT ON COLUMN tag.update_by IS '更新者';
COMMENT ON COLUMN tag.update_time IS '更新时间';
COMMENT ON COLUMN tag.del_flag IS '删除标志: 0存在 1删除';

-- =====================================================
-- 5. music_tag_rel 音乐标签关联表
-- =====================================================
DROP TABLE IF EXISTS music_tag_rel CASCADE;
CREATE TABLE IF NOT EXISTS music_tag_rel
(
    id                int8            NOT NULL,
    music_id          int8            NOT NULL,
    tag_id            int8            NOT NULL,

    -- 关联属性
    tag_weight        int2            DEFAULT 50,
    is_primary        char(1)         DEFAULT '0',
    source            varchar(20)     DEFAULT 'manual',

    -- 系统字段
    tenant_id         varchar(20)     DEFAULT '000000',
    create_by         int8,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_music_tag_rel PRIMARY KEY (id),
    CONSTRAINT uk_music_tag UNIQUE (music_id, tag_id)
);

-- 索引
CREATE INDEX idx_mtr_music_id ON music_tag_rel(music_id);
CREATE INDEX idx_mtr_tag_id ON music_tag_rel(tag_id);

-- 注释
COMMENT ON TABLE music_tag_rel IS '音乐标签关联表';
COMMENT ON COLUMN music_tag_rel.id IS '主键';
COMMENT ON COLUMN music_tag_rel.music_id IS '音乐ID';
COMMENT ON COLUMN music_tag_rel.tag_id IS '标签ID';
COMMENT ON COLUMN music_tag_rel.tag_weight IS '标签权重（0-100）';
COMMENT ON COLUMN music_tag_rel.is_primary IS '是否主标签: 0否 1是';
COMMENT ON COLUMN music_tag_rel.source IS '标签来源: manual/auto/user';
COMMENT ON COLUMN music_tag_rel.tenant_id IS '租户编号';
COMMENT ON COLUMN music_tag_rel.create_by IS '创建者';
COMMENT ON COLUMN music_tag_rel.create_time IS '创建时间';

-- =====================================================
-- 6. music_audit_log 审核日志表
-- =====================================================
DROP TABLE IF EXISTS music_audit_log CASCADE;
CREATE TABLE IF NOT EXISTS music_audit_log
(
    id                int8            NOT NULL,
    music_id          int8            NOT NULL,

    -- 审核信息
    action            int2            NOT NULL,
    old_status        int2,
    new_status        int2,
    reason            varchar(500),
    operator_id       int8,

    -- 系统字段
    tenant_id         varchar(20)     DEFAULT '000000',
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_music_audit_log PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_audit_music_id ON music_audit_log(music_id);
CREATE INDEX idx_audit_operator ON music_audit_log(operator_id);
CREATE INDEX idx_audit_time ON music_audit_log(create_time);

-- 注释
COMMENT ON TABLE music_audit_log IS '音乐审核流水日志表';
COMMENT ON COLUMN music_audit_log.id IS '主键';
COMMENT ON COLUMN music_audit_log.music_id IS '音乐ID';
COMMENT ON COLUMN music_audit_log.action IS '审核动作: 1通过 2拒绝 3下架';
COMMENT ON COLUMN music_audit_log.old_status IS '修改前状态';
COMMENT ON COLUMN music_audit_log.new_status IS '修改后状态';
COMMENT ON COLUMN music_audit_log.reason IS '拒绝或下架原因';
COMMENT ON COLUMN music_audit_log.operator_id IS '操作人ID (后台管理员ID)';
COMMENT ON COLUMN music_audit_log.tenant_id IS '租户编号';
COMMENT ON COLUMN music_audit_log.create_time IS '操作时间';

-- =====================================================
-- 7. music_link_check_log 链接检测日志表
-- =====================================================
DROP TABLE IF EXISTS music_link_check_log CASCADE;
CREATE TABLE IF NOT EXISTS music_link_check_log
(
    id                int8            NOT NULL,

    -- 关联信息
    resource_id       int8            NOT NULL,
    resource_type     varchar(20)     NOT NULL,
    music_id          int8,

    -- 检测信息
    check_url         varchar(1000)   NOT NULL,
    check_result      char(1)         NOT NULL,
    http_status       int2,
    response_time     int4,
    error_message     varchar(500),

    -- 检测时间
    check_time        timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    check_batch       varchar(50),

    -- 系统字段
    tenant_id         varchar(20)     DEFAULT '000000',

    CONSTRAINT pk_music_link_check_log PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_check_resource ON music_link_check_log(resource_id, resource_type);
CREATE INDEX idx_check_music_id ON music_link_check_log(music_id);
CREATE INDEX idx_check_result ON music_link_check_log(check_result);
CREATE INDEX idx_check_time ON music_link_check_log(check_time);
CREATE INDEX idx_check_batch ON music_link_check_log(check_batch);

-- 注释
COMMENT ON TABLE music_link_check_log IS '链接检测日志表';
COMMENT ON COLUMN music_link_check_log.id IS '主键';
COMMENT ON COLUMN music_link_check_log.resource_id IS '资源ID';
COMMENT ON COLUMN music_link_check_log.resource_type IS '资源类型: audio/cover/original';
COMMENT ON COLUMN music_link_check_log.music_id IS '关联音乐ID';
COMMENT ON COLUMN music_link_check_log.check_url IS '检测的URL地址';
COMMENT ON COLUMN music_link_check_log.check_result IS '检测结果: 0正常 1失效 2超时 3异常';
COMMENT ON COLUMN music_link_check_log.http_status IS 'HTTP状态码';
COMMENT ON COLUMN music_link_check_log.response_time IS '响应时间（毫秒）';
COMMENT ON COLUMN music_link_check_log.error_message IS '错误信息';
COMMENT ON COLUMN music_link_check_log.check_time IS '检测时间';
COMMENT ON COLUMN music_link_check_log.check_batch IS '检测批次号';
COMMENT ON COLUMN music_link_check_log.tenant_id IS '租户编号';

-- =====================================================
-- 8. music_notify_log 通知日志表
-- =====================================================
DROP TABLE IF EXISTS music_notify_log CASCADE;
CREATE TABLE IF NOT EXISTS music_notify_log
(
    id                int8            NOT NULL,

    -- 关联信息
    music_id          int8            NOT NULL,
    user_id           int8            NOT NULL,

    -- 通知内容
    notify_type       varchar(20)     NOT NULL,
    notify_title      varchar(200)    NOT NULL,
    notify_content    text,

    -- 发送状态
    send_channel      varchar(20)     DEFAULT 'system',
    send_status       char(1)         DEFAULT '0',
    send_time         timestamp,
    read_status       char(1)         DEFAULT '0',
    read_time         timestamp,

    -- 系统字段
    tenant_id         varchar(20)     DEFAULT '000000',
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_music_notify_log PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_notify_music_id ON music_notify_log(music_id);
CREATE INDEX idx_notify_user_id ON music_notify_log(user_id);
CREATE INDEX idx_notify_type ON music_notify_log(notify_type);
CREATE INDEX idx_notify_send_status ON music_notify_log(send_status);
CREATE INDEX idx_notify_read_status ON music_notify_log(read_status);

-- 注释
COMMENT ON TABLE music_notify_log IS '音乐通知日志表';
COMMENT ON COLUMN music_notify_log.id IS '主键';
COMMENT ON COLUMN music_notify_log.music_id IS '音乐ID';
COMMENT ON COLUMN music_notify_log.user_id IS '接收用户ID（UP主）';
COMMENT ON COLUMN music_notify_log.notify_type IS '通知类型: link_invalid/audit_result/resource_update';
COMMENT ON COLUMN music_notify_log.notify_title IS '通知标题';
COMMENT ON COLUMN music_notify_log.notify_content IS '通知内容';
COMMENT ON COLUMN music_notify_log.send_channel IS '发送渠道: system/email/sms/wechat';
COMMENT ON COLUMN music_notify_log.send_status IS '发送状态: 0待发送 1已发送 2发送失败';
COMMENT ON COLUMN music_notify_log.send_time IS '发送时间';
COMMENT ON COLUMN music_notify_log.read_status IS '阅读状态: 0未读 1已读';
COMMENT ON COLUMN music_notify_log.read_time IS '阅读时间';
COMMENT ON COLUMN music_notify_log.tenant_id IS '租户编号';
COMMENT ON COLUMN music_notify_log.create_time IS '创建时间';

-- =====================================================
-- 9. 字典类型 - 音乐模块
-- =====================================================

-- 审核状态
INSERT INTO sys_dict_type VALUES (100, '000000', '音乐审核状态', 'music_audit_status', 103, 1, now(), NULL, NULL, '音乐审核状态列表') ON CONFLICT DO NOTHING;

-- 资源状态
INSERT INTO sys_dict_type VALUES (101, '000000', '音乐资源状态', 'music_resource_status', 103, 1, now(), NULL, NULL, '音乐资源状态列表') ON CONFLICT DO NOTHING;

-- 来源平台
INSERT INTO sys_dict_type VALUES (102, '000000', '音乐来源平台', 'music_source_type', 103, 1, now(), NULL, NULL, '音乐来源平台列表') ON CONFLICT DO NOTHING;

-- 关系类型
INSERT INTO sys_dict_type VALUES (103, '000000', '原曲关系类型', 'music_relation_type', 103, 1, now(), NULL, NULL, '原曲关系类型列表') ON CONFLICT DO NOTHING;

-- 标签类型
INSERT INTO sys_dict_type VALUES (104, '000000', '音乐标签类型', 'music_tag_type', 103, 1, now(), NULL, NULL, '音乐标签类型列表') ON CONFLICT DO NOTHING;

-- 链接检测结果
INSERT INTO sys_dict_type VALUES (105, '000000', '链接检测结果', 'music_check_result', 103, 1, now(), NULL, NULL, '链接检测结果列表') ON CONFLICT DO NOTHING;

-- 通知类型
INSERT INTO sys_dict_type VALUES (106, '000000', '音乐通知类型', 'music_notify_type', 103, 1, now(), NULL, NULL, '音乐通知类型列表') ON CONFLICT DO NOTHING;

-- 处理状态
INSERT INTO sys_dict_type VALUES (107, '000000', '资源处理状态', 'music_process_status', 103, 1, now(), NULL, NULL, '资源处理状态列表') ON CONFLICT DO NOTHING;

-- =====================================================
-- 10. 字典数据 - 音乐模块
-- =====================================================

-- 审核状态
INSERT INTO sys_dict_data VALUES (100, '000000', 1, '待审核', '0', 'music_audit_status', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (101, '000000', 2, '已通过', '1', 'music_audit_status', '', 'success', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (102, '000000', 3, '已拒绝', '2', 'music_audit_status', '', 'danger', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (103, '000000', 4, '已下架', '3', 'music_audit_status', '', 'warning', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;

-- 资源状态
INSERT INTO sys_dict_data VALUES (104, '000000', 1, '正常', '0', 'music_resource_status', '', 'success', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (105, '000000', 2, '失效', '1', 'music_resource_status', '', 'danger', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (106, '000000', 3, '需补档', '2', 'music_resource_status', '', 'warning', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (107, '000000', 4, '处理中', '3', 'music_resource_status', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;

-- 来源平台
INSERT INTO sys_dict_data VALUES (108, '000000', 1, '本地上传', 'local', 'music_source_type', '', 'primary', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (109, '000000', 2, 'B站', 'bilibili', 'music_source_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (110, '000000', 3, '网易云', 'netease', 'music_source_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (111, '000000', 4, 'YouTube', 'youtube', 'music_source_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (112, '000000', 5, 'SoundCloud', 'soundcloud', 'music_source_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;

-- 关系类型
INSERT INTO sys_dict_data VALUES (113, '000000', 1, '原创', 'original', 'music_relation_type', '', 'success', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (114, '000000', 2, '翻唱', 'cover', 'music_relation_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (115, '000000', 3, '混音', 'remix', 'music_relation_type', '', 'warning', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (116, '000000', 4, '改编', 'arrange', 'music_relation_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (117, '000000', 5, '采样', 'sample', 'music_relation_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;

-- 标签类型
INSERT INTO sys_dict_data VALUES (118, '000000', 1, '风格', 'style', 'music_tag_type', '', 'primary', 'N', 103, 1, now(), NULL, NULL, '摇滚、流行、古风等') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (119, '000000', 2, '情绪', 'mood', 'music_tag_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '欢快、伤感、燃等') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (120, '000000', 3, '场景', 'scene', 'music_tag_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '运动、学习、睡前等') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (121, '000000', 4, '语言', 'language', 'music_tag_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '中文、日文、英文等') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (122, '000000', 5, '乐器', 'instrument', 'music_tag_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '钢琴、吉他、电子等') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (123, '000000', 6, '年代', 'era', 'music_tag_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '80s、90s、现代等') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (124, '000000', 7, '主题', 'theme', 'music_tag_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '动漫、游戏、影视等') ON CONFLICT DO NOTHING;

-- 链接检测结果
INSERT INTO sys_dict_data VALUES (125, '000000', 1, '正常', '0', 'music_check_result', '', 'success', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (126, '000000', 2, '失效', '1', 'music_check_result', '', 'danger', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (127, '000000', 3, '超时', '2', 'music_check_result', '', 'warning', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (128, '000000', 4, '异常', '3', 'music_check_result', '', 'danger', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;

-- 通知类型
INSERT INTO sys_dict_data VALUES (129, '000000', 1, '链接失效', 'link_invalid', 'music_notify_type', '', 'danger', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (130, '000000', 2, '审核结果', 'audit_result', 'music_notify_type', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (131, '000000', 3, '资源更新', 'resource_update', 'music_notify_type', '', 'success', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;

-- 处理状态
INSERT INTO sys_dict_data VALUES (132, '000000', 1, '待处理', '0', 'music_process_status', '', 'info', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (133, '000000', 2, '处理中', '1', 'music_process_status', '', 'warning', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (134, '000000', 3, '已完成', '2', 'music_process_status', '', 'success', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;
INSERT INTO sys_dict_data VALUES (135, '000000', 4, '处理失败', '3', 'music_process_status', '', 'danger', 'N', 103, 1, now(), NULL, NULL, '') ON CONFLICT DO NOTHING;

-- =====================================================
-- 执行完成
-- =====================================================
