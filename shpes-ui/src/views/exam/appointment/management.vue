<template>
  <div class="app-container">

    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" class="form-inline">
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用户">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="loading"
      :data="appointmentList"
      border
      style="width: 100%">
      <el-table-column prop="appointmentNo" label="预约编号" width="180" />
      <el-table-column prop="userName" label="用户姓名" width="120" />
      <el-table-column prop="packageName" label="体检套餐" min-width="150" />
      <el-table-column prop="deptName" label="预约科室" width="120" />
      <el-table-column label="预约日期" width="110" align="center">
        <template slot-scope="{row}">
          {{ formatAppointmentDate(row.appointmentDate) }}
        </template>
      </el-table-column>
      <el-table-column label="预约时间" width="130" align="center">
        <template slot-scope="{row}">
          {{ formatTimeSlot(row) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.status | statusTypeFilter">{{ row.status | statusFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="160" align="center">
        <template slot-scope="{row}">
          {{ formatCreateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220">
        <template slot-scope="{row}">
          <el-button
            v-if="row.status === 1 || row.status === 2"
            type="success"
            size="mini"
            @click="handleComplete(row)">
            完成体检
          </el-button>
          <el-button
            v-if="row.status === 0 || row.status === 1 || row.status === 2"
            type="danger"
            size="mini"
            @click="handleCancel(row)">
            取消
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="handleDetail(row)">
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-if="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="handlePagination"
    />

    <!-- 取消预约对话框 -->
    <el-dialog
      title="取消预约"
      :visible.sync="cancelDialogVisible"
      width="30%">
      <el-form :model="cancelForm" label-width="80px">
        <el-form-item label="取消原因" required>
          <el-input
            v-model="cancelForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入取消原因"
          />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitCancel">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAppointmentList, cancelAppointment, completeAppointment } from '@/api/exam/appointment'
import Pagination from '@/components/Pagination'

export default {
  name: 'AppointmentManagement',
  components: { Pagination },
  filters: {
    statusFilter(status) {
      const statusMap = {
        'PENDING': '待体检',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消',
        0: '待确认',
        1: '待体检',
        2: '进行中',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status]
    },
    statusTypeFilter(status) {
      const statusMap = {
        'PENDING': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'info',
        0: 'info',
        1: 'warning',
        2: 'primary',
        3: 'success',
        4: 'info'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      loading: false,
      total: 0,
      appointmentList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dateRange: [],
        status: undefined,
        userName: undefined
      },
      statusOptions: [
        { label: '待确认', value: 0 },
        { label: '待体检', value: 1 },
        { label: '进行中', value: 2 },
        { label: '已完成', value: 3 },
        { label: '已取消', value: 4 }
      ],
      cancelDialogVisible: false,
      cancelForm: {
        id: undefined,
        reason: ''
      },
      // 防抖动标志
      requestInProgress: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      // 防止重复请求
      if (this.requestInProgress) {
        console.log('已有请求正在进行中，跳过此次请求');
        return;
      }
      
      this.requestInProgress = true;
      console.log('getList 被调用')
      this.loading = true
      try {
        // 处理日期范围
        let params = { ...this.queryParams }
        if (params.dateRange && params.dateRange.length === 2) {
          params.startDate = params.dateRange[0]
          params.endDate = params.dateRange[1]
        }
        delete params.dateRange
        
        console.log('开始请求预约列表数据，参数:', params)
        const response = await getAppointmentList(params)
        console.log('获取预约列表响应:', response)
        
        if (response && response.code === 200) {
          // 检查响应数据结构
          console.log('响应数据结构:', response.data)
          
          // 检查是否有 records 字段替代 list
          const listData = response.data.list || response.data.records || []
          this.appointmentList = listData
          
          this.total = response.data.total || 0
          console.log('处理后的预约列表:', this.appointmentList)
          console.log('总数:', this.total)
        } else {
          this.$message.error(response?.message || '获取预约列表失败')
        }
      } catch (error) {
        console.error('获取预约列表失败:', error)
        this.$message.error('获取预约列表失败: ' + (error.message || '服务器错误'))
      } finally {
        this.loading = false
        this.requestInProgress = false
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        dateRange: [],
        status: undefined,
        userName: undefined
      }
      this.getList()
    },
    // 处理分页事件
    handlePagination() {
      console.log('分页事件触发，页码:', this.queryParams.pageNum, '每页数量:', this.queryParams.pageSize)
      this.getList()
    },
    handleComplete(row) {
      this.$confirm('确认该用户已完成体检吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await completeAppointment(row.id)
          if (response && response.code === 200) {
            this.$message.success('操作成功')
            this.getList()
          } else {
            this.$message.error(response?.message || '操作失败')
          }
        } catch (error) {
          console.error('完成体检失败:', error)
          this.$message.error('操作失败: ' + (error.message || '服务器错误'))
        }
      }).catch(() => {})
    },
    handleCancel(row) {
      this.cancelForm = {
        id: row.id,
        reason: ''
      }
      this.cancelDialogVisible = true
    },
    async submitCancel() {
      if (!this.cancelForm.reason) {
        this.$message.warning('请输入取消原因')
        return
      }
      
      try {
        const response = await cancelAppointment(this.cancelForm.id, this.cancelForm.reason)
        if (response && response.code === 200) {
          this.$message.success('取消预约成功')
          this.cancelDialogVisible = false
          this.getList()
        } else {
          this.$message.error(response?.message || '取消预约失败')
        }
      } catch (error) {
        console.error('取消预约失败:', error)
        this.$message.error('取消预约失败: ' + (error.message || '服务器错误'))
      }
    },
    handleDetail(row) {
      this.$router.push(`/exam/appointment/detail/${row.id}`)
    },
    formatTimeSlot(row) {
      // 如果有开始时间和结束时间，格式化显示
      if (row.startTime && row.endTime) {
        let startTime = this.formatTime(row.startTime)
        let endTime = this.formatTime(row.endTime)
        return `${startTime}-${endTime}`
      }
      return '未设置'
    },
    formatTime(time) {
      if (Array.isArray(time) && time.length >= 2) {
        return `${time[0].toString().padStart(2, '0')}:${time[1].toString().padStart(2, '0')}`
      }
      
      if (typeof time === 'string') {
        const date = new Date(time)
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      }
      
      return '00:00'
    },
    formatAppointmentDate(date) {
      if (Array.isArray(date) && date.length >= 3) {
        return `${date[0]}-${String(date[1]).padStart(2, '0')}-${String(date[2]).padStart(2, '0')}`
      }
      
      return date || '未设置'
    },
    formatCreateTime(time) {
      if (Array.isArray(time) && time.length >= 6) {
        return `${time[0]}-${String(time[1]).padStart(2, '0')}-${String(time[2]).padStart(2, '0')} ${String(time[3]).padStart(2, '0')}:${String(time[4]).padStart(2, '0')}:${String(time[5]).padStart(2, '0')}`
      }
      
      return time || '未设置'
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

  .form-inline {
    display: flex;
    flex-wrap: wrap;
  }
}
</style> 