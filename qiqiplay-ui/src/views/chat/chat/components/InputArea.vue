<template>
  <div class="input-area">
    <!-- 输入框区域 - 一行布局 -->
    <div class="input-wrapper">
      <!-- 语音按钮 -->
      <div class="voice-btn" :class="{ active: isVoiceMode }" @click="toggleVoiceMode">
        <i class="el-icon-microphone"></i>
      </div>

      <!-- 输入框 -->
      <textarea
        v-model="inputMessage"
        placeholder="输入消息..."
        @keyup.enter="handleSendMessage"
        @input="handleTyping"
        rows="1"
        class="message-input"
        ref="messageInput"
      ></textarea>

      <!-- 发送按钮 -->
      <div class="send-btn" @click="sendMessage" :class="{ disabled: !canSend }">
        <i class="el-icon-position"></i>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'InputArea',
  props: {
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      inputMessage: '',
      isVoiceMode: false
    }
  },
  computed: {
    canSend() {
      return this.inputMessage.trim().length > 0 && !this.disabled
    }
  },
  emits: ['send-message', 'typing', 'toggle-voice'],
  methods: {
    sendMessage() {
      if (!this.canSend) return

      const message = this.inputMessage.trim()
      this.$emit('send-message', message)
      this.inputMessage = ''
      this.adjustTextareaHeight()
    },

    handleSendMessage(event) {
      if (event.shiftKey) return
      event.preventDefault()
      this.sendMessage()
    },

    handleTyping() {
      this.$emit('typing')
      this.adjustTextareaHeight()
    },

    toggleVoiceMode() {
      this.isVoiceMode = !this.isVoiceMode
      this.$emit('toggle-voice', this.isVoiceMode)
    },

    adjustTextareaHeight() {
      this.$nextTick(() => {
        const textarea = this.$refs.messageInput
        if (textarea) {
          textarea.style.height = 'auto'
          const maxHeight = 80 // 最大高度
          const scrollHeight = Math.min(textarea.scrollHeight, maxHeight)
          textarea.style.height = scrollHeight + 'px'
        }
      })
    }
  },
  mounted() {
    this.adjustTextareaHeight()
  }
}
</script>

<style lang="scss" scoped>
.input-area {
  position: fixed;
  bottom: 80px; /* 底部导航栏高度 */
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: white;
  border-top: 1px solid #f0f0f0;
  z-index: 999;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);

  .input-wrapper {
    display: flex;
    align-items: flex-end;
    background: #f8f9fa;
    border-radius: 25px;
    padding: 8px;
    gap: 8px; /* 元素间距 */

    .voice-btn {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: white;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s ease;
      flex-shrink: 0; /* 防止压缩 */
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

      &:hover {
        background: #f0f0f0;
      }

      &.active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

        i {
          color: white;
        }
      }

      i {
        font-size: 18px;
        color: #666;
        transition: color 0.3s ease;
      }
    }

    .message-input {
      flex: 1;
      border: none;
      background: transparent;
      resize: none;
      font-size: 14px;
      padding: 8px 12px;
      max-height: 80px;
      outline: none;
      font-family: inherit;
      line-height: 1.4;
      min-height: 20px; /* 最小高度 */
    }

    .send-btn {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s ease;
      flex-shrink: 0; /* 防止压缩 */

      &.disabled {
        background: #ccc;
        cursor: not-allowed;
      }

      &:hover:not(.disabled) {
        transform: scale(1.05);
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
      }

      i {
        color: white;
        font-size: 16px;
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .input-area {
    padding: 8px 12px;
  }
}

/* 确保在小屏幕设备上的表现 */
@media (max-height: 600px) {
  .input-area {
    .input-tools {
      margin-bottom: 4px;
    }
  }
}
</style>