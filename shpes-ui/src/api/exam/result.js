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

// 录入体检结果
export function submitExamResults(data) {
  return request({
    url: '/api/exam/results/batch',
    method: 'post',
    data
  })
}

// 更新体检结果
export function updateExamResults(data) {
  return request({
    url: `/api/exam/report/results/${data.id}`,
    method: 'put',
    data
  })
}

// 提交体检结论
export function submitConclusion(data) {
  return request({
    url: `/api/exam/report/${data.id}/conclusion`,
    method: 'post',
    data
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