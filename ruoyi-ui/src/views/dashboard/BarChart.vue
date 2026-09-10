<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

const animationDuration = 6000

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    },
    chartData: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        if (this.chart) {
          this.setOptions(val)
        }
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions(chartData) {
      const { pageA = [], pageB = [], pageC = [], dates = [] } = chartData || {}
      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { // 坐标轴指示器，坐标轴触发有效
            type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
          }
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['请假申请', '报销申请', '出差申请']
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: 50,
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: dates,
          axisTick: {
            alignWithLabel: true
          }
        }],
        yAxis: [{
          type: 'value',
          axisTick: {
            show: false
          }
        }],
        series: [{
          name: '请假申请',
          type: 'bar',
          stack: 'apply',
          barWidth: '60%',
          data: pageA,
          animationDuration
        }, {
          name: '报销申请',
          type: 'bar',
          stack: 'apply',
          barWidth: '60%',
          data: pageB,
          animationDuration
        }, {
          name: '出差申请',
          type: 'bar',
          stack: 'apply',
          barWidth: '60%',
          data: pageC,
          animationDuration
        }]
      })
    }
  }
}
</script>
