<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" autocomplete="on" label-position="left">
      <div class="title-container">
        <h3 class="title">校医院体检信息系统</h3>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <i class="el-icon-user"/>
        </span>
        <el-input
          ref="username"
          v-model="loginForm.username"
          placeholder="用户名"
          name="username"
          type="text"
          tabindex="1"
          autocomplete="on"
        />
      </el-form-item>

      <el-form-item prop="password">
        <span class="svg-container">
          <i class="el-icon-lock"/>
        </span>
        <el-input
          :key="passwordType"
          ref="password"
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="密码"
          name="password"
          tabindex="2"
          autocomplete="on"
          @keyup.enter.native="handleLogin"
        />
        <span class="show-pwd" @click="showPwd">
          <i :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-hide'"/>
        </span>
      </el-form-item>

      <el-form-item prop="captcha">
        <span class="svg-container">
          <i class="el-icon-picture-outline"/>
        </span>
        <el-input
          v-model="loginForm.captchaCode"
          placeholder="验证码"
          style="width: 65%"
          @keyup.enter.native="handleLogin"
        />
        <img 
          :src="captchaUrl" 
          alt="验证码" 
          class="captcha-img"
          @click="refreshCaptcha"
        >
      </el-form-item>

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">
        登录
      </el-button>
      <el-button type="text" style="width:100%;" @click.native.prevent="handleRegister">
        还没有账号？立即注册
      </el-button>
    </el-form>
  </div>
</template>

<script>
import { validUsername } from '@/utils/validate'
import { login, getCaptcha } from '@/api/auth'

// 登录成功后的路由处理
async function handleLoginSuccess(response, router, store) {
  try {
    // 打印完整的响应数据，用于调试
    console.log('Full response:', JSON.stringify(response, null, 2))
    
    // 确保响应数据结构正确
    if (!response.data || !response.data.data || !response.data.data.user) {
      throw new Error('登录响应数据格式错误')
    }
    
    // 生成路由并添加到路由器
    await store.dispatch('user/generateRoutes')
    
    return true
  } catch (error) {
    console.error('Handle login success error:', error)
    throw error
  }
}

export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('请输入正确的用户名'))
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
    return {
      loginForm: {
        username: 'admin',
        password: '123456',
        captchaCode: '',
        captchaKey: ''
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }],
        captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },
      passwordType: 'password',
      loading: false,
      redirect: undefined,
      otherQuery: {},
      captchaUrl: '',
      captchaLoading: false
    }
  },
  created() {
  // 清除所有本地存储
  localStorage.clear()
  sessionStorage.clear()
  this.getCaptcha()
  },
  watch: {
    $route: {
      handler: function(route) {
        const query = route.query
        if (query) {
          this.redirect = query.redirect
          this.otherQuery = this.getOtherQuery(query)
        }
      },
      immediate: true
    }
  },
  methods: {
    getCaptcha() {
      this.captchaLoading = true
      getCaptcha()
        .then(response => {
          const { key, image } = response.data
          this.captchaUrl = image
          this.loginForm.captchaKey = key
        })
        .catch(() => {
          this.$message.error('获取验证码失败，请刷新重试')
        })
        .finally(() => {
          this.captchaLoading = false
        })
    },
    refreshCaptcha() {
      this.getCaptcha()
    },
    showPwd() {
      this.passwordType = this.passwordType === 'password' ? '' : 'password'
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            // 构造登录参数，包含验证码信息
            const loginData = {
              username: this.loginForm.username,
              password: this.loginForm.password,
              captchaCode: this.loginForm.captchaCode,
              captchaKey: this.loginForm.captchaKey
            }
            
            console.log('Submitting login data:', loginData)
            
            // 调用登录接口
            const { redirectPath } = await this.$store.dispatch('user/login', loginData)
            
             // 登录成功后强制跳转到 dashboard
            await this.$router.push('/dashboard')
            // 显示登录成功消息
            this.$message.success('登录成功')
          } catch (error) {
            console.error('Login error:', error)
            // 登录失败，刷新验证码
            this.getCaptcha()
            this.$message.error(error.message || '登录失败')
          } finally {
            this.loading = false
          }
        }
      })
    },
    getOtherQuery(query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    },
    handleRegister() {
      this.$router.push('/register')
    }
  }
}
</script>

<style lang="scss" scoped>
$bg: #283443;
$light_gray: #eee;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
    &::first-line {
      color: $cursor;
    }
  }
}

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0 auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $light_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $light_gray;
    cursor: pointer;
    user-select: none;
  }

  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0;
      -webkit-appearance: none;
      border-radius: 0;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }

  .captcha-img {
    height: 32px;
    margin-top: 4px;
    margin-left: 10px;
    cursor: pointer;
    vertical-align: middle;
  }

  .el-form-item {
    position: relative;
    
    .el-input {
      display: inline-block;
    }
  }
}
</style>