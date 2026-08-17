<template>
  <div class="dashboard-editor-container">
    <!-- 顶部欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-info">
        <div class="welcome-title">欢迎回来，{{ name }}</div>
        <div class="welcome-sub">今天是 {{ today }}，愿你有一个高效的工作日</div>
      </div>
      <div class="welcome-stats">
        <div class="stat-item">
          <div class="stat-num">{{ panel.todoCount }}</div>
          <div class="stat-label">待办事项</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">{{ panel.msgCount }}</div>
          <div class="stat-label">未读消息</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">{{ panel.leaveCount }}</div>
          <div class="stat-label">审批中</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">{{ panel.finishCount }}</div>
          <div class="stat-label">本月完成</div>
        </div>
      </div>
    </div>

    <el-row :gutter="32" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-panel" @click="handleSetLineChartData('todo')">
          <div class="card-panel-icon-wrapper icon-todo">
            <svg-icon icon-class="message" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">待办审批</div>
            <count-to :start-val="0" :end-val="panel.todoCount" :duration="2600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-panel" @click="handleSetLineChartData('msg')">
          <div class="card-panel-icon-wrapper icon-msg">
            <svg-icon icon-class="bell" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">未读消息</div>
            <count-to :start-val="0" :end-val="panel.msgCount" :duration="3000" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-panel" @click="handleSetLineChartData('leave')">
          <div class="card-panel-icon-wrapper icon-leave">
            <svg-icon icon-class="time" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">请假审批中</div>
            <count-to :start-val="0" :end-val="panel.leaveCount" :duration="3200" class="card-panel-num" />
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-panel" @click="handleSetLineChartData('finish')">
          <div class="card-panel-icon-wrapper icon-finish">
            <svg-icon icon-class="tree" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">本月已完成</div>
            <count-to :start-val="0" :end-val="panel.finishCount" :duration="3600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <div class="chart-title">近 7 日工作动态趋势</div>
      <line-chart :chart-data="lineChartData" />
    </el-row>

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <div class="chart-title">协同办公效率评估</div>
          <raddar-chart :chart-data="raddarChartData" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <div class="chart-title">审批类型分布</div>
          <pie-chart :chart-data="pieChartData" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <div class="chart-title">各部门申请量</div>
          <bar-chart :chart-data="barChartData" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import LineChart from './LineChart'
import RaddarChart from './RaddarChart'
import PieChart from './PieChart'
import BarChart from './BarChart'
import { getPanelData, getLineChartData, getBarChartData, getPieChartData, getRaddarChartData } from '@/api/dashboard'

const defaultLineData = {
  todo: {
    expectedData: [100, 115, 109, 128, 110, 106, 112],
    actualData: [88, 102, 96, 115, 99, 95, 112]
  },
  msg: {
    expectedData: [120, 118, 125, 116, 122, 128, 108],
    actualData: [105, 102, 108, 100, 106, 110, 108]
  },
  leave: {
    expectedData: [83, 95, 84, 72, 96, 83, 73],
    actualData: [72, 84, 73, 61, 85, 72, 73]
  },
  finish: {
    expectedData: [140, 152, 145, 161, 149, 158, 156],
    actualData: [125, 135, 130, 142, 133, 140, 156]
  }
}

export default {
  name: 'Dashboard',
  components: {
    CountTo,
    LineChart,
    RaddarChart,
    PieChart,
    BarChart
  },
  data() {
    return {
      name: '管理员',
      today: '',
      panel: {
        todoCount: 0,
        msgCount: 0,
        leaveCount: 0,
        finishCount: 0,
        todayOnline: 0,
        weekLeave: 0
      },
      lineChartData: defaultLineData.todo,
      barChartData: {},
      pieChartData: {},
      raddarChartData: {}
    }
  },
  created() {
    this.today = this.formatToday()
    this.loadData()
    this.name = this.$store.state.user.name || '管理员'
  },
  methods: {
    formatToday() {
      const d = new Date()
      const week = ['日', '一', '二', '三', '四', '五', '六']
      return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${week[d.getDay()]}`
    },
    loadData() {
      getPanelData().then(res => {
        this.panel = res
      })
      getLineChartData().then(res => {
        defaultLineData.todo.expectedData = res.expectedData
        defaultLineData.todo.actualData = res.actualData
        this.lineChartData = defaultLineData.todo
      })
      getBarChartData().then(res => {
        this.barChartData = res
      })
      getPieChartData().then(res => {
        this.pieChartData = res
      })
      getRaddarChartData().then(res => {
        this.raddarChartData = res
      })
    },
    handleSetLineChartData(type) {
      this.lineChartData = defaultLineData[type]
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 0 16px;

  .welcome-banner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: linear-gradient(135deg, #1890ff 0%, #36a3f7 100%);
    border-radius: 8px;
    padding: 24px 32px;
    margin: 18px 0 0 0;
    color: #fff;

    .welcome-title {
      font-size: 22px;
      font-weight: 600;
    }

    .welcome-sub {
      margin-top: 8px;
      font-size: 14px;
      opacity: 0.9;
    }

    .welcome-stats {
      display: flex;

      .stat-item {
        text-align: center;
        margin-left: 40px;

        .stat-num {
          font-size: 26px;
          font-weight: 700;
        }

        .stat-label {
          margin-top: 4px;
          font-size: 13px;
          opacity: 0.9;
        }
      }
    }
  }

  .panel-group {
    margin-top: 32px;

    .card-panel {
      height: 108px;
      cursor: pointer;
      font-size: 12px;
      position: relative;
      overflow: hidden;
      color: #666;
      background: #fff;
      box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
      border-color: rgba(0, 0, 0, .05);

      &:hover {
        .card-panel-icon-wrapper {
          color: #fff;
        }

        .icon-todo {
          background: #40c9c6;
        }

        .icon-msg {
          background: #36a3f7;
        }

        .icon-leave {
          background: #f4516c;
        }

        .icon-finish {
          background: #34bfa3;
        }
      }

      .icon-todo {
        color: #40c9c6;
      }

      .icon-msg {
        color: #36a3f7;
      }

      .icon-leave {
        color: #f4516c;
      }

      .icon-finish {
        color: #34bfa3;
      }

      .card-panel-icon-wrapper {
        float: left;
        margin: 14px 0 0 14px;
        padding: 16px;
        transition: all 0.38s ease-out;
        border-radius: 6px;
      }

      .card-panel-icon {
        float: left;
        font-size: 48px;
      }

      .card-panel-description {
        float: right;
        font-weight: bold;
        margin: 26px;
        margin-left: 0px;

        .card-panel-text {
          line-height: 18px;
          color: rgba(0, 0, 0, 0.45);
          font-size: 16px;
          margin-bottom: 12px;
        }

        .card-panel-num {
          font-size: 20px;
        }
      }
    }
  }

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }

  .chart-title {
    font-size: 15px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }
}

@media (max-width: 900px) {
  .welcome-banner {
    flex-direction: column;

    .welcome-stats {
      margin-top: 16px;

      .stat-item {
        margin-left: 20px;
        margin-right: 20px;
      }
    }
  }
}

@media (max-width: 550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>