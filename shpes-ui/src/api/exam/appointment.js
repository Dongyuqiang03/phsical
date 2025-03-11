import request from '@/utils/request'

// 创建预约
export function createAppointment(data) {
  return request({
    url: '/api/exam/appointment',
    method: 'post',
    data
  })
}

// 获取预约列表
export function getAppointmentList(params) {
  return request({
    url: '/api/exam/appointment/list',
    method: 'get',
    params
  })
}

// 取消预约
export function cancelAppointment(id) {
  return request({
    url: `/api/exam/appointment/${id}/cancel`,
    method: 'put'
  })
}

// 修改预约时间
export function updateAppointmentTime(id, data) {
  return request({
    url: `/api/exam/appointment/${id}/time`,
    method: 'put',
    data
  })
}

// 获取预约详情
export function getAppointmentDetail(id) {
  return request({
    url: `/api/exam/appointment/${id}`,
    method: 'get'
  })
} 