<template>
  <div class="exam-packages-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="filter-container">
        <el-input
          v-model="listQuery.keyword"
          placeholder="请输入套餐名称"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="handleFilter" />
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
        <el-table-column label="套餐编号" prop="code" align="center" width="120" />
        <el-table-column label="套餐名称" prop="name" align="center" min-width="120" />
        <el-table-column label="项目数量" prop="itemCount" align="center" width="100" />
        <el-table-column label="描述" prop="description" align="center" show-overflow-tooltip />
        <el-table-column label="状态" align="center" width="100">
          <template slot-scope="{row}">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'">
              {{ row.status === 'active' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="250" fixed="right">
          <template slot-scope="{row}">
            <el-button type="text" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="text" @click="handleItems(row)">配置项目</el-button>
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
    <el-dialog :title="dialogStatus === 'create' ? '新增体检套餐' : '编辑体检套餐'" :visible.sync="dialogVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="请输入套餐描述"
            v-model="temp.description" />
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

    <!-- 配置项目对话框 -->
    <el-dialog
      title="配置体检项目"
      :visible.sync="itemsDialogVisible"
      width="70%">
      <div class="items-dialog-content">
        <div class="items-container">
          <div class="items-header">
            <span>可选项目</span>
            <el-input
              v-model="itemsQuery"
              placeholder="搜索项目"
              style="width: 200px;"
              @input="filterItems" />
          </div>
          <el-table
            :data="filteredItems"
            border
            height="400"
            @selection-change="handleItemSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column label="项目名称" prop="name" />
            <el-table-column label="项目分类" prop="categoryName" width="120" />
            <el-table-column label="执行科室" prop="departmentName" width="120" />
          </el-table>
        </div>
        <div class="selected-items-container">
          <div class="items-header">
            <span>已选项目</span>
            <div>共 {{ selectedItems.length }} 项</div>
          </div>
          <el-table
            :data="selectedItems"
            border
            height="400">
            <el-table-column label="项目名称" prop="name" />
            <el-table-column label="项目分类" prop="categoryName" width="120" />
            <el-table-column label="执行科室" prop="departmentName" width="120" />
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="{row, $index}">
                <el-button
                  type="text"
                  class="delete-btn"
                  @click="removeSelectedItem($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="itemsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveItems">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
.exam-packages-container {
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

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }

  .delete-btn {
    color: #f56c6c;
  }

  .items-dialog-content {
    display: flex;
    gap: 20px;

    .items-container,
    .selected-items-container {
      flex: 1;
      display: flex;
      flex-direction: column;

      .items-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;

        span {
          font-size: 16px;
          font-weight: bold;
        }
      }
    }
  }
}
</style>

<script>
export default {
  name: 'ExamPackages',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        keyword: undefined
      },
      dialogVisible: false,
      dialogStatus: '',
      temp: {
        id: undefined,
        name: '',
        description: '',
        remark: ''
      },
      rules: {
        name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
        description: [{ required: true, message: '请输入套餐描述', trigger: 'blur' }]
      },
      itemsDialogVisible: false,
      currentPackage: null,
      allItems: [],
      itemsQuery: '',
      filteredItems: [],
      selectedItems: [],
      tempSelectedItems: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      try {
        this.listLoading = true
        const { data } = await this.$http.get('/api/packages', { params: this.listQuery })
        this.list = data.list
        this.total = data.total
      } catch (error) {
        this.$message.error('获取体检套餐列表失败：' + error.message)
      } finally {
        this.listLoading = false
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
        description: '',
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

        const { data } = await this.$http.post('/api/packages', this.temp)
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

        const { data } = await this.$http.put(`/api/packages/${this.temp.id}`, this.temp)
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
        await this.$confirm(`确认要${action}该套餐吗？`, '提示', {
          type: 'warning'
        })
        
        const { data } = await this.$http.post(`/api/packages/${row.id}/status`, {
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
        await this.$confirm('确认要删除该套餐吗？', '提示', {
          type: 'warning'
        })
        
        await this.$http.delete(`/api/packages/${row.id}`)
        const index = this.list.findIndex(v => v.id === row.id)
        this.list.splice(index, 1)
        this.$message.success('删除成功')
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败：' + error.message)
        }
      }
    },
    async handleItems(row) {
      this.currentPackage = row
      this.itemsDialogVisible = true
      this.itemsQuery = ''
      this.selectedItems = []
      this.tempSelectedItems = []
      
      try {
        // 获取所有可选项目
        const { data: items } = await this.$http.get('/api/items', { params: { limit: 999 }})
        this.allItems = items.list
        this.filteredItems = this.allItems

        // 获取套餐已选项目
        const { data: selectedItems } = await this.$http.get(`/api/packages/${row.id}/items`)
        this.selectedItems = selectedItems
      } catch (error) {
        this.$message.error('获取项目数据失败：' + error.message)
        this.itemsDialogVisible = false
      }
    },
    filterItems(query) {
      this.itemsQuery = query
      if (query) {
        this.filteredItems = this.allItems.filter(item => 
          !this.selectedItems.some(selected => selected.id === item.id) &&
          (item.name.toLowerCase().includes(query.toLowerCase()) ||
           item.categoryName.toLowerCase().includes(query.toLowerCase()))
        )
      } else {
        this.filteredItems = this.allItems.filter(item => 
          !this.selectedItems.some(selected => selected.id === item.id)
        )
      }
    },
    handleItemSelectionChange(items) {
      // 过滤掉已选中的项目
      const newItems = items.filter(item => !this.selectedItems.some(selected => selected.id === item.id))
      this.selectedItems = [...this.selectedItems, ...newItems]
      
      // 更新可选项目列表，移除已选中的项目
      this.filteredItems = this.allItems.filter(item => 
        !this.selectedItems.some(selected => selected.id === item.id) &&
        (this.itemsQuery ? (
          item.name.toLowerCase().includes(this.itemsQuery.toLowerCase()) ||
          item.categoryName.toLowerCase().includes(this.itemsQuery.toLowerCase())
        ) : true)
      )
    },
    removeSelectedItem(index) {
      this.selectedItems.splice(index, 1)
    },
    async saveItems() {
      try {
        await this.$http.post(`/api/packages/${this.currentPackage.id}/items`, {
          itemIds: this.selectedItems.map(item => item.id)
        })
        this.itemsDialogVisible = false
        this.$message.success('保存成功')
        this.getList()
      } catch (error) {
        this.$message.error('保存失败：' + error.message)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.items-dialog-content {
  display: flex;
  gap: 20px;

  .items-container,
  .selected-items-container {
    flex: 1;
    display: flex;
    flex-direction: column;

    .items-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      span {
        font-size: 16px;
        font-weight: bold;
      }
    }
  }
}

.delete-btn {
  color: #f56c6c;
}
</style>
</style>