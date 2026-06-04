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

INSERT INTO emoji (id, url, name, category, status, create_by, create_time, update_by, update_time, del_flag) VALUES
    (60001, '/images/emoji/mambo-start.svg', '曼波开场', '曼波', 1, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, '0'),
    (60002, '/images/emoji/haki-call.svg', '哈气打Call', '评论常用', 1, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, '0'),
    (60003, '/images/emoji/cover-meme.svg', '封面绷不住', '封面梗', 1, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, '0'),
    (60004, '/images/emoji/remix-start.svg', '二创启动', '投稿常用', 1, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, '0'),
    (60005, '/images/emoji/pending-review.svg', '等待审核样例', '待审核', 0, 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, '0')
ON CONFLICT (id) DO NOTHING;

-- 后台表情包审核菜单与权限。静态路由已存在，这里补全非超管角色与权限元数据。
INSERT INTO sys_menu VALUES
    (10220, '表情包审核', 100, 8, 'music_emoji', 'music/emoji/index', '', '1', '0', 'C', '0', '0', 'music:emoji:list', 'material-symbols:emoji-emotions-outline', 103, 1, CURRENT_TIMESTAMP, NULL, NULL, '表情包审核菜单'),
    (10221, '表情包查询', 10220, 1, '#', '', '', '1', '0', 'F', '0', '0', 'music:emoji:query', '#', 103, 1, CURRENT_TIMESTAMP, NULL, NULL, ''),
    (10222, '表情包新增', 10220, 2, '#', '', '', '1', '0', 'F', '0', '0', 'music:emoji:add', '#', 103, 1, CURRENT_TIMESTAMP, NULL, NULL, ''),
    (10223, '表情包修改', 10220, 3, '#', '', '', '1', '0', 'F', '0', '0', 'music:emoji:edit', '#', 103, 1, CURRENT_TIMESTAMP, NULL, NULL, ''),
    (10224, '表情包删除', 10220, 4, '#', '', '', '1', '0', 'F', '0', '0', 'music:emoji:remove', '#', 103, 1, CURRENT_TIMESTAMP, NULL, NULL, ''),
    (10225, '表情包审核', 10220, 5, '#', '', '', '1', '0', 'F', '0', '0', 'music:emoji:audit', '#', 103, 1, CURRENT_TIMESTAMP, NULL, NULL, '')
ON CONFLICT DO NOTHING;

INSERT INTO sys_role_menu (role_id, menu_id) VALUES
    (3, 10220),
    (3, 10221),
    (3, 10222),
    (3, 10223),
    (3, 10224),
    (3, 10225)
ON CONFLICT DO NOTHING;
