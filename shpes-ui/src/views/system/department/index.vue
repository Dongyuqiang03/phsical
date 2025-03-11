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
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%">
      <el-table-column label="部门名称" prop="name" />
      <el-table-column label="负责人" prop="leader" />
      <el-table-column label="联系电话" prop="phone" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="info" size="mini" @click="handleUsers(row)">人员</el-button>
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

    <!-- 部门表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="temp.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="temp.phone" placeholder="请输入联系电话" />
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
import { getDepartmentList, createDepartment, updateDepartment, getDepartmentUsers } from '@/api/department'
import { validPhone } from '@/utils/validate'

export default {
  name: 'Department',
  components: { Pagination },
  data() {
    const validatePhone = (rule, value, callback) => {
      if (value && !validPhone(value)) {
        callback(new Error('请输入正确的手机号'))
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
        name: undefined
      },
      dialogVisible: false,
      dialogTitle: '',
      userVisible: false,
      temp: {
        id: undefined,
        name: '',
        leader: '',
        phone: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入部门名称', trigger: 'blur' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
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
        this.list = data.items
        this.total = data.total
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
        name: '',
        leader: '',
        phone: ''
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