/*
 * Converted from script/sql/ry-config.sql (MySQL) to PostgreSQL.
 * Optimized for Nacos 2.x compatibility.
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
DROP TABLE IF EXISTS "config_info_beta";
DROP TABLE IF EXISTS "config_info_tag";
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
  "encrypted_data_key" text NOT NULL DEFAULT ''
);

CREATE UNIQUE INDEX "uk_configinfo_datagrouptenant" ON "config_info" ("data_id", "group_id", "tenant_id");

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
CREATE TABLE "config_info_beta" (
  "id" bigserial PRIMARY KEY,
  "data_id" varchar(255) NOT NULL,
  "group_id" varchar(128) NOT NULL,
  "app_name" varchar(128),
  "content" text NOT NULL,
  "beta_ips" varchar(1024) NOT NULL,
  "md5" varchar(32),
  "gmt_create" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "gmt_modified" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "src_user" text,
  "src_ip" varchar(50),
  "tenant_id" varchar(128) DEFAULT '',
  "encrypted_data_key" text NOT NULL DEFAULT ''
);

CREATE UNIQUE INDEX "uk_configinfobeta_datagrouptenant" ON "config_info_beta" ("data_id", "group_id", "tenant_id");

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
CREATE TABLE "config_info_tag" (
  "id" bigserial PRIMARY KEY,
  "data_id" varchar(255) NOT NULL,
  "group_id" varchar(128) NOT NULL,
  "tenant_id" varchar(128) DEFAULT '',
  "tag_id" varchar(128) NOT NULL,
  "app_name" varchar(128),
  "content" text NOT NULL,
  "md5" varchar(32),
  "gmt_create" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "gmt_modified" timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "src_user" text,
  "src_ip" varchar(50)
);

CREATE UNIQUE INDEX "uk_configinfotag_datagrouptenanttag" ON "config_info_tag" ("data_id", "group_id", "tenant_id", "tag_id");

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

CREATE UNIQUE INDEX "uk_tenant_info_kptenantid" ON "tenant_info" ("kp", "tenant_id");
CREATE INDEX "idx_tenant_info_tenant_id" ON "tenant_info" ("tenant_id");

-- ----------------------------
-- Table structure for users
-- ----------------------------
CREATE TABLE "users" (
  "username" varchar(50) PRIMARY KEY,
  "password" varchar(500) NOT NULL,
  "enabled" boolean NOT NULL
);

-- ----------------------------
-- Table structure for roles
-- ----------------------------
CREATE TABLE "roles" (
  "username" varchar(50) NOT NULL,
  "role" varchar(50) NOT NULL
);

CREATE UNIQUE INDEX "idx_user_role" ON "roles" ("username", "role");

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
-- Initial data
-- ----------------------------
BEGIN;
INSERT INTO "users" ("username", "password", "enabled") VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', TRUE);
INSERT INTO "roles" ("username", "role") VALUES ('nacos', 'ROLE_ADMIN');
INSERT INTO "tenant_info" ("id", "kp", "tenant_id", "tenant_name", "tenant_desc", "create_source", "gmt_create", "gmt_modified") VALUES
(1, '1', 'dev', 'dev', '开发环境', NULL, 1641741261189, 1641741261189),
(2, '1', 'prod', 'prod', '生产环境', NULL, 1641741270448, 1641741287236);
COMMIT;
