<template>
  <div class="dashboard-editor-container">
    <!-- 顶部欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-info">
        <div class="welcome-title">欢迎回来，{{ name }}</div>
        <div class="welcome-sub">今天是 {{ today }}，统计为全平台口径，待办列表为个人口径</div>
      </div>
      <div v-if="loadState.panel === 'success'" class="welcome-stats">
        <div class="stat-item">
          <div class="stat-num">{{ panel.todoCount }}</div>
          <div class="stat-label">待办事项</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">{{ panel.leaveCount }}</div>
          <div class="stat-label">请假审批中</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">{{ panel.finishCount }}</div>
          <div class="stat-label">本月申请已通过</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">{{ panel.todayOnline }}</div>
          <div class="stat-label">同事总数</div>
        </div>
      </div>
    </div>
    <el-alert v-if="loadState.panel !== 'success'" :title="loadState.panel === 'error' ? '统计加载失败，无法显示数量' : '统计加载中'" :type="loadState.panel === 'error' ? 'error' : 'info'" :closable="false" />
    <el-button v-if="Object.values(loadState).includes('error')" type="text" @click="loadData(); loadTodo(); loadSchedule(); loadNotice()">重新加载失败数据</el-button>

    <!-- 快捷入口 -->
    <el-row :gutter="16" class="quick-entry-row">
      <el-col :xs="12" :sm="6">
        <div class="quick-entry" @click="handleGo('/oa/approval/apply')">
          <div class="quick-icon icon-apply"><svg-icon icon-class="form" /></div>
          <div class="quick-text">发起申请</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="quick-entry" @click="handleGo('/oa/todo')">
          <div class="quick-icon icon-todo-entry"><svg-icon icon-class="message" /></div>
          <div class="quick-text">我的待办</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="quick-entry" @click="handleGo('/oa/calendar')">
          <div class="quick-icon icon-calendar"><svg-icon icon-class="date" /></div>
          <div class="quick-text">会议日程</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="quick-entry" @click="handleGo('/oa/contacts')">
          <div class="quick-icon icon-contacts"><svg-icon icon-class="people" /></div>
          <div class="quick-text">通讯录</div>
        </div>
      </el-col>
    </el-row>

    <!-- 今日待办 + 日程公告 -->
    <el-row :gutter="16" class="workspace-row">
      <el-col :span="14">
        <el-card shadow="never" class="workspace-card">
          <div slot="header" class="card-header">
            <span>今日待办</span>
            <el-button type="text" @click="handleGo('/oa/todo')">查看全部</el-button>
          </div>
          <div v-loading="todoLoading">
            <el-alert v-if="loadState.todo === 'error'" title="待办加载失败，请重试" type="error" :closable="false" />
            <div v-for="item in todoList" :key="item.todoId" class="todo-item" @click="handleGo(item.bizType === 'approval' && item.bizId ? '/oa/approval/detail/' + item.bizId : '/oa/todo')">
              <el-tag size="mini" :type="priorityType(item.priority)">{{ item.priority }}</el-tag>
              <span class="todo-title">{{ item.title }}</span>
              <span class="todo-time">{{ item.submitTime }}</span>
            </div>
            <el-empty v-if="loadState.todo === 'success' && todoList.length === 0" description="暂无待办事项" :image-size="70" />
          </div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="never" class="workspace-card">
          <div slot="header" class="card-header">
            <span>今日日程</span>
            <el-button type="text" @click="handleGo('/oa/calendar')">更多</el-button>
          </div>
          <el-alert v-if="loadState.schedule !== 'success'" :title="loadState.schedule === 'error' ? '日程加载失败，请重试' : '日程加载中'" :type="loadState.schedule === 'error' ? 'error' : 'info'" :closable="false" />
          <div v-for="ev in dayEvents" :key="ev.eventId" class="schedule-item">
            <div class="schedule-time">{{ ev.startTime }}</div>
            <div class="schedule-title">{{ ev.title }}</div>
          </div>
          <el-empty v-if="loadState.schedule === 'success' && dayEvents.length === 0" description="今日暂无日程" :image-size="70" />
        </el-card>

        <el-card shadow="never" class="workspace-card notice-card">
          <div slot="header" class="card-header">
            <span>最新公告</span>
          </div>
          <el-alert v-if="loadState.notice !== 'success'" :title="loadState.notice === 'error' ? '公告加载失败，请重试' : '公告加载中'" :type="loadState.notice === 'error' ? 'error' : 'info'" :closable="false" />
          <div v-for="n in notices" :key="n.noticeId" class="notice-item">
            <span class="notice-title">{{ n.noticeTitle }}</span>
            <span class="notice-time">{{ n.createTime }}</span>
          </div>
          <el-empty v-if="loadState.notice === 'success' && notices.length === 0" description="暂无公告" :image-size="70" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计卡片 -->
    <el-row v-if="loadState.panel === 'success'" :gutter="32" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-panel">
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
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-msg">
            <svg-icon icon-class="bell" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">未读消息</div>
            <count-to v-if="loadState.notice === 'success'" :start-val="0" :end-val="panel.msgCount" :duration="3000" class="card-panel-num" />
            <span v-else>{{ loadState.notice === 'error' ? '加载失败' : '加载中' }}</span>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6">
        <div class="card-panel">
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
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-finish">
            <svg-icon icon-class="tree" class-name="card-panel-icon" />
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">本月申请已通过</div>
            <count-to :start-val="0" :end-val="panel.finishCount" :duration="3600" class="card-panel-num" />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <div class="chart-title">近 7 日申请及已通过数量（按申请日期）</div>
      <el-alert v-if="loadState.line !== 'success'" :title="loadState.line === 'error' ? '趋势加载失败，请重试' : '趋势加载中'" :type="loadState.line === 'error' ? 'error' : 'info'" :closable="false" />
      <line-chart v-else :chart-data="lineChartData" />
    </el-row>

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <div class="chart-title">协同办公效率评估（模拟数据）</div>
          <raddar-chart :chart-data="raddarChartData" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <div class="chart-title">审批类型分布</div>
          <el-alert v-if="loadState.pie !== 'success'" :title="loadState.pie === 'error' ? '分布加载失败，请重试' : '分布加载中'" :type="loadState.pie === 'error' ? 'error' : 'info'" :closable="false" />
          <pie-chart v-else :chart-data="pieChartData" />
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <div class="chart-title">近 9 周各类型申请量（周一起始）</div>
          <el-alert v-if="loadState.bar !== 'success'" :title="loadState.bar === 'error' ? '申请量加载失败，请重试' : '申请量加载中'" :type="loadState.bar === 'error' ? 'error' : 'info'" :closable="false" />
          <bar-chart v-else :chart-data="barChartData" />
        </div>
      </el-col>
    </el-row>

    <ai-assistant />
  </div>
</template>

<script>
import CountTo from 'vue-count-to'
import LineChart from './LineChart'
import RaddarChart from './RaddarChart'
import PieChart from './PieChart'
import BarChart from './BarChart'
import AiAssistant from './AiAssistant'
import { getPanelData, getLineChartData, getBarChartData, getPieChartData, getRaddarChartData } from '@/api/dashboard'
import { listTodo } from '@/api/todo'
import { listCalendarEvent } from '@/api/calendar'
import { listNoticeTop } from '@/api/system/notice'

export default {
  name: 'Dashboard',
  components: {
    CountTo,
    LineChart,
    RaddarChart,
    PieChart,
    BarChart,
    AiAssistant
  },
  data() {
    return {
      name: '管理员',
      today: '',
      loadState: { panel: '', line: '', bar: '', pie: '', todo: '', schedule: '', notice: '' },
      panel: {
        todoCount: 0,
        msgCount: 0,
        leaveCount: 0,
        finishCount: 0,
        todayOnline: 0,
        weekLeave: 0
      },
      todoList: [],
      todoLoading: false,
      dayEvents: [],
      notices: [],
      lineChartData: {},
      barChartData: {},
      pieChartData: {},
      raddarChartData: {}
    }
  },
  created() {
    this.today = this.formatToday()
    this.loadData()
    this.loadTodo()
    this.loadSchedule()
    this.loadNotice()
    this.name = this.$store.state.user.name || '管理员'
  },
  methods: {
    loadSection(key, request, apply) {
      if (this.loadState[key] === 'loading' || this.loadState[key] === 'success') return
      this.loadState[key] = 'loading'
      return request().then(res => {
        apply(res)
        this.loadState[key] = 'success'
      }).catch(() => {
        this.loadState[key] = 'error'
      })
    },
    formatToday() {
      const d = new Date()
      const week = ['日', '一', '二', '三', '四', '五', '六']
      return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${week[d.getDay()]}`
    },
    loadData() {
      this.loadSection('panel', getPanelData, res => {
        const { msgCount, ...panel } = res.data
        this.panel = Object.assign({}, this.panel, panel)
      })
      this.loadSection('line', getLineChartData, res => {
        this.lineChartData = res.data || {}
      })
      this.loadSection('bar', getBarChartData, res => {
        this.barChartData = res.data || {}
      })
      this.loadSection('pie', getPieChartData, res => {
        this.pieChartData = res.data || {}
      })
      getRaddarChartData().then(res => {
        this.raddarChartData = res
      })
    },
    loadTodo() {
      if (this.loadState.todo === 'loading' || this.loadState.todo === 'success') return
      this.todoLoading = true
      this.loadSection('todo', () => listTodo('pending'), res => {
        this.todoList = (res.rows || []).slice(0, 4)
      }).finally(() => {
        this.todoLoading = false
      })
    },
    loadSchedule() {
      this.loadSection('schedule', () => listCalendarEvent(''), res => {
        const d = this.formatKey(new Date())
        this.dayEvents = (res.data || []).filter(ev => ev.eventDate === d)
      })
    },
    loadNotice() {
      this.loadSection('notice', listNoticeTop, res => {
        const list = res.data || []
        this.notices = Array.isArray(list) ? list.slice(0, 3) : []
        this.panel.msgCount = res.unreadCount || 0
      })
    },
    formatKey(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return y + '-' + m + '-' + day
    },
    priorityType(priority) {
      if (priority === '高') return 'danger'
      if (priority === '中') return 'warning'
      return 'info'
    },
    handleGo(path) {
      this.$router.push(path)
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

  .quick-entry-row {
    margin-top: 16px;

    .quick-entry {
      display: flex;
      align-items: center;
      background: #fff;
      border-radius: 8px;
      padding: 18px 20px;
      cursor: pointer;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      transition: box-shadow 0.2s;

      &:hover {
        box-shadow: 0 4px 16px rgba(24, 144, 255, 0.18);
      }

      .quick-icon {
        width: 42px;
        height: 42px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 22px;
        color: #fff;
        margin-right: 12px;

        &.icon-apply { background: linear-gradient(135deg, #1890ff, #36a3f7); }
        &.icon-todo-entry { background: linear-gradient(135deg, #13ce66, #36d67e); }
        &.icon-calendar { background: linear-gradient(135deg, #ffba00, #ffcf3d); }
        &.icon-contacts { background: linear-gradient(135deg, #722ed1, #9254de); }
      }

      .quick-text {
        font-weight: 600;
        color: #333;
      }
    }
  }

  .workspace-row {
    margin-top: 16px;

    .workspace-card {
      border: none;
      margin-bottom: 16px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: 600;
      }
    }

    .todo-item {
      display: flex;
      align-items: center;
      padding: 10px 0;
      border-bottom: 1px dashed #ebeef5;
      cursor: pointer;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        .todo-title {
          color: #1890ff;
        }
      }

      .todo-title {
        margin: 0 12px;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .todo-time {
        color: #909399;
        font-size: 12px;
      }
    }

    .schedule-item {
      display: flex;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px dashed #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .schedule-time {
        width: 72px;
        color: #1890ff;
        font-weight: 600;
        font-size: 13px;
      }

      .schedule-title {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .notice-item {
      display: flex;
      align-items: center;
      padding: 8px 0;
      border-bottom: 1px dashed #ebeef5;

      &:last-child {
        border-bottom: none;
      }

      .notice-title {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .notice-time {
        color: #909399;
        font-size: 12px;
      }
    }
  }

  .panel-group {
    margin-top: 32px;

    .card-panel {
      height: 108px;
      font-size: 12px;
      position: relative;
      overflow: hidden;
      color: #666;
      background: #fff;
      box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
      border-color: rgba(0, 0, 0, .05);

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
  .dashboard-editor-container .panel-group .card-panel {
    margin-bottom: 16px;

    .card-panel-icon-wrapper {
      display: none;
    }

    .card-panel-description {
      float: none;
      margin: 20px 8px;
      text-align: center;

      .card-panel-text {
        font-size: 13px;
      }
    }
  }
}
</style>
