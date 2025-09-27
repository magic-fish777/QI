<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 主要内容区域 -->
    <div class="login-content">
      <!-- Logo和标题区域 -->
      <div class="header-section">
        <div class="logo-container">
          <div class="logo-text">QIQI PLAY</div>
          <div class="logo-subtitle">智能角色对话平台</div>
        </div>
      </div>

      <!-- 登录表单卡片 -->
      <div class="login-card">
        <!-- 登录方式切换 -->
        <div class="login-tabs">
          <div
            class="login-tab"
            :class="{ active: loginType === 'account' }"
            @click="switchLoginType('account')"
          >
            <i class="el-icon-user"></i>
            <span>账号密码</span>
          </div>
          <div
            class="login-tab"
            :class="{ active: loginType === 'email' }"
            @click="switchLoginType('email')"
          >
            <i class="el-icon-message"></i>
            <span>邮箱登录</span>
          </div>
        </div>

        <!-- 登录表单 -->
        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
        >
          <!-- 账号密码登录 -->
          <template v-if="loginType === 'account'">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名/手机号"
                prefix-icon="el-icon-user"
                size="large"
                @keyup.enter.native="handleLogin"
              ></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="el-icon-lock"
                size="large"
                show-password
                @keyup.enter.native="handleLogin"
              ></el-input>
            </el-form-item>
          </template>

          <!-- 邮箱登录 -->
          <template v-else>
            <el-form-item prop="email">
              <el-input
                v-model="loginForm.email"
                placeholder="请输入邮箱地址"
                prefix-icon="el-icon-message"
                size="large"
                @keyup.enter.native="handleLogin"
              ></el-input>
            </el-form-item>
            <el-form-item prop="emailCode">
              <div class="code-input-group">
                <el-input
                  v-model="loginForm.emailCode"
                  placeholder="请输入验证码"
                  prefix-icon="el-icon-key"
                  size="large"
                  @keyup.enter.native="handleLogin"
                ></el-input>
                <el-button
                  type="text"
                  class="send-code-btn"
                  :disabled="codeCountdown > 0"
                  @click="sendEmailCode()"
                >
                  {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>
          </template>

          <!-- 记住我和忘记密码 -->
          <div class="form-options" v-if="loginType === 'account'">
            <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            <el-button type="text" class="forgot-password">忘记密码？</el-button>
          </div>

          <!-- 登录按钮 -->
          <el-form-item class="login-button-item">
            <el-button
              type="primary"
              size="large"
              class="login-button"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '立即登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 其他登录方式 -->
        <div class="other-login">
          <div class="divider">
            <span>或者</span>
          </div>
          <div class="social-login">
            <div class="social-btn wechat" @click="socialLogin('wechat')">
              <i class="el-icon-chat-line-round"></i>
              <span>微信</span>
            </div>
            <div class="social-btn qq" @click="socialLogin('qq')">
              <i class="el-icon-user-solid"></i>
              <span>QQ</span>
            </div>
            <div class="social-btn github" @click="socialLogin('github')">
              <i class="el-icon-link"></i>
              <span>GitHub</span>
            </div>
          </div>
        </div>

        <!-- 注册链接 -->
        <div class="register-link">
          <span>还没有账号？</span>
          <el-button type="text" @click="goToRegister">立即注册</el-button>
        </div>
      </div>

      <!-- 底部信息 -->
      <div class="footer-info">
        <p>登录即表示同意 <el-button type="text">《用户协议》</el-button> 和 <el-button type="text">《隐私政策》</el-button></p>
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
import { aiLogin, sendEmailCode, getAiUserInfo } from '@/api/ai/auth'

export default {
  name: 'AILogin',
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

    return {
      loginType: 'account', // account | email
      loading: false,
      codeCountdown: 0,
      codeTimer: null,
      loginForm: {
        username: '',
        password: '',
        email: '',
        emailCode: '',
        rememberMe: false
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, validator: validateEmail, trigger: 'blur' }
        ],
        emailCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 6, message: '验证码长度为6位', trigger: 'blur' }
        ]
      }
    }
  },
  beforeDestroy() {
    if (this.codeTimer) {
      clearInterval(this.codeTimer)
    }
  },
  methods: {
    // 检查本地登录状态（不调用API）
    checkLocalLoginStatus() {
      try {
        const token = localStorage.getItem('chat_token')
        const isLoggedIn = localStorage.getItem('chat_isLoggedIn')

        console.log('==> 检查本地登录状态, token:', token ? '存在' : '不存在', ', isLoggedIn:', isLoggedIn)

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

    // 完整的登录状态检查（包含API验证）
    async verifyLoginStatus() {
      try {
        const token = localStorage.getItem('chat_token')
        const isLoggedIn = localStorage.getItem('chat_isLoggedIn')

        if (!token || isLoggedIn !== 'true') {
          return false
        }

        // 验证token有效性
        const response = await getAiUserInfo()
        return response && response.code === 200
      } catch (error) {
        console.log('==> token验证失败:', error.message)
        return false
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

    switchLoginType(type) {
      console.log('==> 切换登录方式:', type)
      this.loginType = type
      // 不完全重置表单，保留有用的数据
      this.smartResetForm()
    },

    resetForm() {
      console.log('==> 完全重置表单前:', JSON.stringify(this.loginForm, null, 2))
      this.loginForm = {
        username: '',
        password: '',
        email: '',
        emailCode: '',
        rememberMe: false
      }
      console.log('==> 完全重置表单后:', JSON.stringify(this.loginForm, null, 2))
      if (this.$refs.loginForm) {
        this.$refs.loginForm.clearValidate()
      }
    },

    smartResetForm() {
      console.log('==> 智能重置表单前:', JSON.stringify(this.loginForm, null, 2))
      if (this.loginType === 'account') {
        // 切换到账号登录，保留用户名，清空邮箱相关
        this.loginForm.email = ''
        this.loginForm.emailCode = ''
      } else {
        // 切换到邮箱登录，保留邮箱，清空密码
        this.loginForm.password = ''
        this.loginForm.emailCode = ''
      }
      console.log('==> 智能重置表单后:', JSON.stringify(this.loginForm, null, 2))
      if (this.$refs.loginForm) {
        this.$refs.loginForm.clearValidate()
      }
    },

    async handleLogin() {
      try {
        const valid = await this.$refs.loginForm.validate()
        if (!valid) return

        this.loading = true

        // 调试：输出当前表单数据
        console.log('==> 当前登录类型:', this.loginType)
        console.log('==> 当前表单数据:', JSON.stringify(this.loginForm, null, 2))

        // 构建登录数据
        const loginData = {
          loginType: this.loginType,
          username: this.loginType === 'account' ? this.loginForm.username : this.loginForm.email,
          password: this.loginType === 'account' ? this.loginForm.password : '',
          code: this.loginType === 'email' ? this.loginForm.emailCode : ''
        }

        // 调试：输出构建的登录数据
        console.log('==> 构建的登录数据:', JSON.stringify(loginData, null, 2))

        // 验证关键字段
        if (!loginData.username || loginData.username.trim() === '') {
          this.$message.error('用户名/邮箱不能为空')
          return
        }

        if (this.loginType === 'account' && (!loginData.password || loginData.password.trim() === '')) {
          this.$message.error('密码不能为空')
          return
        }

        if (this.loginType === 'email' && (!loginData.code || loginData.code.trim() === '')) {
          this.$message.error('验证码不能为空')
          return
        }

        const response = await this.aiLogin(loginData)

        if (response.code === 200) {
          this.loginSuccess(response)
        } else {
          this.$message.error(response.msg || '登录失败')
        }
      } catch (error) {
        this.$message.error('登录失败：' + (error.msg || error.message))
      } finally {
        this.loading = false
      }
    },

    loginSuccess(responseData) {
      this.$message.success('登录成功')

      console.log('==> 登录成功，保存登录信息')

      // 保存token
      if (responseData && responseData.token) {
        localStorage.setItem('chat_token', responseData.token)
        console.log('==> 已保存token', responseData.token)
      }

      // 保存登录状态
      localStorage.setItem('chat_isLoggedIn', 'true')
      localStorage.setItem('loginType', this.loginType)

      // 保存用户信息
      const userInfo = {
        username: this.loginType === 'account' ? this.loginForm.username : this.loginForm.email,
        email: this.loginType === 'email' ? this.loginForm.email : '',
        loginTime: new Date().toISOString(),
        loginType: this.loginType
      }
      localStorage.setItem('userInfo', JSON.stringify(userInfo))

      console.log('==> 已保存登录状态和用户信息:', userInfo)

      // 跳转到聊天页面
      this.$router.push('/chat')
    },

    async sendEmailCode() {
      // 严格验证邮箱
      const email = this.loginForm.email?.trim()

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
          this.$message.success('验证码已发送到您的邮箱，请注意查收')
          this.startCountdown()
        } else {
          this.$message.error(response.msg || '验证码发送失败')
        }
      } catch (error) {
        this.$message.error('验证码发送失败：' + (error.msg || error.message))
      }
    },

    startCountdown() {
      this.codeCountdown = 60
      this.codeTimer = setInterval(() => {
        this.codeCountdown--
        if (this.codeCountdown <= 0) {
          clearInterval(this.codeTimer)
        }
      }, 1000)
    },

    socialLogin(platform) {
      this.$message.info(`${platform} 登录功能开发中...`)
    },

    goToRegister() {
      this.$router.push('/chat/register')
    },

    // API方法
    aiLogin
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #667eea 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

// 背景装饰
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;

  .gradient-orb {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(80px);
    animation: float 6s ease-in-out infinite;

    &.orb-1 {
      width: 300px;
      height: 300px;
      top: -150px;
      right: -150px;
      animation-delay: 0s;
    }

    &.orb-2 {
      width: 200px;
      height: 200px;
      bottom: -100px;
      left: -100px;
      animation-delay: 2s;
    }

    &.orb-3 {
      width: 150px;
      height: 150px;
      top: 50%;
      left: -75px;
      animation-delay: 4s;
    }
  }
}

// 主内容区域
.login-content {
  width: 100%;
  max-width: 400px;
  z-index: 2;
  position: relative;
}

// Logo和标题区域
.header-section {
  text-align: center;
  margin-bottom: 32px;

  .logo-container {
    .logo-text {
      font-size: 36px;
      font-weight: bold;
      color: white;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      letter-spacing: 3px;
      margin-bottom: 8px;
    }

    .logo-subtitle {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.8);
      font-weight: 300;
    }
  }
}

// 登录卡片
.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

// 登录方式切换
.login-tabs {
  display: flex;
  margin-bottom: 24px;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 4px;

  .login-tab {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    color: #666;

    i {
      margin-right: 6px;
      font-size: 16px;
    }

    span {
      font-size: 14px;
      font-weight: 500;
    }

    &.active {
      background: white;
      color: #667eea;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
    }

    &:hover:not(.active) {
      background: rgba(255, 255, 255, 0.7);
    }
  }
}

// 表单样式
.login-form {
  .el-form-item {
    margin-bottom: 20px;

    ::v-deep .el-input {
      .el-input__inner {
        height: 48px;
        border-radius: 12px;
        border: 2px solid #f0f0f0;
        background: #fafbfc;
        transition: all 0.3s ease;

        &:focus {
          border-color: #667eea;
          background: white;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
      }
    }
  }

  .code-input-group {
    display: flex;
    gap: 12px;

    .el-input {
      flex: 1;
    }

    .send-code-btn {
      white-space: nowrap;
      color: #667eea;
      font-weight: 500;
      min-width: 100px;

      &:disabled {
        color: #ccc;
      }
    }
  }

  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .forgot-password {
      color: #667eea;
      padding: 0;
      font-size: 14px;
    }
  }

  .login-button-item {
    margin-bottom: 0;

    .login-button {
      width: 100%;
      height: 48px;
      border-radius: 12px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      font-size: 16px;
      font-weight: 600;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
      }
    }
  }
}

// 其他登录方式
.other-login {
  margin-top: 24px;

  .divider {
    text-align: center;
    position: relative;
    margin: 20px 0;

    &::before {
      content: '';
      position: absolute;
      top: 50%;
      left: 0;
      right: 0;
      height: 1px;
      background: #e8e8e8;
    }

    span {
      background: white;
      padding: 0 16px;
      color: #999;
      font-size: 14px;
    }
  }

  .social-login {
    display: flex;
    justify-content: center;
    gap: 16px;

    .social-btn {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 16px 12px;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.3s ease;
      min-width: 60px;

      i {
        font-size: 24px;
        margin-bottom: 4px;
      }

      span {
        font-size: 12px;
        font-weight: 500;
      }

      &.wechat {
        background: #f0f9ff;
        color: #07c160;

        &:hover {
          background: #e0f2fe;
        }
      }

      &.qq {
        background: #fef7e0;
        color: #faad14;

        &:hover {
          background: #fff2cc;
        }
      }

      &.github {
        background: #f5f5f5;
        color: #333;

        &:hover {
          background: #eeeeee;
        }
      }
    }
  }
}

// 注册链接
.register-link {
  text-align: center;
  margin-top: 24px;
  color: #666;
  font-size: 14px;

  .el-button {
    color: #667eea;
    font-weight: 500;
    padding: 0 4px;
  }
}

// 底部信息
.footer-info {
  text-align: center;
  margin-top: 32px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;

  p {
    margin: 8px 0;
  }

  .el-button {
    color: rgba(255, 255, 255, 0.9);
    padding: 0 4px;
    font-size: 12px;
  }

  .copyright {
    font-weight: 300;
  }
}

// 移动端装饰
.mobile-decoration {
  display: none;
}

// 动画效果
@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

// 移动端适配
@media (max-width: 768px) {
  .login-container {
    padding: 16px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .login-content {
    max-width: 100%;
  }

  .header-section {
    margin-bottom: 24px;

    .logo-container .logo-text {
      font-size: 28px;
    }
  }

  .login-card {
    padding: 24px;
    border-radius: 16px;
  }

  .login-tabs {
    .login-tab {
      padding: 10px 8px;

      span {
        font-size: 13px;
      }
    }
  }

  .other-login .social-login {
    gap: 12px;

    .social-btn {
      padding: 12px 8px;
      min-width: 50px;

      i {
        font-size: 20px;
      }
    }
  }

  // 移动端显示装饰元素
  .mobile-decoration {
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;

    .floating-shapes {
      .shape {
        position: absolute;
        background: rgba(255, 255, 255, 0.05);
        border-radius: 50%;
        animation: float 8s ease-in-out infinite;

        &.shape-1 {
          width: 60px;
          height: 60px;
          top: 10%;
          right: 10%;
          animation-delay: 0s;
        }

        &.shape-2 {
          width: 40px;
          height: 40px;
          bottom: 20%;
          left: 5%;
          animation-delay: 3s;
        }

        &.shape-3 {
          width: 30px;
          height: 30px;
          top: 60%;
          right: 5%;
          animation-delay: 6s;
        }
      }
    }
  }

  // 背景装饰在移动端的调整
  .background-decoration .gradient-orb {
    &.orb-1 {
      width: 200px;
      height: 200px;
      top: -100px;
      right: -100px;
    }

    &.orb-2 {
      width: 150px;
      height: 150px;
      bottom: -75px;
      left: -75px;
    }

    &.orb-3 {
      display: none;
    }
  }
}

// 超小屏幕适配
@media (max-width: 480px) {
  .login-container {
    padding: 12px;
  }

  .login-card {
    padding: 20px;
  }

  .header-section .logo-container .logo-text {
    font-size: 24px;
  }
}
</style>
