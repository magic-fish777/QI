<template>
  <div class="settings-container">
    <!-- 移动端设置页面 -->
    <div v-if="isMobile" class="mobile-settings">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-setting"></i>
          <span>设置</span>
        </div>
      </div>

      <div class="settings-content">
        <div class="settings-section">
          <div class="section-title">聊天设置</div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-microphone"></i>
              <div class="label-content">
                <span class="label-title">语音功能</span>
                <span class="label-desc">开启语音输入和回复</span>
              </div>
            </div>
            <el-switch v-model="settings.voiceEnabled"></el-switch>
          </div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-bell"></i>
              <div class="label-content">
                <span class="label-title">消息通知</span>
                <span class="label-desc">接收新消息提醒</span>
              </div>
            </div>
            <el-switch v-model="settings.notificationEnabled"></el-switch>
          </div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-view"></i>
              <div class="label-content">
                <span class="label-title">深色模式</span>
                <span class="label-desc">使用深色主题界面</span>
              </div>
            </div>
            <el-switch v-model="settings.darkMode"></el-switch>
          </div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-download"></i>
              <div class="label-content">
                <span class="label-title">自动保存</span>
                <span class="label-desc">自动保存聊天记录</span>
              </div>
            </div>
            <el-switch v-model="settings.autoSave"></el-switch>
          </div>
        </div>

        <div class="settings-section">
          <div class="section-title">隐私设置</div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-lock"></i>
              <div class="label-content">
                <span class="label-title">聊天记录加密</span>
                <span class="label-desc">保护聊天内容安全</span>
              </div>
            </div>
            <el-switch v-model="settings.encryptChats"></el-switch>
          </div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-time"></i>
              <div class="label-content">
                <span class="label-title">自动清理聊天记录</span>
                <span class="label-desc">定期删除旧的聊天记录</span>
              </div>
            </div>
            <el-switch v-model="settings.autoCleanChats"></el-switch>
          </div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-view"></i>
              <div class="label-content">
                <span class="label-title">隐身模式</span>
                <span class="label-desc">不显示在线状态</span>
              </div>
            </div>
            <el-switch v-model="settings.invisibleMode"></el-switch>
          </div>
        </div>

        <div class="settings-section">
          <div class="section-title">其他设置</div>
          <div class="setting-item clickable" @click="clearCache">
            <div class="setting-label">
              <i class="el-icon-delete"></i>
              <div class="label-content">
                <span class="label-title">清理缓存</span>
                <span class="label-desc">清理应用缓存数据</span>
              </div>
            </div>
            <i class="el-icon-arrow-right"></i>
          </div>
          <div class="setting-item clickable" @click="exportData">
            <div class="setting-label">
              <i class="el-icon-download"></i>
              <div class="label-content">
                <span class="label-title">导出数据</span>
                <span class="label-desc">导出个人数据和聊天记录</span>
              </div>
            </div>
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>

        <div class="settings-section">
          <div class="section-title">关于</div>
          <div class="setting-item clickable" @click="showAbout">
            <div class="setting-label">
              <i class="el-icon-info"></i>
              <div class="label-content">
                <span class="label-title">关于QIQI PLAY</span>
                <span class="label-desc">查看应用信息和版本</span>
              </div>
            </div>
            <i class="el-icon-arrow-right"></i>
          </div>
          <div class="setting-item clickable" @click="showPrivacy">
            <div class="setting-label">
              <i class="el-icon-document"></i>
              <div class="label-content">
                <span class="label-title">隐私政策</span>
                <span class="label-desc">了解数据收集和使用政策</span>
              </div>
            </div>
            <i class="el-icon-arrow-right"></i>
          </div>
          <div class="setting-item clickable" @click="showTerms">
            <div class="setting-label">
              <i class="el-icon-document-checked"></i>
              <div class="label-content">
                <span class="label-title">服务条款</span>
                <span class="label-desc">查看使用条款和协议</span>
              </div>
            </div>
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- 桌面端设置页面 -->
    <div v-else class="desktop-settings">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-setting"></i>
          <span>设置</span>
        </div>
      </div>
      <div class="page-content">
        <div class="settings-grid">
          <div class="settings-section">
            <div class="section-title">聊天设置</div>
            <div class="setting-items">
              <div class="setting-item">
                <div class="setting-label">
                  <i class="el-icon-microphone"></i>
                  <div class="label-content">
                    <span class="label-title">语音功能</span>
                    <span class="label-desc">开启语音输入和回复</span>
                  </div>
                </div>
                <el-switch v-model="settings.voiceEnabled"></el-switch>
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <i class="el-icon-bell"></i>
                  <div class="label-content">
                    <span class="label-title">消息通知</span>
                    <span class="label-desc">接收新消息提醒</span>
                  </div>
                </div>
                <el-switch v-model="settings.notificationEnabled"></el-switch>
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <i class="el-icon-view"></i>
                  <div class="label-content">
                    <span class="label-title">深色模式</span>
                    <span class="label-desc">使用深色主题界面</span>
                  </div>
                </div>
                <el-switch v-model="settings.darkMode"></el-switch>
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <i class="el-icon-download"></i>
                  <div class="label-content">
                    <span class="label-title">自动保存</span>
                    <span class="label-desc">自动保存聊天记录</span>
                  </div>
                </div>
                <el-switch v-model="settings.autoSave"></el-switch>
              </div>
            </div>
          </div>

          <div class="settings-section">
            <div class="section-title">隐私设置</div>
            <div class="setting-items">
              <div class="setting-item">
                <div class="setting-label">
                  <i class="el-icon-lock"></i>
                  <div class="label-content">
                    <span class="label-title">聊天记录加密</span>
                    <span class="label-desc">保护聊天内容安全</span>
                  </div>
                </div>
                <el-switch v-model="settings.encryptChats"></el-switch>
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <i class="el-icon-time"></i>
                  <div class="label-content">
                    <span class="label-title">自动清理聊天记录</span>
                    <span class="label-desc">定期删除旧的聊天记录</span>
                  </div>
                </div>
                <el-switch v-model="settings.autoCleanChats"></el-switch>
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <i class="el-icon-view"></i>
                  <div class="label-content">
                    <span class="label-title">隐身模式</span>
                    <span class="label-desc">不显示在线状态</span>
                  </div>
                </div>
                <el-switch v-model="settings.invisibleMode"></el-switch>
              </div>
            </div>
          </div>

          <div class="settings-section">
            <div class="section-title">其他设置</div>
            <div class="setting-items">
              <div class="setting-item clickable" @click="clearCache">
                <div class="setting-label">
                  <i class="el-icon-delete"></i>
                  <div class="label-content">
                    <span class="label-title">清理缓存</span>
                    <span class="label-desc">清理应用缓存数据</span>
                  </div>
                </div>
                <i class="el-icon-arrow-right"></i>
              </div>
              <div class="setting-item clickable" @click="exportData">
                <div class="setting-label">
                  <i class="el-icon-download"></i>
                  <div class="label-content">
                    <span class="label-title">导出数据</span>
                    <span class="label-desc">导出个人数据和聊天记录</span>
                  </div>
                </div>
                <i class="el-icon-arrow-right"></i>
              </div>
            </div>
          </div>

          <div class="settings-section">
            <div class="section-title">关于</div>
            <div class="setting-items">
              <div class="setting-item clickable" @click="showAbout">
                <div class="setting-label">
                  <i class="el-icon-info"></i>
                  <div class="label-content">
                    <span class="label-title">关于QIQI PLAY</span>
                    <span class="label-desc">查看应用信息和版本</span>
                  </div>
                </div>
                <i class="el-icon-arrow-right"></i>
              </div>
              <div class="setting-item clickable" @click="showPrivacy">
                <div class="setting-label">
                  <i class="el-icon-document"></i>
                  <div class="label-content">
                    <span class="label-title">隐私政策</span>
                    <span class="label-desc">了解数据收集和使用政策</span>
                  </div>
                </div>
                <i class="el-icon-arrow-right"></i>
              </div>
              <div class="setting-item clickable" @click="showTerms">
                <div class="setting-label">
                  <i class="el-icon-document-checked"></i>
                  <div class="label-content">
                    <span class="label-title">服务条款</span>
                    <span class="label-desc">查看使用条款和协议</span>
                  </div>
                </div>
                <i class="el-icon-arrow-right"></i>
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

export default {
  name: 'Settings',
  components: {
    MobileNavigation
  },
  data() {
    return {
      settings: {
        voiceEnabled: true,
        notificationEnabled: true,
        darkMode: false,
        autoSave: true,
        encryptChats: false,
        autoCleanChats: false,
        invisibleMode: false
      }
    }
  },
  computed: {
    isMobile() {
      return window.innerWidth <= 768
    }
  },
  methods: {
    clearCache() {
      this.$confirm('清理缓存将删除本地存储的数据，确认继续吗？', '清理缓存', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟清理缓存
        setTimeout(() => {
          this.$message.success('缓存清理成功')
        }, 1000)
      }).catch(() => {})
    },
    exportData() {
      this.$message.info('数据导出功能开发中...')
    },
    showAbout() {
      this.$message.info('关于页面功能开发中...')
    },
    showPrivacy() {
      this.$message.info('隐私政策页面功能开发中...')
    },
    showTerms() {
      this.$message.info('服务条款页面功能开发中...')
    }
  },
  watch: {
    'settings.darkMode'(newVal) {
      if (newVal) {
        document.body.classList.add('dark-theme')
      } else {
        document.body.classList.remove('dark-theme')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.settings-container {
  min-height: 100vh;
  background: #f8f9fa;
}

// 移动端样式
.mobile-settings {
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

  .settings-content {
    padding: 16px;
    padding-bottom: 80px;
  }

  .settings-section {
    background: white;
    border-radius: 12px;
    margin-bottom: 16px;
    overflow: hidden;

    .section-title {
      padding: 16px;
      font-size: 14px;
      font-weight: 600;
      color: #333;
      border-bottom: 1px solid #f8f8f8;
      background: #fafafa;
    }

    .setting-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px;
      border-bottom: 1px solid #f8f8f8;

      &:last-child {
        border-bottom: none;
      }

      &.clickable {
        cursor: pointer;

        &:hover {
          background: #f8f9fa;
        }
      }

      .setting-label {
        display: flex;
        align-items: center;
        flex: 1;

        i {
          margin-right: 12px;
          color: #667eea;
          font-size: 18px;
          width: 20px;
          text-align: center;
        }

        .label-content {
          .label-title {
            display: block;
            font-size: 14px;
            color: #333;
            font-weight: 500;
            margin-bottom: 2px;
          }

          .label-desc {
            display: block;
            font-size: 12px;
            color: #666;
            line-height: 1.3;
          }
        }
      }

      .el-icon-arrow-right {
        color: #ccc;
        font-size: 14px;
      }
    }
  }
}

// 桌面端样式
.desktop-settings {
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

  .settings-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 24px;

    .settings-section {
      background: white;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

      .section-title {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        margin-bottom: 20px;
        padding-bottom: 12px;
        border-bottom: 2px solid #f0f0f0;
      }

      .setting-items {
        .setting-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 16px 0;
          border-bottom: 1px solid #f8f8f8;

          &:last-child {
            border-bottom: none;
          }

          &.clickable {
            cursor: pointer;
            border-radius: 8px;
            transition: all 0.3s ease;

            &:hover {
              background: rgba(102, 126, 234, 0.05);
              margin: 0 -16px;
              padding-left: 16px;
              padding-right: 16px;
            }
          }

          .setting-label {
            display: flex;
            align-items: center;
            flex: 1;

            i {
              margin-right: 12px;
              color: #667eea;
              font-size: 18px;
              width: 24px;
              text-align: center;
            }

            .label-content {
              .label-title {
                display: block;
                font-size: 15px;
                color: #333;
                font-weight: 500;
                margin-bottom: 4px;
              }

              .label-desc {
                display: block;
                font-size: 12px;
                color: #666;
                line-height: 1.3;
              }
            }
          }

          .el-icon-arrow-right {
            color: #ccc;
            font-size: 14px;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .desktop-settings {
    display: none;
  }
}

@media (min-width: 769px) {
  .mobile-settings {
    display: none;
  }
}
</style>