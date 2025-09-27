<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 主要内容区域 -->
    <div class="register-content">
      <!-- Logo和标题区域 -->
      <div class="header-section">
        <div class="logo-container">
          <div class="logo-text">QIQI PLAY</div>
          <div class="logo-subtitle">智能角色对话平台</div>
        </div>
      </div>

      <!-- 注册表单卡片 -->
      <div class="register-card">
        <div class="card-header">
          <h2>创建账号</h2>
          <p>加入 QIQI PLAY，开启智能对话之旅</p>
        </div>

        <!-- 注册表单 -->
        <el-form
          ref="registerForm"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
        >
          <!-- 用户名 -->
          <el-form-item prop="username">
            <div class="input-container">
              <div class="input-label">
                <i class="el-icon-user"></i>
                <span>用户名</span>
              </div>
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                size="large"
                clearable
              ></el-input>
            </div>
          </el-form-item>

          <!-- 邮箱 -->
          <el-form-item prop="email">
            <div class="input-container">
              <div class="input-label">
                <i class="el-icon-message"></i>
                <span>邮箱</span>
              </div>
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                size="large"
                clearable
              ></el-input>
            </div>
          </el-form-item>

          <!-- 手机号（可选） -->
          <el-form-item prop="phone">
            <div class="input-container">
              <div class="input-label">
                <i class="el-icon-phone"></i>
                <span>手机号（可选）</span>
              </div>
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                size="large"
                clearable
              ></el-input>
            </div>
          </el-form-item>

          <!-- 密码 -->
          <el-form-item prop="password">
            <div class="input-container">
              <div class="input-label">
                <i class="el-icon-lock"></i>
                <span>密码</span>
              </div>
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
              ></el-input>
            </div>
          </el-form-item>

          <!-- 确认密码 -->
          <el-form-item prop="confirmPassword">
            <div class="input-container">
              <div class="input-label">
                <i class="el-icon-lock"></i>
                <span>确认密码</span>
              </div>
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                size="large"
                show-password
              ></el-input>
            </div>
          </el-form-item>

          <!-- 邮箱验证码 -->
          <el-form-item prop="emailCode">
            <div class="input-container">
              <div class="input-label">
                <i class="el-icon-key"></i>
                <span>验证码</span>
              </div>
              <div class="code-input-wrapper">
                <el-input
                  v-model="registerForm.emailCode"
                  placeholder="请输入邮箱验证码"
                  size="large"
                ></el-input>
                <el-button
                  type="primary"
                  class="code-btn"
                  :disabled="codeCountdown > 0 || !registerForm.email"
                  @click="sendEmailCodeway()"
                >
                  {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </div>
          </el-form-item>

          <!-- 用户协议 -->
          <el-form-item prop="agreeTerms" class="terms-item">
            <el-checkbox v-model="registerForm.agreeTerms">
              我已阅读并同意
              <el-button type="text" @click="showTerms">《用户协议》</el-button>
              和
              <el-button type="text" @click="showPrivacy">《隐私政策》</el-button>
            </el-checkbox>
          </el-form-item>

          <!-- 注册按钮 -->
          <el-form-item class="register-button-item">
            <el-button
              type="primary"
              size="large"
              class="register-button"
              :loading="loading"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 其他注册方式 -->
        <div class="other-register">
          <div class="divider">
            <span>或者</span>
          </div>
          <div class="social-register">
            <div class="social-btn wechat" @click="socialRegister('wechat')">
              <i class="el-icon-chat-line-round"></i>
              <span>微信注册</span>
            </div>
            <div class="social-btn qq" @click="socialRegister('qq')">
              <i class="el-icon-user-solid"></i>
              <span>QQ注册</span>
            </div>
            <div class="social-btn github" @click="socialRegister('github')">
              <i class="el-icon-link"></i>
              <span>GitHub注册</span>
            </div>
          </div>
        </div>

        <!-- 登录链接 -->
        <div class="login-link">
          <span>已有账号？</span>
          <el-button type="text" @click="goToLogin">立即登录</el-button>
        </div>
      </div>

      <!-- 底部信息 -->
      <div class="footer-info">
        <p class="copyright">© 2024 QIQI PLAY. All rights reserved.</p>
      </div>
    </div>

    <!-- 移动端适配的侧边装饰 -->
    <div class="mobile-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { aiRegister, sendEmailCode, getAiUserInfo } from '@/api/ai/auth'

export default {
  name: 'AIRegister',
  mounted() {
    // 简单检查本地登录状态，不调用API验证
    this.checkLocalLoginStatus()
  },
  data() {
    // 邮箱验证规则
    const validateEmail = (rule, value, callback) => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!value) {
        callback(new Error('请输入邮箱地址'))
      } else if (!emailRegex.test(value)) {
        callback(new Error('请输入正确的邮箱格式'))
      } else {
        callback()
      }
    }

    // 手机号验证规则（可选）
    const validatePhone = (rule, value, callback) => {
      if (value && !/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号格式'))
      } else {
        callback()
      }
    }

    // 确认密码验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    // 用户协议验证
    const validateTerms = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请同意用户协议和隐私政策'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      codeCountdown: 0,
      codeTimer: null,
      registerForm: {
        username: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        emailCode: '',
        agreeTerms: false
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9\u4e00-\u9fa5_-]+$/, message: '用户名只能包含字母、数字、中文、下划线和短横线', trigger: 'blur' }
        ],
        email: [
          { validator: validateEmail, trigger: 'blur' }
        ],
        phone: [
          { validator: validatePhone, trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
          { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: '密码必须包含字母和数字', trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        emailCode: [
          { required: true, message: '请输入邮箱验证码', trigger: 'blur' },
          { len: 6, message: '验证码长度为6位', trigger: 'blur' }
        ],
        agreeTerms: [
          { validator: validateTerms, trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    // 检查本地登录状态（不调用API）
    checkLocalLoginStatus() {
      try {
        const token = localStorage.getItem('chat_token')
        const isLoggedIn = localStorage.getItem('chat_isLoggedIn')

        console.log('==> 注册页面检查本地登录状态, token:', token ? '存在' : '不存在', ', isLoggedIn:', isLoggedIn)

        // 如果本地显示已登录，直接跳转（让路由守卫来处理具体验证）
        if (token && isLoggedIn === 'true') {
          console.log('==> 本地显示已登录，跳转到聊天页面')
          this.$message.success('检测到已登录，正在跳转...')

          // 直接跳转，让聊天页面来验证token有效性
          this.$router.push('/chat')
        }
      } catch (error) {
        console.log('==> 检查本地登录状态失败:', error.message)
      }
    },

    // 清除登录状态
    clearLoginStatus() {
      localStorage.removeItem('chat_token')
      localStorage.removeItem('chat_isLoggedIn')
      localStorage.removeItem('loginType')
      localStorage.removeItem('userInfo')
      console.log('==> 已清除登录状态')
    },

    // 发送邮箱验证码
    async sendEmailCodeway() {
      // 严格验证邮箱
      const email = this.registerForm.email?.trim()

      if (!email || email === '') {
        this.$message.error('请先输入邮箱地址')
        return
      }

      // 验证邮箱格式
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      if (!emailRegex.test(email)) {
        this.$message.error('请输入正确的邮箱格式')
        return
      }

      // 验证邮箱长度
      if (email.length > 100) {
        this.$message.error('邮箱地址过长')
        return
      }

      console.log('发送验证码到邮箱:', email)

      try {
        const response = await sendEmailCode(email)

        if (response.code === 200) {
          this.$message.success('验证码已发送到您的邮箱')
          this.startCountdown()
        } else {
          this.$message.error(response.msg || '验证码发送失败')
        }
      } catch (error) {
        this.$message.error('验证码发送失败：' + (error.msg || error.message))
      }
    },

    // 开始倒计时
    startCountdown() {
      this.codeCountdown = 60
      this.codeTimer = setInterval(() => {
        this.codeCountdown--
        if (this.codeCountdown <= 0) {
          clearInterval(this.codeTimer)
          this.codeTimer = null
        }
      }, 1000)
    },

    // 处理注册
    async handleRegister() {
      try {
        const valid = await this.$refs.registerForm.validate()
        if (!valid) return

        this.loading = true

        // 构建注册数据
        const registerData = {
          username: this.registerForm.username,
          nickName: this.registerForm.nickName || this.registerForm.username,
          email: this.registerForm.email,
          phone: this.registerForm.phone,
          password: this.registerForm.password,
          confirmPassword: this.registerForm.confirmPassword,
          code: this.registerForm.emailCode,
          agreement: this.registerForm.agreeTerms
        }

        const response = await aiRegister(registerData)

        if (response.code === 200) {
          this.$message.success('注册成功！')
          // 跳转到登录页面
          setTimeout(() => {
            this.$router.push('/chat/login')
          }, 1500)
        } else {
          this.$message.error(response.msg || '注册失败')
        }
      } catch (error) {
        this.$message.error('注册失败：' + (error.msg || error.message))
      } finally {
        this.loading = false
      }
    },

    // 注册成功后自动登录
    autoLoginAfterRegister() {
      // 设置登录状态
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('loginType', 'register')
      localStorage.setItem('userInfo', JSON.stringify({
        name: this.registerForm.username,
        email: this.registerForm.email,
        phone: this.registerForm.phone,
        avatar: ''
      }))

      // 跳转到聊天页面
      this.$router.push('/chat')
    },

    // 第三方注册
    socialRegister(type) {
      this.$message.info(`${type === 'wechat' ? '微信' : type === 'qq' ? 'QQ' : 'GitHub'}注册功能开发中...`)
    },

    // 跳转到登录页面
    goToLogin() {
      this.$router.push('/chat/login')
    },

    // 显示用户协议
    showTerms() {
      this.$message.info('用户协议页面开发中...')
    },

    // 显示隐私政策
    showPrivacy() {
      this.$message.info('隐私政策页面开发中...')
    },


  },

  beforeDestroy() {
    // 清理倒计时
    if (this.codeTimer) {
      clearInterval(this.codeTimer)
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  overflow: hidden;
}

// 背景装饰
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;

  .gradient-orb {
    position: absolute;
    border-radius: 50%;
    background: linear-gradient(45deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
    backdrop-filter: blur(20px);
    animation: float 6s ease-in-out infinite;

    &.orb-1 {
      width: 200px;
      height: 200px;
      top: 10%;
      left: 10%;
      animation-delay: 0s;
    }

    &.orb-2 {
      width: 150px;
      height: 150px;
      top: 60%;
      right: 15%;
      animation-delay: 2s;
    }

    &.orb-3 {
      width: 100px;
      height: 100px;
      bottom: 20%;
      left: 20%;
      animation-delay: 4s;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

// 主要内容区域
.register-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 440px;
  margin: 0 auto;
}

// Logo和标题区域
.header-section {
  text-align: center;
  margin-bottom: 30px;

  .logo-container {
    .logo-text {
      font-size: 36px;
      font-weight: bold;
      color: white;
      margin-bottom: 8px;
      letter-spacing: 3px;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    }

    .logo-subtitle {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.8);
      font-weight: 300;
    }
  }
}

// 注册表单卡片
.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 32px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);

  .card-header {
    text-align: center;
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      color: #333;
      margin: 0 0 8px;
      font-weight: 600;
    }

    p {
      color: #666;
      font-size: 14px;
      margin: 0;
    }
  }
}

// 注册表单
.register-form {
  .input-container {
    margin-bottom: 4px;

    .input-label {
      display: flex;
      align-items: center;
      margin-bottom: 8px;
      font-size: 14px;
      color: #555;
      font-weight: 500;

      i {
        margin-right: 8px;
        font-size: 16px;
        color: #667eea;
      }
    }

    .el-input {
      ::v-deep .el-input__inner {
        border-radius: 12px;
        border: 2px solid #f0f0f0;
        background: #fafbfc;
        transition: all 0.3s ease;
        font-size: 14px;

        &:focus {
          border-color: #667eea;
          background: white;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
      }
    }

    .code-input-wrapper {
      display: flex;
      gap: 12px;

      .el-input {
        flex: 1;
      }

      .code-btn {
        flex-shrink: 0;
        width: 110px;
        border-radius: 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        font-size: 13px;
        font-weight: 600;

        &:hover:not(:disabled) {
          opacity: 0.9;
        }

        &:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }
      }
    }
  }

  .terms-item {
    margin-bottom: 20px;

    ::v-deep .el-form-item__content {
      line-height: 1.5;
    }

    .el-checkbox {
      ::v-deep .el-checkbox__label {
        font-size: 13px;
        color: #666;
        line-height: 1.5;
      }
    }

    .el-button--text {
      color: #667eea;
      font-size: 13px;
      padding: 0;
      margin: 0 2px;

      &:hover {
        color: #764ba2;
      }
    }
  }

  .register-button-item {
    margin-bottom: 0;

    .register-button {
      width: 100%;
      height: 48px;
      border-radius: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      font-size: 16px;
      font-weight: 600;
      letter-spacing: 1px;

      &:hover:not(:disabled) {
        opacity: 0.9;
        transform: translateY(-1px);
      }
    }
  }
}

// 其他注册方式
.other-register {
  margin: 24px 0;

  .divider {
    text-align: center;
    position: relative;
    margin-bottom: 20px;

    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 0;
      right: 0;
      height: 1px;
      background: #e8e8e8;
      z-index: 1;
    }

    span {
      background: white;
      color: #999;
      font-size: 12px;
      padding: 0 16px;
      position: relative;
      z-index: 2;
    }
  }

  .social-register {
    display: flex;
    gap: 12px;
    justify-content: center;

    .social-btn {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 12px 8px;
      border-radius: 12px;
      background: #f8f9fa;
      cursor: pointer;
      transition: all 0.3s ease;
      border: 2px solid transparent;

      &:hover {
        background: #f0f0f0;
        transform: translateY(-2px);
      }

      &.wechat {
        &:hover {
          border-color: #1aad19;
        }

        i {
          color: #1aad19;
        }
      }

      &.qq {
        &:hover {
          border-color: #12b7f5;
        }

        i {
          color: #12b7f5;
        }
      }

      &.github {
        &:hover {
          border-color: #333;
        }

        i {
          color: #333;
        }
      }

      i {
        font-size: 20px;
        margin-bottom: 4px;
      }

      span {
        font-size: 12px;
        color: #666;
        font-weight: 500;
      }
    }
  }
}

// 登录链接
.login-link {
  text-align: center;
  padding: 16px 0 0;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #666;

  .el-button--text {
    color: #667eea;
    font-weight: 600;
    font-size: 14px;

    &:hover {
      color: #764ba2;
    }
  }
}

// 底部信息
.footer-info {
  text-align: center;
  margin-top: 20px;

  .copyright {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.7);
    margin: 0;
  }
}

// 移动端装饰
.mobile-decoration {
  position: fixed;
  bottom: 0;
  right: 0;
  pointer-events: none;
  z-index: 1;

  .floating-shapes {
    position: relative;
    width: 200px;
    height: 200px;

    .shape {
      position: absolute;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 50%;
      animation: float 4s ease-in-out infinite;

      &.shape-1 {
        width: 60px;
        height: 60px;
        top: 20px;
        right: 40px;
        animation-delay: 0s;
      }

      &.shape-2 {
        width: 40px;
        height: 40px;
        bottom: 60px;
        right: 20px;
        animation-delay: 1.5s;
      }

      &.shape-3 {
        width: 30px;
        height: 30px;
        top: 80px;
        right: 10px;
        animation-delay: 3s;
      }
    }
  }
}

// 移动端适配
@media (max-width: 768px) {
  .register-container {
    padding: 16px;
  }

  .header-section {
    margin-bottom: 20px;

    .logo-container .logo-text {
      font-size: 28px;
    }
  }

  .register-card {
    padding: 24px 20px;

    .card-header h2 {
      font-size: 20px;
    }
  }

  .other-register .social-register {
    .social-btn {
      padding: 10px 6px;

      i {
        font-size: 18px;
      }

      span {
        font-size: 11px;
      }
    }
  }
}

@media (max-width: 480px) {
  .register-card {
    padding: 20px 16px;
  }

  .register-form .input-container .code-input-wrapper {
    flex-direction: column;
    gap: 8px;

    .code-btn {
      width: 100%;
    }
  }
}
</style>
