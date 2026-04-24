-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122830891728896, '音乐资源文件', '11807', '1', 'resource', 'music/resource/index', 1, 0, 'C', '0', '0', 'music:resource:list', '#', 103, 1, now(), null, null, '音乐资源文件菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122830891728897, '音乐资源文件查询', 2022122830891728896, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:resource:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122830891728898, '音乐资源文件新增', 2022122830891728896, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:resource:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122830891728899, '音乐资源文件修改', 2022122830891728896, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:resource:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122830891728900, '音乐资源文件删除', 2022122830891728896, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:resource:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122830891728901, '音乐资源文件导出', 2022122830891728896, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:resource:export',       '#', 103, 1, now(), null, null, '');

