import request from '@/utils/request'

// 获取体检项目列表
export function getExamItemList(params) {
  return request({
    url: '/api/items/list',
    method: 'get',
    params
  })
}

// 创建体检项目
export function createExamItem(data) {
  return request({
    url: '/api/exam/item',
    method: 'post',
    data
  })
}

// 更新体检项目
export function updateExamItem(data) {
  return request({
    url: `/api/exam/item/${data.id}`,
    method: 'put',
    data
  })
}

// 删除体检项目
export function deleteExamItem(id) {
  return request({
    url: `/api/exam/item/${id}`,
    method: 'delete'
  })
}

// 获取所有体检项目（用于下拉选择）
export function getAllExamItems() {
  return request({
    url: '/api/exam/item/all',
    method: 'get'
  })
} 