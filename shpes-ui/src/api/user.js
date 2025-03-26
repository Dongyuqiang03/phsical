import request from '@/utils/request'

// 用户管理相关
export function getUserList(params) {
  return request({
    url: '/api/system/user/list',
    method: 'get',
    params
  })
}

export function createUser(data) {
  return request({
    url: '/api/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: `/api/system/user/${data.id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/api/system/user/${id}`,
    method: 'delete'
  })
}

export function batchDeleteUser(ids) {
  return request({
    url: '/api/system/user/batch',
    method: 'delete',
    data: { ids }
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/api/system/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function resetUserPassword(id) {
  return request({
    url: `/api/system/user/${id}/password/reset`,
    method: 'post'
  })
}

export function updateProfile(data) {
  return request({
    url: '/api/system/user/current',
    method: 'put',
    data
  })
}

export function updatePassword(data) {
  return request({
    url: `/api/system/user/${data.id}/password`,
    method: 'post',
    data: { newPassword: data.newPassword }
  })
}

export function exportUser(params) {
  return request({
    url: '/api/system/user/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function downloadTemplate() {
  return request({
    url: '/api/system/user/template',
    method: 'get',
    responseType: 'blob'
  })
} 