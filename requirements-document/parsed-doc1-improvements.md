# Parsed Doc 1 - Improvement Notes

These notes capture items that look unclear or risky in the parsed design and the
adjustments made to keep compatibility with the existing schema and code.

1) IDs and user tables
- The parsed design uses UUID for users, but the project uses BIGINT IDs
  (snowflake) across existing tables. Keeping BIGINT avoids cross-table mismatch.
- OAuth binding and core user data already exist in the base system tables
  (e.g. sys_user, sys_social), so the music SQL does not duplicate them.

2) High-frequency stats
- The parsed design suggests separating counters into a stats table. This was
  added, but the counters in the music table are kept as cached mirrors so the
  current code keeps working. If stats are moved fully, add sync logic.

3) Enum-like fields
- Many fields are modeled as enums in the parsed design. In PostgreSQL, consider
  CHECK constraints or dict tables to enforce valid values consistently.

4) Resource deduplication
- For file hashing, consider a uniqueness rule such as
  (file_hash, res_type, quality_tier) or a partial unique index to enforce
  "same file, same resource" without blocking different variants.

5) Draft JSONB
- Drafts are stored as JSONB for flexibility. If drafts are queried by keys,
  add GIN indexes on content to keep query latency predictable.

6) Like/Dislike toggling
- If "unlike" should be a soft delete, add an active flag and use a partial
  unique index on (user_id, target_id, target_type) where active = true.
