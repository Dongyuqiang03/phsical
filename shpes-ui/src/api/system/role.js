import request from '@/utils/request'

// 获取所有角色列表
export function getAllRoles() {
  return request({
    url: '/api/system/role/all',
    method: 'get'
  })
}

// 获取角色列表
export function getRoleList(params) {
  return request({
    url: '/api/system/role/list',
    method: 'get',
    params
  })
}

// 获取角色详情
export function getRoleDetail(id) {
  return request({
    url: `/api/system/role/${id}`,
    method: 'get'
  })
}

// 创建角色
export function createRole(data) {
  return request({
    url: '/api/system/role',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(id, data) {
  return request({
    url: `/api/system/role/${id}`,
    method: 'put',
    data
  })
}

// 删除角色
export function deleteRole(id) {
  return request({
    url: `/api/system/role/${id}`,
    method: 'delete'
  })
}

// 分配权限
export function assignPermissions(roleId, permissionIds) {
  return request({
    url: `/api/system/role/${roleId}/permissions`,
    method: 'post',
    data: { permissionIds }
  })
} 