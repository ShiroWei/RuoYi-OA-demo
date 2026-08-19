-- OA协同办公平台：Nacos 配置去重脚本（2026-08-19）
-- 背景：共享 Nacos（ry-config 库）曾执行过一次 namespace 迁移（src_user=nacos_namespace_migrate），
--       导致 config_info 表中 11 条 data_id 各被重复插入一条（内容完全相同）。
-- 处理：保留 id 较小的一条，删除重复行（id 较大的那组）。
-- 执行前请核对：SELECT id, data_id, md5 FROM config_info ORDER BY data_id, id;
-- 本脚本为幂等（按 id 精确删除，重复执行无副作用）。

USE ry-config;

DELETE FROM config_info WHERE id IN (10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 22);

-- 验证：应无任何 data_id 重复，且总数 = 11
-- SELECT data_id, COUNT(*) c FROM config_info GROUP BY data_id HAVING c > 1;
