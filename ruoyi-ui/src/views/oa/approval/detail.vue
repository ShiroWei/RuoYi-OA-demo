<template>
  <div class="app-container">
    <el-card shadow="never" class="detail-card">
      <div slot="header" class="card-header">
        <span>审批详情：{{ detail.title }}</span>
        <el-button type="text" icon="el-icon-back" @click="$router.back()">返回</el-button>
      </div>

      <el-row :gutter="32">
        <!-- 左侧：单据信息 -->
        <el-col :span="14">
          <el-descriptions title="单据信息" :column="2" border>
            <el-descriptions-item label="单号">{{ detail.id }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag size="small">{{ detail.type }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请人">{{ detail.applicant }}</el-descriptions-item>
            <el-descriptions-item label="所属部门">{{ detail.dept }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ detail.applyTime }}</el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag size="small" type="warning">{{ detail.status }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开始日期" v-if="detail.startDate">{{ detail.startDate }}</el-descriptions-item>
            <el-descriptions-item label="结束日期" v-if="detail.endDate">{{ detail.endDate }}</el-descriptions-item>
            <el-descriptions-item label="请假天数" v-if="detail.days">{{ detail.days }} 天</el-descriptions-item>
          </el-descriptions>

          <div class="reason-block">
            <div class="reason-title">申请事由</div>
            <div class="reason-content">{{ detail.reason }}</div>
          </div>

          <!-- 审批操作 -->
          <div class="action-bar" v-if="detail.status === '待审批'">
            <el-button type="success" icon="el-icon-check" @click="handlePass">通过</el-button>
            <el-button type="danger" icon="el-icon-close" @click="handleReject">驳回</el-button>
          </div>
        </el-col>

        <!-- 右侧：流程时间线 -->
        <el-col :span="10">
          <div class="flow-title">审批流程</div>
          <el-steps direction="vertical" :active="activeStep" class="flow-steps">
            <el-step
              v-for="(item, index) in flow"
              :key="index"
              :title="item.name"
              :description="item.description + (item.user ? '（' + item.user + '）' : '') + (item.time ? ' ' + item.time : '')"
            />
          </el-steps>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { getApprovalDetail, getApprovalFlow } from '@/api/approval'

export default {
  name: 'ApprovalDetail',
  data() {
    return {
      detail: {},
      flow: [],
      activeStep: 1
    }
  },
  created() {
    const id = this.$route.params.id || 'A20260816001'
    this.loadDetail(id)
    this.loadFlow()
  },
  methods: {
    loadDetail(id) {
      getApprovalDetail(id).then(res => {
        this.detail = res
      })
    },
    loadFlow() {
      getApprovalFlow().then(res => {
        this.flow = res
        const processIdx = res.findIndex(item => item.status === 'process')
        this.activeStep = processIdx >= 0 ? processIdx : 1
      })
    },
    handlePass() {
      this.$modal.msgSuccess('已通过，流程进入下一节点')
    },
    handleReject() {
      this.$modal.msgSuccess('已驳回，流程已退回发起人')
    }
  }
}
</script>

<style scoped>
.detail-card {
  border: none;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
.reason-block {
  margin-top: 24px;
}
.reason-title {
  font-weight: 600;
  margin-bottom: 8px;
}
.reason-content {
  background: #f8f9fb;
  border-radius: 6px;
  padding: 16px;
  line-height: 1.7;
  color: #333;
}
.action-bar {
  margin-top: 24px;
}
.flow-title {
  font-weight: 600;
  margin-bottom: 16px;
}
.flow-steps {
  padding-left: 4px;
}
</style>