<template>
    <div class="app-container">
      <div class="filter-container">
        <el-form :inline="true" :model="listQuery" class="form-inline">
          <el-form-item>
            <el-input
              v-model="listQuery.name"
              placeholder="分类名称"
              clearable
              @keyup.enter.native="handleFilter"
            />
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="listQuery.code"
              placeholder="分类编码"
              clearable
              @keyup.enter.native="handleFilter"
            />
          </el-form-item>
          <el-form-item>
            <el-select v-model="listQuery.status" placeholder="状态" clearable>
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
  
      <div class="action-container">
        <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增分类</el-button>
      </div>
  
      <el-table
        v-loading="listLoading"
        :data="pagedList"
        border
        style="width: 100%"
      >
        <el-table-column label="ID" prop="id" align="center" width="80" />
        <el-table-column label="分类名称" prop="name" />
        <el-table-column label="分类编码" prop="code" />
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
        <el-table-column label="创建时间" prop="createTime" width="180">
          <template slot-scope="{row}">
            <span>{{ row.createTime | formatDateTime }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template slot-scope="{row}">
            <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
  
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="listQuery.pageNum"
        :limit.sync="listQuery.pageSize"
        @pagination="getList"
      />
  
      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
        <el-form
          ref="dataForm"
          :rules="rules"
          :model="temp"
          label-position="right"
          label-width="100px"
        >
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="temp.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="分类编码" prop="code">
            <el-input v-model="temp.code" placeholder="请输入分类编码" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
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
    </div>
  </template>
  
  <script>
  import { getCategoryPage, createCategory, updateCategory, deleteCategory } from '@/api/exam/category'
  import Pagination from '@/components/Pagination'
  
  export default {
    name: 'ExamItemCategory',
    components: { Pagination },
    filters: {
      formatDateTime(time) {
        if (!time) return ''
        
        // 处理不同格式的日期
        let date
        if (Array.isArray(time)) {
          // 处理后端返回的数组格式 [year, month, day, hour, minute, second]
          if (time.length < 6) return ''
          const [year, month, day, hour, minute, second] = time
          return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')} ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}`
        } else if (typeof time === 'string') {
          // 处理ISO格式字符串
          date = new Date(time)
        } else {
          // 处理时间戳
          date = new Date(time)
        }
        
        if (isNaN(date.getTime())) return ''
        
        const year = date.getFullYear()
        const month = (date.getMonth() + 1).toString().padStart(2, '0')
        const day = date.getDate().toString().padStart(2, '0')
        const hour = date.getHours().toString().padStart(2, '0')
        const minute = date.getMinutes().toString().padStart(2, '0')
        const second = date.getSeconds().toString().padStart(2, '0')
        
        return `${year}-${month}-${day} ${hour}:${minute}:${second}`
      }
    },
    data() {
      return {
        list: [],
        total: 0,
        listLoading: true,
        listQuery: {
          pageNum: 1,
          pageSize: 10,
          name: undefined
        },
        dialogVisible: false,
        dialogTitle: '',
        temp: {
          id: undefined,
          name: '',
          code: '',
          status: 1
        },
        rules: {
          name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
          code: [{ required: true, message: '请输入分类编码', trigger: 'blur' }],
          status: [{ required: true, message: '请选择状态', trigger: 'change' }]
        }
      }
    },
    computed: {
      pagedList() {
        return this.list
      }
    },
    created() {
      this.getList()
    },
    methods: {
      async getList() {
        this.listLoading = true
        try {
          const response = await getCategoryPage(this.listQuery)
          if (response.code === 200 && response.data) {
            this.list = response.data.records || []
            this.total = response.data.total || this.list.length
          } else {
            this.list = []
            this.total = 0
            this.$message.warning(response?.message || '获取数据失败')
          }
        } catch (error) {
          console.error('获取分类列表失败:', error)
          this.list = []
          this.total = 0
          this.$message.error('获取分类列表失败')
        } finally {
          this.listLoading = false
        }
      },
      handleFilter() {
        this.listQuery.pageNum = 1
        this.getList()
      },
      resetQuery() {
        this.listQuery = {
          pageNum: 1,
          pageSize: 10,
          name: undefined,
          code: undefined,
          status: undefined
        }
        this.getList()
      },
      resetTemp() {
        this.temp = {
          id: undefined,
          name: '',
          code: '',
          status: 1
        }
      },
      handleCreate() {
        this.resetTemp()
        this.dialogTitle = '新增分类'
        this.dialogVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      handleUpdate(row) {
        this.temp = Object.assign({}, row)
        this.dialogTitle = '编辑分类'
        this.dialogVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      async handleStatusChange(row) {
        if (!row || typeof row.id === 'undefined') {
          this.$message.warning('分类数据无效')
          return
        }
        
        const originalStatus = row.status
        try {
          await updateCategory({
            id: row.id,
            status: row.status
          })
          this.$message.success('状态更新成功')
          this.getList() // 刷新列表以获取最新数据
        } catch (error) {
          console.error('更新分类状态失败:', error)
          // 恢复原状态
          row.status = originalStatus
          this.$message.error('更新状态失败')
        }
      },
      async handleDelete(row) {
        if (!row || !row.id) {
          this.$message.warning('分类数据无效')
          return
        }
        
        try {
          await this.$confirm('是否确认删除该分类?', '提示', {
            type: 'warning'
          })
          await deleteCategory(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          if (error === 'cancel') {
            return
          }
          console.error('删除分类失败:', error)
          this.$message.error('删除失败')
        }
      },
      submitForm() {
        this.$refs['dataForm'].validate(async (valid) => {
          if (valid) {
            try {
              const submitData = { ...this.temp }
              
              if (this.temp.id) {
                await updateCategory(submitData)
                this.$message.success('更新分类成功')
              } else {
                await createCategory(submitData)
                this.$message.success('创建分类成功')
              }
              
              this.dialogVisible = false
              this.getList()
            } catch (error) {
              console.error('保存分类失败:', error)
              this.$message.error(`保存失败: ${error.response?.data?.message || error.message || '未知错误'}`)
            }
          }
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