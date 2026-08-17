<template>
  <div class="app-container">
    <el-card shadow="never" class="apply-card">
      <div slot="header" class="card-header">
        <span>发起申请</span>
        <el-button type="text" icon="el-icon-back" @click="$router.back()">返回</el-button>
      </div>
      <el-form ref="applyForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="申请类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button label="请假">请假</el-radio-button>
            <el-radio-button label="报销">报销</el-radio-button>
            <el-radio-button label="出差">出差</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <template v-if="form.type !== '报销'">
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 220px" />
          </el-form-item>
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" value-format="yyyy-MM-dd" style="width: 220px" />
          </el-form-item>
        </template>

        <template v-if="form.type === '报销'">
          <el-form-item label="报销金额" prop="amount">
            <el-input-number v-model="form.amount" :min="0" :precision="2" :step="100" style="width: 220px" />
          </el-form-item>
        </template>

        <el-form-item label="申请事由" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            placeholder="请填写申请事由，尽量详细说明原因与安排"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { submitApply } from '@/api/approval'

export default {
  name: 'ApprovalApply',
  data() {
    return {
      submitting: false,
      form: {
        type: '请假',
        startDate: '',
        endDate: '',
        amount: 0,
        reason: ''
      },
      rules: {
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
        amount: [{ required: true, message: '请填写报销金额', trigger: 'change' }],
        reason: [{ required: true, message: '请填写申请事由', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleSubmit() {
      this.$refs.applyForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        submitApply(this.form).then(res => {
          this.submitting = false
          this.$modal.msgSuccess('提交成功，单号：' + res.id)
          this.$router.push('/oa/approval/detail/' + res.id)
        })
      })
    },
    resetForm() {
      this.$refs.applyForm.resetFields()
    }
  }
}
</script>

<style scoped>
.apply-card {
  border: none;
  max-width: 720px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
</style>