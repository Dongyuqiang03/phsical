<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="form-inline">
        <el-form-item>
          <el-input
            v-model="listQuery.username"
            placeholder="用户名"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="listQuery.realName"
            placeholder="姓名"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.userType" placeholder="用户类型" clearable>
            <el-option label="医护人员" :value="1" />
            <el-option label="教职工" :value="2" />
            <el-option label="学生" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.roleId" placeholder="角色" clearable>
            <el-option
              v-for="item in roleOptions"
              :key="item.id"
              :label="item.roleName"
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
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增用户</el-button>
      <el-button type="danger" icon="el-icon-delete" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
      <el-button type="success" icon="el-icon-upload2" @click="handleImport">导入</el-button>
      <el-button type="warning" icon="el-icon-download" @click="handleExport">导出</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
      row-key="id"
      :reserve-selection="true">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="姓名">
        <template slot-scope="{row}">
          <span>{{ row.realName || '未设置' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户类型">
        <template slot-scope="{row}">
          <span>{{ getUserTypeName(row.userType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="角色">
        <template slot-scope="{row}">
          <span>{{ row.roles ? row.roles.map(role => role.roleName).join(', ') : '未设置' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="部门">
        <template slot-scope="{row}">
          <span>{{ row.deptName || '未设置' }}</span> 
        </template>
      </el-table-column>
      <el-table-column label="手机号">
        <template slot-scope="{row}">
          <span>{{ row.phone || '未设置' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="邮箱">
        <template slot-scope="{row}">
          <span>{{ row.email || '未设置' }}</span>
        </template>
      </el-table-column>
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
      <el-table-column label="创建时间">
        <template slot-scope="{row}">
          <span>{{ formatCreateTime(row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="300">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="success" size="mini" @click="handleAssignRoles(row)">分配角色</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
          <el-button type="warning" size="mini" @click="handleResetPwd(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Debug info -->
    <div v-if="true" style="margin: 10px 0; color: #606266; background: #f9f9f9; padding: 8px; border-radius: 4px;">
      <span>API返回总条数: {{ total }}</span>
      <span style="margin-left: 15px">当前页: {{ listQuery.pageNum }}</span>
      <span style="margin-left: 15px">每页条数: {{ listQuery.pageSize }}</span>
      <span style="margin-left: 15px">当前记录数: {{ list.length }}</span>
      <span style="margin-left: 15px">有效总条数: {{ effectiveTotal }}</span>
    </div>

    <!-- 分页区域 -->
    <div class="user-pagination" style="text-align: right; margin-top: 15px; display: block !important; visibility: visible !important; overflow: visible !important; position: relative !important; z-index: 1000 !important; min-height: 50px;">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="listQuery.pageNum"
        :page-sizes="[10, 20, 30, 50]"
        :page-size="listQuery.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="effectiveTotal > 0 ? effectiveTotal : (list.length > 0 ? 999 : 0)">
      </el-pagination>
    </div>

    <!-- 用户表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="temp.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="temp.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="temp.userType" placeholder="请选择用户类型" @change="handleUserTypeChange">
            <el-option label="医护人员" :value="1" />
            <el-option label="教职工" :value="2" />
            <el-option label="学生" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-select 
            v-model="temp.deptId" 
            placeholder="请选择部门">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.deptName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="temp.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="temp.email" placeholder="请输入邮箱" />
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

    <!-- 角色分配对话框 -->
    <el-dialog title="分配角色" :visible.sync="roleDialogVisible" width="500px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="用户">
          <span>{{ roleForm.username }} ({{ roleForm.realName }})</span>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="roleForm.roleIds" multiple placeholder="请选择角色">
            <el-option
              v-for="item in roleOptions"
              :key="item.id"
              :label="item.roleName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRoleAssignment">确定</el-button>
      </div>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog title="导入用户" :visible.sync="importVisible" width="400px">
      <el-upload
        class="upload-demo"
        drag
        action="/api/system/user/import"
        :headers="headers"
        :on-success="handleImportSuccess"
        :on-error="handleImportError"
        accept=".xls,.xlsx">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传excel文件，且不超过10MB</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="downloadTemplate">下载模板</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import Pagination from '@/components/Pagination'
import { validUsername, validPhone, validEmail } from '@/utils/validate'
import { formatDateTime } from '@/utils/date'
import { getUserList, createUser, updateUser, deleteUser, batchDeleteUser, updateUserStatus, resetUserPassword, exportUser } from '@/api/user'
import { getAllRoles } from '@/api/system/role'
import { getAllDepartments } from '@/api/department'

export default {
  name: 'User',
  components: { Pagination },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('用户名必须为3-20个字符，可以包含中文、字母、数字和下划线，不能以下划线开头和结尾'))
      } else {
        callback()
      }
    }
    const validatePhone = (rule, value, callback) => {
      if (value && !validPhone(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }
    const validateEmail = (rule, value, callback) => {
      if (value && !validEmail(value)) {
        callback(new Error('请输入正确的邮箱'))
      } else {
        callback()
      }
    }
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        username: undefined,
        realName: undefined,
        roleId: undefined,
        userType: undefined
      },
      roleOptions: [],
      departmentOptions: [],
      selectedIds: [],
      dialogVisible: false,
      dialogTitle: '',
      importVisible: false,
      temp: {
        id: undefined,
        username: '',
        realName: '',
        userType: undefined,
        roles: [],
        deptId: undefined,
        phone: '',
        email: '',
        status: 1
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { validator: validateUsername, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        userType: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ],
        deptId: [
          { required: false, message: '请选择部门', trigger: 'change' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        email: [
          { validator: validateEmail, trigger: 'blur' }
        ]
      },
      headers: {
        Authorization: 'Bearer ' + getToken()
      },
      roleDialogVisible: false,
      roleForm: {
        id: undefined,
        username: '',
        realName: '',
        userType: undefined,
        roleIds: []
      }
    }
  },
  computed: {
    filteredRoleOptions() {
      if (!this.temp.userType) return this.roleOptions;
      
      const roleCodeMap = {
        1: ['ROLE_MEDICAL', 'ROLE_ADMIN'], // 医护人员可以是医护人员或管理员
        2: ['ROLE_USER', 'ROLE_ADMIN'],    // 教职工可以是普通用户或管理员
        3: ['ROLE_USER']                    // 学生只能是普通用户
      };
      
      const allowedRoleCodes = roleCodeMap[this.temp.userType] || [];
      return this.roleOptions.filter(role => allowedRoleCodes.includes(role.roleCode));
    },
    filteredDepartmentOptions() {
      return this.departmentOptions;
    },
    effectiveTotal() {
      // Use our helper method to get correct total
      return this.getTotalRecords();
    }
  },
  created() {
    this.getList()
    this.getRoleOptions()
  },
  mounted() {
    // Add timeout to ensure DOM is fully rendered
    setTimeout(() => {
      const paginationEl = document.querySelector('.user-pagination');
      if (paginationEl) {
        console.log('Pagination element found:', paginationEl);
        console.log('Pagination dimensions:', paginationEl.getBoundingClientRect());
        
        // Ensure the parent container is visible and has height
        const parentEl = paginationEl.parentElement;
        if (parentEl) {
          console.log('Parent element dimensions:', parentEl.getBoundingClientRect());
        }
      } else {
        console.error('Pagination element not found in DOM');
      }
    }, 1000);
  },
  methods: {
    formatCreateTime(timeArray) {
      if (!timeArray || !Array.isArray(timeArray) || timeArray.length < 6) {
        return '未设置';
      }
      try {
        const [year, month, day, hour, minute, second] = timeArray;
        if ([year, month, day, hour, minute, second].some(val => typeof val !== 'number')) {
          return '未设置';
        }
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`;
      } catch (error) {
        console.error('Error formatting time:', error);
        return '未设置';
      }
    },
    async getList(pagination) {
      try {
        if (pagination) {
          this.listQuery.pageNum = pagination.pageNum;
          this.listQuery.pageSize = pagination.pageSize;
        }
        
        this.listLoading = true;
        const response = await getUserList(this.listQuery);
        
        console.log('API Response:', response);
        
        if (response && response.code === 200 && response.data) {
          this.list = response.data.records || [];
          
          // 处理总记录数
          if (response.data.total > 0) {
            // 如果API返回的总记录数大于0，使用API返回的值
            this.total = response.data.total;
          } else if (this.list.length > 0) {
            // 如果API返回总记录数为0但返回了记录，需要处理
            if (this.listQuery.pageNum > 1) {
              // 如果当前不是第一页，我们可以估算总记录数
              // 假设之前页都是满的
              this.total = (this.listQuery.pageNum - 1) * this.listQuery.pageSize + this.list.length;
            } else {
              // 如果是第一页，记录数就是页面上显示的记录数
              this.total = this.list.length;
            }
            console.log('计算得到的总记录数:', this.total);
          } else {
            this.total = 0;
          }
          
          console.log('List data:', this.list);
          console.log('Total records:', this.total);
        } else {
          this.list = [];
          this.total = 0;
          console.log('No valid data in response');
        }
      } catch (error) {
        console.error('Get user list error:', error);
        this.list = [];
        this.total = 0;
        this.$message.error('获取用户列表失败');
      } finally {
        this.listLoading = false;
      }
    },
    async getRoleOptions() {
      try {
        const response = await getAllRoles()
        if (response.code === 200) {
          this.roleOptions = response.data || []
        } else {
          this.roleOptions = []
          this.$message.warning(`获取角色列表失败: ${response.message || '未知错误'}`)
        }
      } catch (error) {
        this.roleOptions = []
        this.$message.error('获取角色列表失败')
        console.error('获取角色列表失败:', error)
      }
    },
    async getDepartmentOptions() {
      try {
        const response = await getAllDepartments()
        if (response.code === 200) {
          this.departmentOptions = response.data || []
        } else {
          this.departmentOptions = []
          this.$message.warning(`获取部门列表失败: ${response.message || '未知错误'}`)
        }
      } catch (error) {
        this.departmentOptions = []
        console.error('获取部门列表失败:', error)
        this.$message.error('获取部门列表失败')
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
        username: undefined,
        realName: undefined,
        roleId: undefined,
        userType: undefined
      }
      this.getList()
    },
    getRowKey(row) {
      return row ? row.id : null;
    },
    checkSelectable(row) {
      return row && row.id;
    },
    handleSelectionChange(val) {
      this.selectedIds = (val || []).filter(item => item && item.id).map(item => item.id);
    },
    async handleCreate() {
      this.dialogTitle = '新增用户'
      this.temp = {
        id: undefined,
        username: '',
        realName: '',
        userType: undefined,
        roles: [],
        deptId: undefined,
        phone: '',
        email: '',
        status: 1
      }
      this.dialogVisible = true
      // 同时获取角色和部门列表
      await Promise.all([
        this.getRoleOptions(),
        this.getDepartmentOptions()
      ])
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    async handleUpdate(row) {
      if (!row || !row.id) {
        this.$message.warning('用户数据无效');
        return;
      }
      
      this.dialogTitle = '编辑用户'
      
      // 先获取部门列表
      await this.getDepartmentOptions()
      
      this.temp = {
        id: row.id,
        username: row.username,
        realName: row.realName,
        userType: row.userType,
        deptId: row.deptId,
        phone: row.phone,
        email: row.email,
        status: row.status
      }
      
      this.dialogVisible = true
      this.$nextTick(() => {
        if (this.$refs['dataForm']) {
          this.$refs['dataForm'].clearValidate()
        }
      })
    },
    async handleDelete(row) {
      if (!row || !row.id) {
        this.$message.warning('用户数据无效');
        return;
      }
      
      try {
        await this.$confirm('是否确认删除该用户?', '提示', {
          type: 'warning'
        })
        await deleteUser(row.id)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('删除用户失败:', error)
      }
    },
    async handleBatchDelete() {
      if (!this.selectedIds || !this.selectedIds.length) {
        this.$message.warning('请先选择要删除的用户');
        return;
      }
      
      try {
        await this.$confirm(`是否确认删除这${this.selectedIds.length}个用户?`, '提示', {
          type: 'warning'
        })
        await batchDeleteUser(this.selectedIds)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('批量删除用户失败:', error)
      }
    },
    async handleStatusChange(row) {
      if (!row || !row.id) {
        this.$message.warning('用户数据无效');
        return;
      }
      
      try {
        await updateUser({
          id: row.id,
          status: row.status
        })
        this.$message.success('状态更新成功')
      } catch (error) {
        console.error('更新用户状态失败:', error)
        // 恢复原状态
        if (row) {
          row.status = row.status === 1 ? 0 : 1
        }
      }
    },
    async handleResetPwd(row) {
      if (!row || typeof row.id === 'undefined') {
        this.$message.warning('用户数据无效');
        return;
      }
      
      try {
        await this.$confirm('是否确认重置该用户的密码?', '提示', {
          type: 'warning'
        })
        await resetUserPassword(row.id)
        this.$message.success('密码重置成功')
      } catch (error) {
        console.error('重置密码失败:', error)
      }
    },
    async handleAssignRoles(row) {
      this.roleForm = {
        id: row.id,
        username: row.username,
        realName: row.realName,
        userType: row.userType,
        roleIds: Array.isArray(row.roles) 
          ? row.roles.filter(role => role && typeof role === 'object' && role.id).map(role => role.id)
          : []
      }
      this.roleDialogVisible = true
    },
    async submitRoleAssignment() {
      try {
        if (!this.roleForm.roleIds || this.roleForm.roleIds.length === 0) {
          this.$message.warning('请至少选择一个角色')
          return
        }
        
        await updateUser({
          id: this.roleForm.id,
          roleIds: this.roleForm.roleIds
        })
        
        this.$message.success('角色分配成功')
        this.roleDialogVisible = false
        this.getList()
      } catch (error) {
        console.error('角色分配失败:', error)
        this.$message.error(`角色分配失败: ${error.response?.data?.message || error.message || '未知错误'}`)
      }
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            const submitData = { ...this.temp }
            
            if (this.temp.id) {
              await updateUser(submitData)
              this.$message.success('更新用户成功')
            } else {
              await createUser(submitData)
              this.$message.success('创建用户成功，初始密码请联系管理员')
            }
            
            this.dialogVisible = false
            this.getList()
          } catch (error) {
            console.error('保存用户失败:', error)
            this.$message.error(`保存用户失败: ${error.response?.data?.message || error.message || '未知错误'}`)
          }
        }
      })
    },
    handleImport() {
      this.importVisible = true
    },
    handleExport() {
      exportUser(this.listQuery)
    },
    handleImportSuccess() {
      this.importVisible = false
      this.$message.success('导入成功')
      this.getList()
    },
    handleImportError() {
      this.$message.error('导入失败')
    },
    downloadTemplate() {
      // TODO: 下载用户导入模板
    },
    handleUserTypeChange(value) {
      // 只更新部门必选验证规则
      this.$set(this.rules.deptId, 0, {
        required: value === 1,
        message: value === 1 ? '医护人员必须选择部门' : '请选择部门',
        trigger: 'change'
      });
    },
    getUserTypeName(type) {
      const typeMap = {
        1: '医护人员',
        2: '教职工',
        3: '学生'
      };
      return typeMap[type] || '未知';
    },
    // 分页相关方法
    handleSizeChange(val) {
      this.listQuery.pageSize = val;
      this.getList();
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
    },
    
    // 获取正确的总记录数，处理后端返回的总记录数可能为0的情况
    getTotalRecords() {
      if (this.total > 0) {
        return this.total;
      }
      
      // 如果后端返回的总记录数为0但有数据，使用当前页面的记录数作为总数
      if (this.list.length > 0) {
        // 估计总记录数为当前页记录数乘以总页数
        return this.list.length * (this.listQuery.pageNum || 1);
      }
      
      return 0;
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

  .upload-demo {
    text-align: center;
  }
}

.user-pagination {
  margin-top: 20px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>

<style>
/* Global styles for pagination */
.el-pagination {
  display: flex !important;
  justify-content: flex-end !important;
  margin-top: 20px !important;
  padding: 10px !important;
  background-color: #f0f2f5 !important;
  border-radius: 4px !important;
  opacity: 1 !important;
}

.app-container .user-pagination,
.user-pagination {
  display: block !important;
  visibility: visible !important;
  opacity: 1 !important;
  position: relative !important;
  z-index: 9999 !important;
  height: auto !important;
  min-height: 40px !important;
  margin-bottom: 20px !important;
}

/* Ensure the container has enough height */
.app-container {
  min-height: 800px !important;
  overflow: visible !important;
}
</style>