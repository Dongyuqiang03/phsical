<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="form-inline">
        <el-form-item>
          <el-input
            v-model="listQuery.name"
            placeholder="部门名称"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-container">
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增部门</el-button>
      <el-button type="danger" icon="el-icon-delete" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="部门名称" prop="deptName" />
      <el-table-column label="部门类型" prop="deptType">
        <template slot-scope="{row}">
          <el-tag :type="row.deptType === 1 ? 'success' : 'info'">{{ row.deptType === 1 ? '医疗科室' : '其他部门' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="部门编码" prop="deptCode" width="120" />
      <el-table-column label="部门描述" prop="description" show-overflow-tooltip />
      <el-table-column label="创建时间" width="180">
        <template slot-scope="{row}">
          <span>{{ formatDateTime(row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="{row}">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="info" size="mini" @click="handleUsers(row)">人员</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.pageNum"
      :limit.sync="listQuery.pageSize"
      @pagination="getList"
    />

    <!-- 部门表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="temp.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="temp.deptCode" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="部门类型" prop="deptType">
          <el-radio-group v-model="temp.deptType">
            <el-radio :label="1">医疗科室</el-radio>
            <el-radio :label="2">其他部门</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="部门描述" prop="description">
          <el-input
            v-model="temp.description"
            type="textarea"
            :rows="3"
            placeholder="请输入部门描述"
          />
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

    <!-- 部门人员对话框 -->
    <el-dialog title="部门人员" :visible.sync="userVisible" width="800px">
      <el-table :data="userList" style="width: 100%">
        <el-table-column label="用户名" prop="username" />
        <el-table-column label="姓名" prop="realName" />
        <el-table-column label="角色" prop="roleName" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import { getDepartmentList, createDepartment, updateDepartment, getDepartmentUsers, deleteDepartment, batchUpdateStatus } from '@/api/department'

export default {
  name: 'Department',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      selectedIds: [],
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        name: undefined
      },
      dialogVisible: false,
      dialogTitle: '',
      userVisible: false,
      temp: {
        id: undefined,
        deptName: '',
        deptCode: '',
        deptType: 2,
        description: '',
        status: 1
      },
      rules: {
        deptName: [
          { required: true, message: '请输入部门名称', trigger: 'blur' }
        ],
        deptCode: [
          { required: true, message: '请输入部门编码', trigger: 'blur' }
        ]
      },
      userList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const { data } = await getDepartmentList(this.listQuery)
        this.list = data.records || []
        this.total = data.total || 0
      } catch (error) {
        console.error('获取部门列表失败:', error)
      }
      this.listLoading = false
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetQuery() {
      this.listQuery = {
        page: 1,
        limit: 10,
        name: undefined
      }
      this.getList()
    },
    handleCreate() {
      this.dialogTitle = '新增部门'
      this.temp = {
        id: undefined,
        deptName: '',
        deptCode: '',
        deptType: 2,
        description: '',
        status: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.dialogTitle = '编辑部门'
      this.temp = Object.assign({}, row)
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    formatDateTime(time) {
      if (!time) return ''
      
      // 处理后端返回的数组格式 [year, month, day, hour, minute, second]
      if (Array.isArray(time)) {
        if (time.length < 6) return ''
        const [year, month, day, hour, minute, second] = time
        return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')} ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}`
      }
      
      // 处理其他格式
      const date = new Date(time)
      if (isNaN(date.getTime())) return ''
      
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      const second = date.getSeconds().toString().padStart(2, '0')
      
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            if (this.temp.id) {
              await updateDepartment(this.temp)
            } else {
              await createDepartment(this.temp)
            }
            this.dialogVisible = false
            this.$message.success('保存成功')
            this.getList()
          } catch (error) {
            console.error('保存部门失败:', error)
          }
        }
      })
    },
    async handleUsers(row) {
      try {
        const { data } = await getDepartmentUsers(row.id)
        this.userList = data
        this.userVisible = true
      } catch (error) {
        console.error('获取部门人员失败:', error)
      }
    },
    handleStatusChange(row) {
      const statusText = row.status === 1 ? '启用' : '禁用'
      this.$confirm(`确认要${statusText}该部门吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await updateDepartment({
            ...row,
            status: row.status
          })
          this.$message.success(`${statusText}成功`)
        } catch (error) {
          console.error(`状态修改失败:`, error)
          // 恢复原状态
          row.status = row.status === 1 ? 0 : 1
        }
      }).catch(() => {
        // 用户取消操作，恢复原状态
        row.status = row.status === 1 ? 0 : 1
        this.$message.info('已取消操作')
      })
    },
    handleDelete(row) {
      this.$confirm('此操作将永久删除该部门，是否继续？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteDepartment(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    handleSelectionChange(val) {
      this.selectedIds = val.map(item => item.id)
    },
    handleBatchDelete() {
      if (!this.selectedIds.length) {
        this.$message.warning('请选择要删除的部门')
        return
      }
      
      this.$confirm(`确认要删除这${this.selectedIds.length}个部门吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 使用 Promise.all 并行处理多个删除请求
          await Promise.all(this.selectedIds.map(id => deleteDepartment(id)))
          this.$message.success('批量删除成功')
          this.getList()
        } catch (error) {
          console.error('批量删除失败:', error)
          this.$message.error('批量删除失败，请重试')
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  height: calc(100vh - 50px);
  overflow-y: auto;

  .filter-container {
    margin-bottom: 20px;
  }

  .action-container {
    margin-bottom: 20px;
  }
}
</style>