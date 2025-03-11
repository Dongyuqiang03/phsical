<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="form-inline">
        <el-form-item>
          <el-input
            v-model="listQuery.name"
            placeholder="套餐名称"
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
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增套餐</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%">
      <el-table-column label="套餐名称" prop="name" />
      <el-table-column label="套餐编码" prop="code" width="120" />
      <el-table-column label="项目数量" prop="itemCount" width="100" align="center" />
      <el-table-column label="描述" prop="description" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="success" size="mini" @click="handleItems(row)">项目</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
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

    <!-- 套餐表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="套餐编码" prop="code">
          <el-input v-model="temp.code" placeholder="请输入套餐编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="temp.description"
            type="textarea"
            :rows="3"
            placeholder="请输入套餐描述"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 套餐项目对话框 -->
    <el-dialog title="套餐项目配置" :visible.sync="itemVisible" width="800px">
      <div class="filter-container">
        <el-input
          v-model="itemQuery.keyword"
          placeholder="请输入项目名称"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="handleItemFilter"
        />
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleItemFilter">
          搜索
        </el-button>
      </div>
      <el-table
        ref="itemTable"
        :data="itemList"
        style="width: 100%"
        @selection-change="handleItemSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="项目名称" prop="name" />
        <el-table-column label="项目编码" prop="code" width="120" />
        <el-table-column label="执行科室" prop="departmentName" width="120" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="itemVisible = false">取消</el-button>
        <el-button type="primary" @click="submitItems">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import { getExamPackageList, createExamPackage, updateExamPackage, deleteExamPackage, getExamPackageItems, updateExamPackageItems } from '@/api/exam/package'
import { getAllExamItems } from '@/api/exam/item'

export default {
  name: 'ExamPackage',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        name: undefined
      },
      dialogVisible: false,
      dialogTitle: '',
      itemVisible: false,
      temp: {
        id: undefined,
        name: '',
        code: '',
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入套餐名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入套餐编码', trigger: 'blur' }
        ]
      },
      itemList: [],
      selectedItems: [],
      itemQuery: {
        keyword: '',
        packageId: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const { data } = await getExamPackageList(this.listQuery)
        this.list = data.items
        this.total = data.total
      } catch (error) {
        console.error('获取体检套餐列表失败:', error)
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
      this.dialogTitle = '新增体检套餐'
      this.temp = {
        id: undefined,
        name: '',
        code: '',
        description: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.dialogTitle = '编辑体检套餐'
      this.temp = Object.assign({}, row)
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('是否确认删除该体检套餐?', '提示', {
          type: 'warning'
        })
        await deleteExamPackage(row.id)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('删除体检套餐失败:', error)
      }
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            if (this.temp.id) {
              await updateExamPackage(this.temp)
            } else {
              await createExamPackage(this.temp)
            }
            this.dialogVisible = false
            this.$message.success('保存成功')
            this.getList()
          } catch (error) {
            console.error('保存体检套餐失败:', error)
          }
        }
      })
    },
    async handleItems(row) {
      this.itemQuery.packageId = row.id
      try {
        const [itemsRes, selectedRes] = await Promise.all([
          getAllExamItems(),
          getExamPackageItems(row.id)
        ])
        this.itemList = itemsRes.data
        this.selectedItems = selectedRes.data
        this.itemVisible = true
        this.$nextTick(() => {
          this.$refs.itemTable.clearSelection()
          this.selectedItems.forEach(item => {
            const row = this.itemList.find(i => i.id === item.id)
            if (row) {
              this.$refs.itemTable.toggleRowSelection(row, true)
            }
          })
        })
      } catch (error) {
        console.error('获取体检项目失败:', error)
      }
    },
    handleItemFilter() {
      // TODO: 根据关键字过滤项目列表
    },
    handleItemSelectionChange(val) {
      this.selectedItems = val
    },
    async submitItems() {
      try {
        await updateExamPackageItems(this.itemQuery.packageId, {
          itemIds: this.selectedItems.map(item => item.id)
        })
        this.$message.success('保存成功')
        this.itemVisible = false
        this.getList()
      } catch (error) {
        console.error('保存套餐项目失败:', error)
      }
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

  .filter-item {
    margin-right: 10px;
  }
}
</style> 