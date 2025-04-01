import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import { refreshToken } from '@/api/auth'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 30000, // 超时时间设置为30秒
  withCredentials: true // 允许携带cookie
})

// 是否正在刷新token的标记
let isRefreshing = false
// 重试队列，每一项将是一个待执行的函数形式
let retryRequests = []

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    console.log('[Request]', config.method.toUpperCase(), config.url, config.data || config.params)
    
    if (store.state.user.token) {
      config.headers['Authorization'] = 'Bearer ' + getToken()
    }
    return config
  },
  error => {
    console.error('[Request Error]', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    console.log('[Response]', response.config.url, response.data)
    const res = response.data

    if (res.code !== 200) {
      Message({
        message: res.message || '错误',
        type: 'error',
        duration: 5 * 1000
      })

      // 50008: 非法token; 50012: 其他客户端登录; 50014: Token过期;
      if (res.code === 50008 || res.code === 50012 || res.code === 50014 || res.code === 401) {
        // 尝试刷新token
        return handleTokenRefresh(response.config)
      }
      return Promise.reject(new Error(res.message || '错误'))
    } else {
      return res
    }
  },
  error => {
    console.error('[Response Error]', error)
    
    // 如果是401错误，可能是token过期，尝试刷新
    if (error.response && error.response.status === 401) {
      return handleTokenRefresh(error.config)
    }
    
    // 处理 CORS 错误
    if (error.message.includes('Network Error')) {
      Message({
        message: '网络错误，请检查您的网络连接或联系管理员',
        type: 'error',
        duration: 5 * 1000
      })
    } else {
      Message({
        message: error.message,
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)

// 处理token刷新
function handleTokenRefresh(config) {
  if (!isRefreshing) {
    isRefreshing = true
    
    // 尝试刷新token
    return refreshToken()
      .then(res => {
        const { token } = res.data
        // 更新store中的token
        store.commit('user/SET_TOKEN', token)
        // 更新localStorage中的token
        localStorage.setItem('Admin-Token', token)
        
        // 执行队列中的请求
        retryRequests.forEach(cb => cb(token))
        // 清空队列
        retryRequests = []
        
        // 重试当前请求
        const newConfig = { ...config }
        newConfig.headers['Authorization'] = 'Bearer ' + token
        return service(newConfig)
      })
      .catch(error => {
        console.error('刷新Token失败，需要重新登录', error)
        // 刷新失败，需要重新登录
        return MessageBox.confirm(
          '您的登录状态已过期，请重新登录',
          '登录过期',
          {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      })
      .finally(() => {
        isRefreshing = false
      })
  } else {
    // 将请求加入队列中
    return new Promise(resolve => {
      // 收集请求，等刷新token后再重新发起请求
      retryRequests.push(token => {
        config.headers['Authorization'] = 'Bearer ' + token
        resolve(service(config))
      })
    })
  }
}

export default service