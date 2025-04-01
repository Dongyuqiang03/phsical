<template>
  <div class="app-container">
    <div class="main-content" style="padding-bottom: 60px;">
      <!-- 搜索区域 -->
      <div class="filter-container">
        <el-form :inline="true" :model="listQuery" class="form-inline">
          <el-form-item>
            <el-input
              v-model="listQuery.keyword"
              placeholder="日期/科室名称"
              clearable
              @keyup.enter.native="handleFilter"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-container">
        <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增时间段</el-button>
        <el-button type="success" icon="el-icon-copy-document" @click="handleBatchCreate">批量设置</el-button>
      </div>

      <!-- 表格区域 -->
      <div class="table-container" style="overflow: auto; max-height: calc(100vh - 280px);">
        <el-table
          v-loading="listLoading"
          :data="list"
          border
          style="width: 100%">
          <el-table-column label="日期" width="120">
            <template slot-scope="{row}">
              {{ formatDate(row.date) }}
            </template>
          </el-table-column>
          <el-table-column label="时间段" width="150">
            <template slot-scope="{row}">
              {{ formatTimeRange(row.startTime, row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column label="科室" prop="deptName" width="120" />
          <el-table-column label="容量" width="120">
            <template slot-scope="{row}">
              <el-input-number
                v-model="row.capacity"
                :min="0"
                :max="999"
                size="mini"
                @change="handleCapacityChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="已预约" prop="appointmentCount" width="100" align="center" />
          <el-table-column label="剩余" width="100" align="center">
            <template slot-scope="{row}">
              {{ row.capacity - row.appointmentCount }}
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
          <el-table-column label="操作" align="center" width="150">
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
      <div style="margin: 10px 0; color: #606266; background: #f9f9f9; padding: 8px; border-radius: 4px;">
        <span>API返回总条数: {{ total }}</span>
        <span style="margin-left: 15px">当前页: {{ listQuery.pageNum }}</span>
        <span style="margin-left: 15px">每页条数: {{ listQuery.pageSize }}</span>
        <span style="margin-left: 15px">当前记录数: {{ list.length }}</span>
        <span style="margin-left: 15px">有效总条数: {{ effectiveTotal }}</span>
      </div>
    </div>

    <!-- 时间段表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="temp.date"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-time-picker
            v-model="temp.timeSlot"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="HH:mm:ss"
            format="HH:mm"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="科室" prop="deptId">
          <el-select v-model="temp.deptId" placeholder="请选择科室" style="width: 100%;">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.deptName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number
            v-model="temp.capacity"
            :min="0"
            :max="999"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="temp.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="temp.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 批量设置对话框 -->
    <el-dialog title="批量设置时间段" :visible.sync="batchVisible" width="600px">
      <el-form
        ref="batchForm"
        :rules="batchRules"
        :model="batchTemp"
        label-position="right"
        label-width="100px">
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="batchTemp.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-time-picker
            v-model="batchTemp.timeSlot"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="HH:mm:ss"
            format="HH:mm"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="科室" prop="deptId">
          <el-select v-model="batchTemp.deptId" placeholder="请选择科室" style="width: 100%;">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.deptName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number
            v-model="batchTemp.capacity"
            :min="0"
            :max="999"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="batchTemp.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="batchTemp.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatchForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import { getTimeSlotList, createTimeSlot, updateTimeSlot, deleteTimeSlot, batchCreateTimeSlot } from '@/api/exam/timeslot'
import { getAllDepartments } from '@/api/department'
import { mapGetters } from 'vuex'

export default {
  name: 'TimeSlot',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined
      },
      departmentOptions: [],
      dialogVisible: false,
      dialogTitle: '',
      batchVisible: false,
      temp: {
        id: undefined,
        date: '',
        timeSlot: null,
        deptId: undefined,
        capacity: 0,
        status: 1,
        remark: ''
      },
      batchTemp: {
        dateRange: [],
        timeSlot: null,
        deptId: undefined,
        capacity: 0,
        status: 1,
        remark: ''
      },
      rules: {
        date: [
          { required: true, message: '请选择日期', trigger: 'change' }
        ],
        timeSlot: [
          { required: true, message: '请选择时间段', trigger: 'change' }
        ],
        deptId: [
          { required: true, message: '请选择科室', trigger: 'change' }
        ],
        capacity: [
          { required: true, message: '请输入容量', trigger: 'blur' }
        ]
      },
      batchRules: {
        dateRange: [
          { required: true, message: '请选择日期范围', trigger: 'change' }
        ],
        timeSlot: [
          { required: true, message: '请选择时间段', trigger: 'change' }
        ],
        deptId: [
          { required: true, message: '请选择科室', trigger: 'change' }
        ],
        capacity: [
          { required: true, message: '请输入容量', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'device'
    ]),
    effectiveTotal() {
      if (this.total > 0) {
        return this.total;
      }
      
      if (this.list.length > 0) {
        if (this.listQuery.pageNum > 1) {
          return (this.listQuery.pageNum - 1) * this.listQuery.pageSize + this.list.length;
        }
        if (this.list.length < this.listQuery.pageSize) {
          return this.list.length;
        }
        return this.list.length * 2;
      }
      
      return 0;
    }
  },
  created() {
    console.log('TimeSlot component created')
    this.getList()
    this.getDepartmentOptions()
  },
  methods: {
    // 格式化日期数组为字符串
    formatDate(dateArray) {
      if (!dateArray || !Array.isArray(dateArray) || dateArray.length < 3) {
        return '';
      }
      
      const [year, month, day] = dateArray;
      return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
    },
    
    // 格式化时间数组为字符串
    formatTimeRange(startTimeArray, endTimeArray) {
      if (!startTimeArray || !endTimeArray || !Array.isArray(startTimeArray) || !Array.isArray(endTimeArray)) {
        return '';
      }
      
      const formatTime = (timeArray) => {
        if (timeArray.length < 3) return '';
        return `${timeArray[0].toString().padStart(2, '0')}:${timeArray[1].toString().padStart(2, '0')}`;
      };
      
      const startTime = formatTime(startTimeArray);
      const endTime = formatTime(endTimeArray);
      
      if (!startTime || !endTime) return '';
      return `${startTime}-${endTime}`;
    },
    
    async getList() {
      console.log('开始获取时间段列表')
      this.listLoading = true
      try {
        console.log('请求参数:', this.listQuery)
        const response = await getTimeSlotList(this.listQuery);
        console.log('获取时间段列表响应:', response)
        if (response && response.code === 200 && response.data) {
          this.list = response.data.records || [];
          this.total = response.data.total || 0;
          console.log('已获取时间段列表:', this.list.length, '条记录')
        } else {
          this.list = [];
          this.total = 0;
          this.$message.warning(response?.message || '获取数据失败');
        }
      } catch (error) {
        console.error('获取时间段列表失败:', error);
        this.list = [];
        this.total = 0;
        this.$message.error('获取时间段列表失败');
      } finally {
        this.listLoading = false;
      }
    },
    async getDepartmentOptions() {
      console.log('开始获取部门列表')
      try {
        const response = await getAllDepartments()
        console.log('获取部门列表响应:', response)
        if (response.code === 200) {
          this.departmentOptions = response.data || []
          console.log('已获取部门列表:', this.departmentOptions.length, '个部门')
        } else {
          this.departmentOptions = []
          this.$message.warning(`获取部门列表失败: ${response.message || '未知错误'}`)
        }
      } catch (error) {
        console.error('获取部门列表失败:', error)
        this.departmentOptions = []
        this.$message.error('获取部门列表失败')
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
      this.dialogTitle = '新增时间段'
      this.temp = {
        id: undefined,
        date: '',
        timeSlot: null,
        deptId: undefined,
        capacity: 0,
        status: 1,
        remark: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleBatchCreate() {
      this.batchTemp = {
        dateRange: [],
        timeSlot: null,
        deptId: undefined,
        capacity: 0,
        status: 1,
        remark: ''
      }
      this.batchVisible = true
      this.$nextTick(() => {
        this.$refs['batchForm'].clearValidate()
      })
    },
    async handleUpdate(row) {
      this.dialogTitle = '编辑时间段'
      
      // 复制行数据并处理时间格式
      const tempData = { ...row }
      
      // 格式化日期
      if (Array.isArray(tempData.date)) {
        const [year, month, day] = tempData.date;
        tempData.date = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
      }
      
      // 如果有开始时间和结束时间，则转换为timeSlot数组
      if (tempData.startTime && tempData.endTime) {
        // 处理数组格式的时间
        if (Array.isArray(tempData.startTime) && Array.isArray(tempData.endTime)) {
          const formatTime = (timeArray) => {
            if (timeArray.length < 3) return '';
            return `${timeArray[0].toString().padStart(2, '0')}:${timeArray[1].toString().padStart(2, '0')}:${timeArray[2].toString().padStart(2, '0')}`;
          };
          
          tempData.timeSlot = [formatTime(tempData.startTime), formatTime(tempData.endTime)];
        } else {
          tempData.timeSlot = [tempData.startTime, tempData.endTime];
        }
      }
      
      // 确保使用deptId，不使用departmentId
      if (tempData.departmentId && !tempData.deptId) {
        tempData.deptId = tempData.departmentId;
        delete tempData.departmentId;
      }
      
      this.temp = tempData;
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate();
      });
    },
    async handleCapacityChange(row) {
      try {
        await updateTimeSlot({
          id: row.id,
          capacity: row.capacity
        })
        this.$message.success('容量更新成功')
      } catch (error) {
        console.error('更新容量失败:', error)
        this.$message.error('更新容量失败')
      }
    },
    async handleStatusChange(row) {
      try {
        await updateTimeSlot({
          id: row.id,
          status: row.status
        })
        this.$message.success('状态更新成功')
      } catch (error) {
        console.error('更新状态失败:', error)
        // 恢复原状态
        row.status = row.status === 1 ? 0 : 1
        this.$message.error('更新状态失败')
      }
    },
    async handleDelete(row) {
      try {
        await this.$confirm('是否确认删除该时间段?', '提示', {
          type: 'warning'
        })
        await deleteTimeSlot(row.id)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('删除时间段失败:', error)
      }
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            const requestData = { ...this.temp }
            
            if (this.temp.timeSlot && this.temp.timeSlot.length === 2) {
              requestData.startTime = this.temp.timeSlot[0]
              requestData.endTime = this.temp.timeSlot[1]
              delete requestData.timeSlot
            }
            
            if (requestData.departmentId) {
              delete requestData.departmentId
            }
            
            if (this.temp.id) {
              await updateTimeSlot(requestData)
            } else {
              await createTimeSlot(requestData)
            }
            this.dialogVisible = false
            this.$message.success('保存成功')
            this.getList()
          } catch (error) {
            console.error('保存时间段失败:', error)
            this.$message.error(`保存失败: ${error.response?.data?.message || error.message || '未知错误'}`)
          }
        }
      })
    },
    submitBatchForm() {
      this.$refs['batchForm'].validate(async (valid) => {
        if (valid) {
          try {
            const requestData = {
              deptId: this.batchTemp.deptId,
              startDate: this.batchTemp.dateRange[0],
              endDate: this.batchTemp.dateRange[1],
              capacity: this.batchTemp.capacity,
              status: this.batchTemp.status,
              remark: this.batchTemp.remark
            }
            
            if (this.batchTemp.timeSlot && this.batchTemp.timeSlot.length === 2) {
              requestData.startTime = this.batchTemp.timeSlot[0]
              requestData.endTime = this.batchTemp.timeSlot[1]
            }
            
            await batchCreateTimeSlot(requestData)
            this.batchVisible = false
            this.$message.success('批量设置成功')
            this.getList()
          } catch (error) {
            console.error('批量设置时间段失败:', error)
            this.$message.error(`批量设置失败: ${error.response?.data?.message || error.message || '未知错误'}`)
          }
        }
      })
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