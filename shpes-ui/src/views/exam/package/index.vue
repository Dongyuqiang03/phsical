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
      <el-table-column label="套餐描述" prop="description" min-width="200" show-overflow-tooltip>
        <template slot-scope="{row}">
          <span>{{ row.description || '暂无描述' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="适用性别" width="100" align="center">
        <template slot-scope="{row}">
          <span>{{ getGenderLabel(row.gender) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="价格" width="120" align="center">
        <template slot-scope="{row}">
          <span>{{ formatPrice(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="80" align="center" />
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
      :page.sync="listQuery.pageNum"
      :limit.sync="listQuery.pageSize"
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
import { getExamPackageList, createExamPackage, updateExamPackage, deleteExamPackage, getExamPackageItems, updateExamPackageItems, updateExamPackageStatus } from '@/api/exam/package'
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
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        gender: undefined,
        status: undefined
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
      },
      allItemList: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const response = await getExamPackageList(this.listQuery)
        if (response && response.code === 200 && response.data) {
          this.list = response.data.records || []
          this.total = response.data.total || 0
        } else {
          this.list = []
          this.total = 0
          this.$message.warning(response?.message || '获取数据失败')
        }
      } catch (error) {
        console.error('获取体检套餐列表失败:', error)
        this.list = []
        this.total = 0
        this.$message.error('获取体检套餐列表失败')
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
        gender: undefined,
        status: undefined
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
      this.itemQuery.keyword = '' // 清空关键字
      
      try {
        const [itemsRes, selectedRes] = await Promise.all([
          getAllExamItems(),
          getExamPackageItems(row.id)
        ])
        
        // 正确处理返回数据格式
        if (itemsRes && itemsRes.code === 200) {
          // 如果是分页数据，使用records属性
          if (itemsRes.data && itemsRes.data.records) {
            this.itemList = itemsRes.data.records
            this.allItemList = [...this.itemList] // 保存完整列表用于过滤
          } else if (Array.isArray(itemsRes.data)) {
            // 如果直接返回数组
            this.itemList = itemsRes.data
            this.allItemList = [...this.itemList] // 保存完整列表用于过滤
          } else {
            this.itemList = []
            this.allItemList = []
            console.warn('获取体检项目格式异常:', itemsRes)
          }
        } else {
          this.itemList = []
          this.allItemList = []
          this.$message.warning(itemsRes?.message || '获取体检项目失败')
          return
        }
        
        // 处理已选项目
        if (selectedRes && selectedRes.code === 200) {
          this.selectedItems = selectedRes.data || []
        } else {
          this.selectedItems = []
          this.$message.warning(selectedRes?.message || '获取已选项目失败')
          return
        }
        
        this.itemVisible = true
        
        // 确保在DOM更新后执行选中操作
        this.$nextTick(() => {
          if (this.$refs.itemTable) {
            this.$refs.itemTable.clearSelection()
            
            if (this.selectedItems && this.selectedItems.length > 0) {
              this.selectedItems.forEach(item => {
                const rowToSelect = this.itemList.find(i => i.id === item.id)
                if (rowToSelect) {
                  this.$refs.itemTable.toggleRowSelection(rowToSelect, true)
                }
              })
            }
          }
        })
      } catch (error) {
        console.error('获取体检项目失败:', error)
        this.$message.error('获取体检项目失败: ' + (error.message || '系统异常'))
      }
    },
    handleItemFilter() {
      if (!this.itemQuery.keyword) {
        // 如果关键字为空，重新加载项目
        this.handleItems({ id: this.itemQuery.packageId })
        return
      }
      
      // 本地过滤，支持项目名称和项目编码搜索
      const keyword = this.itemQuery.keyword.toLowerCase()
      this.itemList = this.allItemList.filter(item => {
        return (
          (item.name && item.name.toLowerCase().includes(keyword)) || 
          (item.code && item.code.toLowerCase().includes(keyword))
        )
      })
      
      // 刷新选中状态
      this.$nextTick(() => {
        if (this.$refs.itemTable) {
          this.$refs.itemTable.clearSelection()
          
          if (this.selectedItems && this.selectedItems.length > 0) {
            this.selectedItems.forEach(item => {
              const rowToSelect = this.itemList.find(i => i.id === item.id)
              if (rowToSelect) {
                this.$refs.itemTable.toggleRowSelection(rowToSelect, true)
              }
            })
          }
        }
      })
    },
    handleItemSelectionChange(val) {
      this.selectedItems = val
    },
    async submitItems() {
      try {
        const response = await updateExamPackageItems(this.itemQuery.packageId, {
          itemIds: this.selectedItems.map(item => item.id)
        })
        
        if (response && response.code === 200) {
          this.$message.success('保存成功')
          this.itemVisible = false
          this.getList()
        } else {
          this.$message.error(response?.message || '保存套餐项目失败')
        }
      } catch (error) {
        console.error('保存套餐项目失败:', error)
        this.$message.error('保存套餐项目失败: ' + (error.message || '系统异常'))
      }
    },
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
    },
    
    getGenderLabel(gender) {
      const genderMap = {
        0: '不限',
        1: '男',
        2: '女'
      }
      return genderMap[gender] || '不限'
    },
    
    formatPrice(price) {
      if (price === null || price === undefined) return '0.00元'
      return (price / 100).toFixed(2) + '元'
    },
    
    async handleStatusChange(row) {
      try {
        await updateExamPackageStatus(row.id, row.status)
        this.$message.success('状态更新成功')
      } catch (error) {
        console.error('更新状态失败:', error)
        // 恢复原状态
        row.status = row.status === 1 ? 0 : 1
        this.$message.error('更新状态失败')
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