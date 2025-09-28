import request from '@/utils/request'

// 生成图片
export function generateImage(data) {
  return request({
    url: '/chat/image/generate',
    method: 'post',
    data: data
  })
}

// 获取图片生成历史
export function getImageHistory() {
  return request({
    url: '/chat/image/history',
    method: 'post'
  })
}

// 检查图片生成权限
export function checkImagePermission() {
  return request({
    url: '/chat/image/permission',
    method: 'get'
  })
}