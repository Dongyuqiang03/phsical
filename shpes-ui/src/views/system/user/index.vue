<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="form-inline">
        <el-form-item>
          <el-input
            v-model="listQuery.username"
            placeholder="用户名"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="listQuery.realName"
            placeholder="姓名"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.roleId" placeholder="角色" clearable>
            <el-option
              v-for="item in roleOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.departmentId" placeholder="部门" clearable>
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-container">
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增用户</el-button>
      <el-button type="danger" icon="el-icon-delete" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
      <el-button type="success" icon="el-icon-upload2" @click="handleImport">导入</el-button>
      <el-button type="warning" icon="el-icon-download" @click="handleExport">导出</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="姓名" prop="realName" />
      <el-table-column label="角色" prop="roleName" />
      <el-table-column label="部门" prop="departmentName" />
      <el-table-column label="手机号" prop="phone" />
      <el-table-column label="邮箱" prop="email" />
      <el-table-column label="状态" align="center">
        <template slot-scope="{row}">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="230">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
          <el-button type="warning" size="mini" @click="handleResetPwd(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <!-- 用户表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="temp.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="temp.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="temp.roleId" placeholder="请选择角色">
            <el-option
              v-for="item in roleOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <el-select v-model="temp.departmentId" placeholder="请选择部门">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="temp.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="temp.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="temp.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog title="导入用户" :visible.sync="importVisible" width="400px">
      <el-upload
        class="upload-demo"
        drag
        action="/api/system/user/import"
        :headers="headers"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        accept=".xls,.xlsx">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传excel文件，且不超过10MB</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="downloadTemplate">下载模板</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination'
import { validUsername, validPhone, validEmail } from '@/utils/validate'

export default {
  name: 'User',
  components: { Pagination },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('请输入正确的用户名'))
      } else {
        callback()
      }
    }
    const validatePhone = (rule, value, callback) => {
      if (value && !validPhone(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }
    const validateEmail = (rule, value, callback) => {
      if (value && !validEmail(value)) {
        callback(new Error('请输入正确的邮箱'))
      } else {
        callback()
      }
    }
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        username: undefined,
        realName: undefined,
        roleId: undefined,
        departmentId: undefined
      },
      roleOptions: [],
      departmentOptions: [],
      selectedIds: [],
      dialogVisible: false,
      dialogTitle: '',
      importVisible: false,
      temp: {
        id: undefined,
        username: '',
        realName: '',
        roleId: undefined,
        departmentId: undefined,
        phone: '',
        email: '',
        status: 1
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { validator: validateUsername, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        roleId: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        departmentId: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        email: [
          { validator: validateEmail, trigger: 'blur' }
        ]
      },
      headers: {
        Authorization: 'Bearer ' + getToken()
      }
    }
  },
  created() {
    this.getList()
    this.getRoleOptions()
    this.getDepartmentOptions()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        // TODO: 调用后端API获取用户列表数据
        // const { data } = await getUserList(this.listQuery)
        // this.list = data.items
        // this.total = data.total
        
        // 模拟数据
        this.list = [
          {
            id: 1,
            username: 'admin',
            realName: '管理员',
            roleName: '系统管理员',
            departmentName: '信息科',
            phone: '13800138000',
            email: 'admin@example.com',
            status: 1,
            createTime: '2024-03-11 10:00:00'
          }
        ]
        this.total = 1
      } catch (error) {
        console.error('获取用户列表失败:', error)
      }
      this.listLoading = false
    },
    async getRoleOptions() {
      try {
        // TODO: 调用后端API获取角色列表
        this.roleOptions = [
          { id: 1, name: '系统管理员' },
          { id: 2, name: '医生' },
          { id: 3, name: '护士' }
        ]
      } catch (error) {
        console.error('获取角色列表失败:', error)
      }
    },
    async getDepartmentOptions() {
      try {
        // TODO: 调用后端API获取部门列表
        this.departmentOptions = [
          { id: 1, name: '信息科' },
          { id: 2, name: '内科' },
          { id: 3, name: '外科' }
        ]
      } catch (error) {
        console.error('获取部门列表失败:', error)
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetQuery() {
      this.listQuery = {
        page: 1,
        limit: 10,
        username: undefined,
        realName: undefined,
        roleId: undefined,
        departmentId: undefined
      }
      this.getList()
    },
    handleSelectionChange(val) {
      this.selectedIds = val.map(item => item.id)
    },
    handleCreate() {
      this.dialogTitle = '新增用户'
      this.temp = {
        id: undefined,
        username: '',
        realName: '',
        roleId: undefined,
        departmentId: undefined,
        phone: '',
        email: '',
        status: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.dialogTitle = '编辑用户'
      this.temp = Object.assign({}, row)
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('是否确认删除该用户?', '提示', {
          type: 'warning'
        })
        // TODO: 调用后端API删除用户
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('删除用户失败:', error)
      }
    },
    async handleBatchDelete() {
      try {
        await this.$confirm(`是否确认删除这${this.selectedIds.length}个用户?`, '提示', {
          type: 'warning'
        })
        // TODO: 调用后端API批量删除用户
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('批量删除用户失败:', error)
      }
    },
    async handleStatusChange(row) {
      try {
        // TODO: 调用后端API更新用户状态
        this.$message.success('状态更新成功')
      } catch (error) {
        console.error('更新用户状态失败:', error)
        row.status = row.status === 1 ? 0 : 1
      }
    },
    async handleResetPwd(row) {
      try {
        await this.$confirm('是否确认重置该用户的密码?', '提示', {
          type: 'warning'
        })
        // TODO: 调用后端API重置用户密码
        this.$message.success('密码重置成功')
      } catch (error) {
        console.error('重置密码失败:', error)
      }
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            if (this.temp.id) {
              // TODO: 调用后端API更新用户
            } else {
              // TODO: 调用后端API创建用户
            }
            this.dialogVisible = false
            this.$message.success('保存成功')
            this.getList()
          } catch (error) {
            console.error('保存用户失败:', error)
          }
        }
      })
    },
    handleImport() {
      this.importVisible = true
    },
    handleExport() {
      // TODO: 调用后端API导出用户数据
    },
    handleImportSuccess() {
      this.importVisible = false
      this.$message.success('导入成功')
      this.getList()
    },
    handleImportError() {
      this.$message.error('导入失败')
    },
    downloadTemplate() {
      // TODO: 下载用户导入模板
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .action-container {
    margin-bottom: 20px;
  }

  .upload-demo {
    text-align: center;
  }
}
</style> 