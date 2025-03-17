import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'
import { removeToken } from '@/utils/auth'

// Create axios instance with base configuration
const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// Request interceptor for API calls
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
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
    if (res.code && res.code !== 200) {
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    console.error('Response error:', error)
    const { response } = error
    let message = '请求失败'
    
    if (response) {
      switch (response.status) {
        case 401:
          message = '未授权，请重新登录'
          if (router.currentRoute.path !== '/login') {
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
  return service.post('/auth/login', data)
}

export const logout = () => {
  return service.post('/auth/logout')
}

export const getUserInfo = () => {
  return service.get('/users/info')
}