<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">校医院体检信息系统</h3>
      <el-form-item prop="username">
        <el-input v-model="loginForm.username" type="text" placeholder="用户名">
          <template #prefix>
            <i class="el-icon-user"></i>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="loginForm.password" type="password" placeholder="密码">
          <template #prefix>
            <i class="el-icon-lock"></i>
          </template>
        </el-input>
      </el-form-item>
      <el-button :loading="loading" type="primary" style="width:100%;" @click.native.prevent="handleLogin">
        {{ loading ? '登录中...' : '登录' }}
      </el-button>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm)
            .then(() => {
              this.$router.push({ path: '/' })
            })
            .catch(() => {
              this.loading = false
            })
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f3f3;

  .login-form {
    width: 400px;
    padding: 35px;
    border-radius: 6px;
    background: #ffffff;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .title {
      text-align: center;
      margin: 0 0 30px;
      color: #333;
      font-size: 22px;
    }
  }
}
</style>