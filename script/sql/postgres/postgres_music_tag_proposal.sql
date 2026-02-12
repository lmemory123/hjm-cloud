-- =====================================================
-- 标签提报申请表 (PostgreSQL)
-- =====================================================

DROP TABLE IF EXISTS tag_proposal CASCADE;
CREATE TABLE IF NOT EXISTS tag_proposal
(
    id                int8            NOT NULL,

    -- 申请信息
    user_id           int8            NOT NULL,
    music_id          int8,
    tag_name          varchar(50)     NOT NULL,
    tag_type          varchar(20),
    description       varchar(500),

    -- 审核信息
    status            char(1)         DEFAULT '0',
    auditor_id        int8,
    audit_time        timestamp,
    audit_remark      varchar(500),
    tag_id            int8,

    -- 系统字段
    create_by         int8,
    create_time       timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_by         int8,
    update_time       timestamp,
    del_flag          char(1)         DEFAULT '0',

    CONSTRAINT pk_tag_proposal PRIMARY KEY (id)
);

-- 索引
CREATE INDEX idx_tag_proposal_user_id ON tag_proposal(user_id);
CREATE INDEX idx_tag_proposal_music_id ON tag_proposal(music_id);
CREATE INDEX idx_tag_proposal_status ON tag_proposal(status);
CREATE INDEX idx_tag_proposal_tag_name ON tag_proposal(tag_name);

-- 注释
COMMENT ON TABLE tag_proposal IS '标签提报申请表';
COMMENT ON COLUMN tag_proposal.id IS '主键';
COMMENT ON COLUMN tag_proposal.user_id IS '提报用户ID';
COMMENT ON COLUMN tag_proposal.music_id IS '关联的音乐ID';
COMMENT ON COLUMN tag_proposal.tag_name IS '标签名称';
COMMENT ON COLUMN tag_proposal.tag_type IS '标签类型';
COMMENT ON COLUMN tag_proposal.description IS '标签描述/申请理由';
COMMENT ON COLUMN tag_proposal.status IS '审核状态: 0待审核 1通过 2拒绝';
COMMENT ON COLUMN tag_proposal.auditor_id IS '审核人ID';
COMMENT ON COLUMN tag_proposal.audit_time IS '审核时间';
COMMENT ON COLUMN tag_proposal.audit_remark IS '审核备注/拒绝原因';
COMMENT ON COLUMN tag_proposal.tag_id IS '通过后关联的正式标签ID';
COMMENT ON COLUMN tag_proposal.create_by IS '创建者';
COMMENT ON COLUMN tag_proposal.create_time IS '创建时间';
COMMENT ON COLUMN tag_proposal.update_by IS '更新者';
COMMENT ON COLUMN tag_proposal.update_time IS '更新时间';
COMMENT ON COLUMN tag_proposal.del_flag IS '删除标志: 0存在 1删除';

