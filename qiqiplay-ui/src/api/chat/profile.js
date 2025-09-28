import request from '@/utils/request'

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/chat/profile/info',
    method: 'get'
  })
}

// 上传用户头像
export function uploadAvatar(formData) {
  return request({
    url: '/chat/profile/avatar/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}