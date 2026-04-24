-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122827242684416, '音乐统计(高频读写)', '11807', '1', 'stat', 'music/stat/index', 1, 0, 'C', '0', '0', 'music:stat:list', '#', 103, 1, now(), null, null, '音乐统计(高频读写)菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122827242684417, '音乐统计(高频读写)查询', 2022122827242684416, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:stat:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122827242684418, '音乐统计(高频读写)新增', 2022122827242684416, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:stat:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122827242684419, '音乐统计(高频读写)修改', 2022122827242684416, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:stat:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122827242684420, '音乐统计(高频读写)删除', 2022122827242684416, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:stat:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122827242684421, '音乐统计(高频读写)导出', 2022122827242684416, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:stat:export',       '#', 103, 1, now(), null, null, '');

