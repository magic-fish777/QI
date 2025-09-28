<template>
  <div class="vip-status-indicator" :class="{ compact: compact, 'show-details': showDetails }">
    <!-- 主要状态显示 -->
    <div class="status-main" @click="toggleDetails">
      <div class="vip-badge">
        <el-tag
          :type="levelBadgeType"
          :color="getVipLevelColor()"
          effect="dark"
          size="small"
        >
          <i class="el-icon-user-solid"></i>
          {{ vipStatus.vipLevelName }}
        </el-tag>
      </div>

      <!-- 过期状态提示 -->
      <div v-if="vipStatus.hasVip && expireStatus.type !== 'success'" class="expire-warning">
        <el-tag :type="expireStatus.type" size="mini">
          {{ expireStatus.message }}
        </el-tag>
      </div>

      <!-- 展开按钮 -->
      <div class="toggle-btn" v-if="!compact">
        <i :class="showDetails ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
      </div>
    </div>

    <!-- 详细信息展开区域 -->
    <transition name="slide-down">
      <div v-show="showDetails && !compact" class="status-details">
        <!-- VIP信息 -->
        <div class="vip-info" v-if="vipStatus.hasVip">
          <div class="info-item">
            <span class="label">等级:</span>
            <span class="value">{{ vipStatus.vipLevelName }}</span>
          </div>
          <div class="info-item" v-if="vipStatus.expireTime">
            <span class="label">到期:</span>
            <span class="value">{{ formatDate(vipStatus.expireTime) }}</span>
          </div>
          <div class="info-item" v-if="vipStatus.daysLeft !== undefined">
            <span class="label">剩余:</span>
            <span class="value">{{ vipStatus.daysLeft }}天</span>
          </div>
        </div>

        <!-- 使用情况 -->
        <div class="usage-info">
          <div class="usage-title">今日使用情况</div>
          <div class="usage-list">
            <div class="usage-item">
              <span class="usage-label">聊天:</span>
              <span class="usage-value" :class="{ 'limited': isFeatureLimited('chat') }">
                {{ formatRemainingUsage(remainingUsage.chatRemaining, remainingUsage.chatLimit) }}
              </span>
            </div>
            <div class="usage-item" v-if="canUseFeature('voice')">
              <span class="usage-label">语音:</span>
              <span class="usage-value" :class="{ 'limited': isFeatureLimited('voice') }">
                {{ formatRemainingUsage(remainingUsage.voiceRemaining, remainingUsage.voiceLimit) }}
              </span>
            </div>
            <div class="usage-item" v-if="canUseFeature('image')">
              <span class="usage-label">图片:</span>
              <span class="usage-value" :class="{ 'limited': isFeatureLimited('image') }">
                {{ formatRemainingUsage(remainingUsage.imageRemaining, remainingUsage.imageLimit) }}
              </span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button size="small" @click="handleRefresh" :loading="vipStatus.loading">
            <i class="el-icon-refresh"></i>
            刷新状态
          </el-button>
          <el-button v-if="!isVipUser" size="small" type="primary" @click="handleUpgrade">
            <i class="el-icon-star-on"></i>
            升级会员
          </el-button>
          <el-button v-else-if="expireStatus.type !== 'success'" size="small" type="warning" @click="handleRenew">
            <i class="el-icon-time"></i>
            续费会员
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 权限不足提示弹窗 -->
    <el-dialog
      title="权限提示"
      :visible.sync="permissionDialogVisible"
      width="400px"
      center
    >
      <div class="permission-dialog-content">
        <div class="permission-icon">
          <i class="el-icon-warning" style="color: #E6A23C; font-size: 48px;"></i>
        </div>
        <div class="permission-message">
          {{ permissionMessage }}
        </div>
        <div class="permission-suggestion">
          升级会员即可解锁更多功能和更高的使用限额
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpgradeFromDialog">立即升级</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { useVipStatus } from '@/composables/useVipStatus'

export default {
  name: 'VipStatusIndicator',
  props: {
    compact: {
      type: Boolean,
      default: false
    },
    autoExpand: {
      type: Boolean,
      default: false
    }
  },
  emits: ['upgrade', 'renew', 'permission-denied'],
  setup(props, { emit }) {
    const vipStatus = useVipStatus()

    return {
      ...vipStatus
    }
  },
  data() {
    return {
      showDetails: false,
      permissionDialogVisible: false,
      permissionMessage: ''
    }
  },
  computed: {
    levelBadgeType() {
      if (!this.vipStatus.hasVip || !this.vipStatus.isValid) {
        return 'info'
      }

      const types = {
        1: 'warning',  // 白银
        2: 'primary',  // 黄金
        3: 'success',  // 铂金
        4: 'danger'    // 钻石
      }
      return types[this.vipStatus.vipLevel] || 'info'
    }
  },
  mounted() {
    if (this.autoExpand) {
      this.showDetails = true
    }
  },
  methods: {
    toggleDetails() {
      if (!this.compact) {
        this.showDetails = !this.showDetails
      }
    },

    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    },

    handleRefresh() {
      this.forceRefresh()
    },

    handleUpgrade() {
      emit('upgrade')
    },

    handleRenew() {
      emit('renew')
    },

    handleUpgradeFromDialog() {
      this.permissionDialogVisible = false
      emit('upgrade')
    },

    // 显示权限不足提示
    showPermissionDialog(feature) {
      this.permissionMessage = this.getPermissionHint(feature)
      this.permissionDialogVisible = true
      emit('permission-denied', { feature, message: this.permissionMessage })
    },

    // 检查功能权限（供外部调用）
    async checkAndUseFeature(feature) {
      const hasPermission = this.canUseFeature(feature)
      const remaining = this.getFeatureRemaining(feature)

      if (!hasPermission) {
        this.showPermissionDialog(feature)
        return false
      }

      if (this.isFeatureLimited(feature)) {
        this.permissionMessage = `今日${feature}使用次数已达上限`
        this.permissionDialogVisible = true
        return false
      }

      return true
    }
  }
}
</script>

<style lang="scss" scoped>
.vip-status-indicator {
  background: white;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  overflow: hidden;
  transition: all 0.3s ease;

  &.compact {
    .status-main {
      padding: 8px 12px;
    }
  }

  &.show-details {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }

  .status-main {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    cursor: pointer;
    transition: background-color 0.2s;

    &:hover {
      background-color: #f8f9fa;
    }

    .vip-badge {
      flex: 1;

      .el-tag {
        .el-icon-user-solid {
          margin-right: 4px;
        }
      }
    }

    .expire-warning {
      margin-left: 8px;
    }

    .toggle-btn {
      margin-left: 12px;
      color: #909399;
      font-size: 14px;
      transition: transform 0.3s ease;
    }
  }

  .status-details {
    border-top: 1px solid #f0f0f0;
    padding: 16px;
    background-color: #fafafa;

    .vip-info {
      margin-bottom: 16px;

      .info-item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        font-size: 13px;

        .label {
          color: #666;
        }

        .value {
          color: #333;
          font-weight: 500;
        }
      }
    }

    .usage-info {
      margin-bottom: 16px;

      .usage-title {
        font-size: 13px;
        color: #666;
        margin-bottom: 8px;
        font-weight: 500;
      }

      .usage-list {
        .usage-item {
          display: flex;
          justify-content: space-between;
          margin-bottom: 6px;
          font-size: 12px;

          .usage-label {
            color: #666;
          }

          .usage-value {
            color: #333;
            font-weight: 500;

            &.limited {
              color: #F56C6C;
            }
          }
        }
      }
    }

    .action-buttons {
      display: flex;
      gap: 8px;

      .el-button {
        flex: 1;
      }
    }
  }
}

// 动画
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// 权限对话框样式
.permission-dialog-content {
  text-align: center;
  padding: 20px 0;

  .permission-icon {
    margin-bottom: 16px;
  }

  .permission-message {
    font-size: 16px;
    color: #333;
    margin-bottom: 12px;
    line-height: 1.5;
  }

  .permission-suggestion {
    font-size: 14px;
    color: #666;
    line-height: 1.4;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .vip-status-indicator {
    .status-main {
      padding: 10px 12px;
    }

    .status-details {
      padding: 12px;

      .action-buttons {
        flex-direction: column;

        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>