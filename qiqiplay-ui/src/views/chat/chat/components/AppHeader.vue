<template>
  <div class="app-header">
    <div class="header-content">
      <div class="app-title">QIQI PLAY</div>
      <div class="user-menu" @click="$emit('toggle-user-menu')">
        <div class="user-avatar">
          <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name">
        </div>
        <i class="el-icon-arrow-down"></i>
      </div>
    </div>
    <div class="search-section">
      <div class="search-bar" @click="$emit('show-search')">
        <i class="el-icon-search"></i>
        <span>搜索角色</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AppHeader',
  props: {
    userInfo: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      defaultAvatar: require('@/assets/images/profile.jpg')
    }
  },
  emits: ['toggle-user-menu', 'show-search']
}
</script>

<style lang="scss" scoped>
.app-header {
  padding: 20px 16px 16px;
  color: white;

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .app-title {
      font-size: 24px;
      font-weight: bold;
    }

    .user-menu {
      display: flex;
      align-items: center;
      cursor: pointer;
      transition: opacity 0.3s ease;

      &:hover {
        opacity: 0.8;
      }

      .user-avatar {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        overflow: hidden;
        margin-right: 8px;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      i {
        font-size: 14px;
        transition: transform 0.3s ease;
      }
    }
  }

  .search-section {
    .search-bar {
      background: rgba(255, 255, 255, 0.2);
      border-radius: 20px;
      padding: 10px 16px;
      display: flex;
      align-items: center;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
      }

      i {
        margin-right: 8px;
        font-size: 16px;
      }

      span {
        color: rgba(255, 255, 255, 0.8);
        font-size: 14px;
      }
    }
  }
}
</style>