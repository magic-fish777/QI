<template>
  <div class="membership-container">
    <!-- 移动端会员中心 -->
    <div v-if="isMobile" class="mobile-membership">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-medal"></i>
          <span>会员中心</span>
        </div>
      </div>

      <div class="membership-content">
        <!-- 当前会员状态卡片 -->
        <div class="current-vip-card">
          <div class="vip-header">
            <div class="vip-level">
              <span class="level-badge" :class="'level-' + userVipInfo.vipLevel">
                {{ getLevelName(userVipInfo.vipLevel) }}
              </span>
              <span class="level-name">{{ getLevelDisplayName(userVipInfo.vipLevel) }}</span>
            </div>
            <div v-if="userVipInfo.isVip" class="remaining-days">
              还有 {{ userVipInfo.remainingDays }} 天到期
            </div>
          </div>

          <div class="vip-privileges">
            <div class="privilege-item">
              <i class="el-icon-chat-line-round"></i>
              <span>每日对话：{{ formatLimit(userVipInfo.privileges.dailyChatLimit) }}</span>
            </div>
            <div class="privilege-item">
              <i class="el-icon-picture"></i>
              <span>图片生成：{{ userVipInfo.privileges.imageEnabled ? formatLimit(userVipInfo.privileges.dailyImageLimit) : '未开通' }}</span>
            </div>
            <div class="privilege-item">
              <i class="el-icon-star-on"></i>
              <span>定制角色：{{ formatLimit(userVipInfo.privileges.customRoleQuota) }}</span>
            </div>
          </div>

          <div v-if="!userVipInfo.isVip || userVipInfo.remainingDays <= 7" class="upgrade-section">
            <el-button type="primary" @click="showUpgradeDialog" round>
              {{ userVipInfo.isVip ? '续费会员' : '开通会员' }}
            </el-button>
          </div>
        </div>

        <!-- 会员套餐列表 -->
        <div class="package-section">
          <div class="section-title">选择套餐</div>
          <div class="package-list">
            <div
              v-for="(pkg, key) in vipPackages"
              :key="key"
              class="package-card"
              :class="{ 'recommended': key === 'gold' }"
              @click="selectPackage(pkg)"
            >
              <div v-if="key === 'gold'" class="recommended-label">推荐</div>
              <div class="package-header">
                <div class="package-name">{{ pkg.levelName }}</div>
                <div class="package-level">Lv.{{ pkg.level }}</div>
              </div>
              <div class="package-prices">
                <div class="price-item">
                  <span class="price">¥{{ (pkg.monthlyPrice / 100).toFixed(2) }}</span>
                  <span class="period">/月</span>
                </div>
                <div class="price-item">
                  <span class="price">¥{{ (pkg.quarterlyPrice / 100).toFixed(2) }}</span>
                  <span class="period">/季</span>
                </div>
                <div class="price-item">
                  <span class="price">¥{{ (pkg.yearlyPrice / 100).toFixed(2) }}</span>
                  <span class="period">/年</span>
                </div>
              </div>
              <div class="package-features">
                <div class="feature">💬 每日对话 {{ formatLimit(pkg.privileges.dailyChatLimit) }}</div>
                <div class="feature">🎨 图片生成 {{ pkg.privileges.imageEnabled ? formatLimit(pkg.privileges.dailyImageLimit) : '❌' }}</div>
                <div class="feature">🗣️ 语音功能 {{ pkg.privileges.voiceEnabled ? '✅' : '❌' }}</div>
                <div class="feature">⭐ 定制角色 {{ formatLimit(pkg.privileges.customRoleQuota) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端会员中心 -->
    <div v-else class="desktop-membership">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-medal"></i>
          <span>会员中心</span>
        </div>
      </div>

      <div class="page-content">
        <!-- 当前会员状态 -->
        <div class="current-status-section">
          <div class="status-card">
            <div class="status-header">
              <div class="vip-info">
                <span class="level-badge" :class="'level-' + userVipInfo.vipLevel">
                  {{ getLevelName(userVipInfo.vipLevel) }}
                </span>
                <span class="level-name">{{ getLevelDisplayName(userVipInfo.vipLevel) }}</span>
              </div>
              <div v-if="userVipInfo.isVip" class="expire-info">
                <div class="expire-date">到期时间：{{ formatDate(userVipInfo.expireTime) }}</div>
                <div class="remaining-days">剩余 {{ userVipInfo.remainingDays }} 天</div>
              </div>
            </div>

            <div class="privilege-grid">
              <div class="privilege-card">
                <div class="privilege-icon">
                  <i class="el-icon-chat-line-round"></i>
                </div>
                <div class="privilege-info">
                  <div class="privilege-title">每日对话</div>
                  <div class="privilege-value">{{ formatLimit(userVipInfo.privileges.dailyChatLimit) }}</div>
                </div>
              </div>
              <div class="privilege-card">
                <div class="privilege-icon">
                  <i class="el-icon-picture"></i>
                </div>
                <div class="privilege-info">
                  <div class="privilege-title">图片生成</div>
                  <div class="privilege-value">
                    {{ userVipInfo.privileges.imageEnabled ? formatLimit(userVipInfo.privileges.dailyImageLimit) : '未开通' }}
                  </div>
                </div>
              </div>
              <div class="privilege-card">
                <div class="privilege-icon">
                  <i class="el-icon-microphone"></i>
                </div>
                <div class="privilege-info">
                  <div class="privilege-title">语音功能</div>
                  <div class="privilege-value">{{ userVipInfo.privileges.voiceEnabled ? '已开通' : '未开通' }}</div>
                </div>
              </div>
              <div class="privilege-card">
                <div class="privilege-icon">
                  <i class="el-icon-star-on"></i>
                </div>
                <div class="privilege-info">
                  <div class="privilege-title">定制角色</div>
                  <div class="privilege-value">{{ formatLimit(userVipInfo.privileges.customRoleQuota) }}</div>
                </div>
              </div>
            </div>

            <div v-if="!userVipInfo.isVip || userVipInfo.remainingDays <= 7" class="upgrade-action">
              <el-button type="primary" size="large" @click="showUpgradeDialog">
                {{ userVipInfo.isVip ? '续费会员' : '立即开通会员' }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 会员套餐选择 -->
        <div class="packages-section">
          <div class="section-title">会员套餐</div>
          <div class="packages-grid">
            <div
              v-for="(pkg, key) in vipPackages"
              :key="key"
              class="package-card"
              :class="{ 'recommended': key === 'gold' }"
            >
              <div v-if="key === 'gold'" class="recommended-badge">推荐</div>
              <div class="package-header">
                <div class="package-icon" :class="'level-' + pkg.level">
                  <i class="el-icon-medal"></i>
                </div>
                <div class="package-info">
                  <div class="package-name">{{ pkg.levelName }}</div>
                  <div class="package-level">Level {{ pkg.level }}</div>
                </div>
              </div>

              <div class="package-pricing">
                <div class="pricing-option">
                  <div class="price">¥{{ (pkg.monthlyPrice / 100).toFixed(2) }}</div>
                  <div class="period">月付</div>
                  <el-button size="small" @click="selectPackage(pkg, 'monthly')">选择</el-button>
                </div>
                <div class="pricing-option popular">
                  <div class="popular-label">最划算</div>
                  <div class="price">¥{{ (pkg.quarterlyPrice / 100).toFixed(2) }}</div>
                  <div class="period">季付</div>
                  <el-button type="primary" size="small" @click="selectPackage(pkg, 'quarterly')">选择</el-button>
                </div>
                <div class="pricing-option">
                  <div class="price">¥{{ (pkg.yearlyPrice / 100).toFixed(2) }}</div>
                  <div class="period">年付</div>
                  <el-button size="small" @click="selectPackage(pkg, 'yearly')">选择</el-button>
                </div>
              </div>

              <div class="package-features">
                <div class="features-title">专享权益</div>
                <div class="feature-list">
                  <div class="feature-item">
                    <i class="el-icon-check"></i>
                    <span>每日对话 {{ formatLimit(pkg.privileges.dailyChatLimit) }}</span>
                  </div>
                  <div class="feature-item">
                    <i :class="pkg.privileges.voiceEnabled ? 'el-icon-check' : 'el-icon-close'"></i>
                    <span>语音聊天功能</span>
                  </div>
                  <div class="feature-item">
                    <i :class="pkg.privileges.imageEnabled ? 'el-icon-check' : 'el-icon-close'"></i>
                    <span>AI图片生成 {{ pkg.privileges.imageEnabled ? formatLimit(pkg.privileges.dailyImageLimit) : '' }}</span>
                  </div>
                  <div class="feature-item">
                    <i class="el-icon-check"></i>
                    <span>定制角色 {{ formatLimit(pkg.privileges.customRoleQuota) }}</span>
                  </div>
                  <div class="feature-item">
                    <i :class="pkg.privileges.prioritySupportEnabled ? 'el-icon-check' : 'el-icon-close'"></i>
                    <span>优先客服支持</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付对话框 -->
    <el-dialog
      title="选择支付方式"
      :visible.sync="paymentDialogVisible"
      width="400px"
      center
    >
      <div v-if="selectedPackage" class="payment-info">
        <div class="package-summary">
          <div class="summary-item">
            <span class="label">套餐：</span>
            <span class="value">{{ selectedPackage.levelName }}</span>
          </div>
          <div class="summary-item">
            <span class="label">时长：</span>
            <span class="value">{{ getPaymentPeriodText() }}</span>
          </div>
          <div class="summary-item">
            <span class="label">价格：</span>
            <span class="value price">¥{{ getPaymentPrice() }}</span>
          </div>
        </div>

        <div class="payment-methods">
          <div class="payment-method" @click="selectPaymentMethod('alipay')">
            <i class="payment-icon alipay"></i>
            <span>支付宝</span>
          </div>
          <div class="payment-method" @click="selectPaymentMethod('wechat')">
            <i class="payment-icon wechat"></i>
            <span>微信支付</span>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 移动端底部导航 -->
    <MobileNavigation v-if="isMobile" />
  </div>
</template>

<script>
import MobileNavigation from '../components/MobileNavigation'
import { getUserVipInfo, getVipPackages, createVipPurchaseOrder, mockPaymentSuccess } from '@/api/chat/vip'

export default {
  name: 'Membership',
  components: {
    MobileNavigation
  },
  data() {
    return {
      userVipInfo: {
        isVip: false,
        vipLevel: 0,
        vipType: null,
        expireTime: null,
        remainingDays: 0,
        privileges: {}
      },
      vipPackages: {},
      paymentDialogVisible: false,
      selectedPackage: null,
      selectedPaymentPeriod: null,
      loading: true,
      currentOrder: null,
      paymentLoading: false
    }
  },
  computed: {
    isMobile() {
      return window.innerWidth <= 768
    }
  },
  created() {
    this.loadUserVipInfo()
    this.loadVipPackages()
  },
  methods: {
    // 加载用户会员信息
    async loadUserVipInfo() {
      try {
        const response = await getUserVipInfo()
        if (response && response.code === 200) {
          this.userVipInfo = response.data
        }
      } catch (error) {
        console.error('加载会员信息失败:', error)
      }
    },

    // 加载会员套餐
    async loadVipPackages() {
      try {
        const response = await getVipPackages()
        if (response && response.code === 200) {
          this.vipPackages = response.data
        }
      } catch (error) {
        console.error('加载会员套餐失败:', error)
      } finally {
        this.loading = false
      }
    },

    // 获取等级名称
    getLevelName(level) {
      const levelNames = {
        0: 'FREE',
        1: 'VIP1',
        2: 'VIP2',
        3: 'VIP3',
        4: 'VIP4'
      }
      return levelNames[level] || 'FREE'
    },

    // 获取等级显示名称
    getLevelDisplayName(level) {
      const displayNames = {
        0: '普通用户',
        1: '白银会员',
        2: '黄金会员',
        3: '铂金会员',
        4: '钻石会员'
      }
      return displayNames[level] || '普通用户'
    },

    // 格式化限制数量
    formatLimit(limit) {
      if (limit === -1) return '无限'
      if (limit === 0) return '0'
      return `${limit}次`
    },

    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '-'
      return new Date(dateStr).toLocaleDateString()
    },

    // 显示升级对话框
    showUpgradeDialog() {
      this.$message.info('会员购买功能开发中...')
    },

    // 选择套餐
    selectPackage(pkg, period = 'quarterly') {
      this.selectedPackage = pkg
      this.selectedPaymentPeriod = period
      this.paymentDialogVisible = true
    },

    // 获取支付周期文本
    getPaymentPeriodText() {
      const periodTexts = {
        monthly: '1个月',
        quarterly: '3个月',
        yearly: '12个月'
      }
      return periodTexts[this.selectedPaymentPeriod] || ''
    },

    // 获取支付价格
    getPaymentPrice() {
      if (!this.selectedPackage) return '0.00'
      const priceMap = {
        monthly: this.selectedPackage.monthlyPrice,
        quarterly: this.selectedPackage.quarterlyPrice,
        yearly: this.selectedPackage.yearlyPrice
      }
      return (priceMap[this.selectedPaymentPeriod] / 100).toFixed(2)
    },

    // 选择支付方式
    async selectPaymentMethod(method) {
      if (!this.selectedPackage || !this.selectedPaymentPeriod) {
        this.$message.error('请先选择套餐')
        return
      }

      this.paymentLoading = true

      try {
        // 创建订单
        const orderData = {
          vipLevel: this.selectedPackage.level,
          periodType: this.selectedPaymentPeriod, // monthly, quarterly, yearly
          paymentMethod: method // alipay, wechat
        }

        const response = await createVipPurchaseOrder(orderData)
        if (response && response.code === 200) {
          this.currentOrder = response.data.order

          // 显示支付信息
          this.$message.success('订单创建成功')

          // 在开发环境中，直接模拟支付成功
          if (process.env.NODE_ENV === 'development') {
            this.$confirm('这是开发环境，是否模拟支付成功？', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'info'
            }).then(() => {
              this.mockPayment()
            }).catch(() => {
              this.paymentDialogVisible = false
            })
          } else {
            // 生产环境中，这里应该调起真实的支付页面
            this.$message.info('请在新打开的页面完成支付')
            // 可以打开支付链接
            // window.open(response.data.paymentParams.payUrl)
          }
        } else {
          this.$message.error(response.msg || '创建订单失败')
        }
      } catch (error) {
        console.error('创建订单失败:', error)
        this.$message.error('创建订单失败：' + (error.message || '网络错误'))
      } finally {
        this.paymentLoading = false
      }
    },

    // 模拟支付成功（开发环境用）
    async mockPayment() {
      if (!this.currentOrder) {
        this.$message.error('订单信息丢失')
        return
      }

      try {
        const response = await mockPaymentSuccess({
          orderNo: this.currentOrder.orderNo,
          paymentMethod: this.currentOrder.paymentMethod
        })

        if (response && response.code === 200) {
          this.$message.success('支付成功！会员已开通')
          this.paymentDialogVisible = false

          // 刷新用户VIP信息
          await this.loadUserVipInfo()
        } else {
          this.$message.error(response.msg || '支付处理失败')
        }
      } catch (error) {
        console.error('模拟支付失败:', error)
        this.$message.error('支付处理失败：' + (error.message || '网络错误'))
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.membership-container {
  min-height: 100vh;
  background: #f8f9fa;
}

// 移动端样式
.mobile-membership {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f8f9fa;
  z-index: 999;
  overflow-y: auto;

  .page-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 40px 16px 20px;
    color: white;

    .header-title {
      display: flex;
      align-items: center;
      font-size: 18px;
      font-weight: 600;

      i {
        margin-right: 8px;
        font-size: 20px;
      }
    }
  }

  .membership-content {
    padding: 16px;
    padding-bottom: 80px;
  }

  .current-vip-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    padding: 20px;
    color: white;
    margin-bottom: 20px;

    .vip-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .vip-level {
        .level-badge {
          background: rgba(255, 255, 255, 0.2);
          padding: 4px 12px;
          border-radius: 12px;
          font-size: 12px;
          margin-right: 8px;
          font-weight: 600;
        }

        .level-name {
          font-size: 16px;
          font-weight: 600;
        }
      }

      .remaining-days {
        font-size: 12px;
        opacity: 0.9;
      }
    }

    .vip-privileges {
      margin-bottom: 20px;

      .privilege-item {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
        font-size: 14px;

        i {
          margin-right: 8px;
          width: 16px;
        }
      }
    }

    .upgrade-section {
      text-align: center;
    }
  }

  .package-section {
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
    }

    .package-list {
      .package-card {
        position: relative;
        background: white;
        border-radius: 12px;
        padding: 16px;
        margin-bottom: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        cursor: pointer;
        transition: all 0.3s ease;

        &.recommended {
          border: 2px solid #667eea;

          .recommended-label {
            position: absolute;
            top: -8px;
            right: 16px;
            background: #667eea;
            color: white;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 11px;
            font-weight: 600;
          }
        }

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
        }

        .package-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;

          .package-name {
            font-size: 16px;
            font-weight: 600;
            color: #333;
          }

          .package-level {
            font-size: 12px;
            color: #667eea;
            background: #f0f4ff;
            padding: 2px 8px;
            border-radius: 8px;
          }
        }

        .package-prices {
          display: flex;
          justify-content: space-around;
          margin-bottom: 12px;

          .price-item {
            text-align: center;

            .price {
              font-size: 14px;
              font-weight: 600;
              color: #667eea;
            }

            .period {
              font-size: 11px;
              color: #999;
            }
          }
        }

        .package-features {
          .feature {
            font-size: 12px;
            color: #666;
            margin-bottom: 4px;
          }
        }
      }
    }
  }
}

// 桌面端样式
.desktop-membership {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;

  .page-header {
    margin-bottom: 32px;

    .header-title {
      display: flex;
      align-items: center;
      font-size: 24px;
      font-weight: 600;
      color: #333;

      i {
        margin-right: 12px;
        color: #667eea;
        font-size: 28px;
      }
    }
  }

  .current-status-section {
    margin-bottom: 32px;

    .status-card {
      background: white;
      border-radius: 20px;
      padding: 32px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

      .status-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;

        .vip-info {
          display: flex;
          align-items: center;

          .level-badge {
            padding: 6px 16px;
            border-radius: 16px;
            font-size: 14px;
            font-weight: 600;
            margin-right: 12px;

            &.level-0 { background: #f0f0f0; color: #666; }
            &.level-1 { background: #e8f4fd; color: #1890ff; }
            &.level-2 { background: #fff7e6; color: #fa8c16; }
            &.level-3 { background: #f0f5ff; color: #722ed1; }
            &.level-4 { background: #fff0f6; color: #eb2f96; }
          }

          .level-name {
            font-size: 18px;
            font-weight: 600;
            color: #333;
          }
        }

        .expire-info {
          text-align: right;

          .expire-date {
            font-size: 14px;
            color: #666;
            margin-bottom: 4px;
          }

          .remaining-days {
            font-size: 16px;
            font-weight: 600;
            color: #667eea;
          }
        }
      }

      .privilege-grid {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 20px;
        margin-bottom: 24px;

        .privilege-card {
          background: #f8f9fa;
          border-radius: 12px;
          padding: 20px;
          text-align: center;

          .privilege-icon {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background: #667eea;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 12px;

            i {
              color: white;
              font-size: 20px;
            }
          }

          .privilege-info {
            .privilege-title {
              font-size: 14px;
              color: #666;
              margin-bottom: 4px;
            }

            .privilege-value {
              font-size: 16px;
              font-weight: 600;
              color: #333;
            }
          }
        }
      }

      .upgrade-action {
        text-align: center;
      }
    }
  }

  .packages-section {
    .section-title {
      font-size: 20px;
      font-weight: 600;
      color: #333;
      margin-bottom: 24px;
    }

    .packages-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 24px;

      .package-card {
        position: relative;
        background: white;
        border-radius: 16px;
        padding: 24px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;

        &.recommended {
          border: 2px solid #667eea;
          transform: scale(1.05);

          .recommended-badge {
            position: absolute;
            top: -12px;
            left: 50%;
            transform: translateX(-50%);
            background: #667eea;
            color: white;
            padding: 6px 20px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
          }
        }

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        }

        .package-header {
          display: flex;
          align-items: center;
          margin-bottom: 20px;

          .package-icon {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 16px;

            &.level-1 { background: linear-gradient(135deg, #1890ff, #36cfc9); }
            &.level-2 { background: linear-gradient(135deg, #fa8c16, #fadb14); }
            &.level-3 { background: linear-gradient(135deg, #722ed1, #eb2f96); }
            &.level-4 { background: linear-gradient(135deg, #eb2f96, #f759ab); }

            i {
              color: white;
              font-size: 20px;
            }
          }

          .package-info {
            .package-name {
              font-size: 18px;
              font-weight: 600;
              color: #333;
              margin-bottom: 4px;
            }

            .package-level {
              font-size: 14px;
              color: #666;
            }
          }
        }

        .package-pricing {
          display: flex;
          justify-content: space-between;
          margin-bottom: 20px;

          .pricing-option {
            position: relative;
            text-align: center;
            flex: 1;
            padding: 12px 8px;
            border-radius: 8px;
            border: 1px solid #f0f0f0;
            margin: 0 4px;

            &.popular {
              border-color: #667eea;
              background: #f0f4ff;

              .popular-label {
                position: absolute;
                top: -8px;
                left: 50%;
                transform: translateX(-50%);
                background: #667eea;
                color: white;
                padding: 2px 8px;
                border-radius: 8px;
                font-size: 10px;
              }
            }

            .price {
              font-size: 16px;
              font-weight: 600;
              color: #333;
              margin-bottom: 4px;
            }

            .period {
              font-size: 12px;
              color: #666;
              margin-bottom: 8px;
            }
          }
        }

        .package-features {
          .features-title {
            font-size: 14px;
            font-weight: 600;
            color: #333;
            margin-bottom: 12px;
          }

          .feature-list {
            .feature-item {
              display: flex;
              align-items: center;
              font-size: 14px;
              color: #666;
              margin-bottom: 8px;

              i {
                margin-right: 8px;
                width: 16px;

                &.el-icon-check {
                  color: #52c41a;
                }

                &.el-icon-close {
                  color: #ff4d4f;
                }
              }
            }
          }
        }
      }
    }
  }
}

// 支付对话框样式
.payment-info {
  .package-summary {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 20px;

    .summary-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 8px;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        color: #666;
      }

      .value {
        font-weight: 600;

        &.price {
          color: #667eea;
          font-size: 18px;
        }
      }
    }
  }

  .payment-methods {
    display: flex;
    justify-content: space-around;

    .payment-method {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 16px;
      border: 1px solid #f0f0f0;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        border-color: #667eea;
        background: #f0f4ff;
      }

      .payment-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        margin-bottom: 8px;

        &.alipay {
          background: #1677ff;
        }

        &.wechat {
          background: #07c160;
        }
      }

      span {
        font-size: 14px;
        color: #333;
      }
    }
  }
}

@media (max-width: 768px) {
  .desktop-membership {
    display: none;
  }
}

@media (min-width: 769px) {
  .mobile-membership {
    display: none;
  }
}
</style>