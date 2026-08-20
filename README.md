<p align="center">
    <img alt="logo" src="https://oscimg.oschina.net/oscnet/up-b99b286755aef70355a7084753f89cdb7c9.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">OA协同办公平台</h1>
<h4 align="center">基于 RuoYi-Cloud 二次开发的以工作流为核心的办公自动化系统（演示项目）</h4>
<p align="center">
    <a href="https://github.com/ShiroWei/RuoYi-OA-demo"><img src="https://img.shields.io/badge/RuoYi--Cloud-3.6.8-brightgreen.svg"></a>
    <a href="https://github.com/ShiroWei/RuoYi-OA-demo/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 平台简介

OA协同办公平台是一套**演示性质**的办公自动化（Office Automation，OA）系统，核心价值在于**流程驱动与协同办公**：员工通过统一工作台处理待办审批、发起各类申请，围绕会议、日程、公告、通讯录等场景完成日常协作，管理者通过流程掌握事项流转与执行情况。项目在开源框架 [RuoYi-Cloud v3.6.8](https://gitee.com/y_project/RuoYi-Cloud)（Spring Boot 4.0.6 / Spring Cloud & Alibaba / Vue 2 + Element UI）基础上二次开发。

* **真实业务闭环**：发起申请 → 自动生成单号并初始化审批流程（提交 → 部门审批 → 人事审批 → 完成）→ 联动生成审批待办 → 逐节点审批流转 → 通过 / 驳回联动完成待办，全链路走真实接口并持久化到数据库。
* **四大办公应用**：待办中心、审批中心、会议日程、组织通讯录，覆盖员工日常办公高频场景。
* **OA 工作台首页**：欢迎横幅统计、快捷入口、今日待办（点击直达审批详情）、今日日程、最新公告、数据图表。
* **智能助手（演示）**：工作台右下角对话助手，支持「我的待办 / 请假 / 报销 / 出差 / 今日日程 / 同事人数 / 工作台统计」问答，返回结构化 JSON 卡片（列表 / 统计），数据来自真实业务统计；当前为规则 mock，后续可平滑接入真实大模型。
* **后台管理**：用户、部门、岗位、角色、菜单、字典、参数、公告、日志等完整系统管理能力。
* **按领域拆分 OA 服务**：审批 / 待办 / 日程 / 通讯录各为独立微服务（9210-9213），工作台统计与智能助手为聚合服务 `ruoyi-portal`（9214，Feign 聚合），审批与待办跨服务联动，为流程引擎等能力扩展预留空间。
* 配套 SQL 脚本：OA 业务表 DDL + 演示数据，与若依原生脚本一并提供。

## 功能特性

| 应用   | 功能模块           | 说明                                                                  |
|:---- |:-------------- |:------------------------------------------------------------------- |
| 工作台  | OA 工作台         | 欢迎横幅（待办/审批中/本月完成/在线统计）、快捷入口、今日待办（点击直达审批详情）、今日日程、最新公告、近 7 日趋势与审批分布图表 |
| 工作台  | 智能助手（演示）      | 右下角悬浮对话，问答返回列表 / 统计卡片，支持跳转（待办/审批/日程等），数据为真实业务统计，模型层为规则 mock |
| 待办中心 | 待我审批           | 待审批事项列表，优先级标记，点击直达审批详情                                              |
| 待办中心 | 我已处理 / 我发起的    | 按处理结果、提交人过滤                                                         |
| 审批中心 | 发起申请           | 请假 / 报销 / 出差表单，提交后自动生成单号、初始化流程节点、联动生成待办                             |
| 审批中心 | 待我审批 / 我已审批    | 按状态过滤列表，展示当前环节                                                      |
| 审批中心 | 审批详情           | 单据信息 + 审批流程时间线，支持通过 / 驳回（带审批意见）                                     |
| 会议日程 | 月历视图 / 当日日程    | 日程事件在日历标记，当日日程列表展示                                                  |
| 通讯录  | 组织架构 / 人员      | 复用真实部门接口的组织树 + 人员卡片（电话 / 邮件）                                        |
| 系统管理 | 用户/部门/岗位/角色/菜单 | 若依原生后台管理能力                                                          |
| 系统管理 | 字典/参数/公告/日志    | 若依原生后台管理能力                                                          |
| 系统监控 | 在线用户/定时任务/服务监控 | 若依原生监控能力                                                            |

> **审批闭环**：申请状态统一为 `待审批(0) → 已通过(1) / 已驳回(2)`；流程节点状态为 `finish（已完成）/ process（处理中）/ wait（等待）`，发起与审批均真实联动待办中心。

## 技术栈

| 组件                   | 版本                 |
|:-------------------- |:------------------ |
| JDK                  | 17+（本机 JDK 24 已验证） |
| Spring Boot          | 4.0.6              |
| Spring Cloud         | 2025.1.1           |
| Spring Cloud Alibaba | 2025.1.0.0         |
| Nacos Server         | 3.0.2              |
| Redis                | 6+                 |
| Vue                  | 2.x                |
| Element UI           | 2.15.x             |
| MySQL                | 8.0（兼容 5.7+）       |
| Maven / Node         | 3.9+ / 20          |

## 系统模块

```
oa
├── ruoyi-ui              // 前端框架（工作台/审批/日程/通讯录/系统管理）[80]
├── ruoyi-gateway         // 网关模块 [8000]
├── ruoyi-auth            // 认证中心 [9200]
├── ruoyi-api             // 接口模块
│       └── ruoyi-api-system                          // 系统接口
├── ruoyi-common          // 通用模块
│       └── ruoyi-common-core                         // 核心模块
│       └── ruoyi-common-datascope                    // 权限范围
│       └── ruoyi-common-datasource                   // 多数据源
│       └── ruoyi-common-log                          // 日志记录
│       └── ruoyi-common-redis                        // 缓存服务
│       └── ruoyi-common-seata                        // 分布式事务
│       └── ruoyi-common-security                     // 安全模块
│       └── ruoyi-common-sensitive                    // 数据脱敏
│       └── ruoyi-common-swagger                      // 系统接口
├── ruoyi-modules         // 业务模块
│       └── ruoyi-system                              // 系统模块 [9201]
│       └── ruoyi-approval                            // OA 审批服务 [9210]
│       └── ruoyi-todo                                // OA 待办服务 [9211]
│       └── ruoyi-calendar                            // OA 日程服务 [9212]
│       └── ruoyi-contacts                            // OA 通讯录服务 [9213]
│       └── ruoyi-portal                              // OA 工作台聚合服务 [9214]
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├── pom.xml               // 公共依赖
```

## 演示账号

| 账号    | 密码       | 角色  |
|:----- |:-------- |:--- |
| admin | admin123 | 管理员 |

## 本地开发环境

环境要求：JDK 17+、Maven 3.9+、Node 16/18/20、MySQL 8、Redis、Nacos 3.x。

1. 初始化数据库（建库 `ry-cloud` 后依次导入）：`sql/ry_20260417.sql`、`sql/ry_config_20260611.sql`、`sql/ry_config_oa_20260819.sql`、`sql/ry_config_oa_split_20260820.sql`（OA 配置与路由拆分增量）、`sql/quartz.sql`、`sql/oa_tables.sql`（OA 业务表 + 演示数据）。
2. 启动 Nacos（3.0.2，默认端口 8848）：`startup.cmd -m standalone`（单机模式）。
3. 依次启动后端微服务：`ruoyi-gateway`(8000) → `ruoyi-auth`(9200) → `ruoyi-modules-system`(9201) → OA 五个服务（approval 9210 / todo 9211 / calendar 9212 / contacts 9213 / portal 9214）→ 其他模块（gen/job/file/monitor）。
4. 前端：`cd ruoyi-ui && npm install && npm run dev`（默认端口 80，若被系统保留端口占用会回退到 8081，代理指向网关 8000）。

> Nacos 配置：网关路由 `/oa/approval|todo|calendar|contacts|dashboard|ai/**` 分别指向五个 OA 服务，`/code` 已加入白名单（登录验证码）；OA 各服务数据源复用 `ry-cloud` 库。运行时配置已同步回仓库，见 `sql/ry_config_oa_split_20260820.sql`（在 `ry_config_20260611.sql` + `ry_config_oa_20260819.sql` 之后执行）。

详细说明见 [docs/本地开发环境配置.md](docs/本地开发环境配置.md)。

## 目录结构

```
ruoyi-ui/                  # 前端（Vue 2 + Element UI）
  src/api/                 # 业务接口（todo / approval / calendar / contacts / dashboard）
  src/views/oa/            # OA 页面（todo / approval / calendar / contacts）
  src/views/dashboard/     # OA 工作台
ruoyi-modules/ruoyi-approval/   # OA 审批服务（端口 9210，approval_apply / approval_flow）
ruoyi-modules/ruoyi-todo/       # OA 待办服务（端口 9211，todo_item）
ruoyi-modules/ruoyi-calendar/   # OA 日程服务（端口 9212，schedule_event）
ruoyi-modules/ruoyi-contacts/   # OA 通讯录服务（端口 9213，contact_person）
ruoyi-modules/ruoyi-portal/     # OA 工作台聚合服务（端口 9214，dashboard + ai，Feign 聚合）
ruoyi-api/ruoyi-api-oa/         # OA 跨服务接口（共享 domain + Feign Client）
ruoyi-gateway/             # 网关（端口 8000）
ruoyi-auth/                # 认证中心
ruoyi-modules-system/      # 系统模块
sql/                       # 数据库脚本（若依原生 + oa_tables.sql）
docs/                      # 本地开发环境配置文档
```

## 功能规划

| 阶段       | 内容                     | 状态               |
|:-------- |:---------------------- |:---------------- |
| 展示层改造    | 品牌中性化、OA 工作台首页、登录页打磨   | 已完成              |
| OA 业务模块  | 待办中心、审批中心、会议日程、通讯录     | 已完成（真实接口 + 审批闭环） |
| 工作台统计真实化 | 工作台统计面板与图表数据接入后端 | 已完成（统计面板/近7日趋势/类型分布/部门申请量为真实聚合；效率雷达为演示评估） |
| 工作流引擎    | 接入 Flowable，实现流程引擎驱动审批 | 规划中              |
| AI 能力    | 预留 Agent 接口（先定义接口契约）   | 规划中              |
| 交付方式     | 独立部署（每客户一套）            | 规划中              |

## 分支约定

| 分支              | 用途                       |
|:--------------- |:------------------------ |
| `master`        | **开发主分支**（当前所在），所有功能在此提交 |
| `dev`           | 已并入 master，不再维护          |
| `docker-deploy` | docker 一键演示部署            |

## 上游来源

本项目基于 [RuoYi-Cloud v3.6.8](https://gitee.com/y_project/RuoYi-Cloud) 二次开发，遵循 MIT License，保留上游完整提交历史。若依官方文档见 [RuoYi 官网](https://ruoyi.vip)。