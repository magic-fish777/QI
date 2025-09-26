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
      <el-form-item label="角色ID" prop="roleId">
        <el-input
          v-model="queryParams.roleId"
          placeholder="请输入角色ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="使用技能ID" prop="skillId">
        <el-input
          v-model="queryParams.skillId"
          placeholder="请输入使用技能ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['ai:record:add']"
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
          v-hasPermi="['ai:record:edit']"
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
      <el-table-column label="记录ID" align="center" prop="recordId" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="角色ID" align="center" prop="roleId" />
      <el-table-column label="输入类型" align="center" prop="userInputType" />
      <el-table-column label="用户输入内容" align="center" prop="userInputContent" />
      <el-table-column label="AI回复内容" align="center" prop="aiReplyContent" />
      <el-table-column label="使用技能ID" align="center" prop="skillId" />
      <el-table-column label="聊天语言" align="center" prop="chatLanguage" />
      <el-table-column label="聊天时间" align="center" prop="chatTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.chatTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ai:record:edit']"
          >修改</el-button>
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

    <!-- 添加或修改聊天记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="角色ID" prop="roleId">
          <el-input v-model="form.roleId" placeholder="请输入角色ID" />
        </el-form-item>
        <el-form-item label="用户输入内容">
          <editor v-model="form.userInputContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="AI回复内容">
          <editor v-model="form.aiReplyContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="使用技能ID" prop="skillId">
          <el-input v-model="form.skillId" placeholder="请输入使用技能ID" />
        </el-form-item>
        <el-form-item label="聊天语言" prop="chatLanguage">
          <el-input v-model="form.chatLanguage" placeholder="请输入聊天语言" />
        </el-form-item>
        <el-form-item label="聊天时间" prop="chatTime">
          <el-date-picker clearable
            v-model="form.chatTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择聊天时间">
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
import { listRecord, getRecord, delRecord, addRecord, updateRecord } from "@/api/ai/record"

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
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        roleId: null,
        userInputType: null,
        userInputContent: null,
        aiReplyContent: null,
        skillId: null,
        chatLanguage: null,
        chatTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        roleId: [
          { required: true, message: "角色ID不能为空", trigger: "blur" }
        ],
        userInputType: [
          { required: true, message: "输入类型不能为空", trigger: "change" }
        ],
        userInputContent: [
          { required: true, message: "用户输入内容不能为空", trigger: "blur" }
        ],
        aiReplyContent: [
          { required: true, message: "AI回复内容不能为空", trigger: "blur" }
        ],
        skillId: [
          { required: true, message: "使用技能ID不能为空", trigger: "blur" }
        ],
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
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        recordId: null,
        userId: null,
        roleId: null,
        userInputType: null,
        userInputContent: null,
        aiReplyContent: null,
        skillId: null,
        chatLanguage: null,
        chatTime: null,
        delFlag: null,
        createTime: null
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
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加聊天记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const recordId = row.recordId || this.ids
      getRecord(recordId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改聊天记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.recordId != null) {
            updateRecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addRecord(this.form).then(response => {
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
      const recordIds = row.recordId || this.ids
      this.$modal.confirm('是否确认删除聊天记录编号为"' + recordIds + '"的数据项？').then(function() {
        return delRecord(recordIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
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
