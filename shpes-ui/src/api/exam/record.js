import request from '@/utils/request'

// 获取体检记录列表（分页）
export function getRecordPage(params) {
  return request({
    url: '/api/exam/records/list',
    method: 'get',
    params
  })
}

// 获取体检记录详情
export function getRecordDetail(id) {
  return request({
    url: `/api/exam/records/${id}`,
    method: 'get'
  })
}

// 获取用户体检记录列表
export function getUserRecords(userId) {
  return request({
    url: `/api/exam/records/user/${userId}`,
    method: 'get'
  })
}

// 获取体检记录的结果列表
export function getResultsByRecordId(recordId) {
  return request({
    url: `/api/exam/results/record/${recordId}`,
    method: 'get'
  })
}

// 创建体检结果
export function createResult(data) {
  return request({
    url: '/api/exam/results',
    method: 'post',
    data
  })
}

// 批量创建体检结果
export function createResults(data) {
  return request({
    url: '/api/exam/results/batch',
    method: 'post',
    data
  })
}

// 更新体检结果
export function updateResult(id, data) {
  return request({
    url: `/api/exam/results/${id}`,
    method: 'put',
    data
  })
}

// 完成体检记录
export function completeRecord(id, data) {
  return request({
    url: `/api/exam/records/${id}/complete`,
    method: 'put',
    data
  })
}

// 获取当前用户的体检记录
export function getUserExamRecords(params) {
  return request({
    url: '/api/exam/records/user/current',
    method: 'get',
    params
  })
}

// 导出体检报告
export function exportExamReport(recordId) {
  return request({
    url: `/api/exam/results/export/${recordId}`,
    method: 'get',
    responseType: 'blob'
  })
} 