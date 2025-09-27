<template>
  <div class="input-area">
    <!-- 工具栏 -->
    <div class="input-tools">
      <el-button
        type="text"
        icon="el-icon-microphone"
        :class="{ active: isVoiceMode }"
        @click="toggleVoiceMode"
      ></el-button>
    </div>

    <!-- 输入框 -->
    <div class="input-wrapper">
      <textarea
        v-model="inputMessage"
        placeholder="输入消息..."
        @keyup.enter="handleSendMessage"
        @input="handleTyping"
        rows="1"
        class="message-input"
        ref="messageInput"
      ></textarea>
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
  padding: 12px 16px;
  background: white;
  border-top: 1px solid #f0f0f0;

  .input-tools {
    display: flex;
    margin-bottom: 8px;

    .el-button {
      padding: 8px;
      margin-right: 8px;

      &.active {
        color: #667eea;
      }
    }
  }

  .input-wrapper {
    display: flex;
    align-items: flex-end;
    background: #f8f9fa;
    border-radius: 20px;
    padding: 8px;

    .message-input {
      flex: 1;
      border: none;
      background: transparent;
      resize: none;
      font-size: 14px;
      padding: 6px 12px;
      max-height: 80px;
      outline: none;
      font-family: inherit;
      line-height: 1.4;
    }

    .send-btn {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      margin-left: 8px;
      transition: all 0.3s ease;

      &.disabled {
        background: #ccc;
        cursor: not-allowed;
      }

      i {
        color: white;
        font-size: 16px;
      }
    }
  }
}
</style>