<template>
  <div class="chat-app">
    <!-- 检查登录状态 -->
    <div v-if="!isLoggedIn" class="login-redirect">
      <div class="login-prompt">
        <i class="el-icon-warning-outline"></i>
        <p>请先登录以使用AI对话功能</p>
        <el-button type="primary" @click="goToLogin">立即登录</el-button>
      </div>
    </div>

    <!-- 主要聊天界面 -->
    <div v-else class="chat-container">
      <!-- 移动端：角色列表页面 -->
      <div v-if="isMobile && !selectedCharacter" class="mobile-character-list">
        <!-- 顶部App头部 -->
        <div class="app-header">
          <div class="header-content">
            <div class="app-title">QIQI PLAY</div>
            <div class="user-menu" @click="showUserMenu = !showUserMenu">
              <div class="user-avatar">
                <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name">
              </div>
              <i class="el-icon-arrow-down"></i>
            </div>
          </div>
          <div class="search-section">
            <div class="search-bar" @click="showSearch = true">
              <i class="el-icon-search"></i>
              <span>搜索角色</span>
            </div>
          </div>
        </div>

        <!-- 角色卡片列表 -->
        <div class="character-grid">
          <div
            v-for="character in characters"
            :key="character.id"
            class="character-card"
            @click="selectCharacter(character)"
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
      </div>

      <!-- 桌面端：双栏布局 -->
      <div v-if="!isMobile" class="desktop-layout">
        <!-- 左侧角色列表 -->
        <div class="sidebar">
          <div class="sidebar-header">
            <div class="app-info">
              <div class="app-title">QIQI PLAY</div>
              <div class="user-info">
                <div class="user-avatar" @click="showUserMenu = !showUserMenu">
                  <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name">
                </div>
                <div class="user-details">
                  <div class="user-name">{{ userInfo.name }}</div>
                  <div class="user-status">在线</div>
                </div>
              </div>
            </div>


            <div class="search-section">
              <div class="search-input">
                <i class="el-icon-search"></i>
                <input v-model="searchQuery" placeholder="搜索角色..." />
              </div>
            </div>
          </div>

          <!-- 左侧内容区域 -->
          <div class="sidebar-content">
            <!-- 角色列表 (主页) -->
            <div v-if="currentTab === 'home'" class="character-list">
              <div
                v-for="character in filteredCharacters"
                :key="character.id"
                class="character-item"
                :class="{ active: selectedCharacter && selectedCharacter.id === character.id }"
                @click="selectCharacter(character)"
              >
                <div class="character-avatar">
                  <img :src="character.avatar" :alt="character.name">
                  <div class="online-indicator" :class="{ online: character.online }"></div>
                </div>
                <div class="character-info">
                  <div class="character-name">{{ character.name }}</div>
                  <div class="character-desc">{{ character.description }}</div>
                  <div class="last-message" v-if="character.lastMessage">
                    {{ character.lastMessage }}
                  </div>
                </div>
                <div class="character-meta">
                  <div class="message-time" v-if="character.lastTime">
                    {{ formatTime(character.lastTime) }}
                  </div>
                  <div v-if="character.voiceDuration" class="voice-duration">
                    <i class="el-icon-microphone"></i>
                    {{ character.voiceDuration }}"
                  </div>
                </div>
              </div>
            </div>

            <!-- 权益页面 -->
            <div v-if="currentTab === 'benefits'" class="sidebar-benefits">
              <div class="benefits-header">
                <h3>会员权益</h3>
              </div>
              <div class="user-level-info">
                <div class="level-badge">{{ userLevel }}</div>
                <div class="level-name">{{ userLevelName }}</div>
                <div class="upgrade-btn" @click="showUpgrade">
                  <i class="el-icon-top"></i>
                  升级
                </div>
              </div>
              <div class="benefits-stats">
                <div class="stat-item">
                  <div class="stat-label">每日对话</div>
                  <div class="stat-value">{{ dailyChats }}/{{ maxDailyChats }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">语音功能</div>
                  <div class="stat-value">{{ hasVoiceAccess ? '已开启' : '未开启' }}</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">图片生成</div>
                  <div class="stat-value">{{ hasImageAccess ? '已开启' : '未开启' }}</div>
                </div>
              </div>
            </div>

            <!-- 个人资料页面 -->
            <div v-if="currentTab === 'profile'" class="sidebar-profile">
              <div class="profile-header">
                <h3>个人中心</h3>
              </div>
              <div class="user-card">
                <div class="user-avatar">
                  <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name">
                </div>
                <div class="user-info">
                  <div class="user-name">{{ userInfo.username || userInfo.name }}</div>
                  <div class="user-id">ID: {{ userInfo.userId || '123456' }}</div>
                  <div class="user-level">{{ userLevelName }}</div>
                </div>
              </div>
              <div class="profile-menu">
                <div class="menu-item" @click="editProfile">
                  <i class="el-icon-edit"></i>
                  <span>编辑资料</span>
                </div>
                <div class="menu-item" @click="viewHistory">
                  <i class="el-icon-time"></i>
                  <span>聊天记录</span>
                </div>
                <div class="menu-item" @click="viewFavorites">
                  <i class="el-icon-star-on"></i>
                  <span>我的收藏</span>
                </div>
                <div class="menu-item" @click="contactSupport">
                  <i class="el-icon-service"></i>
                  <span>客服支持</span>
                </div>
              </div>
            </div>

            <!-- 设置页面 -->
            <div v-if="currentTab === 'settings'" class="sidebar-settings">
              <div class="settings-header">
                <h3>设置</h3>
              </div>
              <div class="settings-groups">
                <div class="settings-group">
                  <div class="group-title">聊天设置</div>
                  <div class="setting-item">
                    <span>语音功能</span>
                    <el-switch v-model="settings.voiceEnabled" size="mini"></el-switch>
                  </div>
                  <div class="setting-item">
                    <span>消息通知</span>
                    <el-switch v-model="settings.notificationEnabled" size="mini"></el-switch>
                  </div>
                  <div class="setting-item">
                    <span>深色模式</span>
                    <el-switch v-model="settings.darkMode" size="mini"></el-switch>
                  </div>
                </div>
                <div class="settings-group">
                  <div class="group-title">隐私设置</div>
                  <div class="setting-item">
                    <span>聊天记录加密</span>
                    <el-switch v-model="settings.encryptChats" size="mini"></el-switch>
                  </div>
                  <div class="setting-item">
                    <span>自动清理记录</span>
                    <el-switch v-model="settings.autoCleanChats" size="mini"></el-switch>
                  </div>
                </div>
                <div class="settings-group">
                  <div class="group-title">关于</div>
                  <div class="menu-item" @click="showAbout">
                    <span>关于QIQI PLAY</span>
                    <i class="el-icon-arrow-right"></i>
                  </div>
                  <div class="menu-item" @click="showPrivacy">
                    <span>隐私政策</span>
                    <i class="el-icon-arrow-right"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 桌面端导航栏 -->
          <div class="sidebar-navigation">
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
        </div>

        <!-- 右侧主聊天区域 -->
        <div class="main-chat">
            <div v-if="!selectedCharacter" class="welcome-screen">
              <div class="welcome-content">
                <i class="el-icon-chat-line-round"></i>
                <h3>欢迎来到 QIQI PLAY</h3>
                <p>选择一个角色开始你的AI对话之旅</p>
              </div>
            </div>

            <div v-else class="chat-interface">
            <!-- 聊天头部 -->
            <div class="chat-header">
              <div class="character-info">
                <div class="character-avatar">
                  <img :src="selectedCharacter.avatar" :alt="selectedCharacter.name">
                  <div class="online-indicator" :class="{ online: selectedCharacter.online }"></div>
                </div>
                <div class="character-details">
                  <div class="character-name">{{ selectedCharacter.name }}</div>
                  <div class="character-status">
                    {{ selectedCharacter.online ? '在线' : '离线' }} • {{ selectedCharacter.description }}
                  </div>
                </div>
              </div>
              <div class="chat-actions">
                <el-button type="text" icon="el-icon-more" @click="showChatMenu = true"></el-button>
              </div>
            </div>

            <!-- 消息区域 -->
            <div class="messages-area" ref="messagesArea">
              <div
                v-for="message in currentMessages"
                :key="message.id"
                class="message-item"
                :class="{ 'user-message': message.isUser }"
              >
                <div class="message-avatar">
                  <img
                    :src="message.isUser ? (userInfo.avatar || defaultAvatar) : selectedCharacter.avatar"
                    :alt="message.isUser ? userInfo.name : selectedCharacter.name"
                  >
                </div>
                <div class="message-content">
                  <div class="message-bubble">
                    {{ message.content }}
                  </div>
                  <div class="message-meta">
                    <span class="message-time">{{ formatMessageTime(message.time) }}</span>
                    <span v-if="message.voiceDuration" class="voice-duration">
                      <i class="el-icon-microphone"></i>
                      {{ message.voiceDuration }}"
                    </span>
                  </div>
                </div>
              </div>

              <!-- 输入状态指示器 -->
              <div v-if="isTyping" class="typing-indicator">
                <div class="typing-avatar">
                  <img :src="selectedCharacter.avatar" :alt="selectedCharacter.name">
                </div>
                <div class="typing-bubble">
                  <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 输入区域 -->
            <div class="input-area">
              <div class="input-tools">
                <el-button
                  type="text"
                  icon="el-icon-microphone"
                  :class="{ active: isVoiceMode }"
                  @click="toggleVoiceMode"
                ></el-button>
                <el-button type="text" icon="el-icon-picture" @click="selectImage"></el-button>
              </div>
              <div class="input-wrapper">
                <el-input
                  v-model="inputMessage"
                  type="textarea"
                  :autosize="{ minRows: 1, maxRows: 4 }"
                  placeholder="输入消息..."
                  @keyup.enter.native="handleSendMessage"
                  @input="handleTyping"
                ></el-input>
                <el-button
                  type="primary"
                  icon="el-icon-position"
                  :disabled="!canSend"
                  @click="sendMessage"
                ></el-button>
              </div>
            </div>
        </div>
      </div>

      <!-- 移动端：聊天页面 -->
      <div v-if="isMobile && selectedCharacter" class="mobile-chat">
        <!-- 聊天头部 -->
        <div class="mobile-chat-header">
          <div class="back-btn" @click="backToCharacterList">
            <i class="el-icon-arrow-left"></i>
          </div>
          <div class="character-info">
            <div class="character-avatar">
              <img :src="selectedCharacter.avatar" :alt="selectedCharacter.name">
              <div class="online-indicator" :class="{ online: selectedCharacter.online }"></div>
            </div>
            <div class="character-details">
              <div class="character-name">{{ selectedCharacter.name }}</div>
              <div class="character-status">{{ selectedCharacter.online ? '在线' : '离线' }}</div>
            </div>
          </div>
          <div class="chat-actions">
            <i class="el-icon-more" @click="showChatMenu = true"></i>
          </div>
        </div>

        <!-- 移动端消息区域 -->
        <div class="mobile-messages-area" ref="mobileMessagesArea">
          <div
            v-for="message in currentMessages"
            :key="message.id"
            class="message-item"
            :class="{ 'user-message': message.isUser }"
          >
            <div class="message-avatar">
              <img
                :src="message.isUser ? (userInfo.avatar || defaultAvatar) : selectedCharacter.avatar"
                :alt="message.isUser ? userInfo.name : selectedCharacter.name"
              >
            </div>
            <div class="message-content">
              <div class="message-bubble">
                {{ message.content }}
              </div>
              <div class="message-meta">
                <span class="message-time">{{ formatMessageTime(message.time) }}</span>
                <span v-if="message.voiceDuration" class="voice-duration">
                  <i class="el-icon-microphone"></i>
                  {{ message.voiceDuration }}"
                </span>
              </div>
            </div>
          </div>

          <!-- 移动端输入状态指示器 -->
          <div v-if="isTyping" class="typing-indicator">
            <div class="typing-avatar">
              <img :src="selectedCharacter.avatar" :alt="selectedCharacter.name">
            </div>
            <div class="typing-bubble">
              <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 移动端输入区域 -->
        <div class="mobile-input-area">
          <div class="input-tools">
            <el-button
              type="text"
              icon="el-icon-microphone"
              :class="{ active: isVoiceMode }"
              @click="toggleVoiceMode"
            ></el-button>
          </div>
          <div class="input-wrapper">
            <textarea
              v-model="inputMessage"
              placeholder="输入消息..."
              @keyup.enter="handleSendMessage"
              @input="handleTyping"
              rows="1"
              class="message-input"
            ></textarea>
            <div class="send-btn" @click="sendMessage" :class="{ disabled: !canSend }">
              <i class="el-icon-position"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部导航栏 (移动端) -->
    <div v-if="isMobile && isLoggedIn" class="bottom-navigation">
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

    <!-- 权益页面 -->
    <div v-if="isMobile && currentTab === 'benefits'" class="benefits-page">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-medal"></i>
          <span>会员权益</span>
        </div>
      </div>
      <div class="benefits-content">
        <div class="membership-card">
          <div class="card-header">
            <div class="user-level">
              <span class="level-badge">{{ userLevel }}</span>
              <span class="level-name">{{ userLevelName }}</span>
            </div>
            <div class="upgrade-btn" @click="showUpgrade">
              <i class="el-icon-top"></i>
              <span>升级</span>
            </div>
          </div>
          <div class="card-content">
            <div class="benefit-item">
              <i class="el-icon-chat-line-round"></i>
              <span>每日对话次数：{{ dailyChats }}/{{ maxDailyChats }}</span>
            </div>
            <div class="benefit-item">
              <i class="el-icon-microphone"></i>
              <span>语音功能</span>
              <span class="status" :class="{ enabled: hasVoiceAccess }">
                {{ hasVoiceAccess ? '已开启' : '未开启' }}
              </span>
            </div>
            <div class="benefit-item">
              <i class="el-icon-picture"></i>
              <span>图片生成</span>
              <span class="status" :class="{ enabled: hasImageAccess }">
                {{ hasImageAccess ? '已开启' : '未开启' }}
              </span>
            </div>
          </div>
        </div>

        <div class="benefits-list">
          <div class="section-title">会员特权</div>
          <div class="benefit-card" v-for="benefit in memberBenefits" :key="benefit.id">
            <div class="benefit-icon">
              <i :class="benefit.icon"></i>
            </div>
            <div class="benefit-info">
              <div class="benefit-title">{{ benefit.title }}</div>
              <div class="benefit-desc">{{ benefit.description }}</div>
            </div>
            <div class="benefit-status">
              <span :class="{ enabled: benefit.enabled }">
                {{ benefit.enabled ? '已开启' : '升级解锁' }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 个人资料页面 -->
    <div v-if="isMobile && currentTab === 'profile'" class="profile-page">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-user"></i>
          <span>个人中心</span>
        </div>
      </div>
      <div class="profile-content">
        <div class="user-card">
          <div class="user-avatar-large">
            <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name">
            <div class="edit-avatar" @click="editAvatar">
              <i class="el-icon-camera"></i>
            </div>
          </div>
          <div class="user-details">
            <div class="user-name">{{ userInfo.username || userInfo.name }}</div>
            <div class="user-id">ID: {{ userInfo.userId || '123456' }}</div>
            <div class="user-level-badge">{{ userLevelName }}</div>
          </div>
        </div>

        <div class="profile-menu">
          <div class="menu-item" @click="editProfile">
            <div class="menu-icon">
              <i class="el-icon-edit"></i>
            </div>
            <div class="menu-content">
              <span>编辑资料</span>
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
          <div class="menu-item" @click="viewHistory">
            <div class="menu-icon">
              <i class="el-icon-time"></i>
            </div>
            <div class="menu-content">
              <span>聊天记录</span>
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
          <div class="menu-item" @click="viewFavorites">
            <div class="menu-icon">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="menu-content">
              <span>我的收藏</span>
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
          <div class="menu-item" @click="contactSupport">
            <div class="menu-icon">
              <i class="el-icon-service"></i>
            </div>
            <div class="menu-content">
              <span>客服支持</span>
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
        </div>

        <div class="logout-section">
          <el-button type="danger" size="medium" @click="handleLogout" plain>
            <i class="el-icon-switch-button"></i>
            退出登录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 设置页面 -->
    <div v-if="isMobile && currentTab === 'settings'" class="settings-page">
      <div class="page-header">
        <div class="header-title">
          <i class="el-icon-setting"></i>
          <span>设置</span>
        </div>
      </div>
      <div class="settings-content">
        <div class="settings-section">
          <div class="section-title">聊天设置</div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-microphone"></i>
              <span>语音功能</span>
            </div>
            <el-switch v-model="settings.voiceEnabled"></el-switch>
          </div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-bell"></i>
              <span>消息通知</span>
            </div>
            <el-switch v-model="settings.notificationEnabled"></el-switch>
          </div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-view"></i>
              <span>深色模式</span>
            </div>
            <el-switch v-model="settings.darkMode"></el-switch>
          </div>
        </div>

        <div class="settings-section">
          <div class="section-title">隐私设置</div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-lock"></i>
              <span>聊天记录加密</span>
            </div>
            <el-switch v-model="settings.encryptChats"></el-switch>
          </div>
          <div class="setting-item">
            <div class="setting-label">
              <i class="el-icon-time"></i>
              <span>自动清理聊天记录</span>
            </div>
            <el-switch v-model="settings.autoCleanChats"></el-switch>
          </div>
        </div>

        <div class="settings-section">
          <div class="section-title">关于</div>
          <div class="setting-item clickable" @click="showAbout">
            <div class="setting-label">
              <i class="el-icon-info"></i>
              <span>关于QIQI PLAY</span>
            </div>
            <i class="el-icon-arrow-right"></i>
          </div>
          <div class="setting-item clickable" @click="showPrivacy">
            <div class="setting-label">
              <i class="el-icon-document"></i>
              <span>隐私政策</span>
            </div>
            <i class="el-icon-arrow-right"></i>
          </div>
          <div class="setting-item clickable" @click="showTerms">
            <div class="setting-label">
              <i class="el-icon-document-checked"></i>
              <span>服务条款</span>
            </div>
            <i class="el-icon-arrow-right"></i>
          </div>
        </div>
      </div>
    </div>
    </div>

    <!-- 搜索弹窗 -->
    <el-dialog
      title="搜索角色"
      :visible.sync="showSearch"
      width="400px"
      :show-close="false"
      :modal-append-to-body="false"
      class="search-dialog"
    >
      <el-input
        v-model="searchQuery"
        placeholder="输入角色名称..."
        prefix-icon="el-icon-search"
        @input="handleSearch"
      ></el-input>
      <div slot="footer">
        <el-button @click="showSearch = false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 用户菜单弹窗 -->
    <el-popover
      v-model="showUserMenu"
      placement="bottom-end"
      width="200"
      trigger="manual"
    >
      <div class="user-menu-content">
        <div class="menu-item" @click="goToProfile">
          <i class="el-icon-user"></i>
          <span>个人资料</span>
        </div>
        <div class="menu-item" @click="goToSettings">
          <i class="el-icon-setting"></i>
          <span>设置</span>
        </div>
        <div class="menu-divider"></div>
        <div class="menu-item logout" @click="handleLogout">
          <i class="el-icon-switch-button"></i>
          <span>退出登录</span>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script>
export default {
  name: 'ChatInterface',
  data() {
    return {
      // 设备检测
      isMobile: false,

      // 登录状态
      isLoggedIn: false,
      userInfo: {
        name: '用户',
        avatar: ''
      },

      // 界面状态
      selectedCharacter: null,
      showSearch: false,
      showUserMenu: false,
      showChatMenu: false,
      showCreateCharacter: false,
      currentTab: 'home', // 当前选中的底部导航标签

      // 聊天状态
      inputMessage: '',
      isTyping: false,
      isVoiceMode: false,
      searchQuery: '',

      // 用户等级信息
      userLevel: 'VIP1',
      userLevelName: '普通会员',
      dailyChats: 45,
      maxDailyChats: 100,
      hasVoiceAccess: true,
      hasImageAccess: false,

      // 会员权益
      memberBenefits: [
        {
          id: 1,
          icon: 'el-icon-chat-line-round',
          title: '无限对话',
          description: '每日对话次数不受限制',
          enabled: false
        },
        {
          id: 2,
          icon: 'el-icon-microphone',
          title: '语音对话',
          description: '支持语音输入和语音回复',
          enabled: true
        },
        {
          id: 3,
          icon: 'el-icon-picture',
          title: 'AI画图',
          description: '生成精美的AI艺术作品',
          enabled: false
        },
        {
          id: 4,
          icon: 'el-icon-cpu',
          title: '优先体验',
          description: '优先体验最新AI模型',
          enabled: false
        },
        {
          id: 5,
          icon: 'el-icon-download',
          title: '对话导出',
          description: '导出聊天记录到本地',
          enabled: false
        }
      ],

      // 设置选项
      settings: {
        voiceEnabled: true,
        notificationEnabled: true,
        darkMode: false,
        encryptChats: false,
        autoCleanChats: false
      },

      // 默认头像
      defaultAvatar: require('@/assets/images/profile.jpg'),

      // 角色数据
      characters: [
        {
          id: 1,
          name: '哈利波特',
          description: '小说/电影角色',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 9,
          lastMessage: '你好，有什么可以帮助你的吗？',
          lastTime: new Date(Date.now() - 300000)
        },
        {
          id: 2,
          name: '苏格拉底',
          description: '古希腊哲学家',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 3,
          lastMessage: '智慧始于承认自己的无知',
          lastTime: new Date(Date.now() - 600000)
        },
        {
          id: 3,
          name: '初音未来',
          description: '虚拟角色',
          avatar: require('@/assets/images/profile.jpg'),
          online: false,
          voiceDuration: 7,
          lastMessage: '♪ 音乐是灵魂的语言',
          lastTime: new Date(Date.now() - 1800000)
        },
        {
          id: 4,
          name: '易烊千玺',
          description: '电影明星',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 11,
          lastMessage: '我在拍戏，祝你生活顺利~',
          lastTime: new Date(Date.now() - 3600000)
        },
        {
          id: 5,
          name: '迪丽热巴',
          description: '电影明星',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 11,
          lastMessage: '今天心情很不错呢！',
          lastTime: new Date(Date.now() - 7200000)
        },
        {
          id: 6,
          name: '刘晓燕',
          description: '英语老师',
          avatar: require('@/assets/images/profile.jpg'),
          online: true,
          voiceDuration: 11,
          lastMessage: '你好同学，欢迎咨询...',
          lastTime: new Date(Date.now() - 10800000)
        }
      ],

      // 当前聊天消息
      currentMessages: []
    }
  },
  computed: {
    // 过滤后的角色列表
    filteredCharacters() {
      if (!this.searchQuery) {
        return this.characters
      }
      return this.characters.filter(character =>
        character.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        character.description.toLowerCase().includes(this.searchQuery.toLowerCase())
      )
    },

    // 是否可以发送消息
    canSend() {
      return this.inputMessage.trim().length > 0
    }
  },
  mounted() {
    this.checkDevice()
    this.checkLoginStatus()
    window.addEventListener('resize', this.checkDevice)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkDevice)
  },
  methods: {
    // 检测设备类型
    checkDevice() {
      this.isMobile = window.innerWidth <= 768
    },

    // 检查登录状态
    async checkLoginStatus() {
      try {
        const token = localStorage.getItem('token')
        const loginStatus = localStorage.getItem('isLoggedIn')
        const userInfo = localStorage.getItem('userInfo')

        console.log('==> 聊天页面检查登录状态, token:', token ? '存在' : '不存在', ', isLoggedIn:', loginStatus)

        // 基础检查
        if (!token || loginStatus !== 'true') {
          console.log('==> 未登录或token不存在')
          this.isLoggedIn = false
          return
        }

        // 验证token有效性
        console.log('==> 验证token有效性')
        const { getAiUserInfo } = await import('@/api/ai/auth')
        const response = await getAiUserInfo()

        if (response && response.code === 200) {
          console.log('==> token有效，保持登录状态')
          this.isLoggedIn = true

          if (userInfo) {
            this.userInfo = JSON.parse(userInfo)
          }

          // 可以在这里更新用户信息
          if (response.user) {
            this.userInfo = { ...this.userInfo, ...response.user }
          }
        } else {
          console.log('==> token无效，清除登录状态')
          this.clearLoginStatus()
        }
      } catch (error) {
        console.log('==> token验证失败，清除登录状态:', error.message)
        this.clearLoginStatus()
      }
    },

    // 清除登录状态
    clearLoginStatus() {
      localStorage.removeItem('token')
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('loginType')
      localStorage.removeItem('userInfo')
      this.isLoggedIn = false
      console.log('==> 已清除登录状态')

      // 延迟跳转到登录页面，给用户看到提示
      setTimeout(() => {
        this.$router.push('/ai/login')
      }, 2000)
    },

    // 跳转到登录页面
    goToLogin() {
      this.$router.push('/ai/login')
    },

    // 选择角色
    selectCharacter(character) {
      this.selectedCharacter = character
      this.loadMessages(character.id)
    },

    // 移动端返回角色列表
    backToCharacterList() {
      this.selectedCharacter = null
      this.currentMessages = []
    },

    // 加载消息历史
    loadMessages(characterId) {
      // 模拟加载消息
      const mockMessages = [
        {
          id: 1,
          content: '你好！',
          isUser: true,
          time: new Date(Date.now() - 600000)
        },
        {
          id: 2,
          content: `你好！我是${this.selectedCharacter.name}，很高兴认识你！`,
          isUser: false,
          time: new Date(Date.now() - 590000),
          voiceDuration: this.selectedCharacter.voiceDuration
        }
      ]
      this.currentMessages = mockMessages
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    // 发送消息
    sendMessage() {
      if (!this.canSend) return

      const newMessage = {
        id: Date.now(),
        content: this.inputMessage,
        isUser: true,
        time: new Date()
      }

      this.currentMessages.push(newMessage)
      this.inputMessage = ''

      // 模拟AI回复
      this.isTyping = true
      setTimeout(() => {
        this.isTyping = false
        const aiReply = {
          id: Date.now() + 1,
          content: this.getRandomReply(),
          isUser: false,
          time: new Date(),
          voiceDuration: Math.floor(Math.random() * 10) + 3
        }
        this.currentMessages.push(aiReply)
        this.scrollToBottom()
      }, 2000)

      this.scrollToBottom()
    },

    // 处理发送消息快捷键
    handleSendMessage(event) {
      if (this.isMobile) {
        if (!event.shiftKey) {
          event.preventDefault()
          this.sendMessage()
        }
      } else {
        if (!event.ctrlKey) {
          event.preventDefault()
          this.sendMessage()
        }
      }
    },

    // 处理输入状态
    handleTyping() {
      // 可以添加输入状态处理逻辑
    },

    // 切换语音模式
    toggleVoiceMode() {
      this.isVoiceMode = !this.isVoiceMode
    },

    // 选择图片
    selectImage() {
      this.$message.info('图片上传功能开发中...')
    },

    // 处理搜索
    handleSearch() {
      // 搜索逻辑已在computed中实现
    },

    // 滚动到底部
    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messagesArea || this.$refs.mobileMessagesArea
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return ''
      const now = new Date()
      const diff = now - time
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (days > 0) return `${days}天前`
      if (hours > 0) return `${hours}小时前`
      if (minutes > 0) return `${minutes}分钟前`
      return '刚刚'
    },

    // 格式化消息时间
    formatMessageTime(time) {
      if (!time) return ''
      const hours = time.getHours().toString().padStart(2, '0')
      const minutes = time.getMinutes().toString().padStart(2, '0')
      return `${hours}:${minutes}`
    },

    // 获取随机回复
    getRandomReply() {
      const replies = [
        '这是一个很有趣的问题！',
        '让我思考一下...',
        '你说得很有道理。',
        '我理解你的想法。',
        '这确实值得深入探讨。'
      ]
      return replies[Math.floor(Math.random() * replies.length)]
    },

    // 跳转到个人资料
    goToProfile() {
      this.showUserMenu = false
      this.$message.info('个人资料页面开发中...')
    },

    // 跳转到设置
    goToSettings() {
      this.showUserMenu = false
      this.currentTab = 'settings'
    },

    // 切换底部导航标签
    switchTab(tab) {
      this.currentTab = tab
      this.showUserMenu = false
      this.showSearch = false

      // 如果切换到主页，显示角色列表
      if (tab === 'home') {
        this.selectedCharacter = null
      }
    },

    // 权益相关方法
    showUpgrade() {
      this.$message.info('会员升级功能开发中...')
    },

    // 个人资料相关方法
    editAvatar() {
      this.$message.info('头像编辑功能开发中...')
    },

    editProfile() {
      this.$message.info('资料编辑功能开发中...')
    },

    viewHistory() {
      this.$message.info('聊天记录功能开发中...')
    },

    viewFavorites() {
      this.$message.info('收藏功能开发中...')
    },

    contactSupport() {
      this.$message.info('客服支持功能开发中...')
    },

    // 设置相关方法
    showAbout() {
      this.$message.info('关于页面开发中...')
    },

    showPrivacy() {
      this.$message.info('隐私政策页面开发中...')
    },

    showTerms() {
      this.$message.info('服务条款页面开发中...')
    },

    // 处理退出登录
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 清除登录状态
        localStorage.removeItem('isLoggedIn')
        localStorage.removeItem('loginType')
        localStorage.removeItem('userInfo')

        // 重置状态
        this.isLoggedIn = false
        this.userInfo = { name: '用户', avatar: '' }
        this.selectedCharacter = null
        this.currentMessages = []
        this.showUserMenu = false

        this.$message.success('已退出登录')

        // 跳转到登录页
        this.$router.push('/ai/login')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-app {
  height: 100vh;
  background: #f8f9fa;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

// 登录重定向提示
.login-redirect {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .login-prompt {
    text-align: center;
    color: white;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(20px);
    padding: 40px;
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.2);

    i {
      font-size: 48px;
      margin-bottom: 16px;
    }

    p {
      font-size: 16px;
      margin: 16px 0 24px;
    }

    .el-button {
      background: white;
      color: #667eea;
      border: none;
      font-weight: 600;

      &:hover {
        background: rgba(255, 255, 255, 0.9);
      }
    }
  }
}

// 主聊天容器
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

// 移动端角色列表
.mobile-character-list {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .app-header {
    padding: 20px 16px 16px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .app-title {
        font-size: 28px;
        font-weight: bold;
        color: white;
        letter-spacing: 2px;
      }

      .user-menu {
        display: flex;
        align-items: center;
        cursor: pointer;

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
          color: white;
          font-size: 12px;
        }
      }
    }

    .search-section {
      .search-bar {
        display: flex;
        align-items: center;
        background: rgba(255, 255, 255, 0.9);
        border-radius: 20px;
        padding: 12px 16px;
        cursor: pointer;

        i {
          color: #999;
          margin-right: 8px;
        }

        span {
          color: #999;
          flex: 1;
        }
      }
    }
  }

  .character-grid {
    flex: 1;
    padding: 8px;
    overflow-y: auto;

    .character-card {
      background: rgba(255, 255, 255, 0.95);
      margin-bottom: 8px;
      padding: 16px;
      border-radius: 16px;
      cursor: pointer;
      transition: all 0.2s ease;
      display: flex;
      align-items: center;

      &:active {
        transform: scale(0.98);
      }

      .character-avatar {
        position: relative;
        margin-right: 12px;

        img {
          width: 56px;
          height: 56px;
          border-radius: 50%;
          object-fit: cover;
        }

        .online-indicator {
          position: absolute;
          bottom: 2px;
          right: 2px;
          width: 14px;
          height: 14px;
          border-radius: 50%;
          background: #ddd;
          border: 2px solid white;

          &.online {
            background: #4CAF50;
          }
        }
      }

      .character-info {
        flex: 1;

        .character-name {
          font-size: 16px;
          font-weight: 600;
          color: #333;
          margin-bottom: 2px;
        }

        .character-desc {
          font-size: 12px;
          color: #666;
          margin-bottom: 6px;
        }

        .character-meta {
          display: flex;
          align-items: center;
          gap: 12px;
          font-size: 11px;

          .voice-duration {
            color: #4CAF50;
            display: flex;
            align-items: center;

            i {
              margin-right: 2px;
            }
          }

          .last-message-time {
            color: #999;
          }
        }
      }
    }
  }
}

// 桌面端布局
.desktop-layout {
  display: flex;
  height: 100vh;

  .sidebar {
    width: 320px;
    background: white;
    border-right: 1px solid #e8e8e8;
    display: flex;
    flex-direction: column;

    .sidebar-header {
      padding: 20px;
      border-bottom: 1px solid #f0f0f0;

      .app-info {
        margin-bottom: 16px;

        .app-title {
          font-size: 20px;
          font-weight: bold;
          color: #333;
          margin-bottom: 12px;
          letter-spacing: 1px;
        }

        .user-info {
          display: flex;
          align-items: center;

          .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            overflow: hidden;
            margin-right: 12px;
            cursor: pointer;

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }

          .user-details {
            .user-name {
              font-size: 14px;
              font-weight: 600;
              color: #333;
              margin-bottom: 2px;
            }

            .user-status {
              font-size: 12px;
              color: #4CAF50;
            }
          }
        }
      }

      .search-section {
        .search-input {
          display: flex;
          align-items: center;
          background: #f8f9fa;
          border-radius: 12px;
          padding: 10px 12px;

          i {
            color: #999;
            margin-right: 8px;
          }

          input {
            flex: 1;
            border: none;
            background: transparent;
            outline: none;
            font-size: 14px;
            color: #333;

            &::placeholder {
              color: #999;
            }
          }
        }
      }
    }

    .character-list {
      flex: 1;
      overflow-y: auto;

      .character-item {
        display: flex;
        align-items: center;
        padding: 16px 20px;
        cursor: pointer;
        border-bottom: 1px solid #f8f8f8;
        transition: background-color 0.2s;

        &:hover {
          background: #f8f8f8;
        }

        &.active {
          background: #e6f7ff;
          border-right: 3px solid #1890ff;
        }

        .character-avatar {
          position: relative;
          margin-right: 12px;

          img {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            object-fit: cover;
          }

          .online-indicator {
            position: absolute;
            bottom: 2px;
            right: 2px;
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: #ddd;
            border: 2px solid white;

            &.online {
              background: #4CAF50;
            }
          }
        }

        .character-info {
          flex: 1;
          min-width: 0;

          .character-name {
            font-size: 14px;
            font-weight: 600;
            color: #333;
            margin-bottom: 2px;
          }

          .character-desc {
            font-size: 12px;
            color: #666;
            margin-bottom: 4px;
          }

          .last-message {
            font-size: 12px;
            color: #888;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .character-meta {
          text-align: right;

          .message-time {
            font-size: 11px;
            color: #ccc;
            margin-bottom: 4px;
          }

          .voice-duration {
            font-size: 11px;
            color: #4CAF50;
            display: flex;
            align-items: center;
            justify-content: flex-end;

            i {
              margin-right: 2px;
            }
          }
        }
      }
    }

    // 左侧内容区域
    .sidebar-content {
      flex: 1;
      overflow-y: auto;
      padding: 0;
    }

    // 侧边栏导航
    .sidebar-navigation {
      padding: 16px 12px;
      border-top: 1px solid #f0f0f0;
      background: white;

      .nav-items {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 8px;

        .nav-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 8px 4px;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s ease;
          font-size: 12px;
          color: #666;

          &:hover {
            background: #f8f9fa;
            color: #333;
          }

          &.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;

            i {
              color: white;
            }
          }

          i {
            font-size: 16px;
            margin-bottom: 4px;
            color: #666;
            transition: color 0.3s ease;
          }

          span {
            font-weight: 500;
          }
        }
      }
    }

    // 侧边栏权益页面
    .sidebar-benefits {
      padding: 20px;

      .benefits-header {
        margin-bottom: 20px;

        h3 {
          font-size: 16px;
          color: #333;
          margin: 0;
          font-weight: 600;
        }
      }

      .user-level-info {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12px;
        padding: 16px;
        color: white;
        margin-bottom: 16px;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .level-badge {
          background: rgba(255, 255, 255, 0.2);
          padding: 4px 8px;
          border-radius: 8px;
          font-size: 12px;
          margin-right: 8px;
        }

        .level-name {
          font-size: 14px;
          font-weight: 600;
          flex: 1;
        }

        .upgrade-btn {
          background: rgba(255, 255, 255, 0.2);
          padding: 6px 12px;
          border-radius: 16px;
          cursor: pointer;
          font-size: 12px;
          display: flex;
          align-items: center;

          &:hover {
            background: rgba(255, 255, 255, 0.3);
          }

          i {
            margin-right: 4px;
          }
        }
      }

      .benefits-stats {
        .stat-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .stat-label {
            font-size: 13px;
            color: #666;
          }

          .stat-value {
            font-size: 13px;
            color: #333;
            font-weight: 500;
          }
        }
      }
    }

    // 侧边栏个人资料页面
    .sidebar-profile {
      padding: 20px;

      .profile-header {
        margin-bottom: 20px;

        h3 {
          font-size: 16px;
          color: #333;
          margin: 0;
          font-weight: 600;
        }
      }

      .user-card {
        background: #f8f9fa;
        border-radius: 12px;
        padding: 16px;
        margin-bottom: 16px;
        display: flex;
        align-items: center;

        .user-avatar {
          width: 48px;
          height: 48px;
          border-radius: 50%;
          overflow: hidden;
          margin-right: 12px;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .user-info {
          flex: 1;

          .user-name {
            font-size: 14px;
            font-weight: 600;
            color: #333;
            margin-bottom: 4px;
          }

          .user-id {
            font-size: 11px;
            color: #999;
            margin-bottom: 6px;
          }

          .user-level {
            font-size: 11px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2px 8px;
            border-radius: 8px;
            display: inline-block;
          }
        }
      }

      .profile-menu {
        .menu-item {
          display: flex;
          align-items: center;
          padding: 12px 0;
          cursor: pointer;
          border-bottom: 1px solid #f0f0f0;
          transition: color 0.3s ease;

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            color: #667eea;
          }

          i {
            margin-right: 8px;
            width: 16px;
            text-align: center;
          }

          span {
            font-size: 13px;
          }
        }
      }
    }

    // 侧边栏设置页面
    .sidebar-settings {
      padding: 20px;

      .settings-header {
        margin-bottom: 20px;

        h3 {
          font-size: 16px;
          color: #333;
          margin: 0;
          font-weight: 600;
        }
      }

      .settings-groups {
        .settings-group {
          margin-bottom: 24px;

          &:last-child {
            margin-bottom: 0;
          }

          .group-title {
            font-size: 13px;
            color: #666;
            margin-bottom: 12px;
            font-weight: 600;
          }

          .setting-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #f8f8f8;

            &:last-child {
              border-bottom: none;
            }

            span {
              font-size: 13px;
              color: #333;
            }
          }

          .menu-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 10px 0;
            cursor: pointer;
            border-bottom: 1px solid #f8f8f8;
            transition: color 0.3s ease;

            &:last-child {
              border-bottom: none;
            }

            &:hover {
              color: #667eea;
            }

            span {
              font-size: 13px;
            }

            i {
              font-size: 12px;
              color: #ccc;
            }
          }
        }
      }
    }
  }

  .main-chat {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: white;

    .welcome-screen {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;

      .welcome-content {
        text-align: center;
        color: #ccc;

        i {
          font-size: 64px;
          margin-bottom: 16px;
        }

        h3 {
          font-size: 20px;
          margin: 16px 0 8px;
          color: #666;
        }

        p {
          font-size: 14px;
          margin: 0;
        }
      }
    }

    .chat-interface {
      height: 100%;
      display: flex;
      flex-direction: column;

      .chat-header {
        padding: 16px 20px;
        border-bottom: 1px solid #f0f0f0;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .character-info {
          display: flex;
          align-items: center;

          .character-avatar {
            position: relative;
            margin-right: 12px;

            img {
              width: 40px;
              height: 40px;
              border-radius: 50%;
              object-fit: cover;
            }

            .online-indicator {
              position: absolute;
              bottom: 0;
              right: 0;
              width: 10px;
              height: 10px;
              border-radius: 50%;
              background: #ddd;
              border: 2px solid white;

              &.online {
                background: #4CAF50;
              }
            }
          }

          .character-details {
            .character-name {
              font-size: 16px;
              font-weight: 600;
              color: #333;
              margin-bottom: 2px;
            }

            .character-status {
              font-size: 12px;
              color: #666;
            }
          }
        }
      }

      .messages-area {
        flex: 1;
        padding: 20px;
        overflow-y: auto;
        background: #f8f9fa;

        .message-item {
          display: flex;
          margin-bottom: 20px;
          align-items: flex-start;

          &.user-message {
            flex-direction: row-reverse;

            .message-content {
              align-items: flex-end;

              .message-bubble {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
              }
            }
          }

          .message-avatar {
            margin: 0 12px;

            img {
              width: 36px;
              height: 36px;
              border-radius: 50%;
              object-fit: cover;
            }
          }

          .message-content {
            display: flex;
            flex-direction: column;
            max-width: 60%;

            .message-bubble {
              padding: 12px 16px;
              border-radius: 18px;
              background: white;
              box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
              word-wrap: break-word;
              line-height: 1.4;
            }

            .message-meta {
              display: flex;
              align-items: center;
              gap: 8px;
              margin-top: 4px;
              padding: 0 4px;

              .message-time {
                font-size: 11px;
                color: #999;
              }

              .voice-duration {
                font-size: 10px;
                color: #4CAF50;
                display: flex;
                align-items: center;

                i {
                  margin-right: 2px;
                }
              }
            }
          }
        }

        .typing-indicator {
          display: flex;
          align-items: flex-start;

          .typing-avatar {
            margin: 0 12px;

            img {
              width: 36px;
              height: 36px;
              border-radius: 50%;
              object-fit: cover;
            }
          }

          .typing-bubble {
            background: white;
            border-radius: 18px;
            padding: 12px 16px;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

            .typing-dots {
              display: flex;

              span {
                width: 6px;
                height: 6px;
                border-radius: 50%;
                background: #999;
                margin: 0 2px;
                animation: typing 1.4s infinite;

                &:nth-child(2) {
                  animation-delay: 0.2s;
                }

                &:nth-child(3) {
                  animation-delay: 0.4s;
                }
              }
            }
          }
        }
      }

      .input-area {
        padding: 20px;
        border-top: 1px solid #f0f0f0;
        background: white;

        .input-tools {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 12px;

          .el-button {
            padding: 8px;
            border-radius: 8px;
            color: #666;

            &.active {
              color: #667eea;
              background: #f0f2ff;
            }
          }
        }

        .input-wrapper {
          display: flex;
          align-items: flex-end;
          gap: 12px;

          .el-textarea {
            flex: 1;

            ::v-deep .el-textarea__inner {
              border-radius: 12px;
              border: 2px solid #f0f0f0;
              background: #fafbfc;
              resize: none;
              font-size: 14px;
              line-height: 1.4;

              &:focus {
                border-color: #667eea;
                background: white;
              }
            }
          }

          .el-button {
            padding: 10px 16px;
            border-radius: 12px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;

            &:hover:not(:disabled) {
              opacity: 0.9;
            }

            &:disabled {
              opacity: 0.5;
            }
          }
        }
      }
    }
  }
}

// 移动端聊天页面
.mobile-chat {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: white;

  .mobile-chat-header {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;

    .back-btn {
      cursor: pointer;
      padding: 8px;
      margin-right: 12px;

      i {
        font-size: 20px;
      }
    }

    .character-info {
      flex: 1;
      display: flex;
      align-items: center;

      .character-avatar {
        position: relative;
        margin-right: 12px;

        img {
          width: 36px;
          height: 36px;
          border-radius: 50%;
          object-fit: cover;
        }

        .online-indicator {
          position: absolute;
          bottom: 0;
          right: 0;
          width: 10px;
          height: 10px;
          border-radius: 50%;
          background: #ddd;
          border: 2px solid white;

          &.online {
            background: #4CAF50;
          }
        }
      }

      .character-details {
        .character-name {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 2px;
        }

        .character-status {
          font-size: 12px;
          opacity: 0.8;
        }
      }
    }

    .chat-actions {
      cursor: pointer;
      padding: 8px;

      i {
        font-size: 18px;
      }
    }
  }

  .mobile-messages-area {
    flex: 1;
    padding: 16px;
    overflow-y: auto;
    background: #f8f9fa;

    .message-item {
      display: flex;
      margin-bottom: 16px;
      align-items: flex-start;

      &.user-message {
        flex-direction: row-reverse;

        .message-content {
          align-items: flex-end;

          .message-bubble {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
          }
        }
      }

      .message-avatar {
        margin: 0 8px;

        img {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          object-fit: cover;
        }
      }

      .message-content {
        display: flex;
        flex-direction: column;
        max-width: 70%;

        .message-bubble {
          padding: 10px 14px;
          border-radius: 16px;
          background: white;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
          word-wrap: break-word;
          line-height: 1.4;
          font-size: 14px;
        }

        .message-meta {
          display: flex;
          align-items: center;
          gap: 6px;
          margin-top: 4px;
          padding: 0 4px;

          .message-time {
            font-size: 10px;
            color: #999;
          }

          .voice-duration {
            font-size: 10px;
            color: #4CAF50;
            display: flex;
            align-items: center;

            i {
              margin-right: 2px;
            }
          }
        }
      }
    }

    .typing-indicator {
      display: flex;
      align-items: flex-start;

      .typing-avatar {
        margin: 0 8px;

        img {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          object-fit: cover;
        }
      }

      .typing-bubble {
        background: white;
        border-radius: 16px;
        padding: 10px 14px;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

        .typing-dots {
          display: flex;

          span {
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background: #999;
            margin: 0 2px;
            animation: typing 1.4s infinite;

            &:nth-child(2) {
              animation-delay: 0.2s;
            }

            &:nth-child(3) {
              animation-delay: 0.4s;
            }
          }
        }
      }
    }
  }

  .mobile-input-area {
    padding: 12px 16px;
    background: white;
    border-top: 1px solid #eee;

    .input-tools {
      margin-bottom: 8px;

      .el-button {
        padding: 8px;
        color: #666;

        &.active {
          color: #667eea;
        }
      }
    }

    .input-wrapper {
      display: flex;
      align-items: flex-end;
      background: #f8f9fa;
      border-radius: 20px;
      padding: 8px 12px;

      .message-input {
        flex: 1;
        border: none;
        background: transparent;
        resize: none;
        outline: none;
        font-size: 14px;
        line-height: 1.4;
        max-height: 80px;
        min-height: 20px;
        color: #333;

        &::placeholder {
          color: #999;
        }
      }

      .send-btn {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        margin-left: 8px;
        transition: all 0.2s ease;

        &.disabled {
          opacity: 0.5;
          cursor: not-allowed;
        }

        i {
          font-size: 14px;
        }
      }
    }
  }
}

// 用户菜单
.user-menu-content {
  .menu-item {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    cursor: pointer;
    transition: background-color 0.2s;

    &:hover {
      background: #f5f5f5;
    }

    &.logout {
      color: #ff4d4f;

      &:hover {
        background: #fff2f0;
      }
    }

    i {
      margin-right: 8px;
      width: 16px;
      text-align: center;
    }

    span {
      font-size: 14px;
    }
  }

  .menu-divider {
    height: 1px;
    background: #f0f0f0;
    margin: 8px 0;
  }
}

// 动画效果
@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-8px);
    opacity: 1;
  }
}

// 滚动条样式
::-webkit-scrollbar {
  width: 4px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 2px;
}

// 底部导航栏样式
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
      transition: all 0.3s ease;
    }

    span {
      font-size: 12px;
      color: #999;
      transition: all 0.3s ease;
    }

    &.active {
      i {
        color: #667eea;
        transform: scale(1.1);
      }

      span {
        color: #667eea;
        font-weight: 600;
      }
    }

    &:hover:not(.active) {
      background: #f8f9fa;

      i, span {
        color: #666;
      }
    }
  }
}

// 页面通用样式
.benefits-page,
.profile-page,
.settings-page {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f8f9fa;
  z-index: 999;
  overflow-y: auto;

  .page-header {
    background: white;
    padding: 20px 16px 16px 16px;
    border-bottom: 1px solid #f0f0f0;
    position: sticky;
    top: 0;
    z-index: 10;

    .header-title {
      display: flex;
      align-items: center;
      font-size: 18px;
      font-weight: 600;
      color: #333;

      i {
        margin-right: 8px;
        color: #667eea;
        font-size: 20px;
      }
    }
  }
}

// 权益页面样式
.benefits-page {
  .benefits-content {
    padding: 16px;
    padding-bottom: 80px;
  }

  .membership-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16px;
    padding: 20px;
    color: white;
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .user-level {
        .level-badge {
          background: rgba(255, 255, 255, 0.2);
          padding: 4px 12px;
          border-radius: 12px;
          font-size: 12px;
          margin-right: 8px;
        }

        .level-name {
          font-size: 16px;
          font-weight: 600;
        }
      }

      .upgrade-btn {
        background: rgba(255, 255, 255, 0.2);
        padding: 8px 16px;
        border-radius: 20px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
        }

        i {
          margin-right: 4px;
        }
      }
    }

    .card-content {
      .benefit-item {
        display: flex;
        align-items: center;
        margin-bottom: 8px;

        i {
          margin-right: 8px;
          font-size: 16px;
        }

        .status {
          margin-left: auto;
          padding: 2px 8px;
          border-radius: 8px;
          font-size: 12px;
          background: rgba(255, 255, 255, 0.2);

          &.enabled {
            background: rgba(76, 175, 80, 0.3);
          }
        }
      }
    }
  }

  .benefits-list {
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 12px;
    }

    .benefit-card {
      background: white;
      border-radius: 12px;
      padding: 16px;
      margin-bottom: 12px;
      display: flex;
      align-items: center;

      .benefit-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;

        i {
          color: white;
          font-size: 18px;
        }
      }

      .benefit-info {
        flex: 1;

        .benefit-title {
          font-size: 14px;
          font-weight: 600;
          color: #333;
          margin-bottom: 4px;
        }

        .benefit-desc {
          font-size: 12px;
          color: #666;
        }
      }

      .benefit-status {
        span {
          font-size: 12px;
          padding: 4px 8px;
          border-radius: 8px;
          background: #f0f0f0;
          color: #999;

          &.enabled {
            background: #e8f5e8;
            color: #4CAF50;
          }
        }
      }
    }
  }
}

// 个人资料页面样式
.profile-page {
  .profile-content {
    padding: 16px;
    padding-bottom: 80px;
  }

  .user-card {
    background: white;
    border-radius: 16px;
    padding: 20px;
    text-align: center;
    margin-bottom: 20px;

    .user-avatar-large {
      position: relative;
      display: inline-block;
      margin-bottom: 16px;

      img {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        object-fit: cover;
      }

      .edit-avatar {
        position: absolute;
        bottom: 0;
        right: 0;
        width: 24px;
        height: 24px;
        background: #667eea;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;

        i {
          color: white;
          font-size: 12px;
        }
      }
    }

    .user-details {
      .user-name {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        margin-bottom: 4px;
      }

      .user-id {
        font-size: 12px;
        color: #999;
        margin-bottom: 8px;
      }

      .user-level-badge {
        display: inline-block;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
      }
    }
  }

  .profile-menu {
    background: white;
    border-radius: 12px;
    margin-bottom: 20px;
    overflow: hidden;

    .menu-item {
      display: flex;
      align-items: center;
      padding: 16px;
      cursor: pointer;
      border-bottom: 1px solid #f8f8f8;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: #f8f9fa;
      }

      .menu-icon {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: #f0f0f0;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 12px;

        i {
          color: #667eea;
          font-size: 16px;
        }
      }

      .menu-content {
        flex: 1;
        display: flex;
        justify-content: space-between;
        align-items: center;

        span {
          font-size: 14px;
          color: #333;
        }

        i {
          color: #ccc;
        }
      }
    }
  }

  .logout-section {
    text-align: center;
  }
}

// 设置页面样式
.settings-page {
  .settings-content {
    padding: 16px;
    padding-bottom: 80px;
  }

  .settings-section {
    background: white;
    border-radius: 12px;
    margin-bottom: 16px;
    padding: 16px;

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 16px;
    }

    .setting-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 0;
      border-bottom: 1px solid #f8f8f8;

      &:last-child {
        border-bottom: none;
      }

      &.clickable {
        cursor: pointer;

        &:hover {
          background: rgba(102, 126, 234, 0.05);
          margin: 0 -16px;
          padding-left: 16px;
          padding-right: 16px;
          border-radius: 8px;
        }
      }

      .setting-label {
        display: flex;
        align-items: center;

        i {
          margin-right: 8px;
          color: #667eea;
          font-size: 16px;
        }

        span {
          font-size: 14px;
          color: #333;
        }
      }
    }
  }
}

// 移动端适配
@media (max-width: 768px) {
  .desktop-layout {
    display: none;
  }

  .mobile-character-list,
  .mobile-chat {
    display: flex;
  }

  // 为底部导航栏预留空间
  .mobile-character-list {
    padding-bottom: 80px;
  }

  .mobile-chat {
    padding-bottom: 80px;
  }
}

@media (min-width: 769px) {
  .mobile-character-list,
  .mobile-chat {
    display: none;
  }

  .bottom-navigation,
  .benefits-page,
  .profile-page,
  .settings-page {
    display: none;
  }
}
</style>
