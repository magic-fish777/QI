<template>
  <div class="chat-header" :class="{ mobile: isMobile }">
    <!-- 移动端返回按钮 -->
    <div v-if="isMobile" class="back-btn" @click="$emit('back')">
      <i class="el-icon-arrow-left"></i>
    </div>

    <!-- 角色信息 -->
    <div class="character-info">
      <div class="character-avatar">
        <img :src="character.avatar" :alt="character.name">
        <div class="online-indicator" :class="{ online: character.online }"></div>
      </div>
      <div class="character-details">
        <div class="character-name">{{ character.name }}</div>
        <div class="character-status">
          {{ character.online ? '在线' : '离线' }}
          <span v-if="!isMobile"> • {{ character.description }}</span>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="chat-actions">
      <el-button
        type="text"
        :icon="isMobile ? 'el-icon-more' : 'el-icon-more'"
        @click="$emit('show-menu')"
      ></el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatHeader',
  props: {
    character: {
      type: Object,
      required: true
    },
    isMobile: {
      type: Boolean,
      default: false
    }
  },
  emits: ['back', 'show-menu']
}
</script>

<style lang="scss" scoped>
.chat-header {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #f0f0f0;

  &.mobile {
    padding: 12px 16px;
  }

  .back-btn {
    margin-right: 12px;
    padding: 8px;
    cursor: pointer;

    i {
      font-size: 20px;
      color: #666;
    }
  }

  .character-info {
    flex: 1;
    display: flex;
    align-items: center;

    .character-avatar {
      position: relative;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      overflow: hidden;
      margin-right: 12px;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .online-indicator {
        position: absolute;
        bottom: 2px;
        right: 2px;
        width: 10px;
        height: 10px;
        border-radius: 50%;
        background: #ccc;
        border: 2px solid white;

        &.online {
          background: #52c41a;
        }
      }
    }

    .character-details {
      .character-name {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }

      .character-status {
        font-size: 12px;
        color: #999;
      }
    }
  }

  .chat-actions {
    padding: 8px;
    cursor: pointer;

    .el-button {
      font-size: 18px;
      color: #666;
    }
  }
}
</style>