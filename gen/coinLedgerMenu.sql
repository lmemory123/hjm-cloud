-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122817734197248, '哈气金流水', '11807', '1', 'coinLedger', 'music/coinLedger/index', 1, 0, 'C', '0', '0', 'music:coinLedger:list', '#', 103, 1, now(), null, null, '哈气金流水菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122817734197249, '哈气金流水查询', 2022122817734197248, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:coinLedger:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122817734197250, '哈气金流水新增', 2022122817734197248, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:coinLedger:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122817734197251, '哈气金流水修改', 2022122817734197248, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:coinLedger:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122817734197252, '哈气金流水删除', 2022122817734197248, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:coinLedger:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122817734197253, '哈气金流水导出', 2022122817734197248, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:coinLedger:export',       '#', 103, 1, now(), null, null, '');

