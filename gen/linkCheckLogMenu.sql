-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122813946740736, '链接检测日志', '11807', '1', 'linkCheckLog', 'music/linkCheckLog/index', 1, 0, 'C', '0', '0', 'music:linkCheckLog:list', '#', 103, 1, now(), null, null, '链接检测日志菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122813946740737, '链接检测日志查询', 2022122813946740736, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:linkCheckLog:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122813946740738, '链接检测日志新增', 2022122813946740736, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:linkCheckLog:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122813946740739, '链接检测日志修改', 2022122813946740736, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:linkCheckLog:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122813946740740, '链接检测日志删除', 2022122813946740736, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:linkCheckLog:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122813946740741, '链接检测日志导出', 2022122813946740736, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:linkCheckLog:export',       '#', 103, 1, now(), null, null, '');

