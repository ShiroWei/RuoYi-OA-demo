import request from '@/utils/request'

// 待办中心：顶部统计
// 当前返回 mock 演示数据；接入真实接口后，改为 request 调用即可：
// export function getTodoStat() {
//   return request({ url: '/oa/todo/stat', method: 'get' })
// }
export function getTodoStat() {
  return Promise.resolve({
    todoCount: 12,
    doneCount: 26,
    applyCount: 6,
    overdueCount: 2
  })
}

// 待办中心：列表（type: pending 待我审批 / done 我已处理 / apply 我发起的）
// 真实接口：
// export function listTodo(type, query) {
//   return request({ url: '/oa/todo/list', method: 'get', params: { type, ...query } })
// }
export function listTodo(type) {
  const db = {
    pending: [
      { id: 'T20260816001', title: '张三提交的请假申请', type: '请假', applicant: '张三', createTime: '2026-08-16 09:12', status: '待审批', priority: '高' },
      { id: 'T20260816002', title: '李四提交的报销申请', type: '报销', applicant: '李四', createTime: '2026-08-16 08:45', status: '待审批', priority: '中' },
      { id: 'T20260815003', title: '王五提交的出差申请', type: '出差', applicant: '王五', createTime: '2026-08-15 16:30', status: '待审批', priority: '中' },
      { id: 'T20260815004', title: '赵六提交的用章申请', type: '用章', applicant: '赵六', createTime: '2026-08-15 14:02', status: '待审批', priority: '低' }
    ],
    done: [
      { id: 'T20260814005', title: '孙七提交的请假申请', type: '请假', applicant: '孙七', createTime: '2026-08-14 10:20', status: '已通过', priority: '中' },
      { id: 'T20260813006', title: '周八提交的报销申请', type: '报销', applicant: '周八', createTime: '2026-08-13 09:15', status: '已驳回', priority: '高' }
    ],
    apply: [
      { id: 'T20260816007', title: '我的请假申请', type: '请假', applicant: '我', createTime: '2026-08-16 08:00', status: '审批中', priority: '中' },
      { id: 'T20260810008', title: '我的出差申请', type: '出差', applicant: '我', createTime: '2026-08-10 11:30', status: '已通过', priority: '低' }
    ]
  }
  return Promise.resolve(db[type] || [])
}