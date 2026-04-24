-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122808234098688, '音乐评论', '11807', '1', 'comment', 'music/comment/index', 1, 0, 'C', '0', '0', 'music:comment:list', '#', 103, 1, now(), null, null, '音乐评论菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122808234098689, '音乐评论查询', 2022122808234098688, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:comment:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122808234098690, '音乐评论新增', 2022122808234098688, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:comment:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122808234098691, '音乐评论修改', 2022122808234098688, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:comment:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122808234098692, '音乐评论删除', 2022122808234098688, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:comment:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122808234098693, '音乐评论导出', 2022122808234098688, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:comment:export',       '#', 103, 1, now(), null, null, '');

