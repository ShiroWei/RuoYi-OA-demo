-- OA协同办公平台：办公自动化模块表结构（域前缀：approval_/schedule_/contact_/todo_）

-- 审批申请
DROP TABLE IF EXISTS approval_apply;
CREATE TABLE approval_apply (
  apply_id      BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  apply_no      VARCHAR(64)  DEFAULT NULL COMMENT '申请单号',
  title         VARCHAR(200) DEFAULT NULL COMMENT '申请标题',
  apply_type    VARCHAR(32)  DEFAULT NULL COMMENT '申请类型（请假/报销/出差/用章）',
  applicant_id  BIGINT(20)   DEFAULT NULL COMMENT '申请人ID',
  applicant     VARCHAR(64)  DEFAULT NULL COMMENT '申请人',
  dept_name     VARCHAR(64)  DEFAULT NULL COMMENT '所属部门',
  content       VARCHAR(1000) DEFAULT NULL COMMENT '申请内容',
  start_date    DATE         DEFAULT NULL COMMENT '开始日期',
  end_date      DATE         DEFAULT NULL COMMENT '结束日期',
  days          INT(11)      DEFAULT NULL COMMENT '天数/数量',
  amount        DECIMAL(10,2) DEFAULT NULL COMMENT '金额',
  status        CHAR(1)      DEFAULT '0' COMMENT '状态（0待审批 1已通过 2已驳回）',
  current_node  VARCHAR(64)  DEFAULT NULL COMMENT '当前环节',
  apply_time    DATETIME     DEFAULT NULL COMMENT '申请时间',
  create_by     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time   DATETIME     DEFAULT NULL COMMENT '创建时间',
  update_by     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  update_time   DATETIME     DEFAULT NULL COMMENT '更新时间',
  remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (apply_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批申请';

-- 审批流程节点
DROP TABLE IF EXISTS approval_flow;
CREATE TABLE approval_flow (
  flow_id      BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '流程ID',
  apply_id     BIGINT(20)   DEFAULT NULL COMMENT '申请ID',
  node_name    VARCHAR(64)  DEFAULT NULL COMMENT '环节名称',
  node_order   INT(11)      DEFAULT NULL COMMENT '环节顺序',
  handler      VARCHAR(64)  DEFAULT NULL COMMENT '处理人',
  handle_time  DATETIME     DEFAULT NULL COMMENT '处理时间',
  comment      VARCHAR(500) DEFAULT NULL COMMENT '处理意见',
  status       VARCHAR(16)  DEFAULT 'wait' COMMENT '状态（finish 已处理 process 处理中 wait 待处理）',
  PRIMARY KEY (flow_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流程节点';

-- 日程事件
DROP TABLE IF EXISTS schedule_event;
CREATE TABLE schedule_event (
  event_id      BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '日程ID',
  title         VARCHAR(200) DEFAULT NULL COMMENT '标题',
  event_date    DATE         DEFAULT NULL COMMENT '事件日期',
  start_time    VARCHAR(16)  DEFAULT NULL COMMENT '开始时间',
  end_time      VARCHAR(16)  DEFAULT NULL COMMENT '结束时间',
  location      VARCHAR(200) DEFAULT NULL COMMENT '地点',
  event_type    VARCHAR(32)  DEFAULT NULL COMMENT '类型（会议/汇报/活动）',
  create_by_id  BIGINT(20)   DEFAULT NULL COMMENT '创建人ID',
  participants  VARCHAR(500) DEFAULT NULL COMMENT '参与人',
  create_by     VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time   DATETIME     DEFAULT NULL COMMENT '创建时间',
  update_by     VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  update_time   DATETIME     DEFAULT NULL COMMENT '更新时间',
  remark        VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (event_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='日程事件';

-- 通讯录人员
DROP TABLE IF EXISTS contact_person;
CREATE TABLE contact_person (
  person_id    BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '人员ID',
  name         VARCHAR(64)  DEFAULT NULL COMMENT '姓名',
  dept_id      BIGINT(20)   DEFAULT NULL COMMENT '部门ID',
  dept_name    VARCHAR(64)  DEFAULT NULL COMMENT '部门名称',
  post         VARCHAR(64)  DEFAULT NULL COMMENT '岗位',
  phone        VARCHAR(32)  DEFAULT NULL COMMENT '手机号',
  email        VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  office_phone VARCHAR(32)  DEFAULT NULL COMMENT '办公电话',
  create_by    VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time  DATETIME     DEFAULT NULL COMMENT '创建时间',
  update_by    VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  update_time  DATETIME     DEFAULT NULL COMMENT '更新时间',
  remark       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (person_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='通讯录人员';

-- 待办事项
DROP TABLE IF EXISTS todo_item;
CREATE TABLE todo_item (
  todo_id      BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '待办ID',
  biz_type     VARCHAR(32)  DEFAULT NULL COMMENT '关联业务类型（approval 审批）',
  biz_id       BIGINT(20)   DEFAULT NULL COMMENT '关联业务ID',
  title        VARCHAR(200) DEFAULT NULL COMMENT '待办标题',
  todo_type    VARCHAR(32)  DEFAULT NULL COMMENT '待办类型（请假/报销/出差/用章）',
  submitter    VARCHAR(64)  DEFAULT NULL COMMENT '提交人',
  handler_id   BIGINT(20)   DEFAULT NULL COMMENT '处理人ID',
  priority     VARCHAR(16)  DEFAULT '中' COMMENT '优先级（高/中/低）',
  status       CHAR(1)      DEFAULT '0' COMMENT '状态（0待处理 1已处理）',
  due_time     DATE         DEFAULT NULL COMMENT '到期时间',
  submit_time  DATETIME     DEFAULT NULL COMMENT '提交时间',
  create_by    VARCHAR(64)  DEFAULT '' COMMENT '创建者',
  create_time  DATETIME     DEFAULT NULL COMMENT '创建时间',
  update_by    VARCHAR(64)  DEFAULT '' COMMENT '更新者',
  update_time  DATETIME     DEFAULT NULL COMMENT '更新时间',
  remark       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (todo_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='待办事项';