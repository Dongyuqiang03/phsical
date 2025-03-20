<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="form-inline">
        <el-form-item>
          <el-input
            v-model="listQuery.name"
            placeholder="项目名称"
            clearable
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.categoryId" placeholder="项目分类" clearable>
            <el-option label="常规检查" :value="1" />
            <el-option label="实验室检查" :value="2" />
            <el-option label="医学影像" :value="3" />
            <el-option label="其他检查" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.departmentId" placeholder="执行科室" clearable>
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-container">
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增项目</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%">
      <el-table-column label="项目名称" prop="name" />
      <el-table-column label="项目编码" prop="code" width="120" />
      <el-table-column label="项目分类" width="120">
        <template slot-scope="{row}">
          <span>{{ getCategoryName(row.categoryId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="执行科室" prop="departmentName" width="120" />
      <el-table-column label="参考值" prop="referenceValue" show-overflow-tooltip />
      <el-table-column label="单位" prop="unit" width="80" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="{row}">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            @change="handleStatusChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <!-- 项目表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目编码" prop="code">
          <el-input v-model="temp.code" placeholder="请输入项目编码" />
        </el-form-item>
        <el-form-item label="项目分类" prop="categoryId">
          <el-select v-model="temp.categoryId" placeholder="请选择项目分类">
            <el-option label="常规检查" :value="1" />
            <el-option label="实验室检查" :value="2" />
            <el-option label="医学影像" :value="3" />
            <el-option label="其他检查" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行科室" prop="departmentId">
          <el-select v-model="temp.departmentId" placeholder="请选择执行科室">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="参考值" prop="referenceValue">
          <el-input
            v-model="temp.referenceValue"
            type="textarea"
            :rows="2"
            placeholder="请输入参考值范围"
          />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="temp.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="temp.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import { getExamItemList, createExamItem, updateExamItem, deleteExamItem, updateExamItemStatus } from '@/api/exam/item'
import { getAllDepartments } from '@/api/department'

export default {
  name: 'ExamItem',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        name: undefined,
        categoryId: undefined,
        departmentId: undefined
      },
      departmentOptions: [],
      dialogVisible: false,
      dialogTitle: '',
      temp: {
        id: undefined,
        name: '',
        code: '',
        categoryId: undefined,
        departmentId: undefined,
        referenceValue: '',
        unit: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入项目名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入项目编码', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择项目分类', trigger: 'change' }
        ],
        departmentId: [
          { required: true, message: '请选择执行科室', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getDepartmentOptions()
  },
  methods: {
    getCategoryName(categoryId) {
      const categoryMap = {
        1: '常规检查',
        2: '实验室检查',
        3: '医学影像',
        4: '其他检查'
      }
      return categoryMap[categoryId] || '未知分类'
    },
    async getList() {
      this.listLoading = true;
      try {
        const response = await getExamItemList(this.listQuery);
        if (response && response.code === 200 && response.data) {
          this.list = response.data.records || [];
          this.total = response.data.total || 0;
        } else {
          this.list = [];
          this.total = 0;
          this.$message.warning(response?.message || '获取数据失败');
        }
      } catch (error) {
        console.error('获取体检项目列表失败:', error);
        this.list = [];
        this.total = 0;
        this.$message.error('获取体检项目列表失败');
      } finally {
        this.listLoading = false;
      }
    },
    async getDepartmentOptions() {
      try {
        const { data } = await getAllDepartments()
        this.departmentOptions = data
      } catch (error) {
        console.error('获取部门列表失败:', error)
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetQuery() {
      this.listQuery = {
        page: 1,
        limit: 10,
        name: undefined,
        categoryId: undefined,
        departmentId: undefined
      }
      this.getList()
    },
    handleCreate() {
      this.dialogTitle = '新增体检项目'
      this.temp = {
        id: undefined,
        name: '',
        code: '',
        categoryId: undefined,
        departmentId: undefined,
        referenceValue: '',
        unit: '',
        remark: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.dialogTitle = '编辑体检项目'
      this.temp = Object.assign({}, row)
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    async handleStatusChange(row) {
      if (!row || typeof row.id === 'undefined') {
        this.$message.warning('项目数据无效');
        return;
      }
      
      try {
        await updateExamItemStatus(row.id, row.status);
        this.$message.success('状态更新成功');
      } catch (error) {
        console.error('更新项目状态失败:', error);
        // 恢复原状态
        if (row) {
          row.status = row.status === 1 ? 0 : 1;
        }
        this.$message.error('更新状态失败');
      }
    },
    async handleDelete(row) {
      if (!row || !row.id) {
        this.$message.warning('项目数据无效');
        return;
      }
      
      try {
        await this.$confirm('是否确认删除该体检项目?', '提示', {
          type: 'warning'
        });
        await deleteExamItem(row.id);
        this.$message.success('删除成功');
        this.getList();
      } catch (error) {
        if (error === 'cancel') {
          return;
        }
        console.error('删除体检项目失败:', error);
        this.$message.error('删除失败');
      }
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            const submitData = { ...this.temp };
            
            if (this.temp.id) {
              await updateExamItem(submitData);
              this.$message.success('更新体检项目成功');
            } else {
              await createExamItem(submitData);
              this.$message.success('创建体检项目成功');
            }
            
            this.dialogVisible = false;
            this.getList();
          } catch (error) {
            console.error('保存体检项目失败:', error);
            this.$message.error(`保存失败: ${error.response?.data?.message || error.message || '未知错误'}`);
          }
        }
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .action-container {
    margin-bottom: 20px;
  }
}
</style> 