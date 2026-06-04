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
(16, 'AI', 'genre', 1, '0', '0', '0', now()),
(17, '高音质', 'quality', 0, '1', '1', '0', now());

-- 2. 示例歌曲 (music)
-- 本地开发样本来自 /Users/momao/Music/网易云音乐，通过 docker-compose volume 暴露为 /media/local/*
TRUNCATE TABLE music CASCADE;
INSERT INTO music (
  id, title, subtitle, original_title, creator_id, creator_name, duration,
  play_count, like_count, collect_count, comment_count, share_count, download_count,
  resource_data, tags_snapshot, extend_data,
  audit_status, is_public, is_original, publish_time, create_time
) VALUES
(1001, 'Call of Silence', '本地 FLAC 样本', 'Call of Silence', 2001, 'Allen', 123, 8600, 260, 98, 18, 42, 16,
 '[{"url":"/media/local/Allen%20-%20Call%20of%20Silence.flac","posterUrl":"/images/music-cover-placeholder.svg","mediaType":"audio","qualityTier":"flac","bitrate":"lossless","container":"flac","codec":"flac","sampleRate":"48kHz","size":15740953,"duration":123}]'::jsonb,
 '[{"id":"12","name":"电音","type":"style"},{"id":"17","name":"高音质","type":"quality"}]'::jsonb,
 '{"source":"local-dev","hasVideo":false,"preloadPolicy":"metadata"}'::jsonb,
 '1', '1', '0', now() - interval '5 hour', now() - interval '5 hour'),
(1002, '玉门关', '本地 OGG 320k 样本', '玉门关', 2002, 'GZQ', 355, 7200, 190, 73, 12, 31, 11,
 '[{"url":"/media/local/GZQ%20-%20%E7%8E%89%E9%97%A8%E5%85%B3.ogg","posterUrl":"/images/music-cover-placeholder.svg","mediaType":"audio","qualityTier":"320k","bitrate":"320k","container":"ogg","codec":"vorbis","sampleRate":"44.1kHz","size":13765382,"duration":355}]'::jsonb,
 '[{"id":"15","name":"原创","type":"genre"},{"id":"17","name":"高音质","type":"quality"}]'::jsonb,
 '{"source":"local-dev","hasVideo":false,"preloadPolicy":"metadata"}'::jsonb,
 '1', '1', '1', now() - interval '9 hour', now() - interval '9 hour'),
(1003, 'Trap Queen', '本地 MP3 128k 样本', 'Trap Queen', 2003, 'MKJEightfold', 317, 6400, 144, 56, 9, 24, 8,
 '[{"url":"/media/local/MKJEightfold%20-%20Trap%20Queen.mp3","posterUrl":"/images/music-cover-placeholder.svg","mediaType":"audio","qualityTier":"128k","bitrate":"128k","container":"mp3","codec":"mp3","sampleRate":"44.1kHz","size":5099868,"duration":317}]'::jsonb,
 '[{"id":"12","name":"电音","type":"style"},{"id":"14","name":"翻唱","type":"genre"}]'::jsonb,
 '{"source":"local-dev","hasVideo":false,"preloadPolicy":"metadata"}'::jsonb,
 '1', '1', '0', now() - interval '1 day', now() - interval '1 day'),
(1004, 'Prayer X', '本地 OGG 320k 样本', 'Prayer X', 2004, 'King Gnu', 198, 5900, 132, 48, 7, 20, 6,
 '[{"url":"/media/local/King%20Gnu%20-%20Prayer%20X.ogg","posterUrl":"/images/music-cover-placeholder.svg","mediaType":"audio","qualityTier":"320k","bitrate":"320k","container":"ogg","codec":"vorbis","sampleRate":"44.1kHz","size":7670255,"duration":198}]'::jsonb,
 '[{"id":"13","name":"鬼畜","type":"genre"},{"id":"14","name":"翻唱","type":"genre"}]'::jsonb,
 '{"source":"local-dev","hasVideo":false,"preloadPolicy":"metadata"}'::jsonb,
 '1', '1', '0', now() - interval '2 day', now() - interval '2 day'),
(1005, '醒', '本地 MP3 320k 样本', '醒', 2005, '把那碗饭给我', 169, 5100, 118, 42, 6, 18, 5,
 '[{"url":"/media/local/%E6%8A%8A%E9%82%A3%E7%A2%97%E9%A5%AD%E7%BB%99%E6%88%91%20-%20%E9%86%92.mp3","posterUrl":"/images/music-cover-placeholder.svg","mediaType":"audio","qualityTier":"320k","bitrate":"320k","container":"mp3","codec":"mp3","sampleRate":"48kHz","size":6916195,"duration":169}]'::jsonb,
 '[{"id":"10","name":"原教旨","type":"genre"},{"id":"15","name":"原创","type":"genre"}]'::jsonb,
 '{"source":"local-dev","hasVideo":false,"preloadPolicy":"metadata"}'::jsonb,
 '1', '1', '1', now() - interval '3 day', now() - interval '3 day');

-- 3. 歌曲资源 (music_resource)
TRUNCATE TABLE music_resource CASCADE;
INSERT INTO music_resource (
  id, music_id, res_type, quality_tier, source_type, url, file_name, file_format, file_size, spec_info, is_primary, status, process_status, sort_order, create_time
) VALUES
(1, 1001, 'audio', 'flac', 'local', '/media/local/Allen%20-%20Call%20of%20Silence.flac', 'Allen - Call of Silence.flac', 'flac', 15740953, '{"bitrate":"lossless","codec":"flac","sampleRate":"48kHz","duration":123,"posterUrl":"/images/music-cover-placeholder.svg"}'::jsonb, '1', '0', '2', 1, now()),
(2, 1002, 'audio', '320k', 'local', '/media/local/GZQ%20-%20%E7%8E%89%E9%97%A8%E5%85%B3.ogg', 'GZQ - 玉门关.ogg', 'ogg', 13765382, '{"bitrate":"320k","codec":"vorbis","sampleRate":"44.1kHz","duration":355,"posterUrl":"/images/music-cover-placeholder.svg"}'::jsonb, '1', '0', '2', 1, now()),
(3, 1003, 'audio', '128k', 'local', '/media/local/MKJEightfold%20-%20Trap%20Queen.mp3', 'MKJEightfold - Trap Queen.mp3', 'mp3', 5099868, '{"bitrate":"128k","codec":"mp3","sampleRate":"44.1kHz","duration":317,"posterUrl":"/images/music-cover-placeholder.svg"}'::jsonb, '1', '0', '2', 1, now()),
(4, 1004, 'audio', '320k', 'local', '/media/local/King%20Gnu%20-%20Prayer%20X.ogg', 'King Gnu - Prayer X.ogg', 'ogg', 7670255, '{"bitrate":"320k","codec":"vorbis","sampleRate":"44.1kHz","duration":198,"posterUrl":"/images/music-cover-placeholder.svg"}'::jsonb, '1', '0', '2', 1, now()),
(5, 1005, 'audio', '320k', 'local', '/media/local/%E6%8A%8A%E9%82%A3%E7%A2%97%E9%A5%AD%E7%BB%99%E6%88%91%20-%20%E9%86%92.mp3', '把那碗饭给我 - 醒.mp3', 'mp3', 6916195, '{"bitrate":"320k","codec":"mp3","sampleRate":"48kHz","duration":169,"posterUrl":"/images/music-cover-placeholder.svg"}'::jsonb, '1', '0', '2', 1, now());

-- 4. 统计数据 (music_stat)
TRUNCATE TABLE music_stat CASCADE;
INSERT INTO music_stat (music_id, play_count, like_count, collect_count, comment_count, score, create_time) VALUES
(1001, 8600, 260, 98, 18, 960, now()),
(1002, 7200, 190, 73, 12, 890, now()),
(1003, 6400, 144, 56, 9, 820, now()),
(1004, 5900, 132, 48, 7, 790, now()),
(1005, 5100, 118, 42, 6, 760, now());

-- 5. 标签关联 (music_tag_rel)
TRUNCATE TABLE music_tag_rel CASCADE;
INSERT INTO music_tag_rel (id, music_id, tag_id, tag_weight) VALUES
(1, 1001, 12, 100),
(2, 1001, 17, 80),
(3, 1002, 15, 100),
(4, 1002, 17, 80),
(5, 1003, 12, 100),
(6, 1003, 14, 80),
(7, 1004, 13, 100),
(8, 1004, 14, 80),
(9, 1005, 10, 100),
(10, 1005, 15, 80);

-- 6. 审核日志 (music_audit_log)
TRUNCATE TABLE music_audit_log CASCADE;
INSERT INTO music_audit_log (id, music_id, action, reason, operator_id, create_time) VALUES
(1, 1001, '1', '本地开发样本导入并通过', 1, now() - interval '5 hour'),
(2, 1002, '1', '本地开发样本导入并通过', 1, now() - interval '9 hour'),
(3, 1003, '1', '本地开发样本导入并通过', 1, now() - interval '1 day'),
(4, 1004, '1', '本地开发样本导入并通过', 1, now() - interval '2 day'),
(5, 1005, '1', '本地开发样本导入并通过', 1, now() - interval '3 day');

-- 7. 示例评论 (music_comment)
TRUNCATE TABLE music_comment CASCADE;
INSERT INTO music_comment (id, music_id, user_id, content, create_time) VALUES
(1, 1001, 3001, '先用真实音频验播放器链路。', now() - interval '2 hour'),
(2, 1002, 3002, '这首可以作为二创样本池。', now() - interval '1 hour'),
(3, 1005, 3003, '320k MP3 播放正常的话，后面再接码率切换。', now() - interval '30 minute');

-- 8. 榜单快照 (music_chart_snapshot)
TRUNCATE TABLE music_chart_item CASCADE;
TRUNCATE TABLE music_chart_snapshot CASCADE;
INSERT INTO music_chart_snapshot (id, chart_type, period_key, status, create_time) VALUES
(1, 'week', '2026W21', 'published', now() - interval '7 day'),
(2, 'month', '2026M05', 'published', now() - interval '1 day');

INSERT INTO music_chart_item (id, snapshot_id, music_id, rank_no, score, play_count) VALUES
(1, 1, 1001, 1, 960, 8600),
(2, 1, 1002, 2, 890, 7200),
(3, 1, 1003, 3, 820, 6400),
(4, 1, 1004, 4, 790, 5900),
(5, 1, 1005, 5, 760, 5100),
(6, 2, 1001, 1, 960, 8600),
(7, 2, 1002, 2, 890, 7200),
(8, 2, 1005, 3, 760, 5100);
