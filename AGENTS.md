# AGENTS.md

本文件记录本项目（OA 协同办公平台）的开发约定，供 AI 协作时遵守。

## 提交规范（重要）

- **commit 消息必须使用 Conventional Commits 前缀**：`feat:`（新功能）、`fix:`（修复）、`docs:`（文档）、`refactor:`（重构）、`chore:`（构建/杂项）、`style:`（格式）。示例：`feat: 新增待办中心`、`fix: 修复审批流转状态错误`。
- **每个功能一次 commit**：按功能拆分提交，一个功能对应一个 commit，禁止把多个功能合并成一个大提交。
- 建议拆分粒度：后端接口、前端组件、bug 修复、文档更新各自独立提交。
- 提交前先 `git status` / `git diff` 确认只暂存本次功能的文件；只在用户明确要求时 push。
- 完整内部手册（架构/流程/Feign/排障等）见 `docs/INTERNAL.md`（已加入 `.gitignore`，不进仓库）。

## 技术栈与环境

- 框架：RuoYi-Cloud v3.6.8（Spring Boot 4.0.6 / Spring Cloud Alibaba / Vue 2 + Element UI / MyBatis）。
- 服务端口：gateway 8000、auth 9200、system 9201、gen 9202、job 9203、file 9300、monitor 9100、**OA 服务：approval 9210 / todo 9211 / calendar 9212 / contacts 9213 / portal 9214**、Nacos 8848、Redis 6379。
- OA 按业务功能拆分为独立微服务：`ruoyi-approval`（审批）、`ruoyi-todo`（待办）、`ruoyi-calendar`（日程）、`ruoyi-contacts`（通讯录）、`ruoyi-portal`（工作台聚合：dashboard + ai）。跨服务接口与共享 domain 在 `ruoyi-api/ruoyi-api-oa`（Feign Client，返回 `R<T>`）；审批发起/通过/驳回通过 Feign 联动 todo 服务生成/完成待办；portal 通过 Feign 聚合四业务服务做统计与智能问答。
- 本机 Nacos 与 ERP demo 项目共享同一套 Nacos(8848)/MySQL(ry-cloud, ry-config)/Redis；网关运行配置含 ERP 拆分路由（`/erp/**`，勿删）。**8000 网关为共享资源，OA 与 ERP 都会启动 gateway 抢占 8000 端口，冲突时需停对方 gateway**。9202 被 ERP 的 `ruoyi-modules-erp` 占用，OA 的 gen 启动会端口冲突。
- 验证码当前关闭（Nacos `ruoyi-gateway-dev.yml` 中 `security.captcha.enabled: false`），登录只需 `{username, password}`（admin/admin123）。

## 关键代码约定

- RuoYi 前端 `request.js` 拦截器返回 HTTP body：`TableDataInfo`（列表接口）用 `res.rows`；`AjaxResult.success(obj)` 用 `res.data` 取业务对象。
- 后端接口统一返回 `success(obj)`（`AjaxResult`），成功 code=200。
- OA 审批状态：申请 `0 待审批 / 1 已通过 / 2 已驳回`；流程节点状态 `finish / process / wait`。发起申请联动生成待办（`handler=1`），通过/驳回联动完成待办。
- 工作台统计、智能助手均为真实业务聚合（复用 mapper Java 层），雷达图与智能助手模型层为 mock。
- 构建后端：`mvn -pl ruoyi-modules/ruoyi-approval -am package -DskipTests`（单个服务）或全量 `mvn package -DskipTests`；前端：`npm run build:prod`（在 `ruoyi-ui` 下）。

## 验证方式

- 后端接口验证用 `C:\Users\UP_WEI~1\AppData\Local\Temp\opencode\` 下的 Python 脚本（登录免验证码，token 取 `data.access_token`）。
- 重启 OA 服务：停对应端口进程 → 重新 `mvn package` → `java -jar ruoyi-modules\ruoyi-xxx\target\ruoyi-modules-xxx.jar`（工作目录仓库根），日志在 `%TEMP%\opencode\services\<name>.log`。
- **Nacos 配置变更必须通过 Nacos 控制台/HTTP API 发布（POST /nacos/v1/cs/configs），直接改 MySQL `ry-config.config_info` 不生效**；Nacos 3.x standalone 实际配置存 `D:\env\nacos-server-3.0.2\data\tenant-config-data`。`sql/ry_config_oa_20260819.sql`、`sql/ry_config_oa_split_20260820.sql`、`sql/ry_config_dedup_20260819.sql` 为仓库初始化/同步脚本。
