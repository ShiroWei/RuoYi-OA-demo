-- OA 业务表统一增加 oa_ 前缀（存量数据库迁移脚本）
-- 执行前必须停止 ruoyi-approval / todo / calendar / contacts / portal 服务并完成备份。
-- 本脚本只做 RENAME TABLE，不复制、不删除数据；新旧表同时存在时主动中止，避免覆盖。

USE `ry-cloud`;

DELIMITER $$

DROP PROCEDURE IF EXISTS assert_oa_table_migration_safe$$
CREATE PROCEDURE assert_oa_table_migration_safe()
BEGIN
    DECLARE conflict_count INT DEFAULT 0;

    SELECT COUNT(*) INTO conflict_count
      FROM (
          SELECT 'approval_apply' AS old_name, 'oa_approval_apply' AS new_name
          UNION ALL SELECT 'approval_flow', 'oa_approval_flow'
          UNION ALL SELECT 'todo_item', 'oa_todo_item'
          UNION ALL SELECT 'schedule_event', 'oa_schedule_event'
          UNION ALL SELECT 'contact_person', 'oa_contact_person'
      ) names
     WHERE EXISTS (
         SELECT 1 FROM information_schema.tables
          WHERE table_schema = DATABASE() AND table_name = names.old_name
     )
       AND EXISTS (
         SELECT 1 FROM information_schema.tables
          WHERE table_schema = DATABASE() AND table_name = names.new_name
     );

    IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'OA table migration stopped: old and new table both exist';
    END IF;
END$$

DROP PROCEDURE IF EXISTS rename_oa_table_if_needed$$
CREATE PROCEDURE rename_oa_table_if_needed(IN old_name VARCHAR(64), IN new_name VARCHAR(64))
BEGIN
    DECLARE old_count INT DEFAULT 0;
    DECLARE new_count INT DEFAULT 0;

    SELECT COUNT(*) INTO old_count
      FROM information_schema.tables
     WHERE table_schema = DATABASE() AND table_name = old_name;

    SELECT COUNT(*) INTO new_count
      FROM information_schema.tables
     WHERE table_schema = DATABASE() AND table_name = new_name;

    IF old_count = 1 AND new_count = 0 THEN
        SET @rename_sql = CONCAT('RENAME TABLE `', old_name, '` TO `', new_name, '`');
        PREPARE rename_stmt FROM @rename_sql;
        EXECUTE rename_stmt;
        DEALLOCATE PREPARE rename_stmt;
    ELSEIF old_count = 1 AND new_count = 1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'OA table migration stopped: old and new table both exist';
    END IF;
END$$

CALL assert_oa_table_migration_safe()$$
CALL rename_oa_table_if_needed('approval_apply', 'oa_approval_apply')$$
CALL rename_oa_table_if_needed('approval_flow', 'oa_approval_flow')$$
CALL rename_oa_table_if_needed('todo_item', 'oa_todo_item')$$
CALL rename_oa_table_if_needed('schedule_event', 'oa_schedule_event')$$
CALL rename_oa_table_if_needed('contact_person', 'oa_contact_person')$$

DROP PROCEDURE rename_oa_table_if_needed$$
DROP PROCEDURE assert_oa_table_migration_safe$$
DELIMITER ;

SELECT table_name, table_rows
  FROM information_schema.tables
 WHERE table_schema = DATABASE()
   AND table_name IN (
       'oa_approval_apply', 'oa_approval_flow', 'oa_todo_item',
       'oa_schedule_event', 'oa_contact_person'
   )
 ORDER BY table_name;
