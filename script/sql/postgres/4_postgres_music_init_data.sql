-- =====================================================
-- 哈基哈米音乐社区核心闭环基础数据初始化脚本 (P1)
-- =====================================================

-- 1. 音乐标签字典 (tag)
TRUNCATE TABLE tag CASCADE;
INSERT INTO tag (id, name, type, parent_id, is_hot, is_recommend, status, create_time) VALUES
(1, '分类', 'genre', 0, '0', '1', '0', now()),
(2, '风格', 'style', 0, '0', '1', '0', now()),
(10, '原教旨', 'genre', 1, '1', '1', '0', now()),
(11, '曼波', 'genre', 1, '1', '1', '0', now()),
(12, '电音', 'style', 2, '1', '0', '0', now()),
(13, '鬼畜', 'genre', 1, '1', '1', '0', now()),
(14, '翻唱', 'genre', 1, '0', '1', '0', now()),
(15, '原创', 'genre', 1, '0', '1', '0', now()),
(16, 'AI', 'genre', 1, '0', '0', '0', now());

-- 2. 示例歌曲 (music)
-- 预置不同状态的歌曲：1-通过, 0-待审, 2-驳回, 3-下架
TRUNCATE TABLE music CASCADE;
INSERT INTO music (id, title, creator_id, creator_name, audit_status, is_public, is_original, publish_time, create_time) VALUES
(1001, '哈基之歌', 2001, '初音未来', '1', '1', '1', now() - interval '1 day', now() - interval '2 day'),
(1002, '曼波曼波', 2002, '曼波达人', '1', '1', '0', now() - interval '12 hour', now() - interval '1 day'),
(1003, '电音震击', 2003, 'DJ-Hakimi', '1', '1', '1', now() - interval '2 day', now() - interval '3 day'),
(1004, '鬼畜全明星', 2004, '鬼畜匠人', '1', '1', '0', now() - interval '3 day', now() - interval '4 day'),
(1005, '待审神曲', 2005, '新人小白', '0', '0', '1', NULL, now()),
(1006, '问题作品', 2006, '违规搬运', '2', '0', '0', NULL, now() - interval '1 hour'),
(1007, '已下架作品', 2007, '旧日回响', '3', '0', '0', now() - interval '10 day', now() - interval '11 day');

-- 3. 歌曲资源 (music_resource)
TRUNCATE TABLE music_resource CASCADE;
INSERT INTO music_resource (id, music_id, res_type, url, file_size, file_format, create_time) VALUES
(1, 1001, 'audio', 'https://oss.hajihami.com/songs/hajisong.mp3', 5242880, 'audio/mpeg', now()),
(2, 1001, 'cover', 'https://oss.hajihami.com/covers/hajisong.jpg', 102400, 'image/jpeg', now()),
(3, 1002, 'audio', 'https://oss.hajihami.com/songs/mambo.mp3', 4194304, 'audio/mpeg', now()),
(4, 1002, 'cover', 'https://oss.hajihami.com/covers/mambo.jpg', 81920, 'image/jpeg', now()),
(5, 1003, 'audio', 'https://oss.hajihami.com/songs/electro.flac', 20971520, 'audio/flac', now()),
(6, 1004, 'audio', 'https://oss.hajihami.com/songs/guichu.mp3', 3145728, 'audio/mpeg', now());

-- 4. 统计数据 (music_stat)
TRUNCATE TABLE music_stat CASCADE;
INSERT INTO music_stat (music_id, play_count, like_count, collect_count, comment_count, score, create_time) VALUES
(1001, 5000, 120, 45, 10, 850, now()),
(1002, 12000, 350, 80, 25, 920, now()),
(1003, 3000, 80, 20, 5, 710, now()),
(1004, 1500, 45, 10, 2, 600, now());

-- 5. 标签关联 (music_tag_rel)
TRUNCATE TABLE music_tag_rel CASCADE;
INSERT INTO music_tag_rel (id, music_id, tag_id, tag_weight) VALUES
(1, 1001, 10, 100), -- 哈基之歌 -> 原教旨
(2, 1001, 15, 80),  -- 哈基之歌 -> 原创
(3, 1002, 11, 100), -- 曼波曼波 -> 曼波
(4, 1002, 14, 80),  -- 曼波曼波 -> 翻唱
(5, 1003, 12, 100), -- 电音震击 -> 电音
(6, 1004, 13, 100); -- 鬼畜全明星 -> 鬼畜

-- 6. 审核日志 (music_audit_log)
TRUNCATE TABLE music_audit_log CASCADE;
INSERT INTO music_audit_log (id, music_id, action, reason, operator_id, create_time) VALUES
(1, 1006, '2', '内容版权归属不明，请重新核对原作者授权信息', 1, now() - interval '30 minute'),
(2, 1007, '3', '应版权方要求下架', 1, now() - interval '5 day');

-- 7. 示例评论 (music_comment)
TRUNCATE TABLE music_comment CASCADE;
INSERT INTO music_comment (id, music_id, user_id, content, create_time) VALUES
(1, 1001, 3001, '太好听了，原教旨赛高！', now() - interval '2 hour'),
(2, 1001, 3002, '曼波曼波，一起曼波', now() - interval '1 hour');

-- 8. 榜单快照 (music_chart_snapshot)
TRUNCATE TABLE music_chart_snapshot CASCADE;
INSERT INTO music_chart_snapshot (id, chart_type, period_key, status, create_time) VALUES
(1, 'week', '2026W21', '1', now() - interval '7 day'),
(2, 'month', '2026M05', '1', now() - interval '1 day');

INSERT INTO music_chart_item (id, snapshot_id, music_id, rank_no, score, play_count) VALUES
(1, 1, 1002, 1, 12000, 12000),
(2, 1, 1001, 2, 5000, 5000),
(3, 2, 1002, 1, 15000, 15000);
