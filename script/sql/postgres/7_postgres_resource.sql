-- 这个文件仅为了满足前置要求，实际的 music_resource 和 music_draft 表已存在于 3_postgres_music.sql 中
-- 防止重复执行引发报错，使用 CREATE TABLE IF NOT EXISTS

CREATE TABLE IF NOT EXISTS music_resource
(
    id                int8            NOT NULL,
    music_id          int8            NOT NULL,
    res_type          varchar(20)     NOT NULL,
    quality_tier      varchar(20),
    source_type       varchar(20)     DEFAULT 'local',
    source_url        varchar(1000),
    source_id         varchar(100),
    url               varchar(1000)   NOT NULL,
    file_path         varchar(500),
    file_name         varchar(200),
    file_format       varchar(20),
    file_size         int8            DEFAULT 0,
    file_hash         varchar(64),
    spec_info         jsonb           DEFAULT '{}'::jsonb,
    cdn_url           varchar(500),
    access_count      int8            DEFAULT 0,
    is_primary        char(1)         DEFAULT '0',
    status            char(1)         DEFAULT '0',
    process_status    char(1)         DEFAULT '0',
    retry_count       int4            DEFAULT 0,
    fail_count        int4            DEFAULT 0,
    fail_reason       varchar(500),
    last_check_time   timestamp,
    sort_order        int4            DEFAULT 0,
    create_by         int8,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_by         int8,
    update_time       timestamp,
    del_flag          char(1)         DEFAULT '0',
    CONSTRAINT pk_music_resource PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS music_draft
(
    id                int8            NOT NULL,
    user_id           int8            NOT NULL,
    content           jsonb           NOT NULL,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time       timestamp,
    CONSTRAINT pk_music_draft PRIMARY KEY (id)
);

