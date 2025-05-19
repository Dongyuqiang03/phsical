<template>
  <div class="register-container">
    <el-form
      ref="registerForm"
      :model="registerForm"
      :rules="registerRules"
      class="register-form"
      label-position="right"
      label-width="100px">
      <div class="title-container">
        <h3 class="title">用户注册</h3>
      </div>

      <el-form-item label="用户类型" prop="userType">
        <el-select v-model="registerForm.userType" placeholder="请选择用户类型" @change="handleUserTypeChange">
          <el-option label="教职工" :value="2" />
          <el-option label="学生" :value="3" />
        </el-select>
      </el-form-item>

      <el-form-item label="学院" prop="deptId">
        <el-select 
          v-model="registerForm.deptId" 
          placeholder="请选择学院">
          <el-option
            v-for="item in departmentOptions"
            :key="item.id"
            :label="item.deptName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="用户编号" prop="userCode">
        <el-input 
          v-model="registerForm.userCode" 
          :placeholder="registerForm.userType === 3 ? '请输入学生号' : '请输入教职工编号'" 
        />
      </el-form-item>

      <el-form-item label="用户名" prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入用户名" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="请输入密码"
          show-password
        />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="请确认密码"
          show-password
        />
      </el-form-item>

      <el-form-item label="姓名" prop="realName">
        <el-input v-model="registerForm.realName" placeholder="请输入姓名" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item>
        <el-button :loading="loading" type="primary" style="width:100%;" @click.native.prevent="handleRegister">
          注册
        </el-button>
        <el-button type="text" style="width:100%;margin-top:10px;" @click.native.prevent="handleBack">
          已有账号？返回登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { validUsername, validPhone, validEmail } from '@/utils/validate'
import { createUser } from '@/api/user'
import { getAllDepartments } from '@/api/department'

export default {
  name: 'Register',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('用户名必须为3-20个字符，可以包含中文、字母、数字和下划线，不能以下划线开头和结尾'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能少于6位'))
      } else {
        callback()
      }
    }
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
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
      registerForm: {
        userCode: '',
        username: '',
        password: '',
        confirmPassword: '',
        realName: '',
        userType: undefined,
        deptId: undefined,
        phone: '',
        email: '',
        status: 1
      },
      registerRules: {
        userCode: [
          { required: true, message: '请输入用户编号', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { validator: validateUsername, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        userType: [
          { required: true, message: '请选择用户类型', trigger: 'change' }
        ],
        deptId: [
          { required: true, message: '请选择学院', trigger: 'change' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        email: [
          { validator: validateEmail, trigger: 'blur' }
        ]
      },
      departmentOptions: [],
      loading: false
    }
  },
  methods: {
    async handleUserTypeChange() {
      // 当用户类型改变时，重新获取部门列表
      await this.getDepartmentOptions()
      // 清空已选择的部门
      this.registerForm.deptId = undefined
    },
    async getDepartmentOptions() {
      try {
        const response = await getAllDepartments(this.registerForm.userType)
        if (response.code === 200) {
          this.departmentOptions = response.data || []
        } else {
          this.departmentOptions = []
          this.$message.warning(`获取学院列表失败: ${response.message || '未知错误'}`)
        }
      } catch (error) {
        this.departmentOptions = []
        console.error('获取学院列表失败:', error)
        this.$message.error('获取学院列表失败')
      }
    },
    async handleRegister() {
      try {
        const { username, password, realName, userType, deptId, phone, email } = this.registerForm
        const response = await createUser({
          username,
          password,
          realName,
          userType,
          deptId,
          phone,
          email,
          status: 1  // 默认启用
        })
        
        if (response.code === 200) {
          this.$message.success('注册成功')
          this.$router.push('/login')
        } else {
          this.$message.error(response.message || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
        this.$message.error('注册失败，请稍后重试')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100%;
  width: 100%;
  background-color: #283443;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .register-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 40px;
    margin: 0 auto;
    overflow: hidden;
    background: #fff;
    border-radius: 6px;
  }

  .title-container {
    position: relative;
    margin-bottom: 30px;

    .title {
      font-size: 26px;
      color: #333;
      margin: 0 auto;
      text-align: center;
      font-weight: bold;
    }
  }
}
</style>