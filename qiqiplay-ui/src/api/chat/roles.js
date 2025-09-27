import request from '@/utils/request'

// 获取AI角色列表 - 前台chat页面专用
export function getChatRoleList(query) {
  return request({
    url: '/ai/ai/list',
    method: 'get',
    params: query,
    headers: {
      'isToken': true // 确保使用token验证
    }
  })
}

// 获取AI角色详情
export function getChatRoleDetail(roleId) {
  return request({
    url: '/ai/ai/' + roleId,
    method: 'get',
    headers: {
      'isToken': true
    }
  })
}