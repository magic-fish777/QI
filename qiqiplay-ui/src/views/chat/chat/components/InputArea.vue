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

      <!-- 图片生成按钮 -->
      <div class="image-btn" @click="showImageDialog" title="AI图片生成">
        <i class="el-icon-picture"></i>
      </div>

      <!-- 发送按钮 -->
      <div class="send-btn" @click="sendMessage" :class="{ disabled: !canSend }">
        <i class="el-icon-position"></i>
      </div>
    </div>

    <!-- 图片生成对话框 -->
    <el-dialog
      title="AI图片生成"
      :visible.sync="imageDialogVisible"
      width="500px"
      center
    >
      <div class="image-generation-dialog">
        <el-form ref="imageForm" :model="imageForm" label-width="80px">
          <el-form-item label="图片描述" required>
            <el-input
              v-model="imageForm.prompt"
              type="textarea"
              :rows="3"
              placeholder="请描述您想要生成的图片，例如：一只可爱的小猫在花园里玩耍"
            ></el-input>
          </el-form-item>
          <el-form-item label="图片风格">
            <el-select v-model="imageForm.style" placeholder="选择风格">
              <el-option label="写实风格" value="realistic"></el-option>
              <el-option label="卡通风格" value="cartoon"></el-option>
              <el-option label="油画风格" value="oil-painting"></el-option>
              <el-option label="水彩风格" value="watercolor"></el-option>
              <el-option label="素描风格" value="sketch"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="图片尺寸">
            <el-select v-model="imageForm.size" placeholder="选择尺寸">
              <el-option label="正方形 (512x512)" value="512x512"></el-option>
              <el-option label="横图 (768x512)" value="768x512"></el-option>
              <el-option label="竖图 (512x768)" value="512x768"></el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <!-- 生成结果展示 -->
        <div v-if="generatedImage" class="generated-image-result">
          <div class="result-title">生成结果</div>
          <div class="image-container">
            <img :src="generatedImage.imageUrl" alt="生成的图片" class="generated-image" />
            <div class="image-actions">
              <el-button size="small" @click="downloadImage">下载图片</el-button>
              <el-button size="small" @click="useImageInChat">发送到聊天</el-button>
            </div>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="imageDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="generateImage"
          :loading="imageGenerating"
          :disabled="!imageForm.prompt"
        >
          {{ imageGenerating ? '生成中...' : '生成图片' }}
        </el-button>
      </div>
    </el-dialog>
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
      recordingTime: 0,
      // 图片生成相关
      imageDialogVisible: false,
      imageGenerating: false,
      imageForm: {
        prompt: '',
        style: 'realistic',
        size: '512x512'
      },
      generatedImage: null
    }
  },
  computed: {
    canSend() {
      return this.inputMessage.trim().length > 0 && !this.disabled
    }
  },
  emits: ['send-message', 'typing', 'toggle-voice', 'send-audio', 'send-image'],
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
        // 检查语音功能权限
        try {
          const { validateFeaturePermission } = await import('@/api/chat/vip')
          const response = await validateFeaturePermission('voice')

          if (response.code === 200 && response.data.hasPermission) {
            // 开始录音
            await this.startRecording()
          } else {
            this.$message.warning(response.data.message || '您的会员等级暂不支持语音功能')
          }
        } catch (error) {
          console.error('检查语音权限失败:', error)
          this.$message.error('权限检查失败')
        }
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
    },

    // 图片生成相关方法
    async showImageDialog() {
      // 检查图片生成权限
      try {
        const { validateFeaturePermission } = await import('@/api/chat/vip')
        const response = await validateFeaturePermission('image')

        if (response.code === 200 && response.data.hasPermission) {
          this.imageDialogVisible = true
          this.generatedImage = null
          this.imageForm = {
            prompt: '',
            style: 'realistic',
            size: '512x512'
          }
        } else {
          this.$message.warning(response.data.message || '您的会员等级暂不支持图片生成功能')
        }
      } catch (error) {
        console.error('检查图片生成权限失败:', error)
        this.$message.error('权限检查失败')
      }
    },

    async generateImage() {
      if (!this.imageForm.prompt.trim()) {
        this.$message.warning('请输入图片描述')
        return
      }

      this.imageGenerating = true

      try {
        const { generateImage } = await import('@/api/chat/image')

        const response = await generateImage({
          prompt: this.imageForm.prompt,
          style: this.imageForm.style,
          size: this.imageForm.size
        })

        if (response && response.code === 200) {
          this.generatedImage = response.data
          this.$message.success('图片生成成功！')
        } else {
          this.$message.error(response.msg || '图片生成失败')
        }
      } catch (error) {
        console.error('图片生成失败:', error)
        if (error.message) {
          this.$message.error(error.message)
        } else {
          this.$message.error('图片生成失败，请稍后重试')
        }
      } finally {
        this.imageGenerating = false
      }
    },

    downloadImage() {
      if (!this.generatedImage) return

      // 创建下载链接
      const link = document.createElement('a')
      link.href = this.generatedImage.imageUrl
      link.download = `generated_image_${this.generatedImage.imageId}.jpg`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      this.$message.success('图片下载已开始')
    },

    useImageInChat() {
      if (!this.generatedImage) return

      // 发送图片到聊天
      this.$emit('send-image', this.generatedImage)
      this.imageDialogVisible = false
      this.$message.success('图片已发送到聊天')
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

    .image-btn {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: white;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.3s ease;
      flex-shrink: 0;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

      &:hover {
        background: #f0f4ff;
        transform: scale(1.05);
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
      }

      i {
        color: #667eea;
        font-size: 16px;
      }
    }
  }
}

// 图片生成对话框样式
.image-generation-dialog {
  .generated-image-result {
    margin-top: 20px;
    border-top: 1px solid #f0f0f0;
    padding-top: 20px;

    .result-title {
      font-size: 14px;
      font-weight: 600;
      color: #333;
      margin-bottom: 12px;
    }

    .image-container {
      text-align: center;

      .generated-image {
        max-width: 100%;
        max-height: 300px;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin-bottom: 12px;
      }

      .image-actions {
        display: flex;
        justify-content: center;
        gap: 12px;
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