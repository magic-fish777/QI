import request from '@/utils/request'

// 获取当前用户会员信息
export function getUserVipInfo() {
  return request({
    url: '/chat/vip/info',
    method: 'get'
  })
}

// 获取会员套餐列表
export function getVipPackages() {
  return request({
    url: '/chat/vip/packages',
    method: 'get'
  })
}

// 购买会员（待实现）
export function purchaseVip(data) {
  return request({
    url: '/chat/vip/purchase',
    method: 'post',
    data: data
  })
}

// 获取会员等级权益信息
export function getVipPrivileges(level) {
  return request({
    url: '/chat/vip/privileges/' + level,
    method: 'get'
  })
}

// 创建VIP购买订单
export function createVipPurchaseOrder(data) {
  return request({
    url: '/chat/vip/purchase',
    method: 'post',
    data: data
  })
}

// 模拟支付成功（开发测试用）
export function mockPaymentSuccess(data) {
  return request({
    url: '/chat/vip/mock-payment-success',
    method: 'post',
    data: data
  })
}

// 查询订单状态
export function getOrderStatus(orderNo) {
  return request({
    url: '/chat/vip/order-status',
    method: 'get',
    params: { orderNo }
  })
}

// 获取实时VIP状态
export function getVipRealtimeStatus() {
  return request({
    url: '/chat/vip/realtime-status',
    method: 'get'
  })
}

// 验证功能权限
export function validateFeaturePermission(feature) {
  return request({
    url: '/chat/vip/validate-permission',
    method: 'get',
    params: { feature }
  })
}

// 获取今日剩余使用次数
export function getTodayRemainingUsage() {
  return request({
    url: '/chat/vip/remaining-usage',
    method: 'get'
  })
}

// 检查是否可以使用某个功能
export function canUseFeature(feature) {
  return request({
    url: '/chat/vip/can-use-feature',
    method: 'get',
    params: { feature }
  })
}