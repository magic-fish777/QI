<template>
  <div class="profile-container">
    <!-- 移动端个人资料页面 -->
    <div v-if="isMobile" class="mobile-profile">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-user"></i>
          <span>个人中心</span>
        </div>
      </div>

      <div class="profile-content">
        <div class="user-card">
          <div class="user-avatar">
            <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name">
            <div class="edit-avatar" @click="editAvatar">
              <i class="el-icon-camera"></i>
            </div>
          </div>
          <div class="user-info">
            <div class="user-name">{{ userInfo.username || userInfo.name }}</div>
            <div class="user-id">ID: {{ userInfo.userId || '123456' }}</div>
            <div class="user-level">{{ userLevelName }}</div>
          </div>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-number">{{ dailyChats }}</div>
              <div class="stat-label">今日对话</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">365</div>
              <div class="stat-label">总对话</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">15</div>
              <div class="stat-label">收藏角色</div>
            </div>
          </div>
        </div>

        <div class="menu-section">
          <div class="menu-group">
            <div class="menu-item" @click="editProfile">
              <div class="menu-icon">
                <i class="el-icon-edit"></i>
              </div>
              <span>个人资料</span>
              <i class="el-icon-arrow-right"></i>
            </div>
            <div class="menu-item" @click="viewHistory">
              <div class="menu-icon">
                <i class="el-icon-time"></i>
              </div>
              <span>聊天记录</span>
              <i class="el-icon-arrow-right"></i>
            </div>
            <div class="menu-item" @click="viewFavorites">
              <div class="menu-icon">
                <i class="el-icon-star-on"></i>
              </div>
              <span>我的收藏</span>
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>

          <div class="menu-group">
            <div class="menu-item" @click="contactSupport">
              <div class="menu-icon">
                <i class="el-icon-service"></i>
              </div>
              <span>客服支持</span>
              <i class="el-icon-arrow-right"></i>
            </div>
            <div class="menu-item" @click="showFeedback">
              <div class="menu-icon">
                <i class="el-icon-chat-dot-round"></i>
              </div>
              <span>意见反馈</span>
              <i class="el-icon-arrow-right"></i>
            </div>
            <div class="menu-item" @click="showAbout">
              <div class="menu-icon">
                <i class="el-icon-info"></i>
              </div>
              <span>关于我们</span>
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>

          <div class="menu-group">
            <div class="menu-item logout" @click="handleLogout">
              <div class="menu-icon">
                <i class="el-icon-switch-button"></i>
              </div>
              <span>退出登录</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端个人资料页面 -->
    <div v-else class="desktop-profile">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-user"></i>
          <span>个人中心</span>
        </div>
      </div>
      <div class="page-content">
        <div class="profile-overview">
          <div class="user-info-card">
            <div class="user-avatar-section">
              <div class="user-avatar-large">
                <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name">
                <div class="edit-avatar" @click="editAvatar">
                  <i class="el-icon-camera"></i>
                </div>
              </div>
              <div class="user-details">
                <div class="user-name">{{ userInfo.username || userInfo.name }}</div>
                <div class="user-id">ID: {{ userInfo.userId || '123456' }}</div>
                <div class="user-level-badge">{{ userLevelName }}</div>
              </div>
            </div>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-number">{{ dailyChats }}</div>
                <div class="stat-label">今日对话</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">365</div>
                <div class="stat-label">总对话</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">15</div>
                <div class="stat-label">收藏角色</div>
              </div>
            </div>
          </div>
        </div>

        <div class="profile-menu-grid">
          <div class="menu-card" @click="editProfile">
            <div class="menu-icon">
              <i class="el-icon-edit"></i>
            </div>
            <div class="menu-content">
              <div class="menu-title">编辑资料</div>
              <div class="menu-desc">修改个人信息</div>
            </div>
          </div>
          <div class="menu-card" @click="viewHistory">
            <div class="menu-icon">
              <i class="el-icon-time"></i>
            </div>
            <div class="menu-content">
              <div class="menu-title">聊天记录</div>
              <div class="menu-desc">查看历史对话</div>
            </div>
          </div>
          <div class="menu-card" @click="viewFavorites">
            <div class="menu-icon">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="menu-content">
              <div class="menu-title">我的收藏</div>
              <div class="menu-desc">收藏的角色和对话</div>
            </div>
          </div>
          <div class="menu-card" @click="contactSupport">
            <div class="menu-icon">
              <i class="el-icon-service"></i>
            </div>
            <div class="menu-content">
              <div class="menu-title">客服支持</div>
              <div class="menu-desc">获取帮助和支持</div>
            </div>
          </div>
          <div class="menu-card" @click="showFeedback">
            <div class="menu-icon">
              <i class="el-icon-chat-dot-round"></i>
            </div>
            <div class="menu-content">
              <div class="menu-title">意见反馈</div>
              <div class="menu-desc">提供建议和反馈</div>
            </div>
          </div>
          <div class="menu-card" @click="showAbout">
            <div class="menu-icon">
              <i class="el-icon-info"></i>
            </div>
            <div class="menu-content">
              <div class="menu-title">关于我们</div>
              <div class="menu-desc">了解QIQI PLAY</div>
            </div>
          </div>
        </div>

        <div class="logout-section">
          <el-button type="danger" size="medium" @click="handleLogout" plain>
            <i class="el-icon-switch-button"></i>
            退出登录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 移动端底部导航 -->
    <MobileNavigation v-if="isMobile" />
  </div>
</template>

<script>
import MobileNavigation from '../components/MobileNavigation'

export default {
  name: 'Profile',
  components: {
    MobileNavigation
  },
  data() {
    return {
      userInfo: {
        name: '用户',
        username: 'qiqi_user',
        userId: '123456',
        avatar: ''
      },
      userLevelName: '白银会员',
      dailyChats: 15,
      defaultAvatar: require('@/assets/images/profile.jpg')
    }
  },
  computed: {
    isMobile() {
      return window.innerWidth <= 768
    }
  },
  methods: {
    editAvatar() {
      this.$message.info('头像编辑功能开发中...')
    },
    editProfile() {
      this.$message.info('个人资料编辑功能开发中...')
    },
    viewHistory() {
      this.$message.info('聊天记录功能开发中...')
    },
    viewFavorites() {
      this.$message.info('我的收藏功能开发中...')
    },
    contactSupport() {
      this.$message.info('客服支持功能开发中...')
    },
    showFeedback() {
      this.$message.info('意见反馈功能开发中...')
    },
    showAbout() {
      this.$message.info('关于我们功能开发中...')
    },
    handleLogout() {
      this.$confirm('确认退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除登录状态
        localStorage.removeItem('chat_token')
        localStorage.removeItem('chat_isLoggedIn')
        this.$message.success('退出登录成功')
        this.$router.push('/chat/login')
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background: #f8f9fa;
}

// 移动端样式
.mobile-profile {
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

  .profile-content {
    padding: 16px;
    padding-bottom: 80px;
  }

  .user-card {
    background: white;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .user-avatar {
      position: relative;
      width: 80px;
      height: 80px;
      margin: 0 auto 16px;

      img {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover;
      }

      .edit-avatar {
        position: absolute;
        bottom: 0;
        right: 0;
        width: 24px;
        height: 24px;
        background: #667eea;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);

        i {
          color: white;
          font-size: 12px;
        }
      }
    }

    .user-info {
      text-align: center;
      margin-bottom: 20px;

      .user-name {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }

      .user-id {
        font-size: 12px;
        color: #999;
        margin-bottom: 8px;
      }

      .user-level {
        display: inline-block;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
      }
    }

    .user-stats {
      display: flex;
      justify-content: space-around;
      align-items: center;

      .stat-item {
        text-align: center;

        .stat-number {
          font-size: 20px;
          font-weight: 600;
          color: #667eea;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 11px;
          color: #666;
        }
      }
    }
  }

  .menu-section {
    .menu-group {
      background: white;
      border-radius: 12px;
      margin-bottom: 16px;
      overflow: hidden;

      .menu-item {
        display: flex;
        align-items: center;
        padding: 16px;
        border-bottom: 1px solid #f8f8f8;
        cursor: pointer;
        transition: background-color 0.3s;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background: #f8f9fa;
        }

        &.logout {
          color: #f56c6c;

          .menu-icon {
            background: #fee;
            color: #f56c6c;
          }
        }

        .menu-icon {
          width: 32px;
          height: 32px;
          border-radius: 8px;
          background: #f0f0f0;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 12px;
          color: #666;

          i {
            font-size: 16px;
          }
        }

        span {
          flex: 1;
          font-size: 14px;
          color: #333;
        }

        .el-icon-arrow-right {
          color: #ccc;
          font-size: 14px;
        }
      }
    }
  }
}

// 桌面端样式
.desktop-profile {
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

  .profile-overview {
    margin-bottom: 32px;

    .user-info-card {
      background: white;
      border-radius: 20px;
      padding: 32px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

      .user-avatar-section {
        display: flex;
        align-items: center;

        .user-avatar-large {
          position: relative;
          margin-right: 24px;

          img {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            object-fit: cover;
          }

          .edit-avatar {
            position: absolute;
            bottom: 0;
            right: 0;
            width: 32px;
            height: 32px;
            background: #667eea;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);

            &:hover {
              background: #5a6fd8;
            }

            i {
              color: white;
              font-size: 14px;
            }
          }
        }

        .user-details {
          .user-name {
            font-size: 24px;
            font-weight: 600;
            color: #333;
            margin-bottom: 6px;
          }

          .user-id {
            font-size: 14px;
            color: #999;
            margin-bottom: 12px;
          }

          .user-level-badge {
            display: inline-block;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 6px 16px;
            border-radius: 16px;
            font-size: 14px;
            font-weight: 500;
          }
        }
      }

      .user-stats {
        display: flex;
        gap: 32px;

        .stat-item {
          text-align: center;

          .stat-number {
            font-size: 24px;
            font-weight: 600;
            color: #667eea;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 12px;
            color: #666;
          }
        }
      }
    }
  }

  .profile-menu-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 32px;

    .menu-card {
      background: white;
      border-radius: 16px;
      padding: 24px;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }

      .menu-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 16px;

        i {
          color: white;
          font-size: 20px;
        }
      }

      .menu-content {
        .menu-title {
          font-size: 16px;
          font-weight: 600;
          color: #333;
          margin-bottom: 6px;
        }

        .menu-desc {
          font-size: 14px;
          color: #666;
          line-height: 1.4;
        }
      }
    }
  }

  .logout-section {
    text-align: center;
  }
}

@media (max-width: 768px) {
  .desktop-profile {
    display: none;
  }
}

@media (min-width: 769px) {
  .mobile-profile {
    display: none;
  }
}
</style>