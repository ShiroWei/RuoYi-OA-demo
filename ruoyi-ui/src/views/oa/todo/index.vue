<template>
  <div class="app-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16" class="todo-stat-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-todo">
          <div class="stat-num">{{ stat.todoCount }}</div>
          <div class="stat-label">待办事项</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-done">
          <div class="stat-num">{{ stat.doneCount }}</div>
          <div class="stat-label">今日已办</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-apply">
          <div class="stat-num">{{ stat.applyCount }}</div>
          <div class="stat-label">我发起的</div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-overdue">
          <div class="stat-num">{{ stat.overdueCount }}</div>
          <div class="stat-label">已超期</div>
        </div>
      </el-col>
    </el-row>

    <!-- 待办列表 -->
    <el-card class="todo-card" shadow="never">
      <el-tabs v-model="activeType" @tab-click="handleLoad">
        <el-tab-pane label="待我审批" name="pending">
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="单号" prop="id" width="160" align="center" />
            <el-table-column label="标题" prop="title" min-width="200" show-overflow-tooltip />
            <el-table-column label="类型" prop="type" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发起人" prop="applicant" width="100" align="center" />
            <el-table-column label="发起时间" prop="createTime" width="160" align="center" />
            <el-table-column label="优先级" prop="priority" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row.priority === '高' ? 'danger' : (scope.row.priority === '中' ? 'warning' : 'info')">{{ scope.row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" prop="status" width="90" align="center" />
            <el-table-column label="操作" width="140" align="center">
              <template slot-scope="scope">
                <el-button type="primary" size="mini" @click="handleGo(scope.row)">去审批</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="我已处理" name="done">
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="单号" prop="id" width="160" align="center" />
            <el-table-column label="标题" prop="title" min-width="200" show-overflow-tooltip />
            <el-table-column label="类型" prop="type" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发起人" prop="applicant" width="100" align="center" />
            <el-table-column label="发起时间" prop="createTime" width="160" align="center" />
            <el-table-column label="结果" prop="status" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini" :type="scope.row.status === '已通过' ? 'success' : 'danger'">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="我发起的" name="apply">
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="单号" prop="id" width="160" align="center" />
            <el-table-column label="标题" prop="title" min-width="200" show-overflow-tooltip />
            <el-table-column label="类型" prop="type" width="90" align="center">
              <template slot-scope="scope">
                <el-tag size="mini">{{ scope.row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发起时间" prop="createTime" width="160" align="center" />
            <el-table-column label="状态" prop="status" width="90" align="center" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { getTodoStat, listTodo } from '@/api/todo'

export default {
  name: 'Todo',
  data() {
    return {
      stat: {},
      activeType: 'pending',
      list: [],
      loading: false
    }
  },
  created() {
    this.loadStat()
    this.loadList()
  },
  methods: {
    loadStat() {
      getTodoStat().then(res => {
        this.stat = res
      })
    },
    loadList() {
      this.loading = true
      listTodo(this.activeType).then(res => {
        this.list = res
        this.loading = false
      })
    },
    handleLoad() {
      this.loadList()
    },
    handleGo(row) {
      this.$router.push({ path: '/oa/approval/detail/' + row.id })
    }
  }
}
</script>

<style scoped>
.todo-stat-row {
  margin-bottom: 16px;
}
.stat-card {
  border-radius: 8px;
  padding: 20px 24px;
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.stat-todo { background: linear-gradient(135deg, #1890ff, #36a3f7); }
.stat-done { background: linear-gradient(135deg, #13ce66, #36d67e); }
.stat-apply { background: linear-gradient(135deg, #ffba00, #ffcf3d); }
.stat-overdue { background: linear-gradient(135deg, #ff4949, #ff7d7d); }
.stat-num {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.2;
}
.stat-label {
  margin-top: 6px;
  font-size: 14px;
  opacity: 0.92;
}
.todo-card {
  border: none;
}
.todo-card >>> .el-tabs__item {
  font-size: 15px;
}
</style>