<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

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
        if (this.chart && val && val.series) {
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
        series: [
          { value: 335, name: '请假申请' },
          { value: 310, name: '报销申请' },
          { value: 234, name: '出差申请' },
          { value: 135, name: '用章申请' },
          { value: 148, name: '其他' }
        ]
      }
    },
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      if (this.chartData && this.chartData.series) {
        this.setOptions(this.chartData)
      } else {
        this.setOptions(this.defaultData())
      }
    },
    setOptions(chartData) {
      const { series } = chartData
      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: series.map(item => item.name)
        },
        series: [
          {
            name: '审批类型分布',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            data: series,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>