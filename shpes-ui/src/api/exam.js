import axios from 'axios'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// Request interceptor
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

// Response interceptor
service.interceptors.response.use(
  response => response.data,
  error => {
    return Promise.reject(error)
  }
)

// Get available exam packages
export const getExamPackages = () => {
  return service.get('/exam/packages')
}

// Get available time slots for a specific date
export const getTimeSlots = (date) => {
  return service.get(`/exam/timeslots?date=${date}`)
}

// Create a new appointment
export const createAppointment = (data) => {
  return service.post('/exam/appointments', data)
}

// Get user's exam records
export const getExamRecords = () => {
  return service.get('/exam/records')
}

// Get exam report by record ID
export const getExamReport = (recordId) => {
  return service.get(`/exam/records/${recordId}/report`)
}