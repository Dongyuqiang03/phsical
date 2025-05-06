<template>
  <div class="app-container" style="overflow-y: auto; max-height: 100vh;">
    <div class="main-content" style="padding-bottom: 60px;">
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
      <div class="table-container" style="overflow: auto; max-height: calc(100vh - 280px);">
        <el-table
          v-loading="listLoading"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%;"
          @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="部门名称" prop="deptName" />
          <el-table-column label="部门类型" prop="deptType">
            <template slot-scope="{row}">
              <el-tag :type="getDeptTypeTag(row.deptType)">{{ getDeptTypeLabel(row.deptType) }}</el-tag>
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
      </div>

      <!-- 分页组件 - 直接放在表格下方 -->
      <div v-if="list.length > 0" class="pagination-wrapper">
        <el-pagination
          class="department-pagination"
          :pager-count="device === 'mobile' ? 3 : 5"
          :current-page="listQuery.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="listQuery.pageSize"
          :layout="device === 'mobile' ? 'prev, pager, next' : 'total, sizes, prev, pager, next'"
          :total="effectiveTotal"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- Debug info only shown in development -->
      <div v-if="false" style="margin: 10px 0; color: #606266; background: #f9f9f9; padding: 8px; border-radius: 4px;">
        <span>API返回总条数: {{ total }}</span>
        <span style="margin-left: 15px">当前页: {{ page }}</span>
        <span style="margin-left: 15px">每页条数: {{ size }}</span>
        <span style="margin-left: 15px">当前记录数: {{ paginatedData.length }}</span>
        <span style="margin-left: 15px">有效总条数: {{ effectiveTotal }}</span>
      </div>
    </div>

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
import { mapGetters } from 'vuex'

export default {
  name: 'Department',
  components: { Pagination },
  data() {
    return {
      loading: false,
      listLoading: false,
      list: [],
      total: 0,
      selectedIds: [],
      listQuery: {
        deptName: '', // 修改参数名从name为deptName
        pageNum: 1,
        pageSize: 10
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
  computed: {
    ...mapGetters([
      'device'
    ]),
    effectiveTotal() {
      // 如果API返回的total大于0，则使用API返回的total
      if (this.total > 0) {
        return this.total;
      }
      
      // 如果API返回total为0但是有数据，则至少返回列表长度或者一个估算值
      if (this.list.length > 0) {
        // 如果当前不是第一页，我们可以估算总数
        if (this.listQuery.pageNum > 1) {
          return (this.listQuery.pageNum - 1) * this.listQuery.pageSize + this.list.length;
        }
        // 如果是第一页且记录不满一页，返回实际记录数
        if (this.list.length < this.listQuery.pageSize) {
          return this.list.length;
        }
        // 如果是第一页且记录数等于每页数量，返回每页数量的2倍作为估计
        return this.list.length * 2;
      }
      
      return 0;
    },
    totalPages() {
      return Math.ceil(this.list.length / this.size);
    },
    paginatedData() {
      const start = (this.page - 1) * this.size;
      const end = start + this.size;
      return this.list.slice(start, end);
    }
  },
  created() {
    this.page = this.listQuery.pageNum
    this.size = this.listQuery.pageSize
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const res = await getDepartmentList(this.listQuery)
        if (res.code === 200) {
          this.list = res.data.records
          this.total = res.data.total
          this.page = res.data.current
          this.size = res.data.size
        }
      } finally {
        this.listLoading = false
      }
    },
    handleFilter() {
      this.page = 1
      this.listQuery.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        deptName: '' // 修改参数名
      }
      this.page = 1
      this.size = 10
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
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val
      this.getList()
    },
    resetQuery() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        deptName: ''
      }
      this.getList()
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    
    getDeptTypeLabel(type) {
      const typeMap = {
      1: '医疗科室',
      2: '教学院系',
      3: '其他部门'
      }
      return typeMap[type] || '未知类型'
    },
    getDeptTypeTag(type) {
      const typeMap = {
      1: 'success',
      2: 'warning',
      3: 'info'
    }
    return typeMap[type] || ''  
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  height: auto;
  min-height: 100%;
  position: relative;
  overflow-y: auto;

  .filter-container {
    margin-bottom: 20px;
  }

  .action-container {
    margin-bottom: 20px;
  }
  
  .main-content {
    min-height: calc(100vh - 180px);
  }
}

.pagination-wrapper {
  padding: 15px 0;
  display: flex;
  justify-content: center;
  background-color: #fff;
}

.fixed-footer {
  position: fixed;
  left: 210px;
  right: 0;
  bottom: 0;
  background-color: #fff;
  box-shadow: 0 -1px 2px rgba(0, 0, 0, 0.03);
  padding: 3px 20px;
  z-index: 100;
  text-align: center;
  width: calc(100% - 210px);
  transition: left 0.28s, width 0.28s;
  border-top: 1px solid #ebeef5;
}

/* Responsive styling for sidebar collapsed state */
.hideSidebar .fixed-footer {
  left: 54px;
  width: calc(100% - 54px);
  margin-left: 54px;
}

/* Mobile view */
.mobile .fixed-footer {
  left: 0;
  width: 100%;
  margin-left: 0;
}
</style>

<style>
/* Global styles for pagination */
.el-pagination {
  display: flex !important;
  justify-content: center !important;
  padding: 8px !important;
  background-color: transparent !important;
  margin: 0 auto !important;
}

.department-pagination {
  width: 100% !important;
  max-width: 800px !important;
}

.el-pagination button, 
.el-pagination span:not([class*=suffix]),
.el-pagination .el-select .el-input .el-input__inner {
  font-size: 12px !important;
  min-width: 24px !important;
  height: 24px !important;
  line-height: 24px !important;
}

.el-pagination .el-select .el-input {
  margin: 0 5px !important;
}

.el-pagination .el-pagination__jump {
  margin-left: 10px !important;
}

.el-pagination .btn-prev,
.el-pagination .btn-next {
  background-color: transparent !important;
}

.el-pagination .number {
  background-color: transparent !important;
}

.el-pagination .number.active {
  color: #409EFF !important;
  background-color: #ecf5ff !important;
  border-color: #b3d8ff !important;
}
</style>