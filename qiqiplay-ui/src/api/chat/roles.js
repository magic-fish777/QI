import request from '@/utils/request'

// 获取AI角色列表 - 前台chat页面专用
export function getChatRoleList(query) {
  return request({
    url: '/chat/ai/list',
    method: 'get',
    params: query
  })
}

// 获取AI角色详情
export function getChatRoleDetail(roleId) {
  return request({
    url: '/chat/ai/' + roleId,
    method: 'get'
  })
}
