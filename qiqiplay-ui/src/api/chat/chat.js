import request from '@/utils/request'

// 文本聊天接口
export function textChat(data) {
  return request({
    url: '/chat/text',
    method: 'post',
    data: data,
    headers: {
      'isToken': true
    }
  })
}

// 语音聊天接口
export function audioChat(data) {
  // 如果是FormData，不要设置Content-Type，让浏览器自动设置
  const headers = {
    'isToken': true
  }

  // 如果不是FormData，设置为JSON
  if (!(data instanceof FormData)) {
    headers['Content-Type'] = 'application/json'
  }

  return request({
    url: '/chat/audio',
    method: 'post',
    data: data,
    headers: headers
  })
}