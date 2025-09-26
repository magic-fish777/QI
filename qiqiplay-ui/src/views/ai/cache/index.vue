<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联聊天记录ID" prop="relatedRecordId">
        <el-input
          v-model="queryParams.relatedRecordId"
          placeholder="请输入关联聊天记录ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="语音文件URL" prop="voiceUrl">
        <el-input
          v-model="queryParams.voiceUrl"
          placeholder="请输入语音文件URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="时长" prop="voiceDuration">
        <el-input
          v-model="queryParams.voiceDuration"
          placeholder="请输入时长"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上传时间" prop="uploadTime">
        <el-date-picker clearable
          v-model="queryParams.uploadTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择上传时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="过期时间" prop="expireTime">
        <el-date-picker clearable
          v-model="queryParams.expireTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择过期时间">
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['ai:cache:add']"
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
          v-hasPermi="['ai:cache:edit']"
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
          v-hasPermi="['ai:cache:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ai:cache:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cacheList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="语音ID" align="center" prop="voiceId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="语音类型" align="center" prop="voiceType" />
      <el-table-column label="关联聊天记录ID" align="center" prop="relatedRecordId" />
      <el-table-column label="语音文件URL" align="center" prop="voiceUrl" />
      <el-table-column label="时长" align="center" prop="voiceDuration" />
      <el-table-column label="上传时间" align="center" prop="uploadTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.uploadTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="过期时间" align="center" prop="expireTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expireTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ai:cache:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ai:cache:remove']"
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

    <!-- 添加或修改语音缓存对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="关联聊天记录ID" prop="relatedRecordId">
          <el-input v-model="form.relatedRecordId" placeholder="请输入关联聊天记录ID" />
        </el-form-item>
        <el-form-item label="语音文件URL" prop="voiceUrl">
          <el-input v-model="form.voiceUrl" placeholder="请输入语音文件URL" />
        </el-form-item>
        <el-form-item label="时长" prop="voiceDuration">
          <el-input v-model="form.voiceDuration" placeholder="请输入时长" />
        </el-form-item>
        <el-form-item label="上传时间" prop="uploadTime">
          <el-date-picker clearable
            v-model="form.uploadTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择上传时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker clearable
            v-model="form.expireTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择过期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCache, getCache, delCache, addCache, updateCache } from "@/api/ai/cache"

export default {
  name: "Cache",
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
      // 语音缓存表格数据
      cacheList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        voiceType: null,
        relatedRecordId: null,
        voiceUrl: null,
        voiceDuration: null,
        uploadTime: null,
        expireTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        voiceType: [
          { required: true, message: "语音类型不能为空", trigger: "change" }
        ],
        relatedRecordId: [
          { required: true, message: "关联聊天记录ID不能为空", trigger: "blur" }
        ],
        voiceUrl: [
          { required: true, message: "语音文件URL不能为空", trigger: "blur" }
        ],
        voiceDuration: [
          { required: true, message: "时长不能为空", trigger: "blur" }
        ],
        expireTime: [
          { required: true, message: "过期时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询语音缓存列表 */
    getList() {
      this.loading = true
      listCache(this.queryParams).then(response => {
        this.cacheList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        voiceId: null,
        userId: null,
        voiceType: null,
        relatedRecordId: null,
        voiceUrl: null,
        voiceDuration: null,
        uploadTime: null,
        expireTime: null,
        delFlag: null
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
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.voiceId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加语音缓存"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const voiceId = row.voiceId || this.ids
      getCache(voiceId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改语音缓存"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.voiceId != null) {
            updateCache(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addCache(this.form).then(response => {
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
      const voiceIds = row.voiceId || this.ids
      this.$modal.confirm('是否确认删除语音缓存编号为"' + voiceIds + '"的数据项？').then(function() {
        return delCache(voiceIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ai/cache/export', {
        ...this.queryParams
      }, `cache_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
