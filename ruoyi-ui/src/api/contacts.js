import request from '@/utils/request'
import { listDept } from '@/api/system/dept'

// 通讯录：组织架构树（走真实部门接口）
export function getContactsTree() {
  return listDept()
}

// 通讯录：按部门/关键字查询人员
export function listContacts(query) {
  return request({
    url: '/oa/contacts/list',
    method: 'get',
    params: query
  })
}

export function addContact(data) {
  return request({ url: '/oa/contacts', method: 'post', data })
}

export function updateContact(data) {
  return request({ url: '/oa/contacts', method: 'put', data })
}

export function delContact(personId) {
  return request({ url: '/oa/contacts/' + personId, method: 'delete' })
}
