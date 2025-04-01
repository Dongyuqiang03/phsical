import request from '@/utils/request'

// 创建预约
export function createAppointment(data) {
  return request({
    url: '/api/exam/appointments',
    method: 'post',
    data
  })
}

// 获取预约列表
export function getAppointmentList(params) {
  return request({
    url: '/api/exam/appointments/list',
    method: 'get',
    params
  })
}

// 取消预约
export function cancelAppointment(id, reason) {
  return request({
    url: `/api/exam/appointments/${id}/cancel`,
    method: 'put',
    data: { reason }
  })
}

// 修改预约时间
export function updateAppointmentTime(id, timeSlotId) {
  return request({
    url: `/api/exam/appointments/${id}/time`,
    method: 'put',
    params: { timeSlotId }
  })
}

// 获取预约详情
export function getAppointmentDetail(id) {
  return request({
    url: `/api/exam/appointments/${id}`,
    method: 'get'
  })
}

// 获取当前用户的预约列表
export function getUserAppointmentList(params) {
  return request({
    url: '/api/exam/appointments/user',
    method: 'get',
    params
  })
}

// 完成体检
export function completeAppointment(id) {
  return request({
    url: `/api/exam/appointments/${id}/complete`,
    method: 'put'
  })
} 