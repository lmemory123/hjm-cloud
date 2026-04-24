-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122810067009536, '音乐互动动作', '11807', '1', 'action', 'music/action/index', 1, 0, 'C', '0', '0', 'music:action:list', '#', 103, 1, now(), null, null, '音乐互动动作菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122810067009537, '音乐互动动作查询', 2022122810067009536, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:action:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122810067009538, '音乐互动动作新增', 2022122810067009536, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:action:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122810067009539, '音乐互动动作修改', 2022122810067009536, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:action:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122810067009540, '音乐互动动作删除', 2022122810067009536, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:action:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122810067009541, '音乐互动动作导出', 2022122810067009536, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:action:export',       '#', 103, 1, now(), null, null, '');

