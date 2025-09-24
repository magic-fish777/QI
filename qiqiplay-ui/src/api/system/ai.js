import request from '@/utils/request'

// 查询AI角色信息列表
export function listAi(query) {
  return request({
    url: '/system/ai/list',
    method: 'get',
    params: query
  })
}

// 查询AI角色信息详细
export function getAi(roleId) {
  return request({
    url: '/system/ai/' + roleId,
    method: 'get'
  })
}

// 新增AI角色信息
export function addAi(data) {
  return request({
    url: '/system/ai',
    method: 'post',
    data: data
  })
}

// 修改AI角色信息
export function updateAi(data) {
  return request({
    url: '/system/ai',
    method: 'put',
    data: data
  })
}

// 删除AI角色信息
export function delAi(roleId) {
  return request({
    url: '/system/ai/' + roleId,
    method: 'delete'
  })
}
