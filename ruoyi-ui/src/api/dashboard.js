import request from '@/utils/request'

// 首页工作台统计卡片数据（真实聚合：待办/审批/通讯录）
export function getPanelData() {
  return request({
    url: '/oa/dashboard/panel',
    method: 'get'
  })
}

// 首页折线图数据（近7日申请/通过趋势）
export function getLineChartData() {
  return request({
    url: '/oa/dashboard/line',
    method: 'get'
  })
}

// 首页柱状图数据（近9周各部门申请量）
export function getBarChartData() {
  return request({
    url: '/oa/dashboard/bar',
    method: 'get'
  })
}

// 首页饼图数据（审批单类型分布）
export function getPieChartData() {
  return request({
    url: '/oa/dashboard/pie',
    method: 'get'
  })
}

// 首页雷达图数据（办公效率多维评估，暂无真实业务数据源，保留演示数据）
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