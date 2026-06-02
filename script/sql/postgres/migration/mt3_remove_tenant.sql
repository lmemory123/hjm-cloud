-- --------------------------------------------------------------------------------
-- 哈基哈米去多租户存量数据迁移脚本 (PostgreSQL)
-- 执行前提：已部署 MT1, MT2 代码，应用不再读写业务 tenant_id
-- --------------------------------------------------------------------------------

-- 1. 删除租户管理相关表
DROP TABLE IF EXISTS "sys_tenant";
DROP TABLE IF EXISTS "sys_tenant_package";

-- 2. 删除租户管理相关菜单 (菜单ID通常为 6 和 121, 122 等)
DELETE FROM sys_menu WHERE menu_id IN (6, 121, 122);
DELETE FROM sys_menu WHERE parent_id IN (6, 121, 122);

-- 3. 删除业务表中的 tenant_id 列 (如果存在)
DO $$ 
DECLARE 
    r RECORD;
BEGIN
    FOR r IN (
        SELECT table_name, column_name 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND column_name = 'tenant_id'
          AND table_name NOT IN ('config_info', 'config_info_beta', 'config_info_tag', 'config_info_gray', 'config_tags_relation', 'his_config_info', 'tenant_info', 'tenant_capacity')
    ) LOOP
        EXECUTE 'ALTER TABLE ' || quote_ident(r.table_name) || ' DROP COLUMN ' || quote_ident(r.column_name);
    END LOOP;
END $$;

-- 4. 清理登录日志和操作日志中的租户数据 (可选，因为列已被删除)
-- 5. 重置所有序列 (如果需要)
