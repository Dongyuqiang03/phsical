<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="form-inline">
        <el-form-item>
          <el-input
            v-model="listQuery.name"
            placeholder="角色名称"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="listQuery.code"
            placeholder="角色编码"
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
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增角色</el-button>
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
      <el-table-column label="角色名称" prop="roleName" />
      <el-table-column label="角色编码" prop="roleCode" />
      <el-table-column label="描述" prop="description" show-overflow-tooltip />
      <el-table-column label="状态" align="center">
        <template slot-scope="{row}">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="300">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="success" size="mini" @click="handlePermission(row)">权限设置</el-button>
          <el-button type="info" size="mini" @click="handleUsers(row)">用户管理</el-button>
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

    <!-- 角色表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="temp.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="temp.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="temp.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
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

    <!-- 权限设置对话框 -->
    <el-dialog title="权限设置" :visible.sync="permissionVisible" width="600px">
      <el-tree
        ref="permissionTree"
        :data="permissionData"
        :props="permissionProps"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedPermissions"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPermissions">确定</el-button>
      </div>
    </el-dialog>

    <!-- 用户管理对话框 -->
    <el-dialog title="用户管理" :visible.sync="userVisible" width="800px">
      <div class="filter-container">
        <el-input
          v-model="userQuery.keyword"
          placeholder="请输入用户名或姓名"
          style="width: 200px;"
          class="filter-item"
          @keyup.enter.native="handleUserFilter"
        />
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleUserFilter">
          搜索
        </el-button>
      </div>
      <el-table :data="userList" style="width: 100%">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="用户名" prop="username" />
        <el-table-column label="姓名" prop="realName" />
        <el-table-column label="部门" prop="departmentName" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="userVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUsers">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import { getRoleList, createRole, updateRole, deleteRole, batchDeleteRole, updateRoleStatus, getRolePermissions, updateRolePermissions, getRoleUsers, updateRoleUsers } from '@/api/system/role'

export default {
  name: 'Role',
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
        code: undefined
      },
      selectedIds: [],
      dialogVisible: false,
      dialogTitle: '',
      permissionVisible: false,
      userVisible: false,
      temp: {
        id: undefined,
        roleName: '',
        roleCode: '',
        description: '',
        status: 1
      },
      rules: {
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' }
        ],
        roleCode: [
          { required: true, message: '请输入角色编码', trigger: 'blur' }
        ]
      },
      permissionData: [], // 权限树数据
      permissionProps: {
        children: 'children',
        label: 'name'
      },
      checkedPermissions: [], // 已选权限ID
      userList: [], // 用户列表
      userQuery: {
        keyword: '',
        roleId: undefined
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
        const response = await getRoleList(this.listQuery)
        if (response.code === 200) {
          this.list = response.data.items || []
          this.total = response.data.total || 0
        } else {
          this.list = []
          this.total = 0
          this.$message.warning(`获取角色列表失败: ${response.message || '未知错误'}`)
          console.warn('获取角色列表失败:', response.message)
        }
      } catch (error) {
        this.list = []
        this.total = 0
        this.$message.warning('获取角色列表失败，可能缺少必要权限')
        console.error('获取角色列表失败:', error)
      } finally {
        this.listLoading = false
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
        code: undefined
      }
      this.getList()
    },
    handleSelectionChange(val) {
      this.selectedIds = val.map(item => item.id)
    },
    handleCreate() {
      this.dialogTitle = '新增角色'
      this.temp = {
        id: undefined,
        roleName: '',
        roleCode: '',
        description: '',
        status: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.dialogTitle = '编辑角色'
      this.temp = Object.assign({}, row)
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('是否确认删除该角色?', '提示', {
          type: 'warning'
        })
        await deleteRole(row.id)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('删除角色失败:', error)
      }
    },
    async handleBatchDelete() {
      try {
        await this.$confirm(`是否确认删除这${this.selectedIds.length}个角色?`, '提示', {
          type: 'warning'
        })
        await batchDeleteRole(this.selectedIds)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('批量删除角色失败:', error)
      }
    },
    async handleStatusChange(row) {
      try {
        await updateRoleStatus(row.id, row.status)
        this.$message.success('状态更新成功')
      } catch (error) {
        console.error('更新角色状态失败:', error)
        row.status = row.status === 1 ? 0 : 1
      }
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            if (this.temp.id) {
              await updateRole(this.temp)
            } else {
              await createRole(this.temp)
            }
            this.dialogVisible = false
            this.$message.success('保存成功')
            this.getList()
          } catch (error) {
            console.error('保存角色失败:', error)
          }
        }
      })
    },
    async handlePermission(row) {
      this.userQuery.roleId = row.id
      try {
        const { data } = await getRolePermissions(row.id)
        this.permissionData = data.permissions
        this.checkedPermissions = data.checkedKeys
        this.permissionVisible = true
      } catch (error) {
        console.error('获取角色权限失败:', error)
      }
    },
    async submitPermissions() {
      try {
        const checkedKeys = this.$refs.permissionTree.getCheckedKeys()
        const halfCheckedKeys = this.$refs.permissionTree.getHalfCheckedKeys()
        await updateRolePermissions(this.userQuery.roleId, {
          permissions: [...checkedKeys, ...halfCheckedKeys]
        })
        this.$message.success('权限设置成功')
        this.permissionVisible = false
      } catch (error) {
        console.error('设置角色权限失败:', error)
      }
    },
    async handleUsers(row) {
      this.userQuery.roleId = row.id
      try {
        const { data } = await getRoleUsers(row.id)
        this.userList = data
        this.userVisible = true
      } catch (error) {
        console.error('获取角色用户失败:', error)
      }
    },
    handleUserFilter() {
      // TODO: 根据关键字过滤用户列表
    },
    async submitUsers() {
      try {
        const selectedUsers = this.$refs.userTable.selection
        await updateRoleUsers(this.userQuery.roleId, {
          userIds: selectedUsers.map(user => user.id)
        })
        this.$message.success('用户分配成功')
        this.userVisible = false
      } catch (error) {
        console.error('分配角色用户失败:', error)
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