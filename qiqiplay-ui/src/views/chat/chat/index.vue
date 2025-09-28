<template>
  <div class="mobile-chat-app">
    <!-- 登录检查 -->
    <div v-if="!isLoggedIn" class="login-redirect">
      <div class="login-prompt">
        <i class="el-icon-warning-outline"></i>
        <p>请先登录以使用AI对话功能</p>
        <el-button type="primary" @click="goToLogin">立即登录</el-button>
      </div>
    </div>

    <!-- 主要内容 -->
    <div v-else>
      <!-- 角色列表页面 -->
      <div v-if="!selectedCharacter" class="character-list-page">
        <div class="character-list-container">
          <AppHeader
            :user-info="userInfo"
            @toggle-user-menu="showUserMenu = !showUserMenu"
            @show-search="showSearch = true"
          />
          <div v-if="charactersLoading" class="loading-container">
            <i class="el-icon-loading"></i>
            <p>正在加载角色列表...</p>
          </div>
          <MobileCharacterGrid
            v-else
            :characters="filteredCharacters"
            @select-character="selectCharacter"
          />
        </div>
      </div>

      <!-- 聊天页面 -->
      <div v-else class="chat-page">
        <ChatHeader
          :character="selectedCharacter"
          :is-mobile="true"
          @back="backToCharacterList"
          @show-menu="showChatMenu = true"
        />
        <!-- VIP状态指示器 -->
        <div class="vip-status-bar">
          <VipStatusIndicator
            :compact="true"
            @upgrade="handleVipUpgrade"
            @permission-denied="handlePermissionDenied"
          />
        </div>
        <MessageArea
          :messages="currentMessages"
          :selected-character="selectedCharacter"
          :user-avatar="userInfo.avatar || defaultAvatar"
          :is-typing="isTyping"
        />
        <!-- InputArea 现在是固定定位，在底部导航栏上方 -->
        <InputArea
          :disabled="isTyping"
          @send-message="sendMessage"
          @typing="handleTyping"
          @toggle-voice="handleToggleVoice"
          @send-audio="sendAudioMessage"
        />
      </div>
    </div>

    <!-- 底部导航 -->
    <MobileNavigation />

    <!-- 搜索弹窗 -->
    <el-dialog
      title="搜索角色"
      :visible.sync="showSearch"
      width="90%"
      :show-close="false"
      class="search-dialog"
    >
      <el-input
        v-model="searchQuery"
        placeholder="输入角色名称..."
        prefix-icon="el-icon-search"
        @input="handleSearch"
      ></el-input>
      <div class="search-results">
        <div
          v-for="character in searchResults"
          :key="character.id"
          class="search-result-item"
          @click="selectCharacterFromSearch(character)"
        >
          <img :src="character.avatar" :alt="character.name">
          <div class="character-name">{{ character.name }}</div>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showSearch = false">取消</el-button>
      </span>
    </el-dialog>

    <!-- 用户菜单弹窗 -->
    <el-dialog
      title="用户菜单"
      :visible.sync="showUserMenu"
      width="80%"
      class="user-menu-dialog"
    >
      <div class="user-menu-content">
        <div class="menu-item" @click="goToProfile">
          <i class="el-icon-user"></i>
          <span>个人资料</span>
        </div>
        <div class="menu-item" @click="goToSettings">
          <i class="el-icon-setting"></i>
          <span>设置</span>
        </div>
        <div class="menu-divider"></div>
        <div class="menu-item logout" @click="handleLogout">
          <i class="el-icon-switch-button"></i>
          <span>退出登录</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AppHeader from './components/AppHeader'
import MobileCharacterGrid from './components/MobileCharacterGrid'
import ChatHeader from './components/ChatHeader'
import MessageArea from './components/MessageArea'
import InputArea from './components/InputArea'
import MobileNavigation from '../components/MobileNavigation'
import VipStatusIndicator from '@/components/VipStatus/VipStatusIndicator.vue'
import { getChatRoleList } from '@/api/chat/roles'
import { textChat, audioChat } from '@/api/chat/chat'
import { getRoleChatRecords } from '@/api/chat/records'
import { getUserInfo } from '@/api/chat/profile'
import { getUserVipInfo } from '@/api/chat/vip'
import { useVipStatus } from '@/composables/useVipStatus'

export default {
  name: 'MobileChatApp',
  components: {
    AppHeader,
    MobileCharacterGrid,
    ChatHeader,
    MessageArea,
    InputArea,
    MobileNavigation,
    VipStatusIndicator
  },
  setup() {
    const vipStatus = useVipStatus()
    return {
      ...vipStatus
    }
  },
  data() {
    return {
      // 登录状态
      isLoggedIn: true,
      userInfo: {
        name: '用户',
        username: 'qiqi_user',
        avatar: ''
      },

      // 界面状态
      selectedCharacter: null,
      showSearch: false,
      showUserMenu: false,
      showChatMenu: false,
      searchQuery: '',
      searchResults: [],

      // 聊天状态
      currentMessages: [],
      isTyping: false,

      // 默认头像
      defaultAvatar: require('@/assets/images/profile.jpg'),

      // 角色数据
      characters: [],
      charactersLoading: true,

      // VIP信息
      userVipInfo: {
        isVip: false,
        vipLevel: 0,
        remainingDays: 0,
        privileges: {
          dailyChatLimit: 10,
          voiceEnabled: false,
          imageEnabled: false
        }
      }
    }
  },
  computed: {
    filteredCharacters() {
      if (!this.searchQuery) return this.characters
      return this.characters.filter(character =>
        character.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        character.description.toLowerCase().includes(this.searchQuery.toLowerCase())
      )
    }
  },
  created() {
    this.loadCharacters()
    this.loadUserInfo()
    this.loadUserVipInfo()
  },
  methods: {
    // 加载用户信息
    async loadUserInfo() {
      try {
        const response = await getUserInfo()
        if (response && response.data) {
          this.userInfo = {
            ...this.userInfo,
            ...response.data
          }
          console.log('聊天页面用户信息加载成功:', this.userInfo)
        }
      } catch (error) {
        console.error('聊天页面加载用户信息失败:', error)
        // 如果加载失败，保持默认用户信息
      }
    },

    // 加载用户VIP信息
    async loadUserVipInfo() {
      try {
        const response = await getUserVipInfo()
        if (response && response.code === 200) {
          this.userVipInfo = response.data
          console.log('用户VIP信息加载成功:', this.userVipInfo)
        }
      } catch (error) {
        console.error('加载用户VIP信息失败:', error)
        // 如果加载失败，保持默认VIP信息（普通用户）
      }
    },

    // 加载AI角色列表
    async loadCharacters() {
      try {
        this.charactersLoading = true
        const response = await getChatRoleList({
          status: 0, // 只获取正常状态的角色
          pageNum: 1,
          pageSize: 100 // 获取所有角色
        })

        if (response && response.rows) {
          this.characters = response.rows.map(role => ({
            id: role.roleId,
            name: role.roleName,
            roleKey: role.roleKey,
            description: role.roleIdentity || role.roleCategory || '智能助手',
            avatar: role.roleAvatar || this.defaultAvatar,
            online: true,
            lastMessage: '你好！有什么可以帮助你的吗？',
            lastTime: new Date(),
            voiceDuration: null,
            category: role.roleCategory,
            isCustom: role.isCustom
          }))
          console.log('角色列表加载成功:', this.characters)
        }
      } catch (error) {
        console.error('加载角色列表失败:', error)
        this.$message.error('加载角色列表失败，请稍后重试')
        // 如果API失败，使用默认角色
        this.characters = [
          {
            id: 1,
            name: '小艾',
            description: '智能助手',
            avatar: this.defaultAvatar,
            online: true,
            lastMessage: '你好！有什么可以帮助你的吗？',
            lastTime: new Date(),
            voiceDuration: null
          }
        ]
      } finally {
        this.charactersLoading = false
      }
    },
    // 导航方法
    goToLogin() {
      this.$router.push('/chat/login')
    },
    goToProfile() {
      this.showUserMenu = false
      this.$router.push('/chat/profile')
    },
    goToSettings() {
      this.showUserMenu = false
      this.$router.push('/chat/settings')
    },

    // 角色选择
    selectCharacter(character) {
      console.log('选择角色:', character.name)
      this.selectedCharacter = character
      this.loadMessages(character.id)
    },

    backToCharacterList() {
      this.selectedCharacter = null
      this.currentMessages = []
    },

    // 消息处理
    async loadMessages(characterId) {
      console.log('加载消息历史，角色ID:', characterId)
      try {
        // 加载该角色的历史对话记录
        const response = await getRoleChatRecords(characterId, {
          pageNum: 1,
          pageSize: 50 // 加载最近50条对话
        })

        if (response && response.rows && response.rows.length > 0) {
          // 转换API数据格式为组件需要的格式
          // 按照原始记录的顺序，每条记录保持用户输入和AI回复的配对关系
          const historyMessages = response.rows
            .sort((a, b) => new Date(a.chatTime) - new Date(b.chatTime)) // 先按时间正序排列原始记录
            .map(record => [
              // 用户消息
              {
                id: `${record.recordId}_user`,
                content: record.userInputContent,
                isUser: true,
                time: new Date(record.chatTime),
                isAudio: record.userInputType === 'audio'
              },
              // AI回复
              {
                id: `${record.recordId}_ai`,
                content: record.aiReplyContent,
                isUser: false,
                time: new Date(new Date(record.chatTime).getTime() + 1000), // AI回复时间稍后
                audioFile: record.aiAudioFile,
                isPlaying: false
              }
            ]).flat() // 展平后保持配对顺序，不再重新排序

          this.currentMessages = historyMessages
          console.log('加载历史消息成功:', historyMessages.length, '条')
        } else {
          // 如果没有历史记录，显示欢迎消息
          this.currentMessages = [
            {
              id: 1,
              content: `你好！我是${this.selectedCharacter.name}，很高兴认识你！有什么可以帮助你的吗？`,
              isUser: false,
              time: new Date(),
              voiceDuration: this.selectedCharacter.voiceDuration
            }
          ]
          console.log('无历史记录，显示欢迎消息')
        }
      } catch (error) {
        console.error('加载历史消息失败:', error)
        // 如果加载失败，使用默认欢迎消息
        this.currentMessages = [
          {
            id: 1,
            content: `你好！我是${this.selectedCharacter.name}，很高兴认识你！`,
            isUser: false,
            time: new Date(),
            voiceDuration: this.selectedCharacter.voiceDuration
          }
        ]
        // 可以选择是否显示错误提示
        // this.$message.warning('加载历史对话失败，将显示新的对话')
      }
    },

    async sendMessage(content) {
      console.log('发送消息:', content)

      // 检查文本聊天权限
      const permissionCheck = this.checkChatPermission()
      if (!permissionCheck.allowed) {
        this.$message.warning(permissionCheck.message)
        return
      }

      const newMessage = {
        id: Date.now(),
        content: content,
        isUser: true,
        time: new Date()
      }

      this.currentMessages.push(newMessage)
      this.isTyping = true

      try {
        // 调用真实的文本聊天API
        const response = await textChat({
          text: content,
          role: this.selectedCharacter.roleKey || this.selectedCharacter.name
        })

        console.log('API响应:', response)

        // 添加AI回复消息
        const aiReply = {
          id: Date.now() + 1,
          content: response.data.reply || '抱歉，我现在无法回复。',
          isUser: false,
          time: new Date(),
          audioFile: response.data.audioFile || null,
          isPlaying: false
        }
        this.currentMessages.push(aiReply)
      } catch (error) {
        console.error('发送消息失败:', error)
        // 错误处理 - 添加错误消息
        const errorReply = {
          id: Date.now() + 1,
          content: '抱歉，发送消息失败，请稍后重试。',
          isUser: false,
          time: new Date(),
          isError: true
        }
        this.currentMessages.push(errorReply)
        this.$message.error('发送消息失败：' + (error.message || '网络错误'))
      } finally {
        this.isTyping = false
      }
    },

    handleTyping() {
      // 处理用户输入状态
    },

    handleToggleVoice(isActive) {
      // 录音状态切换处理
      if (isActive) {
        console.log('开始录音...')
      } else {
        console.log('录音结束')
      }
    },

    async sendAudioMessage(audioBlob) {
      console.log('发送语音消息:', audioBlob)

      // 检查语音聊天权限
      const permissionCheck = this.checkVoicePermission()
      if (!permissionCheck.allowed) {
        this.$message.warning(permissionCheck.message)
        return
      }

      // 添加用户语音消息到聊天记录
      const userAudioMessage = {
        id: Date.now(),
        content: '[语音消息]',
        isUser: true,
        time: new Date(),
        isAudio: true,
        audioBlob: audioBlob
      }
      this.currentMessages.push(userAudioMessage)
      this.isTyping = true

      try {
        // 创建FormData发送文件
        const formData = new FormData()
        formData.append('audio', audioBlob, 'recording.wav')
        formData.append('role', this.selectedCharacter.roleKey || this.selectedCharacter.name)

        console.log('发送音频文件，大小:', audioBlob.size)
        console.log('FormData内容:', formData.get('audio'), formData.get('role'))

        // 调用语音聊天API
        const response = await audioChat(formData)

        console.log('语音API响应:', response)

        // 添加AI回复消息
        const aiReply = {
          id: Date.now() + 1,
          content: response.data.reply || '抱歉，我现在无法回复。',
          isUser: false,
          time: new Date(),
          audioFile: response.data.audioFile || null,
          isPlaying: false
        }
        this.currentMessages.push(aiReply)
      } catch (error) {
        console.error('发送语音消息失败:', error)
        // 错误处理
        const errorReply = {
          id: Date.now() + 1,
          content: '抱歉，语音消息发送失败，请稍后重试。',
          isUser: false,
          time: new Date(),
          isError: true
        }
        this.currentMessages.push(errorReply)
        this.$message.error('发送语音消息失败：' + (error.message || '网络错误'))
      } finally {
        this.isTyping = false
      }
    },


    // 搜索功能
    handleSearch() {
      this.searchResults = this.filteredCharacters.slice(0, 5)
    },

    selectCharacterFromSearch(character) {
      this.selectCharacter(character)
      this.showSearch = false
      this.searchQuery = ''
    },

    // 用户操作
    handleLogout() {
      this.$confirm('确认退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.isLoggedIn = false
        localStorage.removeItem('chat_token')
        localStorage.removeItem('chat_isLoggedIn')
        this.$message.success('退出登录成功')
        this.$router.push('/chat/login')
      }).catch(() => {
        this.showUserMenu = false
      })
    },

    // 检查文本聊天权限
    checkChatPermission() {
      // TODO: 这里应该检查用户今日的实际聊天次数
      // 目前只检查会员等级权限，不检查具体使用次数
      const dailyLimit = this.userVipInfo.privileges.dailyChatLimit

      if (dailyLimit === 0) {
        return {
          allowed: false,
          message: '您的会员等级暂不支持文本聊天功能，请升级会员'
        }
      }

      if (dailyLimit !== -1) {
        // 有限制的情况下，这里应该检查今日使用次数
        // 目前先允许使用，后续可以添加使用次数统计
      }

      return { allowed: true }
    },

    // 检查语音聊天权限
    checkVoicePermission() {
      if (!this.userVipInfo.privileges.voiceEnabled) {
        return {
          allowed: false,
          message: '您的会员等级暂不支持语音聊天功能，请升级会员后使用'
        }
      }

      // 检查聊天次数限制（语音聊天也算在聊天次数内）
      const dailyLimit = this.userVipInfo.privileges.dailyChatLimit
      if (dailyLimit === 0) {
        return {
          allowed: false,
          message: '您的会员等级暂不支持聊天功能，请升级会员'
        }
      }

      return { allowed: true }
    },

    // VIP相关方法
    handleVipUpgrade() {
      console.log('跳转到会员升级页面')
      this.$router.push('/chat/membership')
    },

    handlePermissionDenied(event) {
      console.log('权限不足:', event)
      this.$message.warning(event.message || '权限不足')
    },

    // 更新后的权限检查方法（使用新的VIP状态系统）
    async checkFeaturePermissionNew(feature) {
      try {
        const permission = await this.checkFeaturePermission(feature)
        if (!permission.hasPermission) {
          this.$message.warning(permission.message)
          return false
        }

        // 检查今日使用次数
        if (this.isFeatureLimited(feature)) {
          this.$message.warning(`今日${feature}使用次数已达上限`)
          return false
        }

        return true
      } catch (error) {
        console.error('检查功能权限失败:', error)
        this.$message.error('权限检查失败')
        return false
      }
    },

    // 发送消息前的权限检查（更新版本）
    async sendMessageWithPermissionCheck(content) {
      const canUse = await this.checkFeaturePermissionNew('chat')
      if (canUse) {
        return this.sendMessage(content)
      }
    },

    // 语音功能权限检查（更新版本）
    async handleToggleVoiceWithPermissionCheck() {
      const canUse = await this.checkFeaturePermissionNew('voice')
      if (canUse) {
        this.handleToggleVoice()
      }
    },

    // 刷新VIP状态
    async refreshVipInfo() {
      try {
        await this.forceRefresh()
        this.$message.success('VIP状态已更新')
      } catch (error) {
        console.error('刷新VIP状态失败:', error)
        this.$message.error('刷新失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.mobile-chat-app {
  height: 100vh;
  background: #f8f9fa;
  overflow: hidden;
}

.login-redirect {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;

  .login-prompt {
    text-align: center;

    i {
      font-size: 48px;
      margin-bottom: 16px;
      opacity: 0.8;
    }

    p {
      font-size: 18px;
      margin-bottom: 24px;
    }
  }
}

.character-list-page {
  height: calc(100vh - 80px);
  display: flex;
  flex-direction: column;

  .character-list-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
}

.loading-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;

  i {
    font-size: 32px;
    margin-bottom: 16px;
    animation: rotating 1s linear infinite;
  }

  p {
    font-size: 16px;
    opacity: 0.8;
    margin: 0;
  }
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.chat-page {
  height: calc(100vh - 80px); /* 减去底部导航栏高度 */
  display: flex;
  flex-direction: column;
  background: white;

  /* MessageArea 需要减去ChatHeader、VIP状态栏和InputArea的高度 */
  :deep(.messages-area) {
    height: calc(100vh - 80px - 70px - 50px - 100px); /* 底部导航 + 聊天头部 + VIP状态栏 + 输入区域 */
    max-height: calc(100vh - 80px - 70px - 50px - 100px);
    overflow-y: auto;
  }
}

// VIP状态栏样式
.vip-status-bar {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #dee2e6;
  padding: 8px 16px;
  min-height: 50px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  z-index: 100;

  :deep(.vip-status-indicator) {
    width: 100%;

    .status-main {
      padding: 6px 12px;

      .vip-badge .el-tag {
        font-size: 12px;
        padding: 4px 8px;
      }

      .expire-warning .el-tag {
        font-size: 11px;
        padding: 2px 6px;
      }
    }
  }
}

// 弹窗样式
.search-dialog {
  .search-results {
    max-height: 300px;
    overflow-y: auto;
    margin-top: 16px;

    .search-result-item {
      display: flex;
      align-items: center;
      padding: 8px 0;
      cursor: pointer;
      border-bottom: 1px solid #f0f0f0;

      &:hover {
        background: #f8f9fa;
      }

      img {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        margin-right: 12px;
      }

      .character-name {
        font-size: 14px;
        color: #333;
      }
    }
  }
}

.user-menu-dialog {
  .user-menu-content {
    .menu-item {
      display: flex;
      align-items: center;
      padding: 12px 16px;
      cursor: pointer;
      transition: background-color 0.2s ease;

      &:hover {
        background: #f8f9fa;
      }

      &.logout {
        color: #ff4757;

        &:hover {
          background: rgba(255, 71, 87, 0.1);
        }
      }

      i {
        margin-right: 8px;
        font-size: 16px;
      }

      span {
        font-size: 14px;
      }
    }

    .menu-divider {
      height: 1px;
      background: #f0f0f0;
      margin: 8px 0;
    }
  }
}
</style>