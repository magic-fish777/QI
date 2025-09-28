<template>
  <div class="chat-records-page">
    <!-- 头部 -->
    <div class="page-header">
      <div class="header-content">
        <el-button
          icon="el-icon-arrow-left"
          circle
          size="small"
          @click="goBack"
        ></el-button>
        <h2 class="page-title">聊天记录</h2>
        <el-button
          icon="el-icon-search"
          circle
          size="small"
          @click="showSearch = true"
        ></el-button>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section" v-loading="statsLoading">
      <div class="stat-item">
        <div class="stat-value">{{ stats.totalRecords || 0 }}</div>
        <div class="stat-label">总对话</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ stats.totalRoles || 0 }}</div>
        <div class="stat-label">聊天角色</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ stats.todayRecords || 0 }}</div>
        <div class="stat-label">今日对话</div>
      </div>
    </div>

    <!-- 记录列表 -->
    <div class="records-list" v-loading="loading">
      <div
        v-for="record in records"
        :key="record.recordId"
        class="record-item"
        @click="viewRecord(record)"
      >
        <div class="record-avatar">
          <img :src="record.roleAvatar || defaultAvatar" :alt="record.roleName">
        </div>
        <div class="record-content">
          <div class="record-header">
            <span class="role-name">{{ record.roleName }}</span>
            <span class="record-time">{{ formatTime(record.chatTime) }}</span>
          </div>
          <div class="record-message">{{ truncateText(record.userInputContent) }}</div>
          <div class="record-reply">{{ truncateText(record.aiReplyContent) }}</div>
        </div>
        <i class="el-icon-arrow-right"></i>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && records.length === 0" class="empty-state">
        <i class="el-icon-chat-dot-square"></i>
        <p>暂无聊天记录</p>
        <el-button type="primary" @click="startChat">开始聊天</el-button>
      </div>
    </div>

    <!-- 加载更多 -->
    <div v-if="hasMore" class="load-more" @click="loadMore">
      <el-button :loading="loadingMore" size="small">加载更多</el-button>
    </div>

    <!-- 底部导航 -->
    <MobileNavigation />

    <!-- 搜索弹窗 -->
    <el-dialog
      title="搜索记录"
      :visible.sync="showSearch"
      width="90%"
      class="search-dialog"
    >
      <el-input
        v-model="searchQuery"
        placeholder="输入聊天内容..."
        prefix-icon="el-icon-search"
        @input="handleSearch"
      ></el-input>
      <div class="search-results" v-if="searchResults.length > 0">
        <div
          v-for="result in searchResults"
          :key="result.recordId"
          class="search-item"
          @click="viewSearchResult(result)"
        >
          <div class="search-role">{{ result.roleName }}</div>
          <div class="search-content">{{ result.userInputContent }}</div>
          <div class="search-time">{{ formatTime(result.chatTime) }}</div>
        </div>
      </div>
      <span slot="footer">
        <el-button @click="showSearch = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import MobileNavigation from '../components/MobileNavigation'
import { getUserChatRecords, getChatStatistics, searchChatRecords } from '@/api/chat/records'

export default {
  name: 'ChatRecords',
  components: {
    MobileNavigation
  },
  data() {
    return {
      // 数据
      records: [],
      stats: {},

      // 状态
      loading: true,
      statsLoading: true,
      loadingMore: false,
      hasMore: true,

      // 分页
      currentPage: 1,
      pageSize: 20,

      // 搜索
      showSearch: false,
      searchQuery: '',
      searchResults: [],

      // 默认头像
      defaultAvatar: require('@/assets/images/profile.jpg')
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      await Promise.all([
        this.loadStats(),
        this.loadRecords()
      ])
    },

    async loadStats() {
      try {
        this.statsLoading = true
        const response = await getChatStatistics()
        this.stats = response.data || {}
      } catch (error) {
        console.error('加载统计失败:', error)
      } finally {
        this.statsLoading = false
      }
    },

    async loadRecords() {
      try {
        this.loading = true
        const response = await getUserChatRecords({
          pageNum: this.currentPage,
          pageSize: this.pageSize
        })

        if (response && response.rows) {
          this.records = response.rows
          this.hasMore = response.rows.length === this.pageSize
        }
      } catch (error) {
        console.error('加载记录失败:', error)
        this.$message.error('加载聊天记录失败')
      } finally {
        this.loading = false
      }
    },

    async loadMore() {
      if (this.loadingMore || !this.hasMore) return

      try {
        this.loadingMore = true
        this.currentPage++
        const response = await getUserChatRecords({
          pageNum: this.currentPage,
          pageSize: this.pageSize
        })

        if (response && response.rows) {
          this.records.push(...response.rows)
          this.hasMore = response.rows.length === this.pageSize
        }
      } catch (error) {
        console.error('加载更多失败:', error)
      } finally {
        this.loadingMore = false
      }
    },

    async handleSearch() {
      if (!this.searchQuery.trim()) {
        this.searchResults = []
        return
      }

      try {
        const response = await searchChatRecords({
          keyword: this.searchQuery,
          pageSize: 10
        })
        this.searchResults = response.rows || []
      } catch (error) {
        console.error('搜索失败:', error)
      }
    },

    viewRecord(record) {
      // 跳转到聊天页面，并打开对应角色
      this.$router.push({
        path: '/chat',
        query: { roleId: record.roleId }
      })
    },

    viewSearchResult(result) {
      this.viewRecord(result)
      this.showSearch = false
      this.searchQuery = ''
    },

    goBack() {
      this.$router.go(-1)
    },

    startChat() {
      this.$router.push('/chat')
    },

    // 工具函数
    truncateText(text, length = 60) {
      if (!text) return ''
      return text.length > length ? text.substring(0, length) + '...' : text
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const now = new Date()
      const diff = now - date

      if (diff < 24 * 60 * 60 * 1000) {
        // 今天内显示时间
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } else if (diff < 7 * 24 * 60 * 60 * 1000) {
        // 一周内显示月日
        return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
      } else {
        // 更早显示年月日
        return date.toLocaleDateString('zh-CN')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-records-page {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 80px; // 底部导航栏空间
}

.page-header {
  background: white;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .page-title {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #333;
    }
  }
}

.stats-section {
  display: flex;
  padding: 16px;
  gap: 12px;

  .stat-item {
    flex: 1;
    background: white;
    border-radius: 12px;
    padding: 16px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .stat-value {
      font-size: 20px;
      font-weight: 600;
      color: #667eea;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 12px;
      color: #666;
    }
  }
}

.records-list {
  padding: 0 16px;

  .record-item {
    display: flex;
    align-items: center;
    padding: 16px;
    background: white;
    border-radius: 12px;
    margin-bottom: 12px;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.2s ease;

    &:hover {
      transform: translateY(-1px);
    }

    .record-avatar {
      margin-right: 12px;

      img {
        width: 36px;
        height: 36px;
        border-radius: 50%;
      }
    }

    .record-content {
      flex: 1;

      .record-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 6px;

        .role-name {
          font-size: 14px;
          font-weight: 500;
          color: #333;
        }

        .record-time {
          font-size: 12px;
          color: #999;
        }
      }

      .record-message {
        font-size: 13px;
        color: #666;
        margin-bottom: 4px;
      }

      .record-reply {
        font-size: 12px;
        color: #999;
        background: #f8f9fa;
        padding: 4px 8px;
        border-radius: 4px;
      }
    }

    .el-icon-arrow-right {
      color: #ccc;
      margin-left: 8px;
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;

  i {
    font-size: 48px;
    margin-bottom: 16px;
    opacity: 0.6;
  }

  p {
    font-size: 16px;
    margin-bottom: 20px;
  }
}

.load-more {
  text-align: center;
  padding: 20px;
}

// 搜索弹窗
.search-dialog {
  .search-results {
    max-height: 300px;
    overflow-y: auto;
    margin-top: 16px;

    .search-item {
      padding: 12px 0;
      cursor: pointer;
      border-bottom: 1px solid #f0f0f0;

      &:hover {
        background: #f8f9fa;
      }

      .search-role {
        font-size: 12px;
        color: #667eea;
        margin-bottom: 4px;
      }

      .search-content {
        font-size: 14px;
        color: #333;
        margin-bottom: 4px;
      }

      .search-time {
        font-size: 12px;
        color: #999;
      }
    }
  }
}
</style>