<template>
  <div class="benefits-container">
    <!-- 移动端权益页面 -->
    <div v-if="isMobile" class="mobile-benefits">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-medal"></i>
          <span>会员权益</span>
        </div>
      </div>

      <div class="benefits-content">
        <div class="membership-card">
          <div class="card-header">
            <div class="user-level">
              <span class="level-badge">{{ userLevel }}</span>
              <span class="level-name">{{ userLevelName }}</span>
            </div>
            <div class="upgrade-btn" @click="showUpgrade">
              <i class="el-icon-top"></i>
              <span>升级</span>
            </div>
          </div>
          <div class="card-content">
            <div class="benefit-item">
              <div class="benefit-icon">
                <i class="el-icon-chat-line-round"></i>
              </div>
              <div class="benefit-info">
                <div class="benefit-label">每日对话次数</div>
                <div class="benefit-value">{{ dailyChats }}/{{ maxDailyChats }}</div>
              </div>
            </div>
            <div class="benefit-item">
              <div class="benefit-icon">
                <i class="el-icon-microphone"></i>
              </div>
              <div class="benefit-info">
                <div class="benefit-label">语音功能</div>
                <div class="benefit-value">{{ hasVoiceAccess ? '已开启' : '未开启' }}</div>
              </div>
            </div>
            <div class="benefit-item">
              <div class="benefit-icon">
                <i class="el-icon-picture"></i>
              </div>
              <div class="benefit-info">
                <div class="benefit-label">图片生成</div>
                <div class="benefit-value">{{ hasImageAccess ? '已开启' : '未开启' }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="benefits-list">
          <div class="section-title">会员特权</div>
          <div class="benefit-card" v-for="benefit in memberBenefits" :key="benefit.id">
            <div class="benefit-icon">
              <i :class="benefit.icon"></i>
            </div>
            <div class="benefit-content">
              <div class="benefit-title">{{ benefit.title }}</div>
              <div class="benefit-desc">{{ benefit.description }}</div>
            </div>
            <div class="benefit-status">
              <span :class="{ enabled: benefit.enabled }">
                {{ benefit.enabled ? '已开启' : '升级解锁' }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端权益页面 -->
    <div v-else class="desktop-benefits">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-medal"></i>
          <span>会员权益</span>
        </div>
      </div>
      <div class="page-content">
        <div class="membership-section">
          <div class="membership-card">
            <div class="card-header">
              <div class="user-level">
                <span class="level-badge">{{ userLevel }}</span>
                <span class="level-name">{{ userLevelName }}</span>
              </div>
              <div class="upgrade-btn" @click="showUpgrade">
                <i class="el-icon-top"></i>
                <span>升级</span>
              </div>
            </div>
            <div class="card-content">
              <div class="benefit-stats">
                <div class="stat-item">
                  <div class="stat-icon">
                    <i class="el-icon-chat-line-round"></i>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ dailyChats }}/{{ maxDailyChats }}</div>
                    <div class="stat-label">每日对话次数</div>
                  </div>
                </div>
                <div class="stat-item">
                  <div class="stat-icon">
                    <i class="el-icon-microphone"></i>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ hasVoiceAccess ? '已开启' : '未开启' }}</div>
                    <div class="stat-label">语音功能</div>
                  </div>
                </div>
                <div class="stat-item">
                  <div class="stat-icon">
                    <i class="el-icon-picture"></i>
                  </div>
                  <div class="stat-info">
                    <div class="stat-value">{{ hasImageAccess ? '已开启' : '未开启' }}</div>
                    <div class="stat-label">图片生成</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="benefits-grid">
          <div class="section-title">会员特权</div>
          <div class="benefits-container">
            <div class="benefit-card" v-for="benefit in memberBenefits" :key="benefit.id">
              <div class="benefit-icon">
                <i :class="benefit.icon"></i>
              </div>
              <div class="benefit-info">
                <div class="benefit-title">{{ benefit.title }}</div>
                <div class="benefit-desc">{{ benefit.description }}</div>
              </div>
              <div class="benefit-status">
                <span :class="{ enabled: benefit.enabled }">
                  {{ benefit.enabled ? '已开启' : '升级解锁' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 移动端底部导航 -->
    <MobileNavigation v-if="isMobile" />
  </div>
</template>

<script>
import MobileNavigation from '../components/MobileNavigation'
import { getUserVipInfo } from '@/api/chat/vip'

export default {
  name: 'Benefits',
  components: {
    MobileNavigation
  },
  data() {
    return {
      userLevel: 'FREE',
      userLevelName: '普通用户',
      dailyChats: 0,
      maxDailyChats: 10,
      hasVoiceAccess: false,
      hasImageAccess: false,
      userVipInfo: null,
      memberBenefits: [
        {
          id: 1,
          icon: 'el-icon-chat-line-round',
          title: '无限对话',
          description: '享受不限次数的AI对话体验',
          enabled: true
        },
        {
          id: 2,
          icon: 'el-icon-microphone',
          title: '语音聊天',
          description: '支持语音输入和语音回复',
          enabled: true
        },
        {
          id: 3,
          icon: 'el-icon-picture',
          title: '图片生成',
          description: 'AI绘画和图片生成功能',
          enabled: false
        },
        {
          id: 4,
          icon: 'el-icon-star-on',
          title: '专属角色',
          description: '解锁高级AI角色和人设',
          enabled: false
        },
        {
          id: 5,
          icon: 'el-icon-download',
          title: '聊天导出',
          description: '导出聊天记录为多种格式',
          enabled: false
        },
        {
          id: 6,
          icon: 'el-icon-service',
          title: '优先客服',
          description: '享受7x24小时优先客服支持',
          enabled: true
        }
      ]
    }
  },
  computed: {
    isMobile() {
      return window.innerWidth <= 768
    }
  },
  created() {
    this.loadUserVipInfo()
  },
  methods: {
    // 加载用户会员信息
    async loadUserVipInfo() {
      try {
        const response = await getUserVipInfo()
        if (response && response.code === 200) {
          this.userVipInfo = response.data
          this.updateDisplayInfo()
        }
      } catch (error) {
        console.error('加载会员信息失败:', error)
      }
    },

    // 更新显示信息
    updateDisplayInfo() {
      if (!this.userVipInfo) return

      // 更新会员等级显示
      const levelNames = {
        0: 'FREE',
        1: 'VIP1',
        2: 'VIP2',
        3: 'VIP3',
        4: 'VIP4'
      }
      const levelDisplayNames = {
        0: '普通用户',
        1: '白银会员',
        2: '黄金会员',
        3: '铂金会员',
        4: '钻石会员'
      }

      this.userLevel = levelNames[this.userVipInfo.vipLevel] || 'FREE'
      this.userLevelName = levelDisplayNames[this.userVipInfo.vipLevel] || '普通用户'

      // 更新权益信息
      const privileges = this.userVipInfo.privileges
      this.maxDailyChats = privileges.dailyChatLimit === -1 ? '无限' : privileges.dailyChatLimit
      this.hasVoiceAccess = privileges.voiceEnabled
      this.hasImageAccess = privileges.imageEnabled

      // 更新会员权益状态
      this.memberBenefits.forEach(benefit => {
        switch (benefit.id) {
          case 1: // 无限对话
            benefit.enabled = privileges.dailyChatLimit === -1
            break
          case 2: // 语音聊天
            benefit.enabled = privileges.voiceEnabled
            break
          case 3: // 图片生成
            benefit.enabled = privileges.imageEnabled
            break
          case 4: // 专属角色
            benefit.enabled = privileges.exclusiveRoleEnabled
            break
          case 5: // 聊天导出
            benefit.enabled = privileges.chatExportEnabled
            break
          case 6: // 优先客服
            benefit.enabled = privileges.prioritySupportEnabled
            break
        }
      })
    },

    showUpgrade() {
      // 跳转到会员中心
      this.$router.push('/chat/membership')
    }
  }
}
</script>

<style lang="scss" scoped>
.benefits-container {
  min-height: 100vh;
  background: #f8f9fa;
}

// 移动端样式
.mobile-benefits {
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

  .benefits-content {
    padding: 16px;
    padding-bottom: 80px;
  }

  .membership-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    padding: 20px;
    color: white;
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .user-level {
        .level-badge {
          background: rgba(255, 255, 255, 0.2);
          padding: 4px 12px;
          border-radius: 12px;
          font-size: 12px;
          margin-right: 8px;
        }

        .level-name {
          font-size: 16px;
          font-weight: 600;
        }
      }

      .upgrade-btn {
        background: rgba(255, 255, 255, 0.2);
        padding: 8px 16px;
        border-radius: 20px;
        cursor: pointer;
        font-size: 12px;
        display: flex;
        align-items: center;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
        }

        i {
          margin-right: 4px;
        }
      }
    }

    .card-content {
      .benefit-item {
        display: flex;
        align-items: center;
        margin-bottom: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .benefit-icon {
          width: 40px;
          height: 40px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;

          i {
            font-size: 18px;
          }
        }

        .benefit-info {
          flex: 1;

          .benefit-label {
            font-size: 14px;
            opacity: 0.9;
          }

          .benefit-value {
            font-size: 16px;
            font-weight: 600;
            margin-top: 2px;
          }
        }
      }
    }
  }

  .benefits-list {
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
    }

    .benefit-card {
      background: white;
      border-radius: 12px;
      padding: 16px;
      margin-bottom: 12px;
      display: flex;
      align-items: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .benefit-icon {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;

        i {
          color: white;
          font-size: 18px;
        }
      }

      .benefit-content {
        flex: 1;

        .benefit-title {
          font-size: 14px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }

        .benefit-desc {
          font-size: 12px;
          color: #666;
          line-height: 1.4;
        }
      }

      .benefit-status {
        span {
          font-size: 11px;
          padding: 4px 8px;
          border-radius: 8px;
          background: #f0f0f0;
          color: #999;

          &.enabled {
            background: #e8f5e8;
            color: #4CAF50;
          }
        }
      }
    }
  }
}

// 桌面端样式
.desktop-benefits {
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

  .membership-section {
    margin-bottom: 32px;

    .membership-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 20px;
      padding: 32px;
      color: white;
      box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;

        .user-level {
          .level-badge {
            background: rgba(255, 255, 255, 0.2);
            padding: 6px 16px;
            border-radius: 16px;
            font-size: 14px;
            margin-right: 12px;
            font-weight: 600;
          }

          .level-name {
            font-size: 18px;
            font-weight: 600;
          }
        }

        .upgrade-btn {
          background: rgba(255, 255, 255, 0.2);
          padding: 12px 24px;
          border-radius: 24px;
          cursor: pointer;
          transition: all 0.3s ease;
          display: flex;
          align-items: center;

          &:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateY(-2px);
          }

          i {
            margin-right: 6px;
          }
        }
      }

      .card-content {
        .benefit-stats {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 24px;

          .stat-item {
            display: flex;
            align-items: center;
            background: rgba(255, 255, 255, 0.1);
            padding: 20px;
            border-radius: 16px;
            backdrop-filter: blur(10px);

            .stat-icon {
              width: 48px;
              height: 48px;
              background: rgba(255, 255, 255, 0.2);
              border-radius: 12px;
              display: flex;
              align-items: center;
              justify-content: center;
              margin-right: 16px;

              i {
                font-size: 20px;
                color: white;
              }
            }

            .stat-info {
              .stat-value {
                font-size: 18px;
                font-weight: 600;
                margin-bottom: 4px;
              }

              .stat-label {
                font-size: 14px;
                opacity: 0.9;
              }
            }
          }
        }
      }
    }
  }

  .benefits-grid {
    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin-bottom: 20px;
    }

    .benefits-container {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;

      .benefit-card {
        background: white;
        border-radius: 16px;
        padding: 24px;
        display: flex;
        align-items: center;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        }

        .benefit-icon {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;

          i {
            color: white;
            font-size: 20px;
          }
        }

        .benefit-info {
          flex: 1;

          .benefit-title {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin-bottom: 6px;
          }

          .benefit-desc {
            font-size: 14px;
            color: #666;
            line-height: 1.4;
          }
        }

        .benefit-status {
          span {
            font-size: 12px;
            padding: 6px 12px;
            border-radius: 12px;
            background: #f0f0f0;
            color: #999;
            font-weight: 500;

            &.enabled {
              background: #e8f5e8;
              color: #4CAF50;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .desktop-benefits {
    display: none;
  }
}

@media (min-width: 769px) {
  .mobile-benefits {
    display: none;
  }
}
</style>