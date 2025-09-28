import request from '@/utils/request'

// 获取当前用户的对话记录列表 - 前台专用
export function getUserChatRecords(query) {
  return request({
    url: '/chat/records/list',
    method: 'get',
    params: query
  })
}

// 获取当前用户与特定角色的对话记录
export function getRoleChatRecords(roleId, query) {
  return request({
    url: `/chat/records/role/${roleId}`,
    method: 'get',
    params: query
  })
}

// 获取对话记录详情
export function getChatRecordDetail(recordId) {
  return request({
    url: `/chat/records/${recordId}`,
    method: 'get'
  })
}

// 删除当前用户的对话记录
export function deleteChatRecord(recordId) {
  return request({
    url: `/chat/records/${recordId}`,
    method: 'delete'
  })
}

// 获取用户的对话统计信息
export function getChatStatistics() {
  return request({
    url: '/chat/records/statistics',
    method: 'get'
  })
}

// 搜索对话记录
export function searchChatRecords(query) {
  return request({
    url: '/chat/records/search',
    method: 'get',
    params: query
  })
}