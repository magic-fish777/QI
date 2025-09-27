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
          {{ message.content }}
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