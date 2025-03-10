<template>
  <div class="exam-items-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="filter-container">
        <el-input
          v-model="listQuery.keyword"
          placeholder="请输入项目名称"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="handleFilter" />
        <el-select
          v-model="listQuery.category"
          placeholder="项目分类"
          clearable
          class="filter-item"
          style="width: 160px">
          <el-option
            v-for="item in categoryOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value" />
        </el-select>
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-search"
          @click="handleFilter">搜索</el-button>
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-plus"
          @click="handleCreate">新增</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="listLoading"
        :data="list"
        border
        fit
        highlight-current-row
        style="width: 100%;">
        <el-table-column label="项目编号" prop="code" align="center" width="120" />
        <el-table-column label="项目名称" prop="name" align="center" min-width="120" />
        <el-table-column label="项目分类" prop="categoryName" align="center" width="120" />
        <el-table-column label="执行科室" prop="departmentName" align="center" width="120" />
        <el-table-column label="参考值" align="center" min-width="200">
          <template slot-scope="{row}">
            <div>{{ row.referenceValue }}</div>
            <div v-if="row.unit" class="reference-unit">({{ row.unit }})</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" @click="handleUpdate(row)">编辑</el-button>
            <el-button 
              type="text" 
              @click="handleStatus(row)">{{ row.status === 'active' ? '停用' : '启用' }}</el-button>
            <el-button type="text" @click="handleDelete(row)" class="delete-btn">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="listQuery.page"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="listQuery.limit"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total" />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogStatus === 'create' ? '新增体检项目' : '编辑体检项目'" :visible.sync="dialogVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目分类" prop="category">
          <el-select v-model="temp.category" placeholder="请选择项目分类">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行科室" prop="departmentId">
          <el-select v-model="temp.departmentId" placeholder="请选择执行科室">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="参考值" prop="referenceValue">
          <el-input v-model="temp.referenceValue" placeholder="请输入参考值范围" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="temp.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            type="textarea"
            :rows="2"
            placeholder="请输入备注信息"
            v-model="temp.remark" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ExamItems',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        keyword: undefined,
        category: undefined
      },
      categoryOptions: [],
      departmentOptions: [],
      dialogVisible: false,
      dialogStatus: '',
      temp: {
        id: undefined,
        name: '',
        category: '',
        departmentId: undefined,
        referenceValue: '',
        unit: '',
        remark: ''
      },
      rules: {
        name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择项目分类', trigger: 'change' }],
        departmentId: [{ required: true, message: '请选择执行科室', trigger: 'change' }],
        referenceValue: [{ required: true, message: '请输入参考值范围', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
    this.getCategoryOptions()
    this.getDepartmentOptions()
  },
  methods: {
    async getList() {
      try {
        this.listLoading = true
        const { data } = await this.$http.get('/api/items', { params: this.listQuery })
        this.list = data.list
        this.total = data.total
      } catch (error) {
        this.$message.error('获取体检项目列表失败：' + error.message)
      } finally {
        this.listLoading = false
      }
    },
    async getCategoryOptions() {
      try {
        const { data } = await this.$http.get('/api/dict/items/exam_category')
        this.categoryOptions = data
      } catch (error) {
        this.$message.error('获取项目分类失败：' + error.message)
      }
    },
    async getDepartmentOptions() {
      try {
        const { data } = await this.$http.get('/api/departments')
        this.departmentOptions = data
      } catch (error) {
        this.$message.error('获取科室列表失败：' + error.message)
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleSizeChange(val) {
      this.listQuery.limit = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        name: '',
        category: '',
        departmentId: undefined,
        referenceValue: '',
        unit: '',
        remark: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    async createData() {
      try {
        const valid = await this.$refs['dataForm'].validate()
        if (!valid) return

        const { data } = await this.$http.post('/api/items', this.temp)
        this.list.unshift(data)
        this.dialogVisible = false
        this.$message.success('创建成功')
      } catch (error) {
        this.$message.error('创建失败：' + error.message)
      }
    },
    async updateData() {
      try {
        const valid = await this.$refs['dataForm'].validate()
        if (!valid) return

        const { data } = await this.$http.put(`/api/items/${this.temp.id}`, this.temp)
        const index = this.list.findIndex(v => v.id === this.temp.id)
        this.list.splice(index, 1, data)
        this.dialogVisible = false
        this.$message.success('更新成功')
      } catch (error) {
        this.$message.error('更新失败：' + error.message)
      }
    },
    async handleStatus(row) {
      try {
        const action = row.status === 'active' ? '停用' : '启用'
        await this.$confirm(`确认要${action}该项目吗？`, '提示', {
          type: 'warning'
        })
        
        const { data } = await this.$http.post(`/api/items/${row.id}/status`, {
          status: row.status === 'active' ? 'inactive' : 'active'
        })
        const index = this.list.findIndex(v => v.id === row.id)
        this.list.splice(index, 1, data)
        this.$message.success(`${action}成功`)
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(`${action}失败：` + error.message)
        }
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认要删除该项目吗？', '提示', {
          type: 'warning'
        })
        
        await this.$http.delete(`/api/items/${row.id}`)
        const index = this.list.findIndex(v => v.id === row.id)
        this.list.splice(index, 1)
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败：' + error.message)
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.exam-items-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
    .filter-item {
      margin-right: 10px;
      &:last-child {
        margin-right: 0;
      }
    }
  }

  .reference-unit {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }

  .delete-btn {
    color: #F56C6C;
  }

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }
}
</style>