-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122825397190656, '音乐曲库主', '11807', '1', 'music', 'music/music/index', 1, 0, 'C', '0', '0', 'music:music:list', '#', 103, 1, now(), null, null, '音乐曲库主菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122825397190657, '音乐曲库主查询', 2022122825397190656, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:music:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122825397190658, '音乐曲库主新增', 2022122825397190656, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:music:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122825397190659, '音乐曲库主修改', 2022122825397190656, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:music:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122825397190660, '音乐曲库主删除', 2022122825397190656, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:music:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122825397190661, '音乐曲库主导出', 2022122825397190656, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:music:export',       '#', 103, 1, now(), null, null, '');

