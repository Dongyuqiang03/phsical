import request from '@/utils/request'

// 获取体检套餐列表
export function getExamPackageList(params) {
  return request({
    url: '/api/exam/package/list',
    method: 'get',
    params
  })
}

// 创建体检套餐
export function createExamPackage(data) {
  return request({
    url: '/api/exam/package',
    method: 'post',
    data
  })
}

// 更新体检套餐
export function updateExamPackage(data) {
  return request({
    url: `/api/exam/package/${data.id}`,
    method: 'put',
    data
  })
}

// 删除体检套餐
export function deleteExamPackage(id) {
  return request({
    url: `/api/exam/package/${id}`,
    method: 'delete'
  })
}

// 获取体检套餐项目列表
export function getExamPackageItems(id) {
  return request({
    url: `/api/exam/package/${id}/items`,
    method: 'get'
  })
}

// 更新体检套餐项目
export function updateExamPackageItems(id, data) {
  return request({
    url: `/api/exam/package/${id}/items`,
    method: 'put',
    data
  })
}

// 获取所有体检套餐（用于下拉选择）
export function getAllExamPackages() {
  return request({
    url: '/api/exam/package/all',
    method: 'get'
  })
} 