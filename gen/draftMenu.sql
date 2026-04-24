-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122823522336768, '投稿草稿', '11807', '1', 'draft', 'music/draft/index', 1, 0, 'C', '0', '0', 'music:draft:list', '#', 103, 1, now(), null, null, '投稿草稿菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122823522336769, '投稿草稿查询', 2022122823522336768, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:draft:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122823522336770, '投稿草稿新增', 2022122823522336768, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:draft:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122823522336771, '投稿草稿修改', 2022122823522336768, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:draft:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122823522336772, '投稿草稿删除', 2022122823522336768, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:draft:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122823522336773, '投稿草稿导出', 2022122823522336768, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:draft:export',       '#', 103, 1, now(), null, null, '');

