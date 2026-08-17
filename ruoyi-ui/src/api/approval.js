import request from '@/utils/request'

// 审批中心：待我审批 / 我已审批 列表
// 当前返回 mock 演示数据；接入真实接口后，改为 request 调用即可：
// export function listApproval(type, query) {
//   return request({ url: '/oa/approval/list', method: 'get', params: { type, ...query } })
// }
export function listApproval(type) {
  const db = {
    todo: [
      { id: 'A20260816001', title: '张三的请假申请', type: '请假', applicant: '张三', applyTime: '2026-08-16 09:12', days: 2, status: '待审批' },
      { id: 'A20260816002', title: '李四的报销申请', type: '报销', applicant: '李四', applyTime: '2026-08-16 08:45', amount: 1280.5, status: '待审批' },
      { id: 'A20260815003', title: '王五的出差申请', type: '出差', applicant: '王五', applyTime: '2026-08-15 16:30', days: 3, status: '待审批' }
    ],
    done: [
      { id: 'A20260814005', title: '孙七的请假申请', type: '请假', applicant: '孙七', applyTime: '2026-08-14 10:20', days: 1, status: '已通过' },
      { id: 'A20260813006', title: '周八的报销申请', type: '报销', applicant: '周八', applyTime: '2026-08-13 09:15', amount: 560, status: '已驳回' }
    ]
  }
  return Promise.resolve(db[type] || [])
}

// 审批详情
// 真实接口：
// export function getApprovalDetail(id) {
//   return request({ url: '/oa/approval/' + id, method: 'get' })
// }
export function getApprovalDetail(id) {
  const detail = {
    id,
    title: '张三的请假申请',
    type: '请假',
    applicant: '张三',
    dept: '产品研发部',
    applyTime: '2026-08-16 09:12',
    startDate: '2026-08-18',
    endDate: '2026-08-19',
    days: 2,
    reason: '回老家办理家事，特此请假两天，工作已交接给同事，请领导批准。',
    status: '待审批'
  }
  return Promise.resolve(detail)
}

// 审批流程（时间线）
// 真实接口：
// export function getApprovalFlow(id) {
//   return request({ url: '/oa/approval/flow/' + id, method: 'get' })
// }
export function getApprovalFlow() {
  return Promise.resolve([
    { name: '提交申请', user: '张三', time: '2026-08-16 09:12', status: 'finish', description: '提交请假申请' },
    { name: '部门主管审批', user: '王经理', time: '2026-08-16 10:05', status: 'finish', description: '同意，工作已交接' },
    { name: '人事审批', user: '待审批', time: '', status: 'process', description: '等待人事部门处理' },
    { name: '审批完成', user: '', time: '', status: 'wait', description: '' }
  ])
}

// 发起申请（mock 提交后返回新单号）
// 真实接口：
// export function submitApply(data) {
//   return request({ url: '/oa/approval', method: 'post', data })
// }
export function submitApply(data) {
  return Promise.resolve({ id: 'A2026' + String(Date.now()).slice(-8), ...data, status: '待审批' })
}