/*
 * Converted from script/sql/ry-config.sql (MySQL) to PostgreSQL.
 */

-- Drop tables in reverse dependency/order-friendly sequence
DROP TABLE IF EXISTS "permissions";
DROP TABLE IF EXISTS "roles";
DROP TABLE IF EXISTS "users";
DROP TABLE IF EXISTS "tenant_info";
DROP TABLE IF EXISTS "tenant_capacity";
DROP TABLE IF EXISTS "his_config_info";
DROP TABLE IF EXISTS "group_capacity";
DROP TABLE IF EXISTS "config_tags_relation";
DROP TABLE IF EXISTS "config_info_gray";
DROP TABLE IF EXISTS "config_info";

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
CREATE TABLE "config_info" (
  "id" bigserial PRIMARY KEY,
  "data_id" varchar(255) NOT NULL,
  "group_id" varchar(128),
  "content" text NOT NULL,
  "md5" varchar(32),
  "gmt_create" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "gmt_modified" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "src_user" text,
  "src_ip" varchar(50),
  "app_name" varchar(128),
  "tenant_id" varchar(128) DEFAULT '',
  "c_desc" varchar(256),
  "c_use" varchar(64),
  "effect" varchar(64),
  "type" varchar(64),
  "c_schema" text,
  "encrypted_data_key" text NOT NULL
);

COMMENT ON TABLE "config_info" IS 'config_info';
COMMENT ON COLUMN "config_info"."id" IS 'id';
COMMENT ON COLUMN "config_info"."data_id" IS 'data_id';
COMMENT ON COLUMN "config_info"."group_id" IS 'group_id';
COMMENT ON COLUMN "config_info"."content" IS 'content';
COMMENT ON COLUMN "config_info"."md5" IS 'md5';
COMMENT ON COLUMN "config_info"."gmt_create" IS '创建时间';
COMMENT ON COLUMN "config_info"."gmt_modified" IS '修改时间';
COMMENT ON COLUMN "config_info"."src_user" IS 'source user';
COMMENT ON COLUMN "config_info"."src_ip" IS 'source ip';
COMMENT ON COLUMN "config_info"."app_name" IS 'app_name';
COMMENT ON COLUMN "config_info"."tenant_id" IS '租户字段';
COMMENT ON COLUMN "config_info"."c_desc" IS 'configuration description';
COMMENT ON COLUMN "config_info"."c_use" IS 'configuration usage';
COMMENT ON COLUMN "config_info"."effect" IS '配置生效的描述';
COMMENT ON COLUMN "config_info"."type" IS '配置的类型';
COMMENT ON COLUMN "config_info"."c_schema" IS '配置的模式';
COMMENT ON COLUMN "config_info"."encrypted_data_key" IS '密钥';

CREATE UNIQUE INDEX "uk_configinfo_datagrouptenant" ON "config_info" ("data_id", "group_id", "tenant_id");

BEGIN;
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES
(1, 'application-common.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:18:55', '2022-01-09 15:18:55', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '通用配置基础配置', NULL, NULL, 'yaml', NULL, ''),
(2, 'datasource.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:19:07', '2022-01-09 15:19:07', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '数据源配置', NULL, NULL, 'yaml', NULL, ''),
(3, 'ruoyi-gateway.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:19:43', '2022-01-09 15:22:42', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '网关模块', NULL, NULL, 'yaml', NULL, ''),
(4, 'ruoyi-auth.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:19:43', '2022-01-09 15:22:29', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '认证中心', NULL, NULL, 'yaml', NULL, ''),
(5, 'ruoyi-monitor.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:18', '2022-01-09 15:22:15', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '监控中心', NULL, NULL, 'yaml', NULL, ''),
(6, 'ruoyi-system.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:18', '2022-01-09 15:22:03', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '系统模块', NULL, NULL, 'yaml', NULL, ''),
(7, 'ruoyi-gen.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:18', '2022-01-09 15:21:51', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '代码生成', NULL, NULL, 'yaml', NULL, ''),
(8, 'ruoyi-job.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:18', '2022-01-09 15:21:36', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '定时任务', NULL, NULL, 'yaml', NULL, ''),
(9, 'ruoyi-resource.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:35', '2022-01-09 15:21:21', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '文件服务', NULL, NULL, 'yaml', NULL, ''),
(10, 'ruoyi-workflow.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:35', '2022-01-09 15:21:21', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '工作流服务', NULL, NULL, 'yaml', NULL, ''),
(12, 'seata-server.properties', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:21:02', '2022-01-09 15:21:02', NULL, '0:0:0:0:0:0:0:1', '', 'dev', 'seata配置文件', NULL, NULL, 'properties', NULL, ''),
(14, 'ruoyi-snailjob-server.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:21:02', '2022-01-09 15:21:02', NULL, '0:0:0:0:0:0:0:1', '', 'dev', 'SJ定时任务控制台', NULL, NULL, 'yaml', NULL, ''),
(101, 'application-common.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '通用配置基础配置', NULL, NULL, 'yaml', NULL, ''),
(102, 'datasource.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '数据源配置', NULL, NULL, 'yaml', NULL, ''),
(103, 'ruoyi-gateway.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '网关模块', NULL, NULL, 'yaml', NULL, ''),
(104, 'ruoyi-auth.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '认证中心', NULL, NULL, 'yaml', NULL, ''),
(105, 'ruoyi-monitor.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '监控中心', NULL, NULL, 'yaml', NULL, ''),
(106, 'ruoyi-system.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '系统模块', NULL, NULL, 'yaml', NULL, ''),
(107, 'ruoyi-gen.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '代码生成', NULL, NULL, 'yaml', NULL, ''),
(108, 'ruoyi-job.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '定时任务', NULL, NULL, 'yaml', NULL, ''),
(109, 'ruoyi-resource.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '文件服务', NULL, NULL, 'yaml', NULL, ''),
(110, 'ruoyi-workflow.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '工作流服务', NULL, NULL, 'yaml', NULL, ''),
(112, 'seata-server.properties', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:21:02', '2022-01-09 15:21:02', NULL, '0:0:0:0:0:0:0:1', '', 'prod', 'seata配置文件', NULL, NULL, 'properties', NULL, ''),
(114, 'ruoyi-snailjob-server.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:21:02', '2022-01-09 15:21:02', NULL, '0:0:0:0:0:0:0:1', '', 'prod', 'SJ定时任务控制台', NULL, NULL, 'yaml', NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for config_info_gray
-- ----------------------------
CREATE TABLE "config_info_gray" (
  "id" bigserial PRIMARY KEY,
  "data_id" varchar(255) NOT NULL,
  "group_id" varchar(128) NOT NULL,
  "content" text NOT NULL,
  "md5" varchar(32),
  "src_user" text,
  "src_ip" varchar(100),
  "gmt_create" timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "gmt_modified" timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "app_name" varchar(128),
  "tenant_id" varchar(128) DEFAULT '',
  "gray_name" varchar(128) NOT NULL,
  "gray_rule" text NOT NULL,
  "encrypted_data_key" varchar(256) NOT NULL DEFAULT ''
);

COMMENT ON TABLE "config_info_gray" IS 'config_info_gray';
CREATE UNIQUE INDEX "uk_configinfogray_datagrouptenantgray" ON "config_info_gray" ("data_id", "group_id", "tenant_id", "gray_name");
CREATE INDEX "idx_cfg_gray_dataid_gmt_modified" ON "config_info_gray" ("data_id", "gmt_modified");
CREATE INDEX "idx_cfg_gray_gmt_modified" ON "config_info_gray" ("gmt_modified");

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
CREATE TABLE "config_tags_relation" (
  "id" bigint NOT NULL,
  "tag_name" varchar(128) NOT NULL,
  "tag_type" varchar(64),
  "data_id" varchar(255) NOT NULL,
  "group_id" varchar(128) NOT NULL,
  "tenant_id" varchar(128) DEFAULT '',
  "nid" bigserial PRIMARY KEY
);

COMMENT ON TABLE "config_tags_relation" IS 'config_tag_relation';
CREATE UNIQUE INDEX "uk_configtagrelation_configidtag" ON "config_tags_relation" ("id", "tag_name", "tag_type");
CREATE INDEX "idx_config_tags_tenant_id" ON "config_tags_relation" ("tenant_id");

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
CREATE TABLE "group_capacity" (
  "id" bigserial PRIMARY KEY,
  "group_id" varchar(128) NOT NULL DEFAULT '',
  "quota" integer NOT NULL DEFAULT 0,
  "usage" integer NOT NULL DEFAULT 0,
  "max_size" integer NOT NULL DEFAULT 0,
  "max_aggr_count" integer NOT NULL DEFAULT 0,
  "max_aggr_size" integer NOT NULL DEFAULT 0,
  "max_history_count" integer NOT NULL DEFAULT 0,
  "gmt_create" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "gmt_modified" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE "group_capacity" IS '集群、各Group容量信息表';
CREATE UNIQUE INDEX "uk_group_id" ON "group_capacity" ("group_id");

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
CREATE TABLE "his_config_info" (
  "id" bigint NOT NULL,
  "nid" bigserial PRIMARY KEY,
  "data_id" varchar(255) NOT NULL,
  "group_id" varchar(128) NOT NULL,
  "app_name" varchar(128),
  "content" text NOT NULL,
  "md5" varchar(32),
  "gmt_create" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "gmt_modified" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "src_user" text,
  "src_ip" varchar(50),
  "op_type" char(10),
  "tenant_id" varchar(128) DEFAULT '',
  "encrypted_data_key" varchar(1024) NOT NULL DEFAULT '',
  "publish_type" varchar(50) DEFAULT 'formal',
  "gray_name" varchar(50),
  "ext_info" text
);

COMMENT ON TABLE "his_config_info" IS '多租户改造';
CREATE INDEX "idx_his_config_info_gmt_create" ON "his_config_info" ("gmt_create");
CREATE INDEX "idx_his_config_info_gmt_modified" ON "his_config_info" ("gmt_modified");
CREATE INDEX "idx_his_config_info_data_id" ON "his_config_info" ("data_id");

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
CREATE TABLE "tenant_capacity" (
  "id" bigserial PRIMARY KEY,
  "tenant_id" varchar(128) NOT NULL DEFAULT '',
  "quota" integer NOT NULL DEFAULT 0,
  "usage" integer NOT NULL DEFAULT 0,
  "max_size" integer NOT NULL DEFAULT 0,
  "max_aggr_count" integer NOT NULL DEFAULT 0,
  "max_aggr_size" integer NOT NULL DEFAULT 0,
  "max_history_count" integer NOT NULL DEFAULT 0,
  "gmt_create" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "gmt_modified" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE "tenant_capacity" IS '租户容量信息表';
CREATE UNIQUE INDEX "uk_tenant_id" ON "tenant_capacity" ("tenant_id");

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
CREATE TABLE "tenant_info" (
  "id" bigserial PRIMARY KEY,
  "kp" varchar(128) NOT NULL,
  "tenant_id" varchar(128) DEFAULT '',
  "tenant_name" varchar(128) DEFAULT '',
  "tenant_desc" varchar(256),
  "create_source" varchar(32),
  "gmt_create" bigint NOT NULL,
  "gmt_modified" bigint NOT NULL
);

COMMENT ON TABLE "tenant_info" IS 'tenant_info';
COMMENT ON COLUMN "tenant_info"."id" IS 'id';
COMMENT ON COLUMN "tenant_info"."kp" IS 'kp';
COMMENT ON COLUMN "tenant_info"."tenant_id" IS 'tenant_id';
COMMENT ON COLUMN "tenant_info"."tenant_name" IS 'tenant_name';
COMMENT ON COLUMN "tenant_info"."tenant_desc" IS 'tenant_desc';
COMMENT ON COLUMN "tenant_info"."create_source" IS 'create_source';
COMMENT ON COLUMN "tenant_info"."gmt_create" IS '创建时间';
COMMENT ON COLUMN "tenant_info"."gmt_modified" IS '修改时间';

CREATE UNIQUE INDEX "uk_tenant_info_kptenantid" ON "tenant_info" ("kp", "tenant_id");
CREATE INDEX "idx_tenant_info_tenant_id" ON "tenant_info" ("tenant_id");

BEGIN;
INSERT INTO "tenant_info" ("id", "kp", "tenant_id", "tenant_name", "tenant_desc", "create_source", "gmt_create", "gmt_modified") VALUES
(1, '1', 'dev', 'dev', '开发环境', NULL, 1641741261189, 1641741261189),
(2, '1', 'prod', 'prod', '生产环境', NULL, 1641741270448, 1641741287236);
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
CREATE TABLE "users" (
  "username" varchar(50) PRIMARY KEY,
  "password" varchar(500) NOT NULL,
  "enabled" boolean NOT NULL
);

BEGIN;
INSERT INTO "users" ("username", "password", "enabled") VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);
COMMIT;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
CREATE TABLE "roles" (
  "username" varchar(50) NOT NULL,
  "role" varchar(50) NOT NULL
);

CREATE UNIQUE INDEX "idx_user_role" ON "roles" ("username", "role");

BEGIN;
INSERT INTO "roles" ("username", "role") VALUES ('nacos', 'ROLE_ADMIN');
COMMIT;

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
CREATE TABLE "permissions" (
  "role" varchar(50) NOT NULL,
  "resource" varchar(128) NOT NULL,
  "action" varchar(8) NOT NULL
);

CREATE UNIQUE INDEX "uk_role_permission" ON "permissions" ("role", "resource", "action");

-- ----------------------------
-- Sync sequences after data insertion
-- ----------------------------
SELECT setval(pg_get_serial_sequence('"config_info"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "config_info";
SELECT setval(pg_get_serial_sequence('"config_info_gray"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "config_info_gray";
SELECT setval(pg_get_serial_sequence('"config_tags_relation"', 'nid'), GREATEST(COALESCE(MAX("nid"), 0), 1)) FROM "config_tags_relation";
SELECT setval(pg_get_serial_sequence('"group_capacity"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "group_capacity";
SELECT setval(pg_get_serial_sequence('"his_config_info"', 'nid'), GREATEST(COALESCE(MAX("nid"), 0), 1)) FROM "his_config_info";
SELECT setval(pg_get_serial_sequence('"tenant_capacity"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "tenant_capacity";
SELECT setval(pg_get_serial_sequence('"tenant_info"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "tenant_info";
