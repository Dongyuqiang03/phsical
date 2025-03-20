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
            v-model="listQuery.name"
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
        <!-- 注释掉部门下拉列表
        <el-form-item>
          <el-select v-model="listQuery.departmentId" placeholder="部门" clearable>
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name || item.departmentName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        -->
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
      row-key="id"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="姓名" prop="name">
        <template slot-scope="{row}">
          <span>{{ row && row.name || '未设置' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户类型" prop="userType">
        <template slot-scope="{row}">
          <span>{{ row && getUserTypeName(row.userType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="角色">
        <template slot-scope="{row}">
          <span v-if="row && row.roles && Array.isArray(row.roles)">
            {{ row.roles.map(role => role && role.roleName).filter(Boolean).join(', ') || '未设置' }}
          </span>
          <span v-else>未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="部门" prop="departmentName">
        <template slot-scope="{row}">
          <span>{{ row && row.departmentName || '未设置' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" prop="phone">
        <template slot-scope="{row}">
          <span>{{ row && row.phone || '未设置' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="邮箱" prop="email">
        <template slot-scope="{row}">
          <span>{{ row && row.email || '未设置' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template slot-scope="{row}">
          <el-switch
            v-if="row"
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
          <span v-else>未知</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间">
        <template slot-scope="{row}">
          <span>{{ row && formatCreateTime(row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230">
        <template slot-scope="{row}">
          <template v-if="row && row.id">
            <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
            <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
            <el-button type="warning" size="mini" @click="handleResetPwd(row)">重置密码</el-button>
          </template>
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
        <el-form-item label="姓名" prop="name">
          <el-input v-model="temp.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="temp.userType" placeholder="请选择用户类型" @change="handleUserTypeChange">
            <el-option label="医护人员" :value="1" />
            <el-option label="教职工" :value="2" />
            <el-option label="学生" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="roles">
          <el-select v-model="temp.roles" placeholder="请选择角色" multiple>
            <el-option
              v-for="item in filteredRoleOptions"
              :key="item.id"
              :label="item.roleName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <el-select 
            v-model="temp.departmentId" 
            placeholder="请选择部门">
            <el-option
              v-for="item in filteredDepartmentOptions"
              :key="item.id"
              :label="item.departmentName || item.name"
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
import { getDepartmentList } from '@/api/department'

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
        page: 1,
        limit: 10,
        username: undefined,
        name: undefined,
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
        name: '',
        userType: undefined,
        roles: [],
        departmentId: undefined,
        phone: '',
        email: '',
        status: 1
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { validator: validateUsername, trigger: 'blur' }
        ],
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        userType: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ],
        departmentId: [
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
      }
    }
  },
  computed: {
    filteredRoleOptions() {
      if (!this.temp.userType) return this.roleOptions;
      
      const roleCodeMap = {
        1: ['ROLE_MEDICAL', 'ROLE_ADMIN'],
        2: ['ROLE_USER'],
        3: ['ROLE_USER']
      };
      
      const allowedRoleCodes = roleCodeMap[this.temp.userType] || [];
      return this.roleOptions.filter(role => allowedRoleCodes.includes(role.roleCode));
    },
    filteredDepartmentOptions() {
      if (!this.temp.userType) return this.departmentOptions;
      
      if (this.temp.userType === 1) {
        return this.departmentOptions.filter(dept => dept.parentId === 1);
      }
      
      if (this.temp.userType === 2 || this.temp.userType === 3) {
        return this.departmentOptions.filter(dept => dept.parentId === 7);
      }
      
      return this.departmentOptions;
    }
  },
  created() {
    this.getList()
    this.getRoleOptions()
    this.getDepartmentOptions()
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
    async getList() {
      try {
        this.listLoading = true;
        console.log('Fetching user list with query:', this.listQuery);
        const response = await getUserList(this.listQuery);
        console.log('User list response:', response);
        
        if (response && response.code === 200 && response.data) {
          const { records, total } = response.data;
          if (Array.isArray(records)) {
            this.list = records.map(user => {
              if (!user) return null;
              return {
                ...user,
                name: user.name || '未设置',
                phone: user.phone || '未设置',
                email: user.email || '未设置',
                departmentName: user.departmentName || '未设置',
                roles: Array.isArray(user.roles) ? user.roles : [],
                status: typeof user.status === 'number' ? user.status : 0,
                createTime: Array.isArray(user.createTime) ? user.createTime : null,
                updateTime: Array.isArray(user.updateTime) ? user.updateTime : null
              };
            }).filter(Boolean); // 过滤掉空值
            
            this.total = total || 0;
            console.log('Processed user list:', this.list);
          } else {
            console.warn('User list records is not an array:', records);
            this.list = [];
            this.total = 0;
            this.$message.warning('获取用户列表数据格式错误');
          }
        } else {
          console.error('Failed to get user list:', response ? response.message : 'No response');
          this.$message.warning(`获取用户列表失败: ${response && response.message || '未知错误'}`);
          this.list = [];
          this.total = 0;
        }
      } catch (error) {
        console.error('Get user list error:', error);
        this.$message.error('获取用户列表失败');
        this.list = [];
        this.total = 0;
      } finally {
        this.listLoading = false;
      }
    },
    async getRoleOptions() {
      try {
        const response = await getAllRoles()
        console.log('Role options response:', response)
        if (response.code === 200) {
          this.roleOptions = response.data || []
        } else {
          this.roleOptions = []
          this.$message.warning(`获取角色列表失败: ${response.message || '未知错误'}`)
          console.warn('获取角色列表失败:', response.message)
        }
      } catch (error) {
        this.roleOptions = []
        this.$message.warning('获取角色列表失败，可能缺少必要权限')
        console.error('获取角色列表失败:', error)
      }
    },
    async getDepartmentOptions() {
      try {
        const response = await getDepartmentList()
        if (response.code === 200) {
          this.departmentOptions = response.data || []
        } else {
          this.departmentOptions = []
          this.$message.warning(`获取部门列表失败: ${response.message || '未知错误'}`)
        }
      } catch (error) {
        this.departmentOptions = []
        this.$message.error('获取部门列表失败')
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
        username: undefined,
        name: undefined,
        roleId: undefined,
        userType: undefined
      }
      this.getList()
    },
    handleSelectionChange(val) {
      this.selectedIds = val && Array.isArray(val) 
        ? val.filter(item => item && item.id).map(item => item.id)
        : [];
    },
    handleCreate() {
      this.dialogTitle = '新增用户'
      this.temp = {
        id: undefined,
        username: '',
        name: '',
        userType: undefined,
        roles: [],
        departmentId: undefined,
        phone: '',
        email: '',
        status: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      if (!row || !row.id) {
        this.$message.warning('用户数据无效');
        return;
      }
      
      this.dialogTitle = '编辑用户'
      this.temp = Object.assign({}, row)
      
      this.temp.userType = row.userType;
      
      this.temp.roles = Array.isArray(row.roles) 
        ? row.roles.filter(role => role && typeof role === 'object' && role.id).map(role => role.id)
        : [];
        
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
        await updateUserStatus(row.id, row.status)
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
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            const submitData = { ...this.temp };
            
            if (Array.isArray(submitData.roles)) {
              submitData.roleIds = submitData.roles;
              delete submitData.roles;
            }
            
            if (this.temp.id) {
              await updateUser(submitData);
              this.$message.success('更新用户成功');
            } else {
              await createUser(submitData);
              this.$message.success('创建用户成功');
            }
            
            this.dialogVisible = false;
            this.getList();
          } catch (error) {
            console.error('保存用户失败:', error);
            this.$message.error(`保存用户失败: ${error.message || '未知错误'}`);
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
      // 保留已选角色和部门（不清空），只在未选择角色时设置默认角色
      if (!this.temp.roles || this.temp.roles.length === 0) {
        if (value === 1) {
          const medicalRole = this.roleOptions.find(role => role.roleCode === 'ROLE_MEDICAL');
          if (medicalRole) {
            this.temp.roles = [medicalRole.id];
          }
        } else if (value === 2 || value === 3) {
          const userRole = this.roleOptions.find(role => role.roleCode === 'ROLE_USER');
          if (userRole) {
            this.temp.roles = [userRole.id];
          }
        }
      }
      
      // 更新部门必选验证规则
      this.$set(this.rules.departmentId, 0, {
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
</style>