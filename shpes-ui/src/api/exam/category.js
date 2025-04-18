import request from '@/utils/request'

export function getCategoryPage(params) {
  return request({
    url: '/api/exam/category/list',
    method: 'get',
    params
  })
}

export function createCategory(data) {
  return request({
    url: '/api/exam/category',
    method: 'post',
    data
  })
}

export function updateCategory(data) {
  return request({
    url: '/api/exam/category',
    method: 'put',
    data
  })
}

export function deleteCategory(id) {
  return request({
    url: `/api/exam/category/${id}`,
    method: 'delete'
  })
}