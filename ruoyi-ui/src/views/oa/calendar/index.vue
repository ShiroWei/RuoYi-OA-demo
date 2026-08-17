<template>
  <div class="app-container">
    <el-row :gutter="16">
      <!-- 左侧日历 -->
      <el-col :span="15">
        <el-card shadow="never" class="calendar-card">
          <el-calendar v-model="currentDate">
            <template slot="dateCell" slot-scope="{ date, data }">
              <div class="calendar-cell" :class="{ 'is-selected': isSelected(date) }">
                <div class="cell-date">{{ data.day.split('-')[2] }}</div>
                <div v-for="ev in eventsOfDay(date)" :key="ev.id" class="cell-event" :title="ev.title">
                  {{ ev.time.split('-')[0] }} {{ ev.title }}
                </div>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </el-col>

      <!-- 右侧当日日程 -->
      <el-col :span="9">
        <el-card shadow="never" class="event-card">
          <div slot="header" class="card-header">
            <span>{{ formattedDate }} 日程</span>
          </div>
          <div v-loading="loading">
            <div v-for="ev in dayEvents" :key="ev.id" class="event-item">
              <div class="event-time">{{ ev.time }}</div>
              <div class="event-body">
                <div class="event-title">{{ ev.title }}</div>
                <div class="event-meta">
                  <el-tag size="mini">{{ ev.type }}</el-tag>
                  <span class="event-location">{{ ev.location }}</span>
                </div>
              </div>
            </div>
            <el-empty v-if="!loading && dayEvents.length === 0" description="当日暂无日程安排" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listCalendarEvent } from '@/api/calendar'

export default {
  name: 'Calendar',
  data() {
    const today = new Date()
    this._events = []
    return {
      currentDate: new Date(),
      dayEvents: [],
      loading: false
    }
  },
  computed: {
    formattedDate() {
      const d = this.currentDate
      return d.getFullYear() + ' 年 ' + (d.getMonth() + 1) + ' 月 ' + d.getDate() + ' 日'
    }
  },
  watch: {
    currentDate() {
      this.loadDayEvents()
    }
  },
  created() {
    this.loadEvents()
  },
  methods: {
    loadEvents() {
      this.loading = true
      listCalendarEvent('').then(res => {
        this._events = res
        this.loading = false
        this.loadDayEvents()
      })
    },
    loadDayEvents() {
      const d = this.currentDate
      const key = this.formatKey(d)
      this.dayEvents = this._events.filter(ev => ev.date === key)
    },
    eventsOfDay(date) {
      return this._events.filter(ev => ev.date === this.formatKey(date))
    },
    isSelected(date) {
      return this.formatKey(date) === this.formatKey(this.currentDate)
    },
    formatKey(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return y + '-' + m + '-' + day
    }
  }
}
</script>

<style scoped>
.calendar-card {
  border: none;
}
.event-card {
  border: none;
}
.card-header {
  font-weight: 600;
}
.calendar-cell {
  min-height: 56px;
  padding: 2px;
}
.cell-date {
  font-size: 13px;
}
.cell-event {
  background: #1890ff;
  color: #fff;
  border-radius: 3px;
  font-size: 11px;
  padding: 1px 4px;
  margin-top: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.calendar-cell.is-selected {
  background: rgba(24, 144, 255, 0.08);
  border-radius: 6px;
}
.event-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px dashed #ebeef5;
}
.event-item:last-child {
  border-bottom: none;
}
.event-time {
  width: 88px;
  color: #1890ff;
  font-weight: 600;
  font-size: 14px;
}
.event-title {
  font-weight: 500;
  margin-bottom: 4px;
}
.event-meta {
  display: flex;
  align-items: center;
}
.event-location {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}
</style>