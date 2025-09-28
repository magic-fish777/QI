import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { getVipRealtimeStatus, validateFeaturePermission, getTodayRemainingUsage } from '@/api/chat/vip'

/**
 * VIP状态管理组合式函数
 * 提供实时VIP状态、权限检查、使用次数等功能
 */
export function useVipStatus() {
  // 响应式状态
  const vipStatus = reactive({
    hasVip: false,
    isValid: false,
    vipLevel: 0,
    vipLevelName: '普通用户',
    expireTime: null,
    daysLeft: 0,
    privileges: {},
    loading: false,
    error: null,
    updateTime: null
  })

  const remainingUsage = reactive({
    chatRemaining: 0,
    voiceRemaining: 0,
    imageRemaining: 0,
    chatLimit: 0,
    voiceLimit: 0,
    imageLimit: 0,
    loading: false
  })

  const refreshTimer = ref(null)
  const usageTimer = ref(null)

  // 计算属性
  const isVipUser = computed(() => vipStatus.hasVip && vipStatus.isValid)
  const levelBadgeClass = computed(() => {
    const levelClasses = {
      0: 'el-tag--info',
      1: 'el-tag--warning', // 白银
      2: 'el-tag--primary', // 黄金
      3: 'el-tag--success', // 铂金
      4: 'el-tag--danger'   // 钻石
    }
    return levelClasses[vipStatus.vipLevel] || 'el-tag--info'
  })

  const expireStatus = computed(() => {
    if (!vipStatus.hasVip || !vipStatus.isValid) {
      return { type: 'normal', message: '' }
    }

    const daysLeft = vipStatus.daysLeft
    if (daysLeft <= 1) {
      return { type: 'danger', message: '即将过期' }
    } else if (daysLeft <= 7) {
      return { type: 'warning', message: `${daysLeft}天后过期` }
    } else {
      return { type: 'success', message: `${daysLeft}天后过期` }
    }
  })

  // 刷新VIP状态
  const refreshVipStatus = async () => {
    if (vipStatus.loading) return

    vipStatus.loading = true
    vipStatus.error = null

    try {
      const response = await getVipRealtimeStatus()
      if (response.code === 200) {
        Object.assign(vipStatus, {
          ...response.data,
          loading: false,
          updateTime: new Date()
        })
      } else {
        vipStatus.error = response.msg || '获取VIP状态失败'
        vipStatus.loading = false
      }
    } catch (error) {
      console.error('刷新VIP状态失败:', error)
      vipStatus.error = '网络错误'
      vipStatus.loading = false
    }
  }

  // 刷新使用次数
  const refreshRemainingUsage = async () => {
    if (remainingUsage.loading) return

    remainingUsage.loading = true
    try {
      const response = await getTodayRemainingUsage()
      if (response.code === 200) {
        Object.assign(remainingUsage, {
          ...response.data,
          loading: false
        })
      } else {
        console.error('获取剩余使用次数失败:', response.msg)
        remainingUsage.loading = false
      }
    } catch (error) {
      console.error('刷新使用次数失败:', error)
      remainingUsage.loading = false
    }
  }

  // 验证功能权限
  const checkFeaturePermission = async (feature) => {
    try {
      const response = await validateFeaturePermission(feature)
      if (response.code === 200) {
        return response.data
      } else {
        return {
          hasPermission: false,
          message: response.msg || '权限验证失败'
        }
      }
    } catch (error) {
      console.error('检查功能权限失败:', error)
      return {
        hasPermission: false,
        message: '网络错误'
      }
    }
  }

  // 检查是否可以使用功能（快速检查）
  const canUseFeature = (feature) => {
    if (!vipStatus.privileges) return false

    switch (feature.toLowerCase()) {
      case 'chat':
        return true // 所有用户都可以聊天
      case 'voice':
        return vipStatus.privileges.voiceEnabled || false
      case 'image':
        return vipStatus.privileges.imageEnabled || false
      default:
        return false
    }
  }

  // 获取功能剩余次数
  const getFeatureRemaining = (feature) => {
    switch (feature.toLowerCase()) {
      case 'chat':
        return remainingUsage.chatRemaining
      case 'voice':
        return remainingUsage.voiceRemaining
      case 'image':
        return remainingUsage.imageRemaining
      default:
        return 0
    }
  }

  // 检查功能是否达到限制
  const isFeatureLimited = (feature) => {
    const remaining = getFeatureRemaining(feature)
    return remaining === 0 && remaining !== -1 // -1表示无限制
  }

  // 获取VIP等级颜色
  const getVipLevelColor = (level = vipStatus.vipLevel) => {
    const colors = {
      0: '#909399', // 普通用户 - 灰色
      1: '#C0C4CC', // 白银 - 银色
      2: '#F56C6C', // 黄金 - 金色
      3: '#67C23A', // 铂金 - 绿色
      4: '#E6A23C'  // 钻石 - 橙色
    }
    return colors[level] || colors[0]
  }

  // 格式化剩余使用次数显示
  const formatRemainingUsage = (remaining, limit) => {
    if (remaining === -1) return '无限制'
    if (limit === -1) return '无限制'
    return `${remaining}/${limit}`
  }

  // 获取权限不足的提示信息
  const getPermissionHint = (feature) => {
    if (canUseFeature(feature)) return null

    const featureNames = {
      voice: '语音功能',
      image: '图片生成',
      chat: '聊天功能'
    }

    const requiredLevels = {
      voice: { level: 1, name: '白银会员' },
      image: { level: 2, name: '黄金会员' }
    }

    const featureName = featureNames[feature] || feature
    const required = requiredLevels[feature]

    if (required) {
      return `${featureName}需要${required.name}或更高等级才能使用`
    }

    return `当前VIP等级无法使用${featureName}`
  }

  // 启动自动刷新
  const startAutoRefresh = (statusInterval = 5 * 60 * 1000, usageInterval = 30 * 1000) => {
    // 立即执行一次
    refreshVipStatus()
    refreshRemainingUsage()

    // 设置定时刷新
    refreshTimer.value = setInterval(refreshVipStatus, statusInterval) // 5分钟刷新VIP状态
    usageTimer.value = setInterval(refreshRemainingUsage, usageInterval) // 30秒刷新使用次数
  }

  // 停止自动刷新
  const stopAutoRefresh = () => {
    if (refreshTimer.value) {
      clearInterval(refreshTimer.value)
      refreshTimer.value = null
    }
    if (usageTimer.value) {
      clearInterval(usageTimer.value)
      usageTimer.value = null
    }
  }

  // 手动触发全量刷新
  const forceRefresh = async () => {
    await Promise.all([
      refreshVipStatus(),
      refreshRemainingUsage()
    ])
  }

  // 生命周期管理
  onMounted(() => {
    startAutoRefresh()
  })

  onUnmounted(() => {
    stopAutoRefresh()
  })

  // 返回所有状态和方法
  return {
    // 状态
    vipStatus,
    remainingUsage,

    // 计算属性
    isVipUser,
    levelBadgeClass,
    expireStatus,

    // 方法
    refreshVipStatus,
    refreshRemainingUsage,
    checkFeaturePermission,
    canUseFeature,
    getFeatureRemaining,
    isFeatureLimited,
    getVipLevelColor,
    formatRemainingUsage,
    getPermissionHint,
    startAutoRefresh,
    stopAutoRefresh,
    forceRefresh
  }
}