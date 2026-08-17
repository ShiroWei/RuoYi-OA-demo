<template>
  <div class="app-container">
    <!-- 操作栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleApply">发起申请</el-button>
      </el-col>
    </el-row>

    <el-card shadow="never" class="approval-card">
      <el-tabs v-model="activeType" @tab-click="handleLoad">
        <el-tab-pane label="待我审批" name="todo">
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="单号" prop="id" width="160" align="center" />
            <el-table-column label="标题" prop="title" min-width="200" show-overflow-tooltip>
              <template slot-scope="scope">
                <a class="link-type" @click="handleDetail(scope.row.id)">{{ scope.row.title }}</a>
              </template>
            </el-table-column>
            <el-table-column label="类型" prop="type" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申请人" prop="applicant" width="100" align="center" />
            <el-table-column label="申请时间" prop="applyTime" width="160" align="center" />
            <el-table-column label="天数/金额" width="120" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.days">{{ scope.row.days }} 天</span>
                <span v-else-if="scope.row.amount">¥ {{ scope.row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="160" align="center">
              <template slot-scope="scope">
                <el-button type="primary" size="mini" @click="handleDetail(scope.row.id)">审批</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="我已审批" name="done">
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="单号" prop="id" width="160" align="center" />
            <el-table-column label="标题" prop="title" min-width="200" show-overflow-tooltip>
              <template slot-scope="scope">
                <a class="link-type" @click="handleDetail(scope.row.id)">{{ scope.row.title }}</a>
              </template>
            </el-table-column>
            <el-table-column label="类型" prop="type" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="申请人" prop="applicant" width="100" align="center" />
            <el-table-column label="申请时间" prop="applyTime" width="160" align="center" />
            <el-table-column label="天数/金额" width="120" align="center">
              <template slot-scope="scope">
                <span v-if="scope.row.days">{{ scope.row.days }} 天</span>
                <span v-else-if="scope.row.amount">¥ {{ scope.row.amount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="结果" prop="status" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row.status === '已通过' ? 'success' : 'danger'">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { listApproval } from '@/api/approval'

export default {
  name: 'Approval',
  data() {
    return {
      activeType: 'todo',
      list: [],
      loading: false
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    loadList() {
      this.loading = true
      listApproval(this.activeType).then(res => {
        this.list = res
        this.loading = false
      })
    },
    handleLoad() {
      this.loadList()
    },
    handleApply() {
      this.$router.push('/oa/approval/apply')
    },
    handleDetail(id) {
      this.$router.push('/oa/approval/detail/' + id)
    }
  }
}
</script>

<style scoped>
.approval-card {
  border: none;
}
.approval-card >>> .el-tabs__item {
  font-size: 15px;
}
.link-type {
  color: #1890ff;
  cursor: pointer;
}
</style>