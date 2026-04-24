-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122821563596800, '榜单明细', '11807', '1', 'chartItem', 'music/chartItem/index', 1, 0, 'C', '0', '0', 'music:chartItem:list', '#', 103, 1, now(), null, null, '榜单明细菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122821563596801, '榜单明细查询', 2022122821563596800, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:chartItem:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122821563596802, '榜单明细新增', 2022122821563596800, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:chartItem:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122821563596803, '榜单明细修改', 2022122821563596800, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:chartItem:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122821563596804, '榜单明细删除', 2022122821563596800, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:chartItem:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122821563596805, '榜单明细导出', 2022122821563596800, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:chartItem:export',       '#', 103, 1, now(), null, null, '');

