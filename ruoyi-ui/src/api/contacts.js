import request from '@/utils/request'
import { listDept } from '@/api/system/dept'

// 通讯录：组织架构树（优先走真实部门接口，失败时返回演示数据兜底）
export function getContactsTree() {
  return listDept()
}

// 通讯录：某部门下的人员（当前返回 mock 演示数据）
// 真实接口：
// export function listContacts(query) {
//   return request({ url: '/oa/contacts/list', method: 'get', params: query })
// }
export function listContacts() {
  return Promise.resolve([
    { id: 1, name: '张三', dept: '产品研发部', post: '产品经理', phone: '138****1234', email: 'zhangsan@oa-demo.com' },
    { id: 2, name: '李四', dept: '产品研发部', post: '前端工程师', phone: '139****5678', email: 'lisi@oa-demo.com' },
    { id: 3, name: '王五', dept: '产品研发部', post: '后端工程师', phone: '137****9012', email: 'wangwu@oa-demo.com' },
    { id: 4, name: '赵六', dept: '市场部', post: '市场专员', phone: '136****3456', email: 'zhaoliu@oa-demo.com' },
    { id: 5, name: '孙七', dept: '财务部', post: '会计', phone: '135****7890', email: 'sunqi@oa-demo.com' },
    { id: 6, name: '周八', dept: '人事行政部', post: '人事专员', phone: '134****2345', email: 'zhouba@oa-demo.com' }
  ])
}