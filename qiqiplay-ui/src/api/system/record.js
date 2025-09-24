import request from '@/utils/request'

// 查询聊天记录列表
export function listRecord(query) {
  return request({
    url: '/system/record/list',
    method: 'get',
    params: query
  })
}

// 查询聊天记录详细
export function getRecord(recordId) {
  return request({
    url: '/system/record/' + recordId,
    method: 'get'
  })
}

// 新增聊天记录
export function addRecord(data) {
  return request({
    url: '/system/record',
    method: 'post',
    data: data
  })
}

// 修改聊天记录
export function updateRecord(data) {
  return request({
    url: '/system/record',
    method: 'put',
    data: data
  })
}

// 删除聊天记录
export function delRecord(recordId) {
  return request({
    url: '/system/record/' + recordId,
    method: 'delete'
  })
}
