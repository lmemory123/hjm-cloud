-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122815842566144, '音乐通知日志', '11807', '1', 'notifyLog', 'music/notifyLog/index', 1, 0, 'C', '0', '0', 'music:notifyLog:list', '#', 103, 1, now(), null, null, '音乐通知日志菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122815842566145, '音乐通知日志查询', 2022122815842566144, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:notifyLog:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122815842566146, '音乐通知日志新增', 2022122815842566144, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:notifyLog:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122815842566147, '音乐通知日志修改', 2022122815842566144, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:notifyLog:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122815842566148, '音乐通知日志删除', 2022122815842566144, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:notifyLog:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122815842566149, '音乐通知日志导出', 2022122815842566144, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:notifyLog:export',       '#', 103, 1, now(), null, null, '');

