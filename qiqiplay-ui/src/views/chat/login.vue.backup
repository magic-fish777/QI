<template>
  <div class="login">
    <!-- 左侧：项目介绍 -->
    <div class="left-panel">
      <div class="intro-content">
        <h1 class="main-title">QiQi角色扮演世界</h1>
        <h2 class="subtitle">与历史人物对话</h2>
        <p class="description">
          在这里，你可以与历史上最伟大的思想家进行深度对话。<br>
          探索哲学的奥秘，体验经典戏剧的魅力。
        </p>

        <!-- 角色展示 -->
        <div class="characters">
          <div class="character hamlet">
            <img src="https://upload.wikimedia.org/wikipedia/commons/f/f4/Hamlet_%28Thomas_Bettertom%29_1.jpg" alt="哈姆雷特" class="character-avatar" />
            <div class="character-info">
              <h3>哈姆雷特</h3>
              <p>"生存还是毁灭，这是个问题"</p>
            </div>
          </div>

          <div class="character socrates">
            <img src="https://upload.wikimedia.org/wikipedia/commons/0/08/Socrates_Louvre_profile.jpg" alt="苏格拉底" class="character-avatar" />
            <div class="character-info">
              <h3>苏格拉底</h3>
              <p>"我知道我什么都不知道"</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧：登录表单 -->
    <div class="right-panel">
      <div class="login-container">
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">{{title}}</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  height: 100vh;
  background: #f8f9fa;
}

/* 左侧面板样式 */
.left-panel {
  flex: 1;
  background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.intro-content {
  max-width: 500px;
  padding: 40px;
  text-align: center;
  animation: fadeInUp 1s ease-out;
}

.main-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  background: linear-gradient(45deg, #ffd700, #ffed4a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 24px;
  font-weight: 400;
  margin-bottom: 24px;
  color: #e2e8f0;
}

.description {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 40px;
  color: #cbd5e0;
}

.characters {
  display: flex;
  justify-content: space-around;
  gap: 30px;
}

.character {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 20px;
  transition: transform 0.3s ease;
}

.character:hover {
  transform: translateY(-5px);
}

.character.hamlet {
  animation: slideInLeft 1.2s ease-out;
}

.character.socrates {
  animation: slideInRight 1.2s ease-out;
}

.character-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid rgba(255, 255, 255, 0.3);
  margin-right: 15px;
}

.character-info h3 {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: #ffd700;
}

.character-info p {
  font-size: 14px;
  color: #e2e8f0;
  margin: 0;
  font-style: italic;
}

/* 右侧面板样式 */
.right-panel {
  flex: 0 0 450px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
}

.login-container {
  width: 100%;
  max-width: 380px;
  padding: 40px;
}

.login-form {
  width: 100%;

  .title {
    margin: 0px auto 30px auto;
    text-align: center;
    color: #2d3748;
    font-size: 28px;
    font-weight: 600;
  }

  .el-input {
    height: 45px;
    margin-bottom: 16px;

    input {
      height: 45px;
      border-radius: 8px;
      border: 1px solid #e2e8f0;
      font-size: 16px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }

  .el-button {
    height: 45px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #2d3748 0%, #4a5568 100%);
    border: none;
  }
}
.login-code {
  width: 33%;
  height: 45px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
    height: 45px;
    border-radius: 4px;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  0% {
    transform: translateX(-100px);
    opacity: 0;
  }
  100% {
    transform: translateX(0);
    opacity: 1;
  }
}

@keyframes slideInRight {
  0% {
    transform: translateX(100px);
    opacity: 0;
  }
  100% {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login {
    flex-direction: column;
  }

  .left-panel {
    flex: 0 0 auto;
    min-height: 300px;
  }

  .right-panel {
    flex: 1;
  }

  .characters {
    flex-direction: column;
    align-items: center;
    gap: 15px;
  }

  .character {
    width: 100%;
    max-width: 300px;
  }
}
</style>
