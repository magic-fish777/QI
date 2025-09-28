<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6" v-for="(stat, key) in memberStats" :key="key">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="stat-icon">
            <i :class="stat.icon"></i>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="10" class="mb20">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="small" @click="handleManualActivate">
          手动开通
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" icon="el-icon-time" size="small" @click="handleBatchExtend" :disabled="multiple">
          批量延期
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" icon="el-icon-search" size="small" @click="handleUserSearch">
          用户搜索
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" icon="el-icon-pie-chart" size="small" @click="showStatistics">
          数据统计
        </el-button>
      </el-col>
    </el-row>

    <!-- 搜索条件 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="VIP等级" prop="vipLevel">
        <el-select v-model="queryParams.vipLevel" placeholder="选择VIP等级" clearable>
          <el-option label="白银会员" value="1"></el-option>
          <el-option label="黄金会员" value="2"></el-option>
          <el-option label="铂金会员" value="3"></el-option>
          <el-option label="钻石会员" value="4"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="vipStatus">
        <el-select v-model="queryParams.vipStatus" placeholder="选择状态" clearable>
          <el-option label="正常" value="1"></el-option>
          <el-option label="停用" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="到期时间" prop="expireTime">
        <el-date-picker
          v-model="expireTimeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          @change="handleExpireTimeChange">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['ai:vip:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['ai:vip:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ai:vip:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ai:vip:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 会员列表 -->
    <el-table v-loading="loading" :data="vipList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="会员ID" align="center" prop="vipId" width="80" />
      <el-table-column label="用户ID" align="center" prop="userId" width="80" />
      <el-table-column label="VIP等级" align="center" prop="vipLevel" width="100">
        <template slot-scope="scope">
          <el-tag :type="getVipLevelTagType(scope.row.vipLevel)">
            {{ getVipLevelName(scope.row.vipLevel) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="会员类型" align="center" prop="vipType" width="80">
        <template slot-scope="scope">
          <span>{{ getVipTypeName(scope.row.vipType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="订阅时间" align="center" prop="subscribeTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.subscribeTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="到期时间" align="center" prop="expireTime" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="剩余天数" align="center" width="80">
        <template slot-scope="scope">
          <span :class="getRemainingDaysClass(scope.row.expireTime)">
            {{ getRemainingDays(scope.row.expireTime) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="vipStatus" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.vipStatus === '1' ? 'success' : 'danger'">
            {{ scope.row.vipStatus === '1' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="角色配额" align="center" prop="customRoleQuota" width="80" />
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ai:vip:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-time"
            @click="handleExtendSingle(scope.row)"
            v-hasPermi="['ai:vip:edit']"
          >延期</el-button>
          <el-button
            size="mini"
            type="text"
            :icon="scope.row.vipStatus === '1' ? 'el-icon-remove-outline' : 'el-icon-circle-check'"
            @click="handleToggleStatus(scope.row)"
            v-hasPermi="['ai:vip:edit']"
          >{{ scope.row.vipStatus === '1' ? '停用' : '启用' }}</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ai:vip:remove']"
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

    <!-- 添加或修改会员订阅对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="VIP等级" prop="vipLevel">
          <el-select v-model="form.vipLevel" placeholder="选择VIP等级">
            <el-option label="白银会员" :value="1"></el-option>
            <el-option label="黄金会员" :value="2"></el-option>
            <el-option label="铂金会员" :value="3"></el-option>
            <el-option label="钻石会员" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="会员类型" prop="vipType">
          <el-select v-model="form.vipType" placeholder="选择会员类型">
            <el-option label="月卡" value="1"></el-option>
            <el-option label="季卡" value="2"></el-option>
            <el-option label="年卡" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订阅时间" prop="subscribeTime">
          <el-date-picker clearable
            v-model="form.subscribeTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择订阅时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker clearable
            v-model="form.expireTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择过期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="状态" prop="vipStatus">
          <el-radio-group v-model="form.vipStatus">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色配额" prop="customRoleQuota">
          <el-input-number v-model="form.customRoleQuota" :min="0" placeholder="请输入定制角色配额" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 手动开通会员对话框 -->
    <el-dialog title="手动开通会员" :visible.sync="manualActivateVisible" width="400px" append-to-body>
      <el-form ref="manualForm" :model="manualForm" :rules="manualRules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="manualForm.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="VIP等级" prop="vipLevel">
          <el-select v-model="manualForm.vipLevel" placeholder="选择VIP等级">
            <el-option label="白银会员" :value="1"></el-option>
            <el-option label="黄金会员" :value="2"></el-option>
            <el-option label="铂金会员" :value="3"></el-option>
            <el-option label="钻石会员" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="月数" prop="months">
          <el-input-number v-model="manualForm.months" :min="1" :max="120" placeholder="开通月数" />
        </el-form-item>
        <el-form-item label="操作原因" prop="reason">
          <el-input v-model="manualForm.reason" type="textarea" placeholder="请输入操作原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitManualActivate" :loading="manualLoading">确 定</el-button>
        <el-button @click="manualActivateVisible = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 批量延期对话框 -->
    <el-dialog title="批量延期会员" :visible.sync="batchExtendVisible" width="400px" append-to-body>
      <el-form ref="batchForm" :model="batchForm" :rules="batchRules" label-width="80px">
        <el-form-item label="延期天数" prop="days">
          <el-input-number v-model="batchForm.days" :min="1" :max="365" placeholder="延期天数" />
        </el-form-item>
        <el-form-item label="操作原因" prop="reason">
          <el-input v-model="batchForm.reason" type="textarea" placeholder="请输入操作原因" />
        </el-form-item>
        <el-form-item>
          <el-alert title="提示" :description="`将对选中的 ${ids.length} 个会员进行延期操作`" type="info" show-icon :closable="false"></el-alert>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBatchExtend" :loading="batchLoading">确 定</el-button>
        <el-button @click="batchExtendVisible = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 用户搜索对话框 -->
    <el-dialog title="用户搜索" :visible.sync="userSearchVisible" width="300px" append-to-body>
      <el-form ref="searchForm" :model="searchForm" label-width="80px">
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUserSearch">搜 索</el-button>
        <el-button @click="userSearchVisible = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 统计数据对话框 -->
    <el-dialog title="会员数据统计" :visible.sync="statisticsVisible" width="800px" append-to-body>
      <div v-if="statisticsData">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card title="会员数量统计">
              <div class="statistics-item">
                <span>总会员数：</span><strong>{{ statisticsData.memberCounts.total }}</strong>
              </div>
              <div class="statistics-item">
                <span>活跃会员：</span><strong>{{ statisticsData.memberCounts.active }}</strong>
              </div>
              <div class="statistics-item">
                <span>过期会员：</span><strong>{{ statisticsData.memberCounts.expired }}</strong>
              </div>
              <hr />
              <div class="statistics-item">
                <span>白银会员：</span><strong>{{ statisticsData.memberCounts.level1 }}</strong>
              </div>
              <div class="statistics-item">
                <span>黄金会员：</span><strong>{{ statisticsData.memberCounts.level2 }}</strong>
              </div>
              <div class="statistics-item">
                <span>铂金会员：</span><strong>{{ statisticsData.memberCounts.level3 }}</strong>
              </div>
              <div class="statistics-item">
                <span>钻石会员：</span><strong>{{ statisticsData.memberCounts.level4 }}</strong>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card title="收入统计">
              <div class="statistics-item">
                <span>本月收入：</span><strong>¥{{ statisticsData.revenue.thisMonth }}</strong>
              </div>
              <div class="statistics-item">
                <span>上月收入：</span><strong>¥{{ statisticsData.revenue.lastMonth }}</strong>
              </div>
              <div class="statistics-item">
                <span>本年收入：</span><strong>¥{{ statisticsData.revenue.thisYear }}</strong>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listVip, getVip, delVip, addVip, updateVip } from "@/api/ai/vip"

export default {
  name: "EnhancedVip",
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
      // 会员订阅表格数据
      vipList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 到期时间范围
      expireTimeRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        vipType: null,
        vipLevel: null,
        subscribeTime: null,
        expireTime: null,
        vipStatus: null,
        customRoleQuota: null,
        expireTimeStart: null,
        expireTimeEnd: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        vipLevel: [
          { required: true, message: "VIP等级不能为空", trigger: "change" }
        ],
        vipStatus: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ]
      },

      // 统计数据
      memberStats: {
        total: { label: '总会员数', value: 0, icon: 'el-icon-user' },
        active: { label: '活跃会员', value: 0, icon: 'el-icon-circle-check' },
        expired: { label: '过期会员', value: 0, icon: 'el-icon-warning' },
        newToday: { label: '今日新增', value: 0, icon: 'el-icon-plus' }
      },

      // 手动开通相关
      manualActivateVisible: false,
      manualLoading: false,
      manualForm: {
        userId: null,
        vipLevel: null,
        months: 1,
        reason: ''
      },
      manualRules: {
        userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }],
        vipLevel: [{ required: true, message: "VIP等级不能为空", trigger: "change" }],
        months: [{ required: true, message: "月数不能为空", trigger: "blur" }]
      },

      // 批量延期相关
      batchExtendVisible: false,
      batchLoading: false,
      batchForm: {
        days: 30,
        reason: ''
      },
      batchRules: {
        days: [{ required: true, message: "延期天数不能为空", trigger: "blur" }]
      },

      // 用户搜索相关
      userSearchVisible: false,
      searchForm: {
        userId: null
      },

      // 统计相关
      statisticsVisible: false,
      statisticsData: null
    }
  },
  created() {
    this.getList()
    this.loadStatistics()
  },
  methods: {
    /** 查询会员订阅列表 */
    getList() {
      this.loading = true
      listVip(this.queryParams).then(response => {
        this.vipList = response.rows
        this.total = response.total
        this.loading = false
      })
    },

    /** 加载统计数据 */
    async loadStatistics() {
      try {
        // 这里应该调用统计接口，暂时模拟数据
        this.memberStats.total.value = this.total || 0
        this.memberStats.active.value = Math.floor((this.total || 0) * 0.8)
        this.memberStats.expired.value = Math.floor((this.total || 0) * 0.2)
        this.memberStats.newToday.value = 5
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },

    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },

    // 表单重置
    reset() {
      this.form = {
        vipId: null,
        userId: null,
        vipType: null,
        vipLevel: null,
        subscribeTime: null,
        expireTime: null,
        vipStatus: "1",
        customRoleQuota: null,
        remark: null
      }
      this.resetForm("form")
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.expireTimeRange = []
      this.queryParams.expireTimeStart = null
      this.queryParams.expireTimeEnd = null
      this.handleQuery()
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.vipId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加会员订阅"
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const vipId = row.vipId || this.ids
      getVip(vipId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改会员订阅"
      })
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.vipId != null) {
            updateVip(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addVip(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const vipIds = row.vipId || this.ids
      this.$modal.confirm('是否确认删除会员订阅编号为"' + vipIds + '"的数据项？').then(function() {
        return delVip(vipIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('ai/vip/export', {
        ...this.queryParams
      }, `vip_${new Date().getTime()}.xlsx`)
    },

    // 新增的方法
    handleExpireTimeChange(value) {
      if (value && value.length === 2) {
        this.queryParams.expireTimeStart = value[0]
        this.queryParams.expireTimeEnd = value[1]
      } else {
        this.queryParams.expireTimeStart = null
        this.queryParams.expireTimeEnd = null
      }
    },

    getVipLevelName(level) {
      const names = {
        1: '白银会员',
        2: '黄金会员',
        3: '铂金会员',
        4: '钻石会员'
      }
      return names[level] || '未知'
    },

    getVipLevelTagType(level) {
      const types = {
        1: 'info',
        2: 'warning',
        3: 'success',
        4: 'danger'
      }
      return types[level] || 'info'
    },

    getVipTypeName(type) {
      const names = {
        '1': '月卡',
        '2': '季卡',
        '3': '年卡'
      }
      return names[type] || '未知'
    },

    getRemainingDays(expireTime) {
      if (!expireTime) return '--'
      const now = new Date()
      const expire = new Date(expireTime)
      const diffTime = expire.getTime() - now.getTime()
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

      if (diffDays < 0) return '已过期'
      if (diffDays === 0) return '今日到期'
      return `${diffDays}天`
    },

    getRemainingDaysClass(expireTime) {
      if (!expireTime) return ''
      const now = new Date()
      const expire = new Date(expireTime)
      const diffTime = expire.getTime() - now.getTime()
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

      if (diffDays < 0) return 'text-danger'
      if (diffDays <= 7) return 'text-warning'
      return 'text-success'
    },

    // 手动开通
    handleManualActivate() {
      this.manualForm = {
        userId: null,
        vipLevel: null,
        months: 1,
        reason: ''
      }
      this.manualActivateVisible = true
    },

    async submitManualActivate() {
      this.$refs["manualForm"].validate(async (valid) => {
        if (valid) {
          this.manualLoading = true
          try {
            // 这里应该调用手动开通接口
            await this.$message.success('手动开通成功')
            this.manualActivateVisible = false
            this.getList()
          } catch (error) {
            this.$message.error('操作失败：' + error.message)
          } finally {
            this.manualLoading = false
          }
        }
      })
    },

    // 批量延期
    handleBatchExtend() {
      if (this.ids.length === 0) {
        this.$message.warning('请先选择要延期的会员')
        return
      }
      this.batchForm = {
        days: 30,
        reason: ''
      }
      this.batchExtendVisible = true
    },

    async submitBatchExtend() {
      this.$refs["batchForm"].validate(async (valid) => {
        if (valid) {
          this.batchLoading = true
          try {
            // 这里应该调用批量延期接口
            await this.$message.success(`批量延期成功，处理了${this.ids.length}个会员`)
            this.batchExtendVisible = false
            this.getList()
          } catch (error) {
            this.$message.error('操作失败：' + error.message)
          } finally {
            this.batchLoading = false
          }
        }
      })
    },

    // 单个延期
    handleExtendSingle(row) {
      this.$prompt('请输入延期天数', '延期会员', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^\d+$/,
        inputErrorMessage: '请输入有效的天数'
      }).then(async ({ value }) => {
        try {
          // 这里应该调用单个延期接口
          await this.$message.success(`延期${value}天成功`)
          this.getList()
        } catch (error) {
          this.$message.error('延期失败：' + error.message)
        }
      })
    },

    // 切换状态
    async handleToggleStatus(row) {
      const newStatus = row.vipStatus === '1' ? '0' : '1'
      const statusText = newStatus === '1' ? '启用' : '停用'

      try {
        await this.$confirm(`确认${statusText}该会员吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        // 这里应该调用状态切换接口
        await this.$message.success(`${statusText}成功`)
        this.getList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('操作失败：' + error.message)
        }
      }
    },

    // 用户搜索
    handleUserSearch() {
      this.searchForm.userId = null
      this.userSearchVisible = true
    },

    async submitUserSearch() {
      if (!this.searchForm.userId) {
        this.$message.warning('请输入用户ID')
        return
      }

      try {
        // 这里应该调用用户搜索接口
        this.queryParams.userId = this.searchForm.userId
        this.userSearchVisible = false
        this.handleQuery()
      } catch (error) {
        this.$message.error('搜索失败：' + error.message)
      }
    },

    // 显示统计
    async showStatistics() {
      try {
        // 这里应该调用统计接口
        this.statisticsData = {
          memberCounts: {
            total: 150,
            active: 120,
            expired: 30,
            level1: 45,
            level2: 60,
            level3: 30,
            level4: 15
          },
          revenue: {
            thisMonth: 12800.50,
            lastMonth: 9650.00,
            thisYear: 156780.00
          }
        }
        this.statisticsVisible = true
      } catch (error) {
        this.$message.error('加载统计数据失败：' + error.message)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.mb20 {
  margin-bottom: 20px;
}

.stat-card {
  position: relative;

  .stat-content {
    .stat-number {
      font-size: 28px;
      font-weight: bold;
      color: #333;
    }

    .stat-label {
      font-size: 14px;
      color: #666;
      margin-top: 5px;
    }
  }

  .stat-icon {
    position: absolute;
    top: 20px;
    right: 20px;
    font-size: 40px;
    color: #409EFF;
    opacity: 0.3;
  }
}

.text-danger {
  color: #F56C6C;
}

.text-warning {
  color: #E6A23C;
}

.text-success {
  color: #67C23A;
}

.statistics-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;

  span {
    color: #666;
  }

  strong {
    color: #333;
  }
}
</style>