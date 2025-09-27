<template>
  <div class="mobile-chat-app">
    <!-- 角色列表页面 -->
    <div v-if="!selectedCharacter" class="character-list">
      <!-- 头部 -->
      <div class="header">
        <div class="app-title">QIQI PLAY</div>
        <div class="search-bar" @click="showSearch = true">
          <i class="el-icon-search"></i>
          <span>搜索角色</span>
        </div>
      </div>

      <!-- 角色卡片列表 -->
      <div class="character-cards">
        <div
          v-for="character in characters"
          :key="character.id"
          class="character-card"
          @click="selectCharacter(character)"
        >
          <div class="character-avatar">
            <img :src="character.avatar" :alt="character.name">
            <div class="online-status" :class="{ online: character.online }"></div>
          </div>
          <div class="character-info">
            <div class="character-name">{{ character.name }}</div>
            <div class="character-desc">{{ character.description }}</div>
            <div class="last-message" v-if="character.lastMessage">
              {{ character.lastMessage }}
            </div>
          </div>
          <div class="character-meta">
            <div class="voice-duration" v-if="character.voiceDuration">
              <i class="el-icon-microphone"></i>
              {{ character.voiceDuration }}"
            </div>
            <div class="message-time" v-if="character.lastTime">
              {{ formatTime(character.lastTime) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 对话页面 -->
    <div v-else class="chat-page">
      <!-- 聊天头部 -->
      <div class="chat-header">
        <div class="back-btn" @click="backToList">
          <i class="el-icon-arrow-left"></i>
        </div>
        <div class="character-info">
          <div class="character-avatar-small">
            <img :src="selectedCharacter.avatar" :alt="selectedCharacter.name">
            <div class="online-indicator" :class="{ online: selectedCharacter.online }"></div>
          </div>
          <div class="character-details">
            <div class="character-name">{{ selectedCharacter.name }}</div>
            <div class="online-text">{{ selectedCharacter.online ? '在线' : '离线' }}</div>
          </div>
        </div>
        <div class="chat-actions">
          <i class="el-icon-more"></i>
        </div>
      </div>

      <!-- 消息区域 -->
      <div class="messages-container" ref="messagesContainer">
        <div
          v-for="message in currentMessages"
          :key="message.id"
          class="message-item"
          :class="{ 'user-message': message.isUser }"
        >
          <div class="message-avatar">
            <img
              :src="message.isUser ? userAvatar : selectedCharacter.avatar"
              :alt="message.isUser ? '我' : selectedCharacter.name"
            >
          </div>
          <div class="message-content">
            <div class="message-bubble">
              {{ message.content }}
            </div>
            <div class="message-time">
              {{ formatMessageTime(message.time) }}
            </div>
            <div v-if="message.voiceDuration" class="voice-duration">
              <i class="el-icon-microphone"></i>
              {{ message.voiceDuration }}"
            </div>
          </div>
        </div>

        <!-- 输入状态提示 -->
        <div v-if="isTyping" class="typing-indicator">
          <div class="typing-avatar">
            <img :src="selectedCharacter.avatar" :alt="selectedCharacter.name">
          </div>
          <div class="typing-bubble">
            <div class="typing-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <div class="input-tools">
          <div class="voice-btn" @click="toggleVoiceInput" :class="{ active: isVoiceMode }">
            <i class="el-icon-microphone"></i>
          </div>
        </div>
        <div class="input-wrapper">
          <textarea
            v-model="inputMessage"
            placeholder="输入消息..."
            @keyup.enter="handleSendMessage"
            @input="handleTyping"
            rows="1"
            class="message-input"
          ></textarea>
          <div class="send-btn" @click="sendMessage" :class="{ disabled: !canSend }">
            <i class="el-icon-position"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索遮罩层 -->
    <div v-if="showSearch" class="search-overlay" @click="showSearch = false">
      <div class="search-content" @click.stop>
        <div class="search-input-wrapper">
          <input v-model="searchQuery" placeholder="搜索角色..." class="search-input" />
          <button @click="showSearch = false" class="search-close">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MobileChatInterface',
  data() {
    return {
      selectedCharacter: null,
      inputMessage: '',
      searchQuery: '',
      showSearch: false,
      isVoiceMode: false,
      isTyping: false,
      userAvatar: require('@/assets/images/profile.jpg'),
      characters: [
        {
          id: 1,
          name: '哈利波特',
          description: '小说/电影角色',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 9,
          lastMessage: '你好，有什么可以帮助你的吗？',
          lastTime: new Date(Date.now() - 300000)
        },
        {
          id: 2,
          name: '苏格拉底',
          description: '古希腊哲学家',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 3,
          lastMessage: '智慧始于承认自己的无知',
          lastTime: new Date(Date.now() - 600000)
        },
        {
          id: 3,
          name: '初音未来',
          description: '虚拟角色',
          avatar: require('@/assets/images/profile.jpg'),
          online: false,
          voiceDuration: 7,
          lastMessage: '♪ 音乐是灵魂的语言',
          lastTime: new Date(Date.now() - 1800000)
        },
        {
          id: 4,
          name: '易烊千玺',
          description: '电影明星',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 11,
          lastMessage: '我在拍戏，祝你生活顺利~',
          lastTime: new Date(Date.now() - 3600000)
        },
        {
          id: 5,
          name: '迪丽热巴',
          description: '电影明星',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 11,
          lastMessage: '今天心情很不错呢！',
          lastTime: new Date(Date.now() - 7200000)
        },
        {
          id: 6,
          name: '刘晓燕',
          description: '英语老师',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 11,
          lastMessage: '你好同学，欢迎咨询...',
          lastTime: new Date(Date.now() - 10800000)
        }
      ],
      currentMessages: []
    }
  },
  computed: {
    canSend() {
      return this.inputMessage.trim().length > 0
    }
  },
  methods: {
    selectCharacter(character) {
      this.selectedCharacter = character
      this.loadMessages(character.id)
    },

    backToList() {
      this.selectedCharacter = null
      this.inputMessage = ''
      this.currentMessages = []
    },

    loadMessages(characterId) {
      // 模拟加载对话历史
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
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    sendMessage() {
      if (!this.canSend) return

      const newMessage = {
        id: Date.now(),
        content: this.inputMessage,
        isUser: true,
        time: new Date()
      }

      this.currentMessages.push(newMessage)
      this.inputMessage = ''

      // 模拟AI回复
      this.isTyping = true
      setTimeout(() => {
        this.isTyping = false
        const aiReply = {
          id: Date.now() + 1,
          content: this.getRandomReply(),
          isUser: false,
          time: new Date(),
          voiceDuration: Math.floor(Math.random() * 10) + 3
        }
        this.currentMessages.push(aiReply)
        this.scrollToBottom()
      }, 2000)

      this.scrollToBottom()
    },

    handleSendMessage(event) {
      if (!event.shiftKey) {
        event.preventDefault()
        this.sendMessage()
      }
    },

    handleTyping() {
      // 可以添加输入状态处理
    },

    toggleVoiceInput() {
      this.isVoiceMode = !this.isVoiceMode
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    formatTime(time) {
      if (!time) return ''
      const now = new Date()
      const diff = now - time
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (days > 0) return `${days}天前`
      if (hours > 0) return `${hours}小时前`
      if (minutes > 0) return `${minutes}分钟前`
      return '刚刚'
    },

    formatMessageTime(time) {
      if (!time) return ''
      const hours = time.getHours().toString().padStart(2, '0')
      const minutes = time.getMinutes().toString().padStart(2, '0')
      return `${hours}:${minutes}`
    },

    getRandomReply() {
      const replies = [
        '这是一个很有趣的问题！',
        '让我思考一下...',
        '你说得很有道理。',
        '我理解你的想法。',
        '这确实值得深入探讨。'
      ]
      return replies[Math.floor(Math.random() * replies.length)]
    }
  }
}
</script>

<style lang="scss" scoped>
.mobile-chat-app {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  position: relative;
}

// 角色列表页面
.character-list {
  flex: 1;
  display: flex;
  flex-direction: column;

  .header {
    padding: 20px 16px 16px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);

    .app-title {
      font-size: 28px;
      font-weight: bold;
      color: white;
      text-align: center;
      margin-bottom: 16px;
      letter-spacing: 2px;
    }

    .search-bar {
      display: flex;
      align-items: center;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 20px;
      padding: 12px 16px;
      cursor: pointer;

      i {
        color: #999;
        margin-right: 8px;
      }

      span {
        color: #999;
        flex: 1;
      }
    }
  }

  .character-cards {
    flex: 1;
    padding: 8px;
    overflow-y: auto;
  }

  .character-card {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.95);
    margin-bottom: 8px;
    padding: 16px;
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.2s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    &:active {
      transform: scale(0.98);
      background: rgba(255, 255, 255, 0.9);
    }

    .character-avatar {
      position: relative;
      margin-right: 12px;

      img {
        width: 56px;
        height: 56px;
        border-radius: 50%;
        object-fit: cover;
      }

      .online-status {
        position: absolute;
        bottom: 2px;
        right: 2px;
        width: 14px;
        height: 14px;
        border-radius: 50%;
        background: #ddd;
        border: 2px solid white;

        &.online {
          background: #4CAF50;
        }
      }
    }

    .character-info {
      flex: 1;
      min-width: 0;

      .character-name {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 2px;
      }

      .character-desc {
        font-size: 12px;
        color: #666;
        margin-bottom: 4px;
      }

      .last-message {
        font-size: 14px;
        color: #888;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .character-meta {
      text-align: right;

      .voice-duration {
        display: flex;
        align-items: center;
        justify-content: flex-end;
        font-size: 12px;
        color: #4CAF50;
        margin-bottom: 4px;

        i {
          margin-right: 4px;
        }
      }

      .message-time {
        font-size: 11px;
        color: #999;
      }
    }
  }
}

// 对话页面
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;

  .chat-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;

    .back-btn {
      cursor: pointer;
      padding: 8px;
      margin-right: 12px;

      i {
        font-size: 20px;
      }
    }

    .character-info {
      flex: 1;
      display: flex;
      align-items: center;

      .character-avatar-small {
        position: relative;
        margin-right: 12px;

        img {
          width: 36px;
          height: 36px;
          border-radius: 50%;
          object-fit: cover;
        }

        .online-indicator {
          position: absolute;
          bottom: 0;
          right: 0;
          width: 10px;
          height: 10px;
          border-radius: 50%;
          background: #ddd;
          border: 2px solid white;

          &.online {
            background: #4CAF50;
          }
        }
      }

      .character-details {
        .character-name {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 2px;
        }

        .online-text {
          font-size: 12px;
          opacity: 0.8;
        }
      }
    }

    .chat-actions {
      cursor: pointer;
      padding: 8px;

      i {
        font-size: 18px;
      }
    }
  }

  .messages-container {
    flex: 1;
    padding: 16px;
    overflow-y: auto;
    background: #f8f9fa;

    .message-item {
      display: flex;
      margin-bottom: 16px;
      align-items: flex-start;

      &.user-message {
        flex-direction: row-reverse;

        .message-content {
          align-items: flex-end;

          .message-bubble {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
          }
        }
      }

      .message-avatar {
        margin: 0 8px;

        img {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          object-fit: cover;
        }
      }

      .message-content {
        display: flex;
        flex-direction: column;
        max-width: 70%;

        .message-bubble {
          padding: 12px 16px;
          border-radius: 18px;
          background: white;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
          word-wrap: break-word;
          line-height: 1.4;
        }

        .message-time {
          font-size: 10px;
          color: #999;
          margin-top: 4px;
          padding: 0 4px;
        }

        .voice-duration {
          font-size: 10px;
          color: #4CAF50;
          margin-top: 2px;
          display: flex;
          align-items: center;

          i {
            margin-right: 2px;
          }
        }
      }
    }

    .typing-indicator {
      display: flex;
      align-items: flex-start;
      margin-bottom: 16px;

      .typing-avatar {
        margin: 0 8px;

        img {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          object-fit: cover;
        }
      }

      .typing-bubble {
        background: white;
        border-radius: 18px;
        padding: 12px 16px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

        .typing-dots {
          display: flex;

          span {
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background: #999;
            margin: 0 2px;
            animation: typing 1.4s infinite;

            &:nth-child(2) {
              animation-delay: 0.2s;
            }

            &:nth-child(3) {
              animation-delay: 0.4s;
            }
          }
        }
      }
    }
  }

  .input-area {
    padding: 12px 16px;
    background: white;
    border-top: 1px solid #eee;
    display: flex;
    align-items: flex-end;

    .input-tools {
      margin-right: 12px;

      .voice-btn {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: #f0f0f0;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.2s ease;

        &.active {
          background: #4CAF50;
          color: white;
        }

        i {
          font-size: 18px;
        }
      }
    }

    .input-wrapper {
      flex: 1;
      display: flex;
      align-items: flex-end;
      background: #f8f9fa;
      border-radius: 20px;
      padding: 8px 12px;

      .message-input {
        flex: 1;
        border: none;
        background: transparent;
        resize: none;
        outline: none;
        font-size: 16px;
        line-height: 1.4;
        max-height: 100px;
        min-height: 24px;
      }

      .send-btn {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-left: 8px;

        &.disabled {
          opacity: 0.5;
          cursor: not-allowed;
        }

        i {
          font-size: 14px;
        }
      }
    }
  }
}

// 搜索遮罩层
.search-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 80px;

  .search-content {
    width: 90%;
    max-width: 400px;
    background: white;
    border-radius: 12px;
    padding: 20px;

    .search-input-wrapper {
      display: flex;
      align-items: center;

      .search-input {
        flex: 1;
        padding: 12px;
        border: 1px solid #ddd;
        border-radius: 8px;
        font-size: 16px;
        outline: none;
        margin-right: 12px;

        &:focus {
          border-color: #667eea;
        }
      }

      .search-close {
        padding: 12px 16px;
        background: #667eea;
        color: white;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        font-size: 14px;
      }
    }
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

// 滚动条样式
::-webkit-scrollbar {
  width: 4px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 2px;
}
</style>