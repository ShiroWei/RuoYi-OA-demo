import request from '@/utils/request'

// 首页工作台统计卡片数据
// 当前返回 mock 演示数据；接入真实接口后，改为 request 调用即可：
// export function getPanelData() {
//   return request({
//     url: '/system/dashboard/panel',
//     method: 'get'
//   })
// }
export function getPanelData() {
  return Promise.resolve({
    todoCount: 12,
    msgCount: 8,
    leaveCount: 3,
    finishCount: 56,
    todayOnline: 128,
    weekLeave: 23
  })
}

// 首页折线图数据（近7日趋势）
// 真实接口：
// export function getLineChartData() {
//   return request({ url: '/system/dashboard/line', method: 'get' })
// }
export function getLineChartData() {
  return Promise.resolve({
    expectedData: [100, 120, 161, 134, 105, 160, 165],
    actualData: [120, 82, 91, 154, 162, 140, 145]
  })
}

// 首页柱状图数据（各部门审批量）
// 真实接口：
// export function getBarChartData() {
//   return request({ url: '/system/dashboard/bar', method: 'get' })
// }
export function getBarChartData() {
  return Promise.resolve({
    pageA: [30, 42, 35, 51, 49, 62, 69, 91, 126],
    pageB: [20, 32, 25, 41, 39, 52, 59, 71, 96],
    pageC: [10, 22, 15, 31, 29, 42, 49, 61, 76]
  })
}

// 首页饼图数据（审批单类型分布）
// 真实接口：
// export function getPieChartData() {
//   return request({ url: '/system/dashboard/pie', method: 'get' })
// }
export function getPieChartData() {
  return Promise.resolve({
    series: [
      { value: 335, name: '请假申请' },
      { value: 310, name: '报销申请' },
      { value: 234, name: '出差申请' },
      { value: 135, name: '用章申请' },
      { value: 148, name: '其他' }
    ]
  })
}

// 首页雷达图数据（办公效率多维评估）
// 真实接口：
// export function getRaddarChartData() {
//   return request({ url: '/system/dashboard/raddar', method: 'get' })
// }
export function getRaddarChartData() {
  return Promise.resolve({
    indicator: [
      { name: '审批效率', max: 100 },
      { name: '考勤达标', max: 100 },
      { name: '任务完成', max: 100 },
      { name: '协作沟通', max: 100 },
      { name: '文档规范', max: 100 },
      { name: '流程合规', max: 100 }
    ],
    series: [81, 92, 76, 68, 85, 88]
  })
}
