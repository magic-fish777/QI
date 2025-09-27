<template>
  <div class="messages-area" ref="messagesArea">
    <!-- 调试信息（开发阶段） -->
    <div v-if="messages.length === 0" class="debug-info">
      当前消息数量：{{ messages.length }}
      <br>
      选中角色：{{ selectedCharacter ? selectedCharacter.name : '无' }}
    </div>

    <!-- 消息列表 -->
    <div
      v-for="message in messages"
      :key="message.id"
      class="message-item"
      :class="{ 'user-message': message.isUser }"
    >
      <div class="message-avatar">
        <img
          :src="message.isUser ? userAvatar : selectedCharacter.avatar"
          :alt="message.isUser ? '用户' : selectedCharacter.name"
        >
      </div>
      <div class="message-content">
        <div class="message-bubble">
          <span v-if="!message.isAudio">{{ message.content }}</span>
          <!-- 用户语音消息显示 -->
          <div v-if="message.isAudio && message.isUser" class="voice-message">
            <i class="el-icon-microphone"></i>
            <span>语音消息</span>
            <el-button
              type="text"
              size="mini"
              :icon="message.isPlaying ? 'el-icon-video-pause' : 'el-icon-video-play'"
              @click="toggleUserAudio(message)"
              class="audio-play-btn"
            >
              {{ message.isPlaying ? '暂停' : '播放' }}
            </el-button>
          </div>
          <!-- AI回复音频播放按钮 -->
          <div v-if="message.audioFile && !message.isUser" class="audio-controls">
            <el-button
              type="text"
              size="mini"
              :icon="message.isPlaying ? 'el-icon-video-pause' : 'el-icon-video-play'"
              @click="toggleAudio(message)"
              class="audio-play-btn"
            >
              {{ message.isPlaying ? '暂停' : '播放' }}
            </el-button>
          </div>
        </div>
        <div class="message-meta">
          <span class="message-time">{{ formatMessageTime(message.time) }}</span>
          <span v-if="message.voiceDuration" class="voice-duration">
            <i class="el-icon-microphone"></i>
            {{ message.voiceDuration }}"
          </span>
        </div>
      </div>
    </div>

    <!-- 输入状态指示器 -->
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
</template>

<script>
export default {
  name: 'MessageArea',
  props: {
    messages: {
      type: Array,
      default: () => []
    },
    selectedCharacter: {
      type: Object,
      default: null
    },
    userAvatar: {
      type: String,
      default: ''
    },
    isTyping: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      currentAudio: null, // 当前播放的音频对象
      playingMessageId: null // 当前播放中的消息ID
    }
  },
  methods: {
    formatMessageTime(time) {
      return new Date(time).toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesArea
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },
    toggleAudio(message) {
      // 如果当前有音频在播放，先停止
      if (this.currentAudio && !this.currentAudio.paused) {
        this.currentAudio.pause()
        this.updatePlayingState(this.playingMessageId, false)
      }

      // 如果点击的是同一个消息，则停止播放
      if (this.playingMessageId === message.id) {
        this.playingMessageId = null
        this.currentAudio = null
        return
      }

      // 播放新的音频
      this.playAudio(message)
    },
    playAudio(message) {
      try {
        // 创建新的音频对象
        this.currentAudio = new Audio(message.audioFile)
        this.playingMessageId = message.id

        // 设置播放状态
        this.updatePlayingState(message.id, true)

        // 播放音频
        this.currentAudio.play()

        // 监听播放结束事件
        this.currentAudio.addEventListener('ended', () => {
          this.updatePlayingState(message.id, false)
          this.playingMessageId = null
          this.currentAudio = null
        })

        // 监听播放错误事件
        this.currentAudio.addEventListener('error', (e) => {
          console.error('音频播放失败:', e)
          this.$message.error('音频播放失败')
          this.updatePlayingState(message.id, false)
          this.playingMessageId = null
          this.currentAudio = null
        })

      } catch (error) {
        console.error('音频播放异常:', error)
        this.$message.error('音频播放失败：' + error.message)
        this.updatePlayingState(message.id, false)
      }
    },
    updatePlayingState(messageId, isPlaying) {
      // 更新消息的播放状态
      const message = this.messages.find(msg => msg.id === messageId)
      if (message) {
        this.$set(message, 'isPlaying', isPlaying)
      }
    },
    toggleUserAudio(message) {
      // 如果当前有音频在播放，先停止
      if (this.currentAudio && !this.currentAudio.paused) {
        this.currentAudio.pause()
        this.updatePlayingState(this.playingMessageId, false)
      }

      // 如果点击的是同一个消息，则停止播放
      if (this.playingMessageId === message.id) {
        this.playingMessageId = null
        this.currentAudio = null
        return
      }

      // 播放用户的语音消息
      this.playUserAudio(message)
    },
    playUserAudio(message) {
      try {
        // 将Blob转换为可播放的URL
        const audioUrl = URL.createObjectURL(message.audioBlob)

        // 创建新的音频对象
        this.currentAudio = new Audio(audioUrl)
        this.playingMessageId = message.id

        // 设置播放状态
        this.updatePlayingState(message.id, true)

        // 播放音频
        this.currentAudio.play()

        // 监听播放结束事件
        this.currentAudio.addEventListener('ended', () => {
          this.updatePlayingState(message.id, false)
          this.playingMessageId = null
          this.currentAudio = null
          // 释放URL对象
          URL.revokeObjectURL(audioUrl)
        })

        // 监听播放错误事件
        this.currentAudio.addEventListener('error', (e) => {
          console.error('用户语音播放失败:', e)
          this.$message.error('语音播放失败')
          this.updatePlayingState(message.id, false)
          this.playingMessageId = null
          this.currentAudio = null
          // 释放URL对象
          URL.revokeObjectURL(audioUrl)
        })

      } catch (error) {
        console.error('用户语音播放异常:', error)
        this.$message.error('语音播放失败：' + error.message)
        this.updatePlayingState(message.id, false)
      }
    }
  },
  beforeDestroy() {
    // 组件销毁时停止音频播放
    if (this.currentAudio) {
      this.currentAudio.pause()
      this.currentAudio = null
    }
  },
  watch: {
    messages: {
      handler() {
        this.scrollToBottom()
      },
      deep: true
    },
    isTyping() {
      this.scrollToBottom()
    }
  }
}
</script>

<style lang="scss" scoped>
.messages-area {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f8f9fa;

  .debug-info {
    padding: 20px;
    text-align: center;
    color: #666;
    background: #fff;
    border-radius: 8px;
    margin-bottom: 16px;
  }

  .message-item {
    display: flex;
    margin-bottom: 16px;

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
      width: 36px;
      height: 36px;
      border-radius: 50%;
      overflow: hidden;
      margin: 0 8px;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .message-content {
      flex: 1;
      display: flex;
      flex-direction: column;

      .message-bubble {
        background: white;
        padding: 12px 16px;
        border-radius: 18px;
        max-width: 80%;
        word-wrap: break-word;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

        .audio-controls {
          margin-top: 8px;
          padding-top: 8px;
          border-top: 1px solid #f0f0f0;

          .audio-play-btn {
            color: #667eea;
            font-size: 12px;
            padding: 4px 8px;

            &:hover {
              color: #764ba2;
              background: rgba(102, 126, 234, 0.1);
            }
          }
        }
      }

      .message-meta {
        font-size: 11px;
        color: #999;
        margin-top: 4px;
        display: flex;
        align-items: center;

        .voice-duration {
          margin-left: 8px;

          i {
            margin-right: 2px;
          }
        }
      }
    }
  }

  .typing-indicator {
    display: flex;
    margin-bottom: 16px;

    .typing-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      overflow: hidden;
      margin-right: 8px;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .typing-bubble {
      background: white;
      padding: 12px 16px;
      border-radius: 18px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

      .typing-dots {
        display: flex;
        align-items: center;

        span {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: #ccc;
          margin: 0 2px;
          animation: typing 1.5s infinite;

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

@keyframes typing {
  0%, 60%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  30% {
    opacity: 1;
    transform: scale(1.2);
  }
}
</style>