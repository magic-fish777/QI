import request from '@/utils/request'

// 查询会员订阅列表
export function listVip(query) {
  return request({
    url: '/system/vip/list',
    method: 'get',
    params: query
  })
}

// 查询会员订阅详细
export function getVip(vipId) {
  return request({
    url: '/system/vip/' + vipId,
    method: 'get'
  })
}

// 新增会员订阅
export function addVip(data) {
  return request({
    url: '/system/vip',
    method: 'post',
    data: data
  })
}

// 修改会员订阅
export function updateVip(data) {
  return request({
    url: '/system/vip',
    method: 'put',
    data: data
  })
}

// 删除会员订阅
export function delVip(vipId) {
  return request({
    url: '/system/vip/' + vipId,
    method: 'delete'
  })
}
