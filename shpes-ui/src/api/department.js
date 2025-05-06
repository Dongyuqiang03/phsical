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
    url: '/api/system/department/create',
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
export function getAllDepartments(userType) {
  return request({
    url: '/system/department/all',
    method: 'get',
    params: { userType }
  })
}

// 删除部门
export function deleteDepartment(id) {
  return request({
    url: `/api/system/department/${id}`,
    method: 'delete'
  })
}

// 批量更新部门状态
export function batchUpdateStatus(ids, status) {
  return request({
    url: '/api/system/department/batch-status',
    method: 'put',
    params: {
      ids,
      status
    }
  })
}