<template>
  <div class="input-area">
    <!-- 输入框区域 - 一行布局 -->
    <div class="input-wrapper">
      <!-- 语音按钮 -->
      <div class="voice-btn" :class="{ active: isVoiceMode, recording: isRecording }" @click="toggleVoiceMode">
        <i :class="isRecording ? 'el-icon-video-pause' : 'el-icon-microphone'"></i>
        <span v-if="isRecording" class="recording-time">{{ recordingTime }}s</span>
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
      isVoiceMode: false,
      isRecording: false,
      mediaRecorder: null,
      audioChunks: [],
      recordingTimer: null,
      recordingTime: 0
    }
  },
  computed: {
    canSend() {
      return this.inputMessage.trim().length > 0 && !this.disabled
    }
  },
  emits: ['send-message', 'typing', 'toggle-voice', 'send-audio'],
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

    async toggleVoiceMode() {
      if (!this.isRecording) {
        // 开始录音
        await this.startRecording()
      } else {
        // 停止录音
        this.stopRecording()
      }
    },

    async startRecording() {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({
          audio: {
            sampleRate: 16000,
            channelCount: 1,
            echoCancellation: true,
            noiseSuppression: true
          }
        })

        // 检查浏览器支持的音频格式
        const options = {}
        if (MediaRecorder.isTypeSupported('audio/webm;codecs=opus')) {
          options.mimeType = 'audio/webm;codecs=opus'
        } else if (MediaRecorder.isTypeSupported('audio/mp4')) {
          options.mimeType = 'audio/mp4'
        } else if (MediaRecorder.isTypeSupported('audio/webm')) {
          options.mimeType = 'audio/webm'
        }

        this.mediaRecorder = new MediaRecorder(stream, options)
        this.audioChunks = []
        this.recordingTime = 0

        this.mediaRecorder.ondataavailable = (event) => {
          if (event.data.size > 0) {
            this.audioChunks.push(event.data)
          }
        }

        this.mediaRecorder.onstop = () => {
          this.handleRecordingComplete()
          stream.getTracks().forEach(track => track.stop())
        }

        this.mediaRecorder.start()
        this.isRecording = true
        this.isVoiceMode = true

        // 开始计时
        this.recordingTimer = setInterval(() => {
          this.recordingTime++
          if (this.recordingTime >= 60) { // 最大录音60秒
            this.stopRecording()
          }
        }, 1000)

        this.$emit('toggle-voice', true)
        console.log('开始录音')
      } catch (error) {
        console.error('录音失败:', error)
        this.$message.error('无法访问麦克风，请检查权限设置')
      }
    },

    stopRecording() {
      if (this.mediaRecorder && this.isRecording) {
        this.mediaRecorder.stop()
        this.isRecording = false
        this.isVoiceMode = false

        if (this.recordingTimer) {
          clearInterval(this.recordingTimer)
          this.recordingTimer = null
        }

        this.$emit('toggle-voice', false)
        console.log('停止录音')
      }
    },

    async handleRecordingComplete() {
      if (this.audioChunks.length === 0) {
        this.$message.warning('录音数据为空')
        return
      }

      try {
        // 创建音频Blob
        const audioBlob = new Blob(this.audioChunks, {
          type: this.mediaRecorder.mimeType || 'audio/webm'
        })

        console.log('录音完成，大小:', audioBlob.size, 'bytes')
        console.log('录音格式:', audioBlob.type)
        console.log('录音时长:', this.recordingTime, '秒')

        // 发送音频数据
        this.$emit('send-audio', audioBlob)
      } catch (error) {
        console.error('处理录音数据失败:', error)
        this.$message.error('录音处理失败')
      }
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

      &.recording {
        background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
        animation: recording-pulse 1.5s ease-in-out infinite;
        position: relative;
        min-width: 60px;

        i {
          color: white;
        }

        .recording-time {
          color: white;
          font-size: 10px;
          position: absolute;
          bottom: -18px;
          left: 50%;
          transform: translateX(-50%);
          white-space: nowrap;
          background: rgba(255, 107, 107, 0.9);
          padding: 2px 6px;
          border-radius: 8px;
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

/* 录音脉动动画 */
@keyframes recording-pulse {
  0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(255, 107, 107, 0.7); }
  50% { transform: scale(1.05); box-shadow: 0 0 0 10px rgba(255, 107, 107, 0); }
  100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(255, 107, 107, 0); }
}
</style>