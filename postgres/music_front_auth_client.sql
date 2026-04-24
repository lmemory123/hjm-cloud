-- 前台登录专用 client 配置
-- 使用说明：
-- 1. 执行本 SQL，新增前台专用 sys_client
-- 2. 将 client_id 同步到 nacos 的 ruoyi-auth.yml:
--    security.front-auth.client-id: 7f57f2e3c3f14d15a7c4dd8b7e69a241
-- 3. 重启 ruoyi-auth 与 gateway

delete from sys_client where client_id = '7f57f2e3c3f14d15a7c4dd8b7e69a241';

insert into sys_client (
    id,
    client_id,
    client_key,
    client_secret,
    grant_type,
    device_type,
    active_timeout,
    timeout,
    status,
    del_flag,
    create_dept,
    create_by,
    create_time,
    update_by,
    update_time
) values (
    coalesce((select max(id) + 1 from sys_client), 3),
    '7f57f2e3c3f14d15a7c4dd8b7e69a241',
    'music-web',
    'music-web123',
    'password',
    'web',
    1800,
    604800,
    '0',
    '0',
    103,
    1,
    now(),
    1,
    now()
);

-- 可选核对
select id, client_id, client_key, grant_type, device_type, status
from sys_client
where client_id = '7f57f2e3c3f14d15a7c4dd8b7e69a241';
