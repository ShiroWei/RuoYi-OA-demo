# AGENTS.md

本文件记录本项目（OA 协同办公平台）的开发约定，供 AI 协作时遵守。

## 提交规范（重要）

- **每个功能一次 commit**：按功能拆分提交，一个功能对应一个 commit，禁止把多个功能合并成一个大提交。
- 建议拆分粒度：后端接口、前端组件、bug 修复、文档更新各自独立提交。
- 提交前先 `git status` / `git diff` 确认只暂存本次功能的文件；只在用户明确要求时 push。

## 技术栈与环境

- 框架：RuoYi-Cloud v3.6.8（Spring Boot 4.0.6 / Spring Cloud Alibaba / Vue 2 + Element UI / MyBatis）。
- 服务端口：gateway 8000、auth 9200、system 9201、gen 9202、job 9203、**ruoyi-oa 9204**、file 9300、monitor 9100、Nacos 8848、Redis 6379。
- OA 业务为独立微服务 `ruoyi-oa`（9204），源码在 `ruoyi-modules/ruoyi-oa`，按 `approval / calendar / contacts / todo / dashboard / ai` 分包。
- 本机 Nacos 与 ERP demo 项目共享同一套 Nacos(8848)/MySQL(ry-cloud, ry-config)/Redis；网关运行配置含 `/erp/**` 路由（勿删）。9202 被 ERP 的 `ruoyi-modules-erp` 占用，OA 的 gen 启动会端口冲突。
- 验证码当前关闭（Nacos `ruoyi-gateway-dev.yml` 中 `security.captcha.enabled: false`），登录只需 `{username, password}`（admin/admin123）。

## 关键代码约定

- RuoYi 前端 `request.js` 拦截器返回 HTTP body：`TableDataInfo`（列表接口）用 `res.rows`；`AjaxResult.success(obj)` 用 `res.data` 取业务对象。
- 后端接口统一返回 `success(obj)`（`AjaxResult`），成功 code=200。
- OA 审批状态：申请 `0 待审批 / 1 已通过 / 2 已驳回`；流程节点状态 `finish / process / wait`。发起申请联动生成待办（`handler=1`），通过/驳回联动完成待办。
- 工作台统计、智能助手均为真实业务聚合（复用 mapper Java 层），雷达图与智能助手模型层为 mock。
- 构建后端：`mvn -pl ruoyi-modules/ruoyi-oa -am package -DskipTests`；前端：`npm run build:prod`（在 `ruoyi-ui` 下）。

## 验证方式

- 后端接口验证用 `C:\Users\UP_WEI~1\AppData\Local\Temp\opencode\` 下的 Python 脚本（登录免验证码，token 取 `data.access_token`）。
- 重启 oa：停 9204 进程 → 重新 `mvn package` → `java -jar ruoyi-modules\ruoyi-oa\target\ruoyi-modules-oa.jar`（工作目录仓库根），日志在 `%TEMP%\opencode\services\oa.log`。
- Nacos 配置改动直接改 `ry-config.config_info` 或通过控制台发布；`sql/ry_config_oa_20260819.sql`、`sql/ry_config_dedup_20260819.sql` 为运行时配置同步/去重脚本。
