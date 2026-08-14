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
        if (this.chart && val && val.pageA) {
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
    defaultData() {
      return {
        pageA: [30, 42, 35, 51, 49, 62, 69, 91, 126],
        pageB: [20, 32, 25, 41, 39, 52, 59, 71, 96],
        pageC: [10, 22, 15, 31, 29, 42, 49, 61, 76]
      }
    },
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      if (this.chartData && this.chartData.pageA) {
        this.setOptions(this.chartData)
      } else {
        this.setOptions(this.defaultData())
      }
    },
    setOptions(chartData) {
      const { pageA, pageB, pageC } = chartData
      const xData = []
      for (let i = 1; i <= pageA.length; i++) {
        xData.push('第' + i + '周')
      }
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
          data: ['报销申请', '出差申请', '请假申请']
        },
        grid: {
          top: 10,
          left: '2%',
          right: '2%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [{
          type: 'category',
          data: xData,
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
          name: '报销申请',
          type: 'bar',
          stack: 'apply',
          barWidth: '60%',
          data: pageA,
          animationDuration
        }, {
          name: '出差申请',
          type: 'bar',
          stack: 'apply',
          barWidth: '60%',
          data: pageB,
          animationDuration
        }, {
          name: '请假申请',
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