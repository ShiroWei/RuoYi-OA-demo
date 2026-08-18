import request from '@/utils/request'

// 审批中心：列表（type: todo 待我审批 / done 我已审批 / apply 我发起的）
export function listApproval(type, query) {
  return request({
    url: '/oa/approval/list',
    method: 'get',
    params: { type, ...query }
  })
}

// 审批详情
export function getApprovalDetail(id) {
  return request({
    url: '/oa/approval/' + id,
    method: 'get'
  })
}

// 审批流程（时间线）
export function getApprovalFlow(id) {
  return request({
    url: '/oa/approval/flow/' + id,
    method: 'get'
  })
}

// 发起申请（后端返回 id / applyNo）
export function submitApply(data) {
  return request({
    url: '/oa/approval',
    method: 'post',
    data
  })
}

// 审批通过
export function approveApply(id, comment) {
  return request({
    url: '/oa/approval/approve/' + id,
    method: 'post',
    params: { comment }
  })
}

// 审批驳回
export function rejectApply(id, comment) {
  return request({
    url: '/oa/approval/reject/' + id,
    method: 'post',
    params: { comment }
  })
}