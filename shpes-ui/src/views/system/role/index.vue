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
    <div class="table-container" style="overflow: auto; max-height: calc(100vh - 280px);">
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
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

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
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getRoleList, createRole, updateRole, deleteRole, batchDeleteRole, getPermissionTree, updateRolePermissions } from '@/api/role'

export default {
  name: 'Role',
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        code: undefined
      },
      selectedIds: [],
      dialogVisible: false,
      dialogTitle: '',
      permissionVisible: false,
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
        label: 'permissionName',
        id: 'id'
      },
      checkedPermissions: [] // 已选权限ID
    }
  },
  computed: {
    ...mapGetters([
      'device'
    ])
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
          this.list = response.data.records || []
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
      this.listQuery.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
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
        await updateRole({
          id: row.id,
          status: row.status
        })
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
      try {
        // 获取权限树数据
        const { data: treeData } = await getPermissionTree()
        this.permissionData = treeData || []
        
        // 获取当前角色已有的权限
        const { data: rolePermissions } = await getRolePermissions(row.id)
        this.checkedPermissions = rolePermissions || []
        
        this.temp.id = row.id
        this.permissionVisible = true
      } catch (error) {
        console.error('获取角色权限失败:', error)
        this.$message.error('获取角色权限失败')
      }
    },
    async submitPermissions() {
      try {
        const checkedKeys = this.$refs.permissionTree.getCheckedKeys()
        const halfCheckedKeys = this.$refs.permissionTree.getHalfCheckedKeys()
        await updateRolePermissions(this.temp.id, {
          permissions: [...checkedKeys, ...halfCheckedKeys]
        })
        this.$message.success('权限设置成功')
        this.permissionVisible = false
      } catch (error) {
        console.error('设置角色权限失败:', error)
      }
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val;
      this.listQuery.pageNum = 1;  // 切换每页条数时重置为第一页
      this.getList();
      // 修复滚动问题
      this.$nextTick(() => {
        document.documentElement.scrollTop = 0;
        document.body.scrollTop = 0;
        const mainContent = document.querySelector('.app-container');
        if (mainContent) mainContent.scrollTop = 0;
      });
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
      // 滚动到顶部
      this.$nextTick(() => {
        document.documentElement.scrollTop = 0;
        document.body.scrollTop = 0;
      });
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

  .filter-item {
    margin-right: 10px;
  }
}

.table-container {
  margin-bottom: 10px;
}

.pagination-wrapper {
  padding: 15px 0;
  display: flex;
  justify-content: center;
  background-color: #fff;
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