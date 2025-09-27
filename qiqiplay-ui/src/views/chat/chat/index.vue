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
import { getChatRoleList } from '@/api/chat/roles'
import { textChat, audioChat } from '@/api/chat/chat'

export default {
  name: 'MobileChatApp',
  components: {
    AppHeader,
    MobileCharacterGrid,
    ChatHeader,
    MessageArea,
    InputArea,
    MobileNavigation
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
      charactersLoading: true
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
  },
  methods: {
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
    loadMessages(characterId) {
      console.log('加载消息历史，角色ID:', characterId)
      const mockMessages = [
        {
          id: 1,
          content: '你好！',
          isUser: true,
          time: new Date(Date.now() - 600000)
        },
        {
          id: 2,
          content: `你好！我是${this.selectedCharacter.name}，很高兴认识你！`,
          isUser: false,
          time: new Date(Date.now() - 590000),
          voiceDuration: this.selectedCharacter.voiceDuration
        }
      ]
      this.currentMessages = mockMessages
      console.log('设置消息数据:', mockMessages)
    },

    async sendMessage(content) {
      console.log('发送消息:', content)
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
          role: this.selectedCharacter.name
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
        formData.append('role', this.selectedCharacter.name)

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

  /* MessageArea 需要减去ChatHeader和InputArea的高度 */
  :deep(.messages-area) {
    height: calc(100vh - 80px - 70px - 100px); /* 底部导航 + 聊天头部 + 输入区域 */
    max-height: calc(100vh - 80px - 70px - 100px);
    overflow-y: auto;
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