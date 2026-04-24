-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122806396993536, '音乐标签关联', '11807', '1', 'tagRel', 'music/tagRel/index', 1, 0, 'C', '0', '0', 'music:tagRel:list', '#', 103, 1, now(), null, null, '音乐标签关联菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122806396993537, '音乐标签关联查询', 2022122806396993536, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:tagRel:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122806396993538, '音乐标签关联新增', 2022122806396993536, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:tagRel:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122806396993539, '音乐标签关联修改', 2022122806396993536, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:tagRel:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122806396993540, '音乐标签关联删除', 2022122806396993536, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:tagRel:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122806396993541, '音乐标签关联导出', 2022122806396993536, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:tagRel:export',       '#', 103, 1, now(), null, null, '');

