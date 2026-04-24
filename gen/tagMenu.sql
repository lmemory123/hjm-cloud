-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122832741416960, '标签字典', '11807', '1', 'tag', 'music/tag/index', 1, 0, 'C', '0', '0', 'music:tag:list', '#', 103, 1, now(), null, null, '标签字典菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122832741416961, '标签字典查询', 2022122832741416960, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:tag:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122832741416962, '标签字典新增', 2022122832741416960, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:tag:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122832741416963, '标签字典修改', 2022122832741416960, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:tag:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122832741416964, '标签字典删除', 2022122832741416960, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:tag:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122832741416965, '标签字典导出', 2022122832741416960, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:tag:export',       '#', 103, 1, now(), null, null, '');

