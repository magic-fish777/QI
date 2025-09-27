<template>
  <div class="character-grid">
    <div
      v-for="character in characters"
      :key="character.id"
      class="character-card"
      @click="$emit('select-character', character)"
    >
      <div class="character-avatar">
        <img :src="character.avatar" :alt="character.name">
        <div class="online-indicator" :class="{ online: character.online }"></div>
      </div>
      <div class="character-info">
        <div class="character-name">{{ character.name }}</div>
        <div class="character-desc">{{ character.description }}</div>
        <div class="character-meta">
          <span v-if="character.voiceDuration" class="voice-duration">
            <i class="el-icon-microphone"></i>
            {{ character.voiceDuration }}"
          </span>
          <span v-if="character.lastMessage" class="last-message-time">
            {{ formatTime(character.lastTime) }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MobileCharacterGrid',
  props: {
    characters: {
      type: Array,
      required: true
    }
  },
  emits: ['select-character'],
  methods: {
    formatTime(time) {
      const now = new Date()
      const messageTime = new Date(time)
      const diffMs = now - messageTime
      const diffMins = Math.floor(diffMs / 60000)
      const diffHours = Math.floor(diffMs / 3600000)
      const diffDays = Math.floor(diffMs / 86400000)

      if (diffMins < 1) return '刚刚'
      if (diffMins < 60) return `${diffMins}分钟前`
      if (diffHours < 24) return `${diffHours}小时前`
      if (diffDays < 7) return `${diffDays}天前`
      return messageTime.toLocaleDateString()
    }
  }
}
</script>

<style lang="scss" scoped>
.character-grid {
  flex: 1;
  padding: 0 16px;
  overflow-y: auto;

  .character-card {
    background: white;
    border-radius: 16px;
    padding: 16px;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    cursor: pointer;
    transition: transform 0.2s ease;

    &:hover {
      transform: translateY(-2px);
    }

    .character-avatar {
      position: relative;
      width: 50px;
      height: 50px;
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
        width: 12px;
        height: 12px;
        border-radius: 50%;
        background: #ccc;
        border: 2px solid white;

        &.online {
          background: #52c41a;
        }
      }
    }

    .character-info {
      flex: 1;

      .character-name {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }

      .character-desc {
        font-size: 14px;
        color: #666;
        margin-bottom: 8px;
      }

      .character-meta {
        display: flex;
        align-items: center;
        font-size: 12px;
        color: #999;

        .voice-duration {
          margin-right: 12px;

          i {
            margin-right: 4px;
          }
        }
      }
    }
  }
}
</style>