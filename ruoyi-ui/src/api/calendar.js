import request from '@/utils/request'

// 会议日程：按月份获取日程事件
// 当前返回 mock 演示数据；接入真实接口后，改为 request 调用即可：
// export function listCalendarEvent(month) {
//   return request({ url: '/oa/calendar/events', method: 'get', params: { month } })
// }
export function listCalendarEvent(month) {
  const day = month ? month.slice(-2) : '16'
  const events = [
    { id: 'C1', date: '2026-08-16', title: '产品需求评审会', time: '09:30-11:00', location: '3F 会议室A', type: '会议' },
    { id: 'C2', date: '2026-08-16', title: '与供应商视频会议', time: '14:00-15:00', location: '线上', type: '会议' },
    { id: 'C3', date: '2026-08-18', title: '项目周会', time: '10:00-11:30', location: '3F 会议室B', type: '会议' },
    { id: 'C4', date: '2026-08-20', title: '季度总结汇报', time: '15:00-17:00', location: '2F 大会议室', type: '汇报' },
    { id: 'C5', date: '2026-08-25', title: '团建活动', time: '全天', location: '郊外营地', type: '活动' }
  ]
  return Promise.resolve(events)
}