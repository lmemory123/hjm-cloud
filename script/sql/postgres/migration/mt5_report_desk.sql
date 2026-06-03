-- 新增举报处理控制台菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (10100, '举报处理台', 100, 2, 'report', 'music/report/index', 1, 0, 'C', '0', '0', 'music:auditLog:list', 'material-symbols:report-outline', 1, now(), NULL, NULL, '举报处理控制台菜单');
