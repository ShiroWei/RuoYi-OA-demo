<template>
  <div class="app-container">
    <el-row :gutter="16">
      <!-- 左侧日历 -->
      <el-col :xs="24" :sm="15">
        <el-card shadow="never" class="calendar-card">
          <el-calendar v-model="currentDate">
            <template slot="dateCell" slot-scope="{ date, data }">
              <div class="calendar-cell" :class="{ 'is-selected': isSelected(date) }">
                <div class="cell-date">{{ data.day.split('-')[2] }}</div>
                <div v-for="ev in eventsOfDay(date)" :key="ev.eventId" class="cell-event" :title="ev.title">
                  {{ ev.startTime }} {{ ev.title }}
                </div>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </el-col>

      <!-- 右侧当日日程 -->
      <el-col :xs="24" :sm="9">
        <el-card shadow="never" class="event-card">
          <div slot="header" class="card-header">
            <span>{{ formattedDate }} 日程</span>
            <el-button type="primary" size="mini" @click="openForm()">新增日程</el-button>
          </div>
          <div v-loading="loading">
            <el-alert v-if="loadError" title="日程加载失败，请重试" type="error" :closable="false">
              <el-button type="text" @click="loadEvents">重新加载</el-button>
            </el-alert>
            <div v-for="ev in dayEvents" :key="ev.eventId" class="event-item">
              <div class="event-time">{{ ev.startTime + '-' + ev.endTime }}</div>
              <div class="event-body">
                <div class="event-title">{{ ev.title }}</div>
                <div class="event-meta">
                  <el-tag size="mini">{{ ev.eventType }}</el-tag>
                  <span class="event-location">{{ ev.location }}</span>
                </div>
                <el-button type="text" @click="openForm(ev)">编辑</el-button>
                <el-button type="text" :disabled="deletingId === ev.eventId" @click="handleDelete(ev)">删除</el-button>
              </div>
            </div>
            <el-empty v-if="!loading && !loadError && dayEvents.length === 0" description="当日暂无日程安排" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog :title="form.eventId ? '编辑日程' : '新增日程'" :visible.sync="formOpen" width="560px" append-to-body :close-on-click-modal="false" :before-close="closeForm">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px" :disabled="saving">
        <el-form-item label="标题" prop="title"><el-input v-model.trim="form.title" maxlength="200" /></el-form-item>
        <el-form-item label="日期" prop="eventDate"><el-date-picker v-model="form.eventDate" type="date" value-format="yyyy-MM-dd" :editable="false" /></el-form-item>
        <el-form-item label="开始时间" prop="startTime"><el-time-picker v-model="form.startTime" value-format="HH:mm" format="HH:mm" :editable="false" /></el-form-item>
        <el-form-item label="结束时间" prop="endTime"><el-time-picker v-model="form.endTime" value-format="HH:mm" format="HH:mm" :editable="false" /></el-form-item>
        <el-form-item label="类型" prop="eventType"><el-select v-model="form.eventType"><el-option v-for="type in ['会议', '汇报', '活动']" :key="type" :label="type" :value="type" /></el-select></el-form-item>
        <el-form-item label="地点"><el-input v-model="form.location" maxlength="200" /></el-form-item>
        <el-form-item label="参与人"><el-input v-model="form.participants" maxlength="500" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button :disabled="saving" @click="closeForm()">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCalendarEvent, addCalendarEvent, updateCalendarEvent, delCalendarEvent } from '@/api/calendar'

export default {
  name: 'Calendar',
  data() {
    return {
      currentDate: new Date(),
      events: [],
      loading: false,
      requestId: 0,
      loadError: false,
      formOpen: false,
      saving: false,
      deletingId: null,
      form: {},
      rules: {
        title: [{ required: true, whitespace: true, message: '请输入标题', trigger: 'blur' }],
        eventDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
        eventType: [{ required: true, message: '请选择类型', trigger: 'change' }]
      }
    }
  },
  computed: {
    dayEvents() {
      return this.eventsOfDay(this.currentDate)
    },
    formattedDate() {
      const d = this.currentDate
      return d.getFullYear() + ' 年 ' + (d.getMonth() + 1) + ' 月 ' + d.getDate() + ' 日'
    }
  },
  created() {
    this.loadEvents()
  },
  methods: {
    loadEvents() {
      const requestId = ++this.requestId
      this.loading = true
      this.loadError = false
      listCalendarEvent('').then(res => {
        if (requestId !== this.requestId) return
        this.events = res.data || []
      }).catch(() => {
        if (requestId !== this.requestId) return
        this.events = []
        this.loadError = true
      }).finally(() => {
        if (requestId === this.requestId) this.loading = false
      })
    },
    eventsOfDay(date) {
      return this.events.filter(ev => ev.eventDate === this.formatKey(date))
    },
    openForm(event) {
      this.form = event ? { ...event } : {
        title: '', eventDate: this.formatKey(this.currentDate), startTime: '09:00',
        endTime: '10:00', eventType: '会议', location: '', participants: ''
      }
      this.formOpen = true
      this.$nextTick(() => this.$refs.form.clearValidate())
    },
    closeForm() {
      if (!this.saving) this.formOpen = false
    },
    submitForm() {
      if (this.saving) return
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.endTime <= this.form.startTime) return this.$modal.msgWarning('结束时间必须晚于开始时间')
        this.saving = true
        const save = this.form.eventId ? updateCalendarEvent : addCalendarEvent
        save(this.form).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.formOpen = false
          const [year, month, day] = this.form.eventDate.split('-').map(Number)
          this.currentDate = new Date(year, month - 1, day)
          this.loadEvents()
        }).catch(() => {}).finally(() => { this.saving = false })
      })
    },
    handleDelete(event) {
      if (this.deletingId !== null) return
      this.deletingId = event.eventId
      this.$modal.confirm('确认删除日程“' + event.title + '”？').then(() => delCalendarEvent(event.eventId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadEvents()
      }).catch(() => {}).finally(() => { this.deletingId = null })
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
