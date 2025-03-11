import request from '@/utils/request'

// 获取部门列表
export function getDepartmentList(params) {
  return request({
    url: '/api/system/department/list',
    method: 'get',
    params
  })
}

// 创建部门
export function createDepartment(data) {
  return request({
    url: '/api/system/department',
    method: 'post',
    data
  })
}

// 更新部门
export function updateDepartment(data) {
  return request({
    url: `/api/system/department/${data.id}`,
    method: 'put',
    data
  })
}

// 获取部门人员列表
export function getDepartmentUsers(id) {
  return request({
    url: `/api/system/department/${id}/users`,
    method: 'get'
  })
}

// 获取所有部门（用于下拉选择）
export function getAllDepartments() {
  return request({
    url: '/api/system/department/all',
    method: 'get'
  })
} 