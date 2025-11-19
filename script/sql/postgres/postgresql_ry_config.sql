/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS "config_info";
CREATE TABLE "config_info" (
  "id" bigserial NOT NULL,
  "data_id" varchar(255)  NOT NULL,
  "group_id" varchar(255) ,
  "content" text  NOT NULL,
  "md5" varchar(32) ,
  "gmt_create" timestamp(6) NOT NULL,
  "gmt_modified" timestamp(6) NOT NULL,
  "src_user" text ,
  "src_ip" varchar(20) ,
  "app_name" varchar(128) ,
  "tenant_id" varchar(128) ,
  "c_desc" varchar(256) ,
  "c_use" varchar(64) ,
  "effect" varchar(64) ,
  "type" varchar(64) ,
  "c_schema" text ,
  "encrypted_data_key" text  NOT NULL
)
;

COMMENT ON COLUMN "config_info"."id" IS 'id';
COMMENT ON COLUMN "config_info"."data_id" IS 'data_id';
COMMENT ON COLUMN "config_info"."content" IS 'content';
COMMENT ON COLUMN "config_info"."md5" IS 'md5';
COMMENT ON COLUMN "config_info"."gmt_create" IS '创建时间';
COMMENT ON COLUMN "config_info"."gmt_modified" IS '修改时间';
COMMENT ON COLUMN "config_info"."src_user" IS 'source user';
COMMENT ON COLUMN "config_info"."src_ip" IS 'source ip';
COMMENT ON COLUMN "config_info"."tenant_id" IS '租户字段';
COMMENT ON COLUMN "config_info"."encrypted_data_key" IS '秘钥';
COMMENT ON TABLE "config_info" IS 'config_info';

-- ----------------------------
-- Records of config_info
-- ----------------------------
BEGIN;
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (1, 'application-common.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:18:55', '2022-01-09 15:18:55', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '通用配置基础配置', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (2, 'datasource.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:19:07', '2022-01-09 15:19:07', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '数据源配置', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (3, 'ruoyi-gateway.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:19:43', '2022-01-09 15:22:42', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '网关模块', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (4, 'ruoyi-auth.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:19:43', '2022-01-09 15:22:29', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '认证中心', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (5, 'ruoyi-monitor.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:18', '2022-01-09 15:22:15', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '监控中心', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (6, 'ruoyi-system.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:18', '2022-01-09 15:22:03', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '系统模块', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (7, 'ruoyi-gen.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:18', '2022-01-09 15:21:51', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '代码生成', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (8, 'ruoyi-job.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:18', '2022-01-09 15:21:36', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '定时任务', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (9, 'ruoyi-resource.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:35', '2022-01-09 15:21:21', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '文件服务', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (10, 'ruoyi-workflow.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:20:35', '2022-01-09 15:21:21', NULL, '0:0:0:0:0:0:0:1', '', 'dev', '工作流服务', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (12, 'seata-server.properties', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:21:02', '2022-01-09 15:21:02', NULL, '0:0:0:0:0:0:0:1', '', 'dev', 'seata配置文件', NULL, NULL, 'properties', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (14, 'ruoyi-snailjob-server.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:21:02', '2022-01-09 15:21:02', NULL, '0:0:0:0:0:0:0:1', '', 'dev', 'SJ定时任务控制台', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (101, 'application-common.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '通用配置基础配置', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (102, 'datasource.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '数据源配置', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (103, 'ruoyi-gateway.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '网关模块', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (104, 'ruoyi-auth.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '认证中心', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (105, 'ruoyi-monitor.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '监控中心', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (106, 'ruoyi-system.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '系统模块', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (107, 'ruoyi-gen.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '代码生成', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (108, 'ruoyi-job.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '定时任务', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (109, 'ruoyi-resource.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '文件服务', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (110, 'ruoyi-workflow.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:23:00', '2022-01-09 15:23:00', NULL, '0:0:0:0:0:0:0:1', '', 'prod', '工作流服务', NULL, NULL, 'yaml', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (112, 'seata-server.properties', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:21:02', '2022-01-09 15:21:02', NULL, '0:0:0:0:0:0:0:1', '', 'prod', 'seata配置文件', NULL, NULL, 'properties', NULL, '');
INSERT INTO "config_info" ("id", "data_id", "group_id", "content", "md5", "gmt_create", "gmt_modified", "src_user", "src_ip", "app_name", "tenant_id", "c_desc", "c_use", "effect", "type", "c_schema", "encrypted_data_key") VALUES (114, 'ruoyi-snailjob-server.yml', 'DEFAULT_GROUP', '# 将项目路径：config/下对应文件中内容复制到此处', '2944a25cb97926efcaa43b3ad7a64cf0', '2022-01-09 15:21:02', '2022-01-09 15:21:02', NULL, '0:0:0:0:0:0:0:1', '', 'prod', 'SJ定时任务控制台', NULL, NULL, 'yaml', NULL, '');
COMMIT;

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS "his_config_info";
CREATE TABLE "his_config_info" (
  "nid" bigserial NOT NULL,
  "id" bigint NOT NULL,
  "data_id" varchar(255) NOT NULL,
  "group_id" varchar(255) NOT NULL,
  "tenant_id" varchar(128) DEFAULT '',
  "app_name" varchar(128),
  "content" text,
  "md5" varchar(32) DEFAULT NULL,
  "gmt_create" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "gmt_modified" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "src_user" text,
  "src_ip" varchar(20) DEFAULT NULL,
  "op_type" char(10) DEFAULT NULL,
  "publish_type" varchar(50) DEFAULT 'formal',
  "gray_name" varchar(50) DEFAULT NULL,
  "ext_info" text,
  "encrypted_data_key" text NOT NULL
);

COMMENT ON COLUMN "his_config_info"."nid" IS 'nid, 主键';
COMMENT ON COLUMN "his_config_info"."id" IS '配置ID';
COMMENT ON COLUMN "his_config_info"."data_id" IS 'data_id';
COMMENT ON COLUMN "his_config_info"."group_id" IS 'group_id';
COMMENT ON COLUMN "his_config_info"."tenant_id" IS 'tenant_id';
COMMENT ON COLUMN "his_config_info"."app_name" IS 'app_name';
COMMENT ON COLUMN "his_config_info"."content" IS 'content';
COMMENT ON COLUMN "his_config_info"."md5" IS 'md5';
COMMENT ON COLUMN "his_config_info"."gmt_create" IS '创建时间';
COMMENT ON COLUMN "his_config_info"."gmt_modified" IS '修改时间';
COMMENT ON COLUMN "his_config_info"."src_user" IS 'source user';
COMMENT ON COLUMN "his_config_info"."src_ip" IS 'source ip';
COMMENT ON COLUMN "his_config_info"."op_type" IS '操作类型';
COMMENT ON COLUMN "his_config_info"."publish_type" IS '发布类型';
COMMENT ON COLUMN "his_config_info"."gray_name" IS '灰度名称';
COMMENT ON COLUMN "his_config_info"."ext_info" IS '扩展信息';
COMMENT ON COLUMN "his_config_info"."encrypted_data_key" IS '秘钥';
COMMENT ON TABLE "his_config_info" IS '多租户改造';

-- ----------------------------
-- Primary Key structure for table his_config_info
-- ----------------------------
ALTER TABLE "his_config_info" ADD CONSTRAINT "his_config_info_pkey" PRIMARY KEY ("nid");

-- ----------------------------
-- Indexes structure for table his_config_info
-- ----------------------------
CREATE INDEX "idx_his_config_info_data_id" ON "his_config_info" ("data_id");
CREATE INDEX "idx_his_config_info_tenant_id" ON "his_config_info" ("tenant_id");
CREATE INDEX "idx_his_config_info_gmt_create" ON "his_config_info" ("gmt_create");
CREATE INDEX "idx_his_config_info_gmt_modified" ON "his_config_info" ("gmt_modified");

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS "tenant_info";
CREATE TABLE "tenant_info" (
  "id" bigserial NOT NULL,
  "kp" varchar(128)  NOT NULL,
  "tenant_id" varchar(128) ,
  "tenant_name" varchar(128) ,
  "tenant_desc" varchar(256) ,
  "create_source" varchar(32) ,
  "gmt_create" int8 NOT NULL,
  "gmt_modified" int8 NOT NULL
)
;
COMMENT ON COLUMN "tenant_info"."id" IS 'id';
COMMENT ON COLUMN "tenant_info"."kp" IS 'kp';
COMMENT ON COLUMN "tenant_info"."tenant_id" IS 'tenant_id';
COMMENT ON COLUMN "tenant_info"."tenant_name" IS 'tenant_name';
COMMENT ON COLUMN "tenant_info"."tenant_desc" IS 'tenant_desc';
COMMENT ON COLUMN "tenant_info"."create_source" IS 'create_source';
COMMENT ON COLUMN "tenant_info"."gmt_create" IS '创建时间';
COMMENT ON COLUMN "tenant_info"."gmt_modified" IS '修改时间';
COMMENT ON TABLE "tenant_info" IS 'tenant_info';

-- ----------------------------
-- Records of tenant_info
-- ----------------------------
BEGIN;
INSERT INTO "tenant_info" ("id", "kp", "tenant_id", "tenant_name", "tenant_desc", "create_source", "gmt_create", "gmt_modified") VALUES (1, '1', 'dev', 'dev', '开发环境', NULL, 1641741261189, 1641741261189);
INSERT INTO "tenant_info" ("id", "kp", "tenant_id", "tenant_name", "tenant_desc", "create_source", "gmt_create", "gmt_modified") VALUES (2, '1', 'prod', 'prod', '生产环境', NULL, 1641741270448, 1641741287236);
COMMIT;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS "users";
CREATE TABLE "users" (
  "username" varchar(50)  NOT NULL,
  "password" varchar(500)  NOT NULL,
  "enabled" boolean NOT NULL
)
;

-- ----------------------------
-- Records of users
-- ----------------------------
BEGIN;
INSERT INTO "users" VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);
COMMIT;

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS "roles";
CREATE TABLE "roles" (
  "username" varchar(50)  NOT NULL,
  "role" varchar(50)  NOT NULL
)
;

-- ----------------------------
-- Records of roles
-- ----------------------------
BEGIN;
INSERT INTO "roles" VALUES ('nacos', 'ROLE_ADMIN');
COMMIT;

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS "permissions";
CREATE TABLE "permissions" (
  "role" varchar(50)  NOT NULL,
  "resource" varchar(512)  NOT NULL,
  "action" varchar(8)  NOT NULL
)
;

-- ----------------------------
-- Records of permissions
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Indexes structure for table config_info
-- ----------------------------
CREATE UNIQUE INDEX "uk_configinfo_datagrouptenant" ON "config_info" ("data_id","group_id","tenant_id");

-- ----------------------------
-- Primary Key structure for table config_info
-- ----------------------------
ALTER TABLE "config_info" ADD CONSTRAINT "config_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table tenant_info
-- ----------------------------
CREATE UNIQUE INDEX "uk_tenant_info_kptenantid" ON "tenant_info" USING btree (
  "kp",
  "tenant_id"
);

-- ----------------------------
-- Primary Key structure for table tenant_info
-- ----------------------------
ALTER TABLE "tenant_info" ADD CONSTRAINT "tenant_info_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table users
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table users
-- ----------------------------
ALTER TABLE "users" ADD CONSTRAINT "users_pkey" PRIMARY KEY ("username");

-- ----------------------------
-- Indexes structure for table roles
-- ----------------------------
CREATE UNIQUE INDEX "uk_username_role" ON "roles" USING btree (
  "username",
  "role"
);

-- ----------------------------
-- Indexes structure for table permissions
-- ----------------------------
CREATE UNIQUE INDEX "uk_role_permission" ON "permissions" USING btree (
  "role",
  "resource",
  "action"
);

-- ----------------------------
-- Sync sequences after data insertion
-- ----------------------------
-- Sync config_info sequence
SELECT setval(pg_get_serial_sequence('"config_info"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "config_info";

-- Sync tenant_info sequence
SELECT setval(pg_get_serial_sequence('"tenant_info"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "tenant_info";

-- Sync his_config_info sequence
SELECT setval(pg_get_serial_sequence('"his_config_info"', 'nid'), GREATEST(COALESCE(MAX("nid"), 0), 1)) FROM "his_config_info";

-- Sync config_info_aggr sequence
SELECT setval(pg_get_serial_sequence('"config_info_aggr"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "config_info_aggr";

-- Sync config_info_beta sequence
SELECT setval(pg_get_serial_sequence('"config_info_beta"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "config_info_beta";

-- Sync config_info_tag sequence
SELECT setval(pg_get_serial_sequence('"config_info_tag"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "config_info_tag";

-- Sync config_tags_relation sequence
SELECT setval(pg_get_serial_sequence('"config_tags_relation"', 'nid'), GREATEST(COALESCE(MAX("nid"), 0), 1)) FROM "config_tags_relation";

-- Sync group_capacity sequence
SELECT setval(pg_get_serial_sequence('"group_capacity"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "group_capacity";

-- Sync tenant_capacity sequence
SELECT setval(pg_get_serial_sequence('"tenant_capacity"', 'id'), GREATEST(COALESCE(MAX("id"), 0), 1)) FROM "tenant_capacity";
