import request from '@/utils/request'

// 角色列表
export function getRoleList(params) {
  return request({
    url: '/api/system/role/list',
    method: 'get',
    params
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(data) {
  return request({
    url: `/system/role/${data.id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete'
  })
}

// 批量删除角色
export function batchDeleteRole(ids) {
  return request({
    url: '/system/role/batch',
    method: 'delete',
    data: { ids }
  })
}

// 更新角色状态
export function updateRoleStatus(id, status) {
  return request({
    url: `/system/role/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 获取角色权限
export function getRolePermissions(id) {
  return request({
    url: `/system/role/${id}/permissions`,
    method: 'get'
  })
}

// 更新角色权限
export function updateRolePermissions(id, data) {
  return request({
    url: `/system/role/${id}/permissions`,
    method: 'put',
    data
  })
}

// 获取角色用户列表
export function getRoleUsers(id) {
  return request({
    url: `/system/role/${id}/users`,
    method: 'get'
  })
}

// 更新角色用户
export function updateRoleUsers(id, data) {
  return request({
    url: `/system/role/${id}/users`,
    method: 'put',
    data
  })
}

// 获取所有角色（用于下拉选择）
export function getAllRoles() {
  return request({
    url: '/system/role/all',
    method: 'get'
  })
} 