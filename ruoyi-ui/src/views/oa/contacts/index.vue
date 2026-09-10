<template>
  <div class="app-container">
    <el-row :gutter="16">
      <!-- 左侧部门树 -->
      <el-col :xs="24" :sm="6">
        <el-card shadow="never" class="dept-card">
          <div slot="header" class="card-header">组织架构</div>
          <el-alert v-if="treeError" title="部门加载失败，请检查部门查询权限或重试" type="error" :closable="false">
            <el-button type="text" @click="loadTree">重新加载</el-button>
          </el-alert>
          <el-tree
            v-loading="treeLoading"
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
      <el-col :xs="24" :sm="18">
        <el-card shadow="never" class="contact-card">
          <div slot="header" class="card-header">
            <span>{{ currentDept }}（{{ total }} 人）</span>
            <el-button type="primary" size="mini" :disabled="treeLoading || treeError" @click="openForm()">新增人员</el-button>
          </div>
          <el-alert v-if="loadError" title="人员加载失败，请重试" type="error" :closable="false">
            <el-button type="text" @click="loadContacts">重新加载</el-button>
          </el-alert>
          <el-row :gutter="16" v-loading="loading">
            <el-col :xs="12" :sm="8" :lg="8" v-for="person in contacts" :key="person.personId">
              <div class="contact-item">
                <el-avatar :size="44" class="contact-avatar">{{ (person.name || '').charAt(0) }}</el-avatar>
                <div class="contact-info">
                  <div class="contact-name">{{ person.name }}</div>
                  <div class="contact-post">{{ person.post }}</div>
                  <el-button type="text" :disabled="treeLoading || treeError" @click="openForm(person)">编辑</el-button>
                  <el-button type="text" :disabled="deletingId === person.personId" @click="handleDelete(person)">删除</el-button>
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
          <el-empty v-if="!loading && !loadError && contacts.length === 0" description="该部门暂无人员" :image-size="80" />
          <pagination v-show="total > 0" :total="total" :page.sync="query.pageNum" :limit.sync="query.pageSize" @pagination="loadContacts" />
        </el-card>
      </el-col>
    </el-row>
    <el-dialog :title="form.personId ? '编辑人员' : '新增人员'" :visible.sync="formOpen" width="560px" append-to-body :close-on-click-modal="false" :before-close="closeForm">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px" :disabled="saving">
        <el-form-item label="姓名" prop="name"><el-input v-model.trim="form.name" maxlength="64" /></el-form-item>
        <el-form-item label="部门" prop="deptId"><treeselect v-model="form.deptId" :options="deptOptions" :disabled="saving" placeholder="请选择真实部门" /></el-form-item>
        <el-form-item label="岗位"><el-input v-model="form.post" maxlength="64" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model.trim="form.phone" maxlength="32" /></el-form-item>
        <el-form-item label="办公电话"><el-input v-model.trim="form.officePhone" maxlength="32" /></el-form-item>
        <el-form-item label="邮箱" prop="email"><el-input v-model.trim="form.email" maxlength="128" /></el-form-item>
      </el-form>
      <div slot="footer">
        <el-button :disabled="saving" @click="closeForm()">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getContactsTree, listContacts, addContact, updateContact, delContact } from '@/api/contacts'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  name: 'Contacts',
  components: { Treeselect },
  data() {
    return {
      deptTree: [],
      deptOptions: [],
      departments: [],
      treeLoading: false,
      treeError: false,
      deptProps: { children: 'children', label: 'label' },
      contacts: [],
      loading: false,
      loadError: false,
      requestId: 0,
      total: 0,
      query: { pageNum: 1, pageSize: 12 },
      formOpen: false,
      saving: false,
      deletingId: null,
      form: {},
      rules: {
        name: [{ required: true, whitespace: true, message: '请输入姓名', trigger: 'blur' }],
        deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
        email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }]
      },
      currentDept: '全部成员',
      selectedDeptId: null
    }
  },
  created() {
    this.loadTree()
    this.loadContacts()
  },
  methods: {
    loadTree() {
      if (this.treeLoading) return
      this.treeLoading = true
      this.treeError = false
      getContactsTree().then(res => {
        const build = nodes => {
          return (nodes || []).map(n => ({
            id: n.deptId,
            label: n.deptName,
            ...(n.children && n.children.length ? { children: build(n.children) } : {})
          }))
        }
        this.departments = res.data || []
        this.deptOptions = build(this.handleTree(this.departments, 'deptId'))
        this.deptTree = [{ id: 'all', label: '全部成员', children: this.deptOptions }]
      }).catch(() => {
        this.deptTree = []
        this.deptOptions = []
        this.departments = []
        this.treeError = true
      }).finally(() => {
        this.treeLoading = false
      })
    },
    loadContacts() {
      const requestId = ++this.requestId
      this.loading = true
      this.loadError = false
      const query = { ...this.query }
      if (this.selectedDeptId) query.deptId = this.selectedDeptId
      listContacts(query).then(res => {
        if (requestId !== this.requestId) return
        this.contacts = res.rows || []
        this.total = res.total || 0
        if (!this.contacts.length && this.total > 0 && this.query.pageNum > 1) {
          this.query.pageNum = Math.ceil(this.total / this.query.pageSize)
          this.loadContacts()
        }
      }).catch(() => {
        if (requestId !== this.requestId) return
        this.contacts = []
        this.total = 0
        this.loadError = true
      }).finally(() => {
        if (requestId === this.requestId) this.loading = false
      })
    },
    handleDeptClick(data) {
      this.currentDept = data.label
      this.selectedDeptId = data.id === 'all' ? null : data.id
      this.query.pageNum = 1
      this.loadContacts()
    },
    openForm(person) {
      this.form = person ? { ...person } : {
        name: '', deptId: this.selectedDeptId, post: '', phone: '', officePhone: '', email: ''
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
        const dept = this.departments.find(item => item.deptId === this.form.deptId)
        if (!dept) return this.$modal.msgWarning('请选择有效部门')
        this.saving = true
        const save = this.form.personId ? updateContact : addContact
        const { personId, name, deptId, post, phone, officePhone, email } = this.form
        save({ personId, name, deptId, deptName: dept.deptName, post, phone, officePhone, email }).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.formOpen = false
          this.loadContacts()
        }).catch(() => {}).finally(() => { this.saving = false })
      })
    },
    handleDelete(person) {
      if (this.deletingId !== null) return
      this.deletingId = person.personId
      this.$modal.confirm('确认删除人员“' + person.name + '”？').then(() => delContact(person.personId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadContacts()
      }).catch(() => {}).finally(() => { this.deletingId = null })
    },
    handleCall(person) {
      const phone = person.phone || person.officePhone
      if (!phone) return this.$modal.msgWarning('未填写联系电话')
      window.location.href = 'tel:' + encodeURIComponent(phone)
    },
    handleMail(person) {
      if (!person.email) return this.$modal.msgWarning('未填写邮箱')
      window.location.href = 'mailto:' + encodeURIComponent(person.email)
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
