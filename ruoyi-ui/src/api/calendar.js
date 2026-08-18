import request from '@/utils/request'

// 会议日程：按月份获取日程事件（month: yyyy-MM，可为空查全部）
export function listCalendarEvent(month) {
  return request({
    url: '/oa/calendar/events',
    method: 'get',
    params: { month }
  })
}

// 新增日程
export function addCalendarEvent(data) {
  return request({
    url: '/oa/calendar',
    method: 'post',
    data
  })
}

// 修改日程
export function updateCalendarEvent(data) {
  return request({
    url: '/oa/calendar',
    method: 'put',
    data
  })
}

// 删除日程
export function delCalendarEvent(eventId) {
  return request({
    url: '/oa/calendar/' + eventId,
    method: 'delete'
  })
}