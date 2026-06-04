DROP TABLE IF EXISTS emoji CASCADE;
CREATE TABLE IF NOT EXISTS emoji
(
    id                int8            NOT NULL,
    url               varchar(1000)   NOT NULL,
    name              varchar(255)    NOT NULL,
    category          varchar(100)    NOT NULL,
    status            int4            DEFAULT 0,

    create_by         int8,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_by         int8,
    update_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    del_flag          char(1)         DEFAULT '0',

    CONSTRAINT pk_emoji PRIMARY KEY (id)
);

CREATE INDEX idx_emoji_category ON emoji(category);
CREATE INDEX idx_emoji_status ON emoji(status);

COMMENT ON TABLE emoji IS '表情包表';
COMMENT ON COLUMN emoji.id IS '主键ID';
COMMENT ON COLUMN emoji.url IS '表情URL';
COMMENT ON COLUMN emoji.name IS '表情名称';
COMMENT ON COLUMN emoji.category IS '分类';
COMMENT ON COLUMN emoji.status IS '状态: 0=待审核, 1=已通过, 2=已拒绝';
COMMENT ON COLUMN emoji.create_by IS '创建人ID';
COMMENT ON COLUMN emoji.create_time IS '创建时间';
COMMENT ON COLUMN emoji.update_by IS '更新人ID';
COMMENT ON COLUMN emoji.update_time IS '更新时间';
COMMENT ON COLUMN emoji.del_flag IS '删除标志: 0存在 1删除';
