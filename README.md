# 办公管理系统

<p align="center">
  <h1 align="center">办公管理系统</h1>
  <h4 align="center">基于 Vue / Element UI 与 Spring Boot / Spring Cloud & Alibaba 的前后端分离分布式微服务办公系统</h4>
</p>

## 平台简介

办公管理系统是一套面向企业内部使用的 OA 办公管理平台，提供用户、部门、岗位、角色等基础权限管理，以及通知公告、日志审计、任务调度、服务监控等办公能力，并预留了工作流与 AI 能力扩展。

* 采用前后端分离模式：前端 Vue 2 + Element UI，后端 Spring Boot 4 + Spring Cloud & Alibaba。
* 注册中心、配置中心选型 Nacos，权限认证使用 Redis。
* 流量控制框架选型 Sentinel，分布式事务选型 Seata。
* 面向演示与内部展示优化：品牌信息中性化、首页仪表盘、登录页打磨；演示数据采用 mock + 预留真实接口。

## 技术栈

| 组件 | 版本 |
| :--- | :--- |
| JDK | 17+（本机 JDK 24 已验证） |
| Spring Boot | 4.0.6 |
| Spring Cloud | 2025.1.1 |
| Spring Cloud Alibaba | 2025.1.0.0 |
| Nacos Server | 3.0.2 |
| MySQL | 8.0（兼容 5.7+） |
| Redis | 6+ |
| Vue / Element UI | 2.x / Element UI |
| Maven / Node | 3.9+ / 20 |

## 系统模块

~~~
oa
├── ruoyi-ui              // 前端框架 [80]
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
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├── pom.xml               // 公共依赖
~~~

## 内置功能

1. 用户管理：系统用户配置与维护。
2. 部门管理：组织结构（公司、部门、小组）配置，树结构展示，支持数据权限。
3. 岗位管理：配置系统用户所属职务。
4. 菜单管理：配置系统菜单、操作权限、按钮权限标识。
5. 角色管理：角色菜单权限分配，按机构设置数据范围权限。
6. 字典管理：维护系统中经常使用的固定数据。
7. 参数管理：动态配置系统常用参数。
8. 通知公告：通知公告信息发布与维护。
9. 操作日志：正常/异常操作日志记录与查询。
10. 登录日志：登录日志与登录异常记录查询。
11. 在线用户：当前系统活跃用户状态监控。
12. 定时任务：在线添加/修改/删除任务调度，包含执行结果日志。
13. 代码生成：前后端代码生成（java、html、xml、sql），支持 CRUD 下载。
14. 系统接口：根据业务代码自动生成 API 接口文档。
15. 服务监控：监视当前系统 CPU、内存、磁盘、堆栈等信息。
16. 在线构建器：拖动表单元素生成 HTML 代码。
17. 连接池监视：监视数据库连接池状态，分析 SQL 定位性能瓶颈。

## 首页仪表盘

展示层改造新增首页仪表盘：欢迎横幅 + 数据统计卡片 + 折线/柱状/饼图/雷达图表。
当前图表数据为演示 mock（`ruoyi-ui/src/api/dashboard.js`），真实接口已在同一文件预留（`/system/dashboard/*`），接入真实数据时无需改页面。

## 功能规划

| 阶段 | 内容 | 状态 |
| :--- | :--- | :--- |
| 展示层改造 | 品牌中性化、仪表盘首页、登录页打磨 | 已完成 |
| 工作流引擎 | 接入 Flowable，实现审批流程 | 规划中 |
| AI 能力 | 预留 AI 接口（先定义接口契约） | 规划中 |
| 交付方式 | 独立部署（每客户一套） | 规划中 |

## 演示账号

- 管理员：admin / admin123

## 本地开发

参见 [docs/本地开发环境配置.md](docs/本地开发环境配置.md)（含数据库初始化、Nacos 安装、前后端启动步骤）。

## 分支约定

| 分支 | 用途 |
| :--- | :--- |
| `master` | **开发主分支**（当前所在），所有功能在此提交 |
| `dev` | 已并入 master，不再维护 |
| `docker-deploy` | docker 一键演示部署 |