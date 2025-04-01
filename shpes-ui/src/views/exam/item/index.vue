<template>
  <div class="app-container">
    <div class="main-content" style="padding-bottom: 60px;">
      <!-- 搜索区域 -->
      <div class="filter-container">
        <el-form :inline="true" :model="listQuery" class="form-inline">
          <el-form-item>
            <el-input
              v-model="listQuery.keyword"
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
      <div class="table-container" style="overflow: auto; max-height: calc(100vh - 280px);">
        <el-table
          v-loading="listLoading"
          :data="list"
          border
          style="width: 100%">
          <el-table-column label="项目名称" prop="name" />
          <el-table-column label="项目编码" prop="code" width="120" />
          <el-table-column label="项目分类" width="120">
            <template slot-scope="{row}">
              <span>{{ getCategoryName(row.category) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="执行科室" prop="departmentName" width="120" />
          <el-table-column label="参考值" prop="referenceValue" show-overflow-tooltip />
          <el-table-column label="价格" width="100">
            <template slot-scope="{row}">
              <span>{{ formatPrice(row.price) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" show-overflow-tooltip />
          <el-table-column label="创建时间" width="180">
            <template slot-scope="{row}">
              <span>{{ formatDateTime(row.createTime) }}</span>
            </template>
          </el-table-column>
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
      </div>

      <!-- 分页组件 -->
      <div v-if="list.length > 0" class="pagination-wrapper">
        <el-pagination
          class="department-pagination"
          :pager-count="device === 'mobile' ? 3 : 5"
          :current-page="listQuery.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="listQuery.pageSize"
          :layout="device === 'mobile' ? 'prev, pager, next' : 'total, sizes, prev, pager, next'"
          :total="effectiveTotal"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- Debug info -->
      <div v-if="false" style="margin: 10px 0; color: #606266; background: #f9f9f9; padding: 8px; border-radius: 4px;">
        <span>API返回总条数: {{ total }}</span>
        <span style="margin-left: 15px">当前页: {{ listQuery.pageNum }}</span>
        <span style="margin-left: 15px">每页条数: {{ listQuery.pageSize }}</span>
        <span style="margin-left: 15px">当前记录数: {{ list.length }}</span>
        <span style="margin-left: 15px">有效总条数: {{ effectiveTotal }}</span>
      </div>
    </div>

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
        <el-form-item label="项目分类" prop="category">
          <el-select v-model="temp.category" placeholder="请选择项目分类">
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
            placeholder="请输入完整的参考值描述，包含数值和单位，例如：'90-120 mmHg'、'4.0-6.0 mmol/L'或'阴性'"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="temp.price"
            :min="0"
            :precision="0"
            :step="100"
            controls-position="right"
            placeholder="请输入价格（分）"
          />
          <span class="price-note">单位: 分，例如: 100元 = 10000分</span>
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
import { mapGetters } from 'vuex'
// import { getAllDepartments } from '@/api/department'

export default {
  name: 'ExamItem',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        code: undefined
      },
      departmentOptions: [
        { id: 1, name: '内科' },
        { id: 2, name: '外科' },
        { id: 3, name: '检验科' },
        { id: 4, name: '影像科' },
        { id: 5, name: '心电图室' },
        { id: 6, name: '超声科' }
      ],
      dialogVisible: false,
      dialogTitle: '',
      temp: {
        id: undefined,
        name: '',
        code: '',
        category: undefined,
        departmentId: undefined,
        referenceValue: '',
        price: 0,
        remark: '',
        status: 1
      },
      rules: {
        name: [
          { required: true, message: '请输入项目名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入项目编码', trigger: 'blur' }
        ],
        category: [
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
    // this.getDepartmentOptions()
  },
  computed: {
    ...mapGetters([
      'device'
    ]),
    effectiveTotal() {
      // 如果API返回的total大于0，则使用API返回的total
      if (this.total > 0) {
        return this.total;
      }
      
      // 如果API返回total为0但是有数据，则至少返回列表长度或者一个估算值
      if (this.list.length > 0) {
        // 如果当前不是第一页，我们可以估算总数
        if (this.listQuery.pageNum > 1) {
          return (this.listQuery.pageNum - 1) * this.listQuery.pageSize + this.list.length;
        }
        // 如果是第一页且记录不满一页，返回实际记录数
        if (this.list.length < this.listQuery.pageSize) {
          return this.list.length;
        }
        // 如果是第一页且记录数等于每页数量，返回每页数量的2倍作为估计
        return this.list.length * 2;
      }
      
      return 0;
    }
  },
  methods: {
    formatDateTime(time) {
      if (!time) return ''
      
      // 处理不同格式的日期
      let date
      if (Array.isArray(time)) {
        // 处理后端返回的数组格式 [year, month, day, hour, minute, second]
        if (time.length < 6) return ''
        const [year, month, day, hour, minute, second] = time
        return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')} ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}`
      } else if (typeof time === 'string') {
        // 处理ISO格式字符串
        date = new Date(time)
      } else {
        // 处理时间戳
        date = new Date(time)
      }
      
      if (isNaN(date.getTime())) return ''
      
      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hour = date.getHours().toString().padStart(2, '0')
      const minute = date.getMinutes().toString().padStart(2, '0')
      const second = date.getSeconds().toString().padStart(2, '0')
      
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    },
    getCategoryName(categoryId) {
      const categoryMap = {
        1: '常规检查',
        2: '实验室检查',
        3: '医学影像',
        4: '其他检查'
      }
      return categoryMap[categoryId] || '未知分类'
    },
    formatPrice(price) {
      if (price === null || price === undefined) return '0.00元';
      return (price / 100).toFixed(2) + '元';
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
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined
      }
      this.getList()
    },
    handleCreate() {
      this.dialogTitle = '新增体检项目'
      this.temp = {
        id: undefined,
        name: '',
        code: '',
        category: undefined,
        departmentId: undefined,
        referenceValue: '',
        price: 0,
        remark: '',
        status: 1
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
      try {
        await updateExamItemStatus(row.id, row.status);
        this.$message.success('状态更新成功');
      } catch (error) {
        console.error('更新状态失败:', error);
        // 恢复原状态
        row.status = row.status === 1 ? 0 : 1;
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
            const submitData = Object.assign({}, this.temp);
            
            if (this.dialogTitle === '编辑体检项目') {
              await updateExamItem(submitData);
              this.$message.success('更新项目成功');
            } else {
              await createExamItem(submitData);
              this.$message.success('创建项目成功');
            }
            
            this.dialogVisible = false;
            this.getList();
          } catch (error) {
            console.error('保存项目失败:', error);
            this.$message.error(`保存失败: ${error.response?.data?.message || error.message || '未知错误'}`);
          }
        }
      });
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val;
      this.listQuery.pageNum = 1;  // 切换每页条数时重置为第一页
      this.getList();
      // 修复滚动问题
      this.$nextTick(() => {
        document.documentElement.scrollTop = 0;
        document.body.scrollTop = 0;
        const mainContent = document.querySelector('.app-container');
        if (mainContent) mainContent.scrollTop = 0;
      });
    },
    handleCurrentChange(val) {
      this.listQuery.pageNum = val;
      this.getList();
      // 滚动到顶部
      this.$nextTick(() => {
        document.documentElement.scrollTop = 0;
        document.body.scrollTop = 0;
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  height: auto;
  min-height: 100%;
  position: relative;
  overflow-y: auto;

  .filter-container {
    margin-bottom: 20px;
  }

  .action-container {
    margin-bottom: 20px;
  }
  
  .price-note {
    margin-left: 8px;
    color: #909399;
    font-size: 12px;
  }
}

.pagination-wrapper {
  padding: 15px 0;
  display: flex;
  justify-content: center;
  background-color: #fff;
}
</style>

<style>
/* Global styles for pagination */
.el-pagination {
  display: flex !important;
  justify-content: center !important;
  padding: 8px !important;
  background-color: transparent !important;
  margin: 0 auto !important;
}

.department-pagination {
  width: 100% !important;
  max-width: 800px !important;
}

.el-pagination button, 
.el-pagination span:not([class*=suffix]),
.el-pagination .el-select .el-input .el-input__inner {
  font-size: 12px !important;
  min-width: 24px !important;
  height: 24px !important;
  line-height: 24px !important;
}

.el-pagination .el-select .el-input {
  margin: 0 5px !important;
}

.el-pagination .el-pagination__jump {
  margin-left: 10px !important;
}

.el-pagination .btn-prev,
.el-pagination .btn-next {
  background-color: transparent !important;
}

.el-pagination .number {
  background-color: transparent !important;
}

.el-pagination .number.active {
  color: #409EFF !important;
  background-color: #ecf5ff !important;
  border-color: #b3d8ff !important;
}

/* Remove unnecessary bottom padding */
.app-container {
  min-height: 800px !important;
  overflow: visible !important;
  padding-bottom: 20px !important;
}
</style>