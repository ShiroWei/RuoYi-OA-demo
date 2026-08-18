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
            <el-descriptions-item label="单号">{{ detail.applyNo }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag size="small">{{ detail.applyType }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请人">{{ detail.applicant }}</el-descriptions-item>
            <el-descriptions-item label="所属部门">{{ detail.deptName }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ detail.applyTime }}</el-descriptions-item>
            <el-descriptions-item label="当前状态">
              <el-tag size="small" :type="detail.status === '1' ? 'success' : (detail.status === '2' ? 'danger' : 'warning')">{{ statusText(detail.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="当前环节">{{ detail.currentNode }}</el-descriptions-item>
            <el-descriptions-item label="开始日期" v-if="detail.startDate">{{ detail.startDate }}</el-descriptions-item>
            <el-descriptions-item label="结束日期" v-if="detail.endDate">{{ detail.endDate }}</el-descriptions-item>
            <el-descriptions-item label="请假天数" v-if="detail.days">{{ detail.days }} 天</el-descriptions-item>
          </el-descriptions>

          <div class="reason-block">
            <div class="reason-title">申请事由</div>
            <div class="reason-content">{{ detail.content }}</div>
          </div>

          <!-- 审批操作 -->
          <div class="action-bar" v-if="detail.status === '0'">
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
              :title="item.nodeName"
              :description="item.comment + (item.handler ? '（' + item.handler + '）' : '') + (item.handleTime ? ' ' + item.handleTime : '')"
            />
          </el-steps>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { getApprovalDetail, getApprovalFlow, approveApply, rejectApply } from '@/api/approval'

export default {
  name: 'ApprovalDetail',
  data() {
    return {
      detail: {},
      flow: [],
      activeStep: 1,
      applyId: null
    }
  },
  created() {
    this.applyId = this.$route.params.id
    this.loadDetail()
    this.loadFlow()
  },
  methods: {
    statusText(status) {
      return status === '1' ? '已通过' : (status === '2' ? '已驳回' : '待审批')
    },
    loadDetail() {
      getApprovalDetail(this.applyId).then(res => {
        this.detail = res
      })
    },
    loadFlow() {
      getApprovalFlow(this.applyId).then(res => {
        this.flow = res
        const processIdx = res.findIndex(item => item.status === 'process')
        this.activeStep = processIdx >= 0 ? processIdx : res.length - 1
      })
    },
    handlePass() {
      this.$prompt('请输入审批意见', '审批通过', {
        confirmButtonText: '确认通过',
        cancelButtonText: '取消',
        inputPlaceholder: '审批意见（可为空）'
      }).then(({ value }) => {
        approveApply(this.applyId, value || '同意').then(() => {
          this.$modal.msgSuccess('审批通过，流程进入下一节点')
          this.loadDetail()
          this.loadFlow()
        })
      }).catch(() => {})
    },
    handleReject() {
      this.$prompt('请输入驳回原因', '审批驳回', {
        confirmButtonText: '确认驳回',
        cancelButtonText: '取消',
        inputPlaceholder: '驳回原因'
      }).then(({ value }) => {
        rejectApply(this.applyId, value || '不同意').then(() => {
          this.$modal.msgSuccess('已驳回，流程已退回发起人')
          this.loadDetail()
          this.loadFlow()
        })
      }).catch(() => {})
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