<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="form-inline">
        <el-form-item>
          <el-input
            v-model="listQuery.name"
            placeholder="项目名称"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.categoryId" placeholder="项目分类" clearable>
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.departmentId" placeholder="执行科室" clearable>
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
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增项目</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%">
      <el-table-column label="项目名称" prop="name" />
      <el-table-column label="项目编码" prop="code" width="120" />
      <el-table-column label="项目分类" prop="categoryName" width="120" />
      <el-table-column label="执行科室" prop="departmentName" width="120" />
      <el-table-column label="参考值" prop="referenceValue" show-overflow-tooltip />
      <el-table-column label="单位" prop="unit" width="80" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
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

    <!-- 项目表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目编码" prop="code">
          <el-input v-model="temp.code" placeholder="请输入项目编码" />
        </el-form-item>
        <el-form-item label="项目分类" prop="categoryId">
          <el-select v-model="temp.categoryId" placeholder="请选择项目分类">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="执行科室" prop="departmentId">
          <el-select v-model="temp.departmentId" placeholder="请选择执行科室">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="参考值" prop="referenceValue">
          <el-input
            v-model="temp.referenceValue"
            type="textarea"
            :rows="2"
            placeholder="请输入参考值范围"
          />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="temp.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="temp.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
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
import Pagination from '@/components/Pagination'
import { getExamItemList, createExamItem, updateExamItem, deleteExamItem } from '@/api/exam/item'
import { getAllDepartments } from '@/api/department'
import { getDictItems } from '@/api/dict'

export default {
  name: 'ExamItem',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        name: undefined,
        categoryId: undefined,
        departmentId: undefined
      },
      categoryOptions: [],
      departmentOptions: [],
      dialogVisible: false,
      dialogTitle: '',
      temp: {
        id: undefined,
        name: '',
        code: '',
        categoryId: undefined,
        departmentId: undefined,
        referenceValue: '',
        unit: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入项目名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入项目编码', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择项目分类', trigger: 'change' }
        ],
        departmentId: [
          { required: true, message: '请选择执行科室', trigger: 'change' }
        ]
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
      this.listLoading = true
      try {
        const { data } = await getExamItemList(this.listQuery)
        this.list = data.items
        this.total = data.total
      } catch (error) {
        console.error('获取体检项目列表失败:', error)
      }
      this.listLoading = false
    },
    async getCategoryOptions() {
      try {
        const { data } = await getDictItems('exam_item_category')
        this.categoryOptions = data
      } catch (error) {
        console.error('获取项目分类失败:', error)
      }
    },
    async getDepartmentOptions() {
      try {
        const { data } = await getAllDepartments()
        this.departmentOptions = data
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
        name: undefined,
        categoryId: undefined,
        departmentId: undefined
      }
      this.getList()
    },
    handleCreate() {
      this.dialogTitle = '新增体检项目'
      this.temp = {
        id: undefined,
        name: '',
        code: '',
        categoryId: undefined,
        departmentId: undefined,
        referenceValue: '',
        unit: '',
        remark: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.dialogTitle = '编辑体检项目'
      this.temp = Object.assign({}, row)
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('是否确认删除该体检项目?', '提示', {
          type: 'warning'
        })
        await deleteExamItem(row.id)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('删除体检项目失败:', error)
      }
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            if (this.temp.id) {
              await updateExamItem(this.temp)
            } else {
              await createExamItem(this.temp)
            }
            this.dialogVisible = false
            this.$message.success('保存成功')
            this.getList()
          } catch (error) {
            console.error('保存体检项目失败:', error)
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

  .filter-container {
    margin-bottom: 20px;
  }

  .action-container {
    margin-bottom: 20px;
  }
}
</style> 