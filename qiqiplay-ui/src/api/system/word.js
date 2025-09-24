import request from '@/utils/request'

// 查询敏感词库列表
export function listWord(query) {
  return request({
    url: '/system/word/list',
    method: 'get',
    params: query
  })
}

// 查询敏感词库详细
export function getWord(wordId) {
  return request({
    url: '/system/word/' + wordId,
    method: 'get'
  })
}

// 新增敏感词库
export function addWord(data) {
  return request({
    url: '/system/word',
    method: 'post',
    data: data
  })
}

// 修改敏感词库
export function updateWord(data) {
  return request({
    url: '/system/word',
    method: 'put',
    data: data
  })
}

// 删除敏感词库
export function delWord(wordId) {
  return request({
    url: '/system/word/' + wordId,
    method: 'delete'
  })
}
