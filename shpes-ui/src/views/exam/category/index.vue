<template>
    <div class="app-container">
      <!-- 搜索区域 -->
      <el-card class="filter-container" shadow="never">
        <div>
          <el-form :inline="true" :model="listQuery" class="demo-form-inline">
            <el-form-item label="分类名称">
              <el-input
                v-model="listQuery.name"
                placeholder="请输入分类名称"
                clearable
                size="small"
                @keyup.enter.native="handleFilter"
              />
            </el-form-item>
            <el-form-item label="分类编码">
              <el-input
                v-model="listQuery.code"
                placeholder="请输入分类编码"
                clearable
                size="small"
                @keyup.enter.native="handleFilter"
              />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="listQuery.status" placeholder="请选择状态" clearable size="small" style="width: 130px">
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="small" @click="handleFilter">查询</el-button>
              <el-button size="small" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
  
      <!-- 操作按钮区域 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <el-button
            class="filter-item"
            style="margin-left: 10px;"
            type="primary"
            icon="el-icon-plus"
            size="mini"
            @click="handleCreate"
          >添加</el-button>
        </div>
  
        <!-- 表格区域 -->
        <el-table
          v-loading="listLoading"
          :data="list"
          border
          fit
          highlight-current-row
          style="width: 100%;"
        >
          <el-table-column label="ID" prop="id" align="center" width="80" />
          <el-table-column label="分类名称" prop="name" align="center" />
          <el-table-column label="分类编码" prop="code" align="center" />
          <el-table-column label="状态" align="center" width="100">
            <template slot-scope="{row}">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="center" width="180">
            <template slot-scope="{row}">
              <span>{{ row.createTime | formatDateTime }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="200">
            <template slot-scope="{row}">
              <el-button
                size="mini"
                type="primary"
                @click="handleUpdate(row)"
              >编辑</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(row)"
              >删除</el-button>
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
      </el-card>
  
      <!-- 添加/编辑对话框 -->
      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible">
        <el-form
          ref="dataForm"
          :rules="rules"
          :model="temp"
          label-position="right"
          label-width="100px"
          style="width: 600px; margin-left:50px;"
        >
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="temp.name" />
          </el-form-item>
          <el-form-item label="分类编码" prop="code">
            <el-input v-model="temp.code" />
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
          <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">确认</el-button>
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
        const date = new Date(time)
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
        list: null,
        total: 0,
        listLoading: true,
        listQuery: {
          pageNum: 1,
          pageSize: 10,
          name: undefined,
          code: undefined,
          status: undefined
        },
        dialogVisible: false,
        dialogStatus: '',
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
    created() {
      this.getList()
    },
    methods: {
      getList() {
        this.listLoading = true
        getCategoryPage(this.listQuery).then(response => {
          this.list = response.data.list
          this.total = response.data.total
          this.listLoading = false
        })
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
        this.dialogStatus = 'create'
        this.dialogTitle = '添加分类'
        this.dialogVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      createData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            createCategory(this.temp).then(() => {
              this.list.unshift(this.temp)
              this.dialogVisible = false
              this.$notify({
                title: '成功',
                message: '创建成功',
                type: 'success',
                duration: 2000
              })
            })
          }
        })
      },
      handleUpdate(row) {
        this.temp = Object.assign({}, row)
        this.dialogStatus = 'update'
        this.dialogTitle = '编辑分类'
        this.dialogVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      },
      updateData() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            const tempData = Object.assign({}, this.temp)
            updateCategory(tempData).then(() => {
              const index = this.list.findIndex(v => v.id === this.temp.id)
              this.list.splice(index, 1, this.temp)
              this.dialogVisible = false
              this.$notify({
                title: '成功',
                message: '更新成功',
                type: 'success',
                duration: 2000
              })
            })
          }
        })
      },
      handleDelete(row) {
        this.$confirm('确认删除该分类吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          deleteCategory(row.id).then(() => {
            const index = this.list.findIndex(v => v.id === row.id)
            this.list.splice(index, 1)
            this.$notify({
              title: '成功',
              message: '删除成功',
              type: 'success',
              duration: 2000
            })
          })
        })
      }
    }
  }
  </script>
  
  <style scoped>
  .filter-container {
    padding-bottom: 10px;
  }
  .filter-item {
    display: inline-block;
    vertical-align: middle;
    margin-bottom: 10px;
    margin-right: 10px;
  }
  </style>