import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'
import { setToken, removeToken } from '@/utils/auth'

// Create axios instance with base configuration
const service = axios.create({
  baseURL: '/api',  // 添加 baseURL
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 不需要token的白名单
const whiteList = [
  '/api/auth/login',
  '/api/auth/captcha',
  '/api/auth/register'
]

// Request interceptor for API calls
service.interceptors.request.use(
  config => {
    // 检查请求路径是否在白名单中
    const isWhitelisted = whiteList.some(path => config.url === path)
    
    console.log('Request path check:', {
      url: config.url,
      isWhitelisted: isWhitelisted
    })
    
    // 只有不在白名单中的请求才添加token
    if (!isWhitelisted) {
      const token = localStorage.getItem('token')
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
    }
    
    // 打印请求信息，用于调试
    console.log('Request:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data
    })
    
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor for API calls
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 打印响应信息，用于调试
    console.log('Response:', {
      url: response.config.url,
      status: response.status,
      data: res
    })

    // 处理业务状态码
    if (res.code === 200) {
      return res
    }

    // 处理特定的业务状态码
    if (res.code === 401) {
      // 如果不是登录请求，则清除 token 并跳转到登录页
      const isWhitelisted = whiteList.some(path => response.config.url === path)
      if (!isWhitelisted) {
        removeToken()
        router.push('/login')
      }
    }

    // 显示错误消息
    Message({
      message: res.message || '请求失败',
      type: 'error',
      duration: 5 * 1000
    })
    
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    console.error('Response error:', error)
    const { response } = error
    let message = '请求失败'
    
    if (response) {
      // 打印详细的错误信息
      console.error('Error response:', {
        status: response.status,
        statusText: response.statusText,
        data: response.data,
        headers: response.headers,
        config: response.config
      })

      switch (response.status) {
        case 401:
          message = response.data?.message || '未授权，请重新登录'
          // 如果不是白名单中的请求，则清除 token 并跳转到登录页
          const isWhitelisted = whiteList.some(path => response.config.url === path)
          if (!isWhitelisted) {
            removeToken()
            router.push('/login')
          }
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求错误，未找到该资源'
          break
        case 500:
          message = '服务器错误'
          break
        default:
          message = response.data?.message || '请求失败'
      }
    } else {
      message = '网络错误，请检查网络连接'
    }

    Message({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export const login = (data) => {
  // 确保移除旧的 token
  removeToken()
  console.log('Sending login request with data:', data)
  return service.post('/api/auth/login', {
    ...data,
    captchaCode: data.captchaCode || '',  // 添加验证码
    captchaKey: data.captchaKey || ''     // 添加验证码key
  })
}

export const logout = () => {
  return service.post('/api/auth/logout')
}

export const getUserInfo = () => {
  return service.get('/api/users/info')
}

export const getCaptcha = () => {
  return service.get('/api/auth/captcha')
}