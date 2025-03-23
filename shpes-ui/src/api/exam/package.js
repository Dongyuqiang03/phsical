import request from '@/utils/request'

// 获取体检套餐列表
export function getExamPackageList(params) {
  return request({
    url: '/api/exam/packages/list',
    method: 'get',
    params
  })
}

// 创建体检套餐
export function createExamPackage(data) {
  return request({
    url: '/api/exam/packages',
    method: 'post',
    data
  })
}

// 更新体检套餐
export function updateExamPackage(data) {
  return request({
    url: `/api/exam/packages/${data.id}`,
    method: 'put',
    data
  })
}

// 删除体检套餐
export function deleteExamPackage(id) {
  return request({
    url: `/api/exam/packages/${id}`,
    method: 'delete'
  })
}

// 获取体检套餐项目列表
export function getExamPackageItems(id) {
  return request({
    url: `/api/exam/packages/${id}/items`,
    method: 'get'
  })
}

// 更新体检套餐项目
export function updateExamPackageItems(id, data) {
  return request({
    url: `/api/exam/packages/${id}/items`,
    method: 'put',
    data
  })
}

// 获取所有体检套餐（用于下拉选择）
export function getAllExamPackages(params) {
  return request({
    url: '/api/exam/packages/available',
    method: 'get',
    params
  })
}

// 更新体检套餐状态
export function updateExamPackageStatus(id, status) {
  return request({
    url: `/api/exam/packages/${id}/status`,
    method: 'put',
    params: { status }
  })
} 