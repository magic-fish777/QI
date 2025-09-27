import request from '@/utils/request'

// AI前端用户登录
export function aiLogin(data) {
  return request({
    url: '/ai/auth/login',
    headers: {
      isToken: false,
      repeatSubmit: false
    },
    method: 'post',
    data: data
  })
}

// AI前端用户注册
export function aiRegister(data) {
  return request({
    url: '/ai/auth/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 发送邮箱验证码
export function sendEmailCode(email) {
  return request({
    url: '/ai/auth/sendCode',
    headers: {
      isToken: false
    },
    method: 'post',
    data: {
      email: email
    }
  })
}

// 获取AI前端用户信息
export function getAiUserInfo() {
  return request({
    url: '/ai/auth/getInfo',
    method: 'get'
  })
}

// AI前端用户退出登录
export function aiLogout() {
  return request({
    url: '/ai/auth/logout',
    method: 'post'
  })
}