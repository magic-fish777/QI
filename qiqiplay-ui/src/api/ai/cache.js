import request from '@/utils/request'

// 查询语音缓存列表
export function listCache(query) {
  return request({
    url: '/ai/cache/list',
    method: 'get',
    params: query
  })
}

// 查询语音缓存详细
export function getCache(voiceId) {
  return request({
    url: '/ai/cache/' + voiceId,
    method: 'get'
  })
}

// 新增语音缓存
export function addCache(data) {
  return request({
    url: '/ai/cache',
    method: 'post',
    data: data
  })
}

// 修改语音缓存
export function updateCache(data) {
  return request({
    url: '/ai/cache',
    method: 'put',
    data: data
  })
}

// 删除语音缓存
export function delCache(voiceId) {
  return request({
    url: '/ai/cache/' + voiceId,
    method: 'delete'
  })
}
