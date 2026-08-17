<template>
  <div class="app-container">
    <el-row :gutter="16">
      <!-- 左侧部门树 -->
      <el-col :span="6">
        <el-card shadow="never" class="dept-card">
          <div slot="header" class="card-header">组织架构</div>
          <el-tree
            :data="deptTree"
            :props="deptProps"
            node-key="id"
            highlight-current
            default-expand-all
            :expand-on-click-node="false"
            @node-click="handleDeptClick"
          >
            <span slot-scope="{ node, data }" class="dept-node">
              <svg-icon icon-class="tree" class="dept-icon" />
              <span>{{ data.label }}</span>
            </span>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧人员列表 -->
      <el-col :span="18">
        <el-card shadow="never" class="contact-card">
          <div slot="header" class="card-header">
            <span>{{ currentDept }}（{{ contacts.length }} 人）</span>
          </div>
          <el-row :gutter="16" v-loading="loading">
            <el-col :xs="12" :sm="8" :lg="8" v-for="person in contacts" :key="person.id">
              <div class="contact-item">
                <el-avatar :size="44" class="contact-avatar">{{ person.name.charAt(0) }}</el-avatar>
                <div class="contact-info">
                  <div class="contact-name">{{ person.name }}</div>
                  <div class="contact-post">{{ person.post }}</div>
                </div>
                <div class="contact-actions">
                  <el-tooltip content="拨打电话" placement="top">
                    <el-button type="text" icon="el-icon-phone" @click="handleCall(person)" />
                  </el-tooltip>
                  <el-tooltip content="发送邮件" placement="top">
                    <el-button type="text" icon="el-icon-message" @click="handleMail(person)" />
                  </el-tooltip>
                </div>
              </div>
            </el-col>
          </el-row>
          <el-empty v-if="!loading && contacts.length === 0" description="该部门暂无人员" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getContactsTree, listContacts } from '@/api/contacts'

export default {
  name: 'Contacts',
  data() {
    return {
      deptTree: [],
      deptProps: { children: 'children', label: 'label' },
      contacts: [],
      loading: false,
      currentDept: '全部成员'
    }
  },
  created() {
    this.loadTree()
    this.loadContacts()
  },
  methods: {
    loadTree() {
      getContactsTree().then(res => {
        // 真实部门接口返回树形数据，这里做一层适配，保证叶子节点都含 children 字段
        const build = nodes => {
          return (nodes || []).map(n => ({
            id: n.deptId,
            label: n.deptName,
            children: build(n.children)
          }))
        }
        this.deptTree = build(res.data || res)
        if (this.deptTree.length) {
          this.currentDept = this.deptTree[0].label
        }
      }).catch(() => {
        // 接口异常时给出演示结构
        this.deptTree = [
          { id: 100, label: '演示科技有限公司', children: [
            { id: 101, label: '产品研发部' },
            { id: 102, label: '市场部' },
            { id: 103, label: '财务部' },
            { id: 104, label: '人事行政部' }
          ] }
        ]
      })
    },
    loadContacts() {
      this.loading = true
      listContacts().then(res => {
        this.contacts = res
        this.loading = false
      })
    },
    handleDeptClick(data) {
      this.currentDept = data.label
      this.loadContacts()
    },
    handleCall(person) {
      this.$modal.msgSuccess('正在拨打 ' + person.name + '（' + person.phone + '）')
    },
    handleMail(person) {
      this.$modal.msgSuccess('正在发送邮件至 ' + person.email)
    }
  }
}
</script>

<style scoped>
.dept-card {
  border: none;
}
.contact-card {
  border: none;
}
.card-header {
  font-weight: 600;
}
.dept-node {
  display: flex;
  align-items: center;
}
.dept-icon {
  margin-right: 6px;
  color: #1890ff;
}
.contact-item {
  display: flex;
  align-items: center;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 14px;
  margin-bottom: 12px;
  transition: box-shadow 0.2s;
}
.contact-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
.contact-avatar {
  background: #1890ff;
  color: #fff;
  flex-shrink: 0;
}
.contact-info {
  margin-left: 12px;
  flex: 1;
  min-width: 0;
}
.contact-name {
  font-weight: 600;
}
.contact-post {
  color: #909399;
  font-size: 12px;
  margin-top: 2px;
}
.contact-actions {
  display: flex;
}
</style>