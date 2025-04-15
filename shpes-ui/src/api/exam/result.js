import request from '@/utils/request'

// 获取体检报告详情
export function getExamReport(id) {
  return request({
    url: `/api/exam/records/${id}`,
    method: 'get'
  })
}

// 导出体检报告PDF
export function exportReport(id) {
  return request({
    url: `/api/exam/report/${id}/export`,
    method: 'get',
    responseType: 'blob'
  })
}

// 获取体检报告列表
export function getReportList(params) {
  return request({
    url: '/api/exam/report/list',
    method: 'get',
    params
  })
}

// 获取体检结果列表
export function getResultList(params) {
  return request({
    url: '/api/exam/results/list',
    method: 'get',
    params
  })
}

// 获取体检记录的结果列表
export function getResultsByRecordId(recordId) {
  return request({
    url: `/api/exam/results/record/${recordId}`,
    method: 'get'
  })
}

// 录入体检结果
export function createResult(data) {
  return request({
    url: '/api/exam/results',
    method: 'post',
    data
  })
}

// 批量录入体检结果
export function createResults(data) {
  return request({
    url: '/api/exam/results/batch',
    method: 'post',
    data
  })
}

// 提交体检结果 (添加这个函数来修复错误)
export function submitExamResults(data) {
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

// 获取当前用户的体检记录
export function getUserExamRecords(params) {
  console.warn('此API已移动到exam/record.js，请使用新的API路径 /api/exam/records/user/current');
  return request({
    url: '/api/exam/records/user/current',
    method: 'get',
    params
  })
}

// 获取当前用户的体检结果
export function getUserExamResults(params) {
  return request({
    url: '/api/exam/results/user/current',
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

// 提交体检结论
export function submitConclusion(data) {
  return request({
    url: `/api/exam/records/${data.id}/complete`,
    method: 'put',
    params: {
      conclusion: data.findings,
      suggestion: data.suggestion
    }
  })
}

// 审核体检报告
export function reviewReport(id, data) {
  return request({
    url: `/api/exam/report/${id}/review`,
    method: 'post',
    data
  })
}

// 获取体检结果统计
export function getReportStatistics(params) {
  return request({
    url: '/api/exam/report/statistics',
    method: 'get',
    params
  })
}

// 批量导出体检报告
export function batchExportReports(params) {
  return request({
    url: '/api/exam/report/export/batch',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取异常结果列表
export function getAbnormalResults(params) {
  return request({
    url: '/api/exam/report/abnormal',
    method: 'get',
    params
  })
}

// 获取科室体检结果
export function getDepartmentResults(params) {
  return request({
    url: '/api/exam/report/department',
    method: 'get',
    params
  })
}

// 获取体检套餐对应的标准体检项目
export function getPackageItems(packageId) {
  return request({
    url: `/api/exam/packages/${packageId}/items`,
    method: 'get'
  })
} 