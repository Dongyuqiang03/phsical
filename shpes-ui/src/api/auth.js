import axios from 'axios'

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
    return Promise.reject(error)
  }
)

// Response interceptor for API calls
service.interceptors.response.use(
  response => response.data,
  error => {
    const { response } = error
    if (response && response.status === 401) {
      // Handle unauthorized access
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
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