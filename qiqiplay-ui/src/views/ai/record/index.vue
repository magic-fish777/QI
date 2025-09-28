<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户昵称" prop="userNickname">
        <el-input
          v-model="queryParams.userNickname"
          placeholder="请输入用户昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="输入类型" prop="userInputType">
        <el-select v-model="queryParams.userInputType" placeholder="请选择输入类型" clearable>
          <el-option label="文本" value="1"/>
          <el-option label="语音" value="2"/>
        </el-select>
      </el-form-item>
      <el-form-item label="聊天语言" prop="chatLanguage">
        <el-input
          v-model="queryParams.chatLanguage"
          placeholder="请输入聊天语言"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="聊天时间" prop="chatTime">
        <el-date-picker clearable
          v-model="queryParams.chatTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择聊天时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ai:record:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ai:record:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户信息" align="center" width="150">
        <template slot-scope="scope">
          <div style="display: flex; align-items: center; justify-content: center;">
            <el-avatar
              :size="40"
              :src="scope.row.userAvatar"
              style="margin-right: 8px;"
            >
              <i class="el-icon-user-solid"></i>
            </el-avatar>
            <span>{{ scope.row.userNickname || scope.row.userName || '未知用户' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="聊天角色" align="center" width="120">
        <template slot-scope="scope">
          <div style="display: flex; align-items: center; justify-content: center;">
            <el-avatar
              :size="30"
              :src="scope.row.roleAvatar"
              style="margin-right: 6px;"
            >
              <i class="el-icon-service"></i>
            </el-avatar>
            <span>{{ scope.row.roleName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="输入类型" align="center" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.userInputType === '1'" type="primary" size="small">文本</el-tag>
          <el-tag v-else-if="scope.row.userInputType === '2'" type="success" size="small">语音</el-tag>
          <el-tag v-else size="small">{{ scope.row.userInputType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="用户输入内容" align="left" min-width="200">
        <template slot-scope="scope">
          <div>
            <div>{{ scope.row.userInputContent }}</div>
            <div v-if="scope.row.userAudioFile" style="margin-top: 8px;">
              <el-button
                size="mini"
                type="success"
                icon="el-icon-microphone"
                @click="playUserAudio(scope.row.userAudioFile, scope.$index)"
                :loading="userAudioPlaying[scope.$index]"
                style="margin-right: 8px;"
              >
                {{ userAudioPlaying[scope.$index] ? '播放中...' : '播放用户语音' }}
              </el-button>
              <audio
                :ref="`userAudio_${scope.$index}`"
                :src="scope.row.userAudioFile"
                preload="none"
                @ended="onUserAudioEnded(scope.$index)"
                @error="onUserAudioError(scope.$index)"
                style="display: none;"
              ></audio>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="AI回复内容" align="left" min-width="200">
        <template slot-scope="scope">
          <div>
            <div>{{ scope.row.aiReplyContent }}</div>
            <div v-if="scope.row.aiAudioFile" style="margin-top: 8px;">
              <el-button
                size="mini"
                type="primary"
                icon="el-icon-video-play"
                @click="playAiAudio(scope.row.aiAudioFile, scope.$index)"
                :loading="audioPlaying[scope.$index]"
                style="margin-right: 8px;"
              >
                {{ audioPlaying[scope.$index] ? '播放中...' : '播放AI语音' }}
              </el-button>
              <audio
                :ref="`aiAudio_${scope.$index}`"
                :src="scope.row.aiAudioFile"
                preload="none"
                @ended="onAudioEnded(scope.$index)"
                @error="onAudioError(scope.$index)"
                style="display: none;"
              ></audio>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="聊天时间" align="center" prop="chatTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.chatTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="80">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ai:record:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

  </div>
</template>

<script>
import { listRecord, delRecord } from "@/api/ai/record"

export default {
  name: "Record",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 聊天记录表格数据
      recordList: [],
      // AI音频播放状态数组
      audioPlaying: {},
      // 用户音频播放状态数组
      userAudioPlaying: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userNickname: null,
        roleName: null,
        userInputType: null,
        userInputContent: null,
        aiReplyContent: null,
        chatLanguage: null,
        chatTime: null,
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询聊天记录列表 */
    getList() {
      this.loading = true
      listRecord(this.queryParams).then(response => {
        this.recordList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const recordIds = row.recordId || this.ids
      this.$modal.confirm('是否确认删除聊天记录编号为"' + recordIds + '"的数据项？').then(function() {
        return delRecord(recordIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 播放AI语音 */
    playAiAudio(audioUrl, index) {
      if (!audioUrl) {
        this.$message.warning('音频文件不存在')
        return
      }

      // 停止所有正在播放的音频
      this.stopAllAudio()

      // 设置播放状态
      this.$set(this.audioPlaying, index, true)

      try {
        // 直接创建audio元素来播放
        const audio = new Audio(audioUrl)

        // 存储当前播放的audio元素
        this.currentAiAudio = audio
        this.currentAiIndex = index

        audio.onended = () => {
          this.$set(this.audioPlaying, index, false)
          this.currentAiAudio = null
          this.currentAiIndex = null
        }

        audio.onerror = (error) => {
          console.error('AI音频加载失败:', error)
          this.$message.error('AI音频加载失败')
          this.$set(this.audioPlaying, index, false)
          this.currentAiAudio = null
          this.currentAiIndex = null
        }

        // 播放音频
        audio.play().catch(error => {
          console.error('AI音频播放失败:', error)
          this.$message.error('AI音频播放失败')
          this.$set(this.audioPlaying, index, false)
          this.currentAiAudio = null
          this.currentAiIndex = null
        })

      } catch (error) {
        console.error('创建AI音频元素失败:', error)
        this.$message.error('创建AI音频元素失败')
        this.$set(this.audioPlaying, index, false)
      }
    },

    /** 播放用户语音 */
    playUserAudio(audioUrl, index) {
      if (!audioUrl) {
        this.$message.warning('音频文件不存在')
        return
      }

      // 停止所有正在播放的音频
      this.stopAllAudio()

      // 设置播放状态
      this.$set(this.userAudioPlaying, index, true)

      // 使用$nextTick确保DOM已更新
      this.$nextTick(() => {
        try {
          // 获取音频元素
          const audioRef = this.$refs[`userAudio_${index}`]
          console.log('User Audio Ref:', audioRef, 'Index:', index)

          if (audioRef && audioRef.length > 0) {
            const audio = audioRef[0]
            console.log('User Audio Element:', audio)

            // 播放音频
            audio.currentTime = 0
            audio.play().catch(error => {
              console.error('用户音频播放失败:', error)
              this.$message.error('用户音频播放失败')
              this.$set(this.userAudioPlaying, index, false)
            })
          } else {
            console.error('用户音频元素未找到:', `userAudio_${index}`)
            this.$message.error('用户音频元素未找到')
            this.$set(this.userAudioPlaying, index, false)
          }
        } catch (error) {
          console.error('播放用户音频时出错:', error)
          this.$message.error('播放用户音频时出错')
          this.$set(this.userAudioPlaying, index, false)
        }
      })
    },

    /** 停止所有音频播放 */
    stopAllAudio() {
      // 停止AI音频
      Object.keys(this.audioPlaying).forEach(index => {
        if (this.audioPlaying[index]) {
          const audioRef = this.$refs[`aiAudio_${index}`]
          if (audioRef && audioRef[0]) {
            audioRef[0].pause()
            audioRef[0].currentTime = 0
          }
          this.$set(this.audioPlaying, index, false)
        }
      })

      // 停止用户音频
      Object.keys(this.userAudioPlaying).forEach(index => {
        if (this.userAudioPlaying[index]) {
          const audioRef = this.$refs[`userAudio_${index}`]
          if (audioRef && audioRef[0]) {
            audioRef[0].pause()
            audioRef[0].currentTime = 0
          }
          this.$set(this.userAudioPlaying, index, false)
        }
      })
    },

    /** 音频播放结束 */
    onAudioEnded(index) {
      this.$set(this.audioPlaying, index, false)
    },

    /** 音频播放错误 */
    onAudioError(index) {
      this.$set(this.audioPlaying, index, false)
      this.$message.error('AI音频加载失败')
    },

    /** 用户音频播放结束 */
    onUserAudioEnded(index) {
      this.$set(this.userAudioPlaying, index, false)
    },

    /** 用户音频播放错误 */
    onUserAudioError(index) {
      this.$set(this.userAudioPlaying, index, false)
      this.$message.error('用户音频加载失败')
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('ai/record/export', {
        ...this.queryParams
      }, `record_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
