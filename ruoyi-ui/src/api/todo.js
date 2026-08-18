import request from '@/utils/request'

// 待办中心：顶部统计
export function getTodoStat() {
  return request({
    url: '/oa/todo/stat',
    method: 'get'
  })
}

// 待办中心：列表（type: pending 待我审批 / done 我已处理 / apply 我发起的）
export function listTodo(type, query) {
  return request({
    url: '/oa/todo/list',
    method: 'get',
    params: { type, ...query }
  })
}

// 标记待办已处理
export function completeTodo(todoId) {
  return request({
    url: '/oa/todo/complete/' + todoId,
    method: 'post'
  })
}