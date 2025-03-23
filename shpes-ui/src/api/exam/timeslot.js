import request from '@/utils/request'

// 获取时间段列表
export function getTimeSlotList(params) {
  return request({
    url: '/api/exam/timeslots/list',
    method: 'get',
    params
  })
}

// 创建时间段
export function createTimeSlot(data) {
  return request({
    url: '/api/exam/timeslots',
    method: 'post',
    data
  })
}

// 更新时间段
export function updateTimeSlot(data) {
  return request({
    url: `/api/exam/timeslots/${data.id}`,
    method: 'put',
    data
  })
}

// 删除时间段
export function deleteTimeSlot(id) {
  return request({
    url: `/api/exam/timeslots/${id}`,
    method: 'delete'
  })
}

// 批量创建时间段
export function batchCreateTimeSlot(data) {
  return request({
    url: '/api/exam/timeslots/batch',
    method: 'post',
    data
  })
}

// 更新时间段容量
export function updateTimeSlotCapacity(id, capacity) {
  return request({
    url: `/api/exam/timeslots/${id}/capacity`,
    method: 'put',
    data: { capacity }
  })
}

// 获取可用时间段
export function getAvailableTimeSlots(params) {
  return request({
    url: '/api/exam/timeslots/available',
    method: 'get',
    params
  })
} 