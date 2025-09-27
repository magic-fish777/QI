<template>
  <div class="bottom-navigation">
    <div class="nav-items">
      <div
        class="nav-item"
        :class="{ active: currentTab === 'home' }"
        @click="switchTab('home')"
      >
        <i class="el-icon-chat-line-round"></i>
        <span>主页</span>
      </div>
      <div
        class="nav-item"
        :class="{ active: currentTab === 'benefits' }"
        @click="switchTab('benefits')"
      >
        <i class="el-icon-medal"></i>
        <span>权益</span>
      </div>
      <div
        class="nav-item"
        :class="{ active: currentTab === 'profile' }"
        @click="switchTab('profile')"
      >
        <i class="el-icon-user"></i>
        <span>我的</span>
      </div>
      <div
        class="nav-item"
        :class="{ active: currentTab === 'settings' }"
        @click="switchTab('settings')"
      >
        <i class="el-icon-setting"></i>
        <span>设置</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MobileNavigation',
  computed: {
    currentTab() {
      const route = this.$route.path
      if (route === '/chat' || route.includes('/chat/chat')) return 'home'
      if (route.includes('/chat/benefits')) return 'benefits'
      if (route.includes('/chat/profile')) return 'profile'
      if (route.includes('/chat/settings')) return 'settings'
      return 'home'
    }
  },
  methods: {
    switchTab(tab) {
      const routes = {
        home: '/chat',
        benefits: '/chat/benefits',
        profile: '/chat/profile',
        settings: '/chat/settings'
      }

      if (this.$route.path !== routes[tab]) {
        this.$router.push(routes[tab])
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.bottom-navigation {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-top: 1px solid #f0f0f0;
  padding: 8px 0 16px 0;
  z-index: 1000;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);

  .nav-items {
    display: flex;
    justify-content: space-around;
    align-items: center;
  }

  .nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    border-radius: 12px;
    min-width: 60px;

    i {
      font-size: 22px;
      margin-bottom: 4px;
      color: #999;
      transition: color 0.3s ease;
    }

    span {
      font-size: 11px;
      color: #999;
      font-weight: 500;
      transition: color 0.3s ease;
    }

    &:hover {
      background: rgba(102, 126, 234, 0.1);

      i, span {
        color: #667eea;
      }
    }

    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

      i, span {
        color: white;
      }
    }
  }
}
</style>