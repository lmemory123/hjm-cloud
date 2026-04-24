# Workspace Snapshot - 2026-02-01

This file captures the current state of work so the workspace can be restored quickly.

## 1) Scope / Summary
- Added RustFS (S3-compatible) support for OSS, including direct-upload presign flow and multipart presign flow.
- Added portal-facing OSS endpoints for frontend direct upload, plus multipart init/complete/abort.
- Wrote multipart upload usage doc: `docs/oss-multipart-upload.md`.
- Music module requirements doc exists: `requirements-document/music-module-requirements-v1.md`.

## 2) Key Code Changes (OSS)
- `ruoyi-common/ruoyi-common-oss/src/main/java/org/dromara/common/oss/core/OssClient.java`
  - Accepts endpoint with `http://` / `https://` (scheme normalized internally).
  - Added APIs: `getPresignedPutUrl`, `createMultipartUpload`, `getPresignedUploadPartUrl`,
    `completeMultipartUpload`, `abortMultipartUpload`, `exists`, `getPrefix`, `getBucketName`.
- `ruoyi-modules/ruoyi-resource/src/main/java/org/dromara/resource/service/ISysOssService.java`
  - Added presign/complete + multipart init/complete/abort methods.
- `ruoyi-modules/ruoyi-resource/src/main/java/org/dromara/resource/service/impl/SysOssServiceImpl.java`
  - Implemented presign + complete, multipart init/complete/abort.
  - Uses prefix + bizType for objectKey.
  - Normalizes ETag by stripping quotes.
- `ruoyi-modules/ruoyi-resource/src/main/java/org/dromara/resource/controller/SysOssPortalController.java`
  - New endpoints for portal upload flows.
- DTO/VO additions:
  - `.../domain/bo/SysOssPresignBo.java`
  - `.../domain/bo/SysOssCompleteBo.java`
  - `.../domain/bo/SysOssMultipartInitBo.java`
  - `.../domain/bo/SysOssMultipartPartBo.java`
  - `.../domain/bo/SysOssMultipartCompleteBo.java`
  - `.../domain/bo/SysOssMultipartAbortBo.java`
  - `.../domain/vo/SysOssMultipartInitVo.java`
  - `.../domain/vo/SysOssMultipartPartVo.java`

## 3) OSS Portal Endpoints (via gateway)
Base path (gateway): `http://localhost:8080/resource`

- `POST /resource/oss/portal/presign`
- `POST /resource/oss/portal/complete`
- `POST /resource/oss/portal/multipart/init`
- `POST /resource/oss/portal/multipart/complete`
- `POST /resource/oss/portal/multipart/abort`

Auth headers:
- `Authorization: Bearer <token>`
- `clientid: <clientId>`

Login (captcha disabled in nacos config):
- `POST /auth/login` with body: `clientId`, `grantType=password`, `tenantId=000000`, `username=admin`, `password=admin123`
- clientId used: `e5cd7e4891bf95d1d19206ce24a7b32e`

## 4) RustFS Config / SQL
The sys_oss_config row that was inserted:
```
6,000000,rustfs,momaofs,memory1314,hjm,"",http://8.148.70.157:9000,"",N,"",0,0,"",103,1,2026-01-31 09:05:10.567383,1,2026-01-31 09:05:10.567383,rustfs
```
Notes:
- endpoint is `http://8.148.70.157:9000`.
- bucket: `hjm` (must exist in RustFS).
- `is_https` uses `N`.
- only one config should be `status=0` (default), others should be set to `1`.

## 5) Gateway Routing
Gateway config snippet:
```
# 资源服务
- id: ruoyi-resource
  uri: lb://ruoyi-resource
  predicates:
    - Path=/resource/**
  filters:
    - StripPrefix=1
```

## 6) Build / Compile Notes
If `ruoyi-resource` is built alone, it needs updated `ruoyi-common-oss` installed in local repo.
Commands that were used to avoid compilation errors:
```
mvn -q -DskipTests -N install
(then)
cd ruoyi-common
mvn -q -DskipTests -N install
cd ruoyi-common/ruoyi-common-oss
mvn -q -DskipTests install
```
Then `mvn -q -DskipTests compile` in `ruoyi-modules/ruoyi-resource` succeeds.

## 7) Testing Status
- Single-file presign flow: verified.
- Multipart presign flow: verified (init -> upload parts -> complete).
- Example successful complete response included ossId + objectKey.
- See `docs/oss-multipart-upload.md` for detailed request samples.

## 8) Requirements Docs
- Main music module requirement doc: `requirements-document/music-module-requirements-v1.md`.
- Improvement notes: `requirements-document/parsed-doc1-improvements.md`.

## 9) Current Git Status (summary)
- Branch: `new-boot4-copy`.
- Many changes already exist in the repo (music module refactors + nacos configs). Do not revert unrelated files.

## 10) Next Work Candidates
- Start implementing music module interfaces based on the requirements doc.
- Confirm decisions on playback access policy, upload constraints, tag governance.

