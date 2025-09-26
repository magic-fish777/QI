import request from '@/utils/request'

// 查询前台用户信息（精简版）列表
export function listUser(query) {
  return request({
    url: '/ai/user/list',
    method: 'get',
    params: query
  })
}

// 查询前台用户信息（精简版）详细
export function getUser(userId) {
  return request({
    url: '/ai/user/' + userId,
    method: 'get'
  })
}

// 新增前台用户信息（精简版）
export function addUser(data) {
  return request({
    url: '/ai/user',
    method: 'post',
    data: data
  })
}

// 修改前台用户信息（精简版）
export function updateUser(data) {
  return request({
    url: '/ai/user',
    method: 'put',
    data: data
  })
}

// 删除前台用户信息（精简版）
export function delUser(userId) {
  return request({
    url: '/ai/user/' + userId,
    method: 'delete'
  })
}
