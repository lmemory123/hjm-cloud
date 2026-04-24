-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122829054623744, '音乐原曲关联', '11807', '1', 'original', 'music/original/index', 1, 0, 'C', '0', '0', 'music:original:list', '#', 103, 1, now(), null, null, '音乐原曲关联菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122829054623745, '音乐原曲关联查询', 2022122829054623744, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:original:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122829054623746, '音乐原曲关联新增', 2022122829054623744, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:original:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122829054623747, '音乐原曲关联修改', 2022122829054623744, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:original:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122829054623748, '音乐原曲关联删除', 2022122829054623744, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:original:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122829054623749, '音乐原曲关联导出', 2022122829054623744, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:original:export',       '#', 103, 1, now(), null, null, '');

