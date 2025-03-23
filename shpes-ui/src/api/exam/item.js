import request from '@/utils/request'

// 获取体检项目列表
export function getExamItemList(params) {
  return request({
    url: '/api/exam/items/list',
    method: 'get',
    params
  })
}

// 创建体检项目
export function createExamItem(data) {
  return request({
    url: '/api/exam/items',
    method: 'post',
    data
  })
}

// 更新体检项目
export function updateExamItem(data) {
  return request({
    url: `/api/exam/items/${data.id}`,
    method: 'put',
    data
  })
}

// 删除体检项目
export function deleteExamItem(id) {
  return request({
    url: `/api/exam/items/${id}`,
    method: 'delete'
  })
}

// 更新体检项目状态
export function updateExamItemStatus(id, status) {
  return request({
    url: `/api/exam/items/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 获取所有体检项目（用于下拉选择）
export function getAllExamItems() {
  return request({
    url: '/api/exam/items/available',
    method: 'get'
  })
}
