-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122819650994176, '榜单快照', '11807', '1', 'chartSnapshot', 'music/chartSnapshot/index', 1, 0, 'C', '0', '0', 'music:chartSnapshot:list', '#', 103, 1, now(), null, null, '榜单快照菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122819650994177, '榜单快照查询', 2022122819650994176, '1',  '#', '', 1, 0, 'F', '0', '0', 'music:chartSnapshot:query',        '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122819650994178, '榜单快照新增', 2022122819650994176, '2',  '#', '', 1, 0, 'F', '0', '0', 'music:chartSnapshot:add',          '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122819650994179, '榜单快照修改', 2022122819650994176, '3',  '#', '', 1, 0, 'F', '0', '0', 'music:chartSnapshot:edit',         '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122819650994180, '榜单快照删除', 2022122819650994176, '4',  '#', '', 1, 0, 'F', '0', '0', 'music:chartSnapshot:remove',       '#', 103, 1, now(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(2022122819650994181, '榜单快照导出', 2022122819650994176, '5',  '#', '', 1, 0, 'F', '0', '0', 'music:chartSnapshot:export',       '#', 103, 1, now(), null, null, '');

