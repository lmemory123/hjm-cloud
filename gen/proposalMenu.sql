-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122804513751040, '标签提报申请', '3', '1', 'proposal', 'music/proposal/index', 1, 0, 'C', '0', '0', 'music:proposal:list', '#', 103, 1, now(), null, null, '标签提报申请菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122804513751041, '标签提报申请查询', 2022122804513751040, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:proposal:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122804513751042, '标签提报申请新增', 2022122804513751040, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:proposal:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122804513751043, '标签提报申请修改', 2022122804513751040, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:proposal:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122804513751044, '标签提报申请删除', 2022122804513751040, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:proposal:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122804513751045, '标签提报申请导出', 2022122804513751040, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:proposal:export',       '#', 103, 1, now(), null, null, '');

