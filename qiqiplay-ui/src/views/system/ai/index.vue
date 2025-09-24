<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色身份描述" prop="roleIdentity">
        <el-input
          v-model="queryParams.roleIdentity"
          placeholder="请输入角色身份描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色分类" prop="roleCategory">
        <el-input
          v-model="queryParams.roleCategory"
          placeholder="请输入角色分类"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色头像URL" prop="roleAvatar">
        <el-input
          v-model="queryParams.roleAvatar"
          placeholder="请输入角色头像URL"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否定制角色" prop="isCustom">
        <el-input
          v-model="queryParams.isCustom"
          placeholder="请输入是否定制角色"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建者ID" prop="creatorUserId">
        <el-input
          v-model="queryParams.creatorUserId"
          placeholder="请输入创建者ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['system:ai:add']"
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
          v-hasPermi="['system:ai:edit']"
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
          v-hasPermi="['system:ai:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:ai:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="aiList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="角色ID" align="center" prop="roleId" />
      <el-table-column label="角色名称" align="center" prop="roleName" />
      <el-table-column label="角色身份描述" align="center" prop="roleIdentity" />
      <el-table-column label="角色分类" align="center" prop="roleCategory" />
      <el-table-column label="角色头像URL" align="center" prop="roleAvatar" />
      <el-table-column label="是否定制角色" align="center" prop="isCustom" />
      <el-table-column label="创建者ID" align="center" prop="creatorUserId" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:ai:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:ai:remove']"
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

    <!-- 添加或修改AI角色信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色身份描述" prop="roleIdentity">
          <el-input v-model="form.roleIdentity" placeholder="请输入角色身份描述" />
        </el-form-item>
        <el-form-item label="角色分类" prop="roleCategory">
          <el-input v-model="form.roleCategory" placeholder="请输入角色分类" />
        </el-form-item>
        <el-form-item label="角色头像URL" prop="roleAvatar">
          <el-input v-model="form.roleAvatar" placeholder="请输入角色头像URL" />
        </el-form-item>
        <el-form-item label="是否定制角色" prop="isCustom">
          <el-input v-model="form.isCustom" placeholder="请输入是否定制角色" />
        </el-form-item>
        <el-form-item label="创建者ID" prop="creatorUserId">
          <el-input v-model="form.creatorUserId" placeholder="请输入创建者ID" />
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listAi, getAi, delAi, addAi, updateAi } from "@/api/system/ai"

export default {
  name: "Ai",
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
      // AI角色信息表格数据
      aiList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roleName: null,
        roleIdentity: null,
        roleCategory: null,
        roleAvatar: null,
        isCustom: null,
        creatorUserId: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" }
        ],
        roleIdentity: [
          { required: true, message: "角色身份描述不能为空", trigger: "blur" }
        ],
        roleCategory: [
          { required: true, message: "角色分类不能为空", trigger: "blur" }
        ],
        roleAvatar: [
          { required: true, message: "角色头像URL不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询AI角色信息列表 */
    getList() {
      this.loading = true
      listAi(this.queryParams).then(response => {
        this.aiList = response.rows
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
        roleId: null,
        roleName: null,
        roleIdentity: null,
        roleCategory: null,
        roleAvatar: null,
        isCustom: null,
        creatorUserId: null,
        status: null,
        delFlag: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
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
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.roleId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加AI角色信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const roleId = row.roleId || this.ids
      getAi(roleId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改AI角色信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.roleId != null) {
            updateAi(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAi(this.form).then(response => {
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
      const roleIds = row.roleId || this.ids
      this.$modal.confirm('是否确认删除AI角色信息编号为"' + roleIds + '"的数据项？').then(function() {
        return delAi(roleIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/ai/export', {
        ...this.queryParams
      }, `ai_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
