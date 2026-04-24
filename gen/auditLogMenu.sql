-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122811971223552, '音乐审核流水日志', '11807', '1', 'auditLog', 'music/auditLog/index', 1, 0, 'C', '0', '0', 'music:auditLog:list', '#', 103, 1, now(), null, null, '音乐审核流水日志菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122811971223553, '音乐审核流水日志查询', 2022122811971223552, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:auditLog:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122811971223554, '音乐审核流水日志新增', 2022122811971223552, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:auditLog:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122811971223555, '音乐审核流水日志修改', 2022122811971223552, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:auditLog:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122811971223556, '音乐审核流水日志删除', 2022122811971223552, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:auditLog:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122811971223557, '音乐审核流水日志导出', 2022122811971223552, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:auditLog:export',       '#', 103, 1, now(), null, null, '');

