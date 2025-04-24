<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
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
        <el-form-item label="预约状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 130px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button v-if="isAdmin" type="success" icon="el-icon-s-tools" @click="goToManagement">管理视图</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="appointmentList"
      border
      style="width: 100%">
      <el-table-column label="预约编号" prop="appointmentNo" width="180" />
      <el-table-column label="预约人" prop="userName" width="120" />
      <el-table-column label="体检套餐" prop="packageName" min-width="150" />
      <el-table-column label="预约日期" width="120">
        <template slot-scope="{row}">
          <date-time-format :value="row.appointmentDate" type="date" />
        </template>
      </el-table-column>
      <el-table-column label="预约时间" width="120">
        <template slot-scope="{row}">
          <date-time-format :value="row.startTime" type="time" /> - 
          <date-time-format :value="row.endTime" type="time" />
        </template>
      </el-table-column>
      <el-table-column label="科室" prop="deptName" width="120"/>

      <el-table-column label="状态" width="100">
        <template slot-scope="{row}">
          <el-tag :type="row.status | statusTypeFilter">
            {{ row.status | statusFilter }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template slot-scope="{row}">
          <date-time-format :value="row.createTime" type="datetime" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="{row}">
          <el-button
            v-if="row.status === 1 || row.statusString === 'PENDING'"
            size="mini"
            type="primary"
            @click="handleChangeTime(row)">
            改期
          </el-button>
          <el-button
            v-if="row.status === 1 || row.statusString === 'PENDING'"
            size="mini"
            type="danger"
            @click="handleCancel(row)">
            取消
          </el-button>
          <el-button
            size="mini"
            type="info"
            @click="handleDetail(row)">
            详情
          </el-button>
          <el-button
            v-if="row.status === 3 || row.statusString === 'COMPLETED'"
            size="mini"
            type="success"
            @click="handleViewResult(row)">
            体检结果
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 改期对话框 -->
    <el-dialog title="预约改期" :visible.sync="changeTimeDialog" width="600px">
      <el-form :model="changeTimeForm" label-width="100px">
        <el-form-item label="当前预约信息">
          <div class="current-appointment-info">
            <el-tag>{{ getCurrentAppointmentInfo() }}</el-tag>
          </div>
        </el-form-item>
        <el-form-item label="选择新日期" required>
          <el-date-picker
            v-model="changeTimeForm.date"
            type="date"
            placeholder="选择日期"
            :picker-options="pickerOptions"
            value-format="yyyy-MM-dd"
            style="width: 100%"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="选择新时间段" required>
          <div v-if="!changeTimeForm.date" class="no-date-selected">
            <el-alert type="warning" :closable="false" show-icon title="请先选择日期"></el-alert>
          </div>
          <div v-else-if="timeSlotList.length === 0 && !timeSlotLoading" class="no-slots-available">
            <el-alert type="info" :closable="false" show-icon title="所选日期没有可用时间段"></el-alert>
          </div>
          <div v-else>
            <el-table
              v-loading="timeSlotLoading"
              :data="timeSlotList"
              border
              :row-class-name="highlightSelectedRow"
              style="width: 100%">
              <el-table-column label="时间段" width="150">
                <template slot-scope="{row}">
                  <date-time-format :value="row.startTime" type="time" /> - 
                  <date-time-format :value="row.endTime" type="time" />
                </template>
              </el-table-column>
              <el-table-column label="科室" width="120">
                <template slot-scope="{row}">
                  {{ row.deptName || '未指定科室' }}
                </template>
              </el-table-column>
              <el-table-column label="剩余名额" width="100" align="center">
                <template slot-scope="{row}">
                  {{ row.remainingCapacity || 0 }}
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="120">
                <template slot-scope="{row}">
                  <el-button
                    type="primary"
                    size="mini"
                    :disabled="(row.remainingCapacity || 0) <= 0"
                    @click="selectTimeSlotForReschedule(row)">
                    选择
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-if="changeTimeForm.timeSlotId" class="selected-slot-info">
            已选择: {{ getSelectedTimeSlotInfo() }}
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="changeTimeDialog = false">取 消</el-button>
        <el-button type="primary" @click="submitChangeTime" :disabled="!changeTimeForm.timeSlotId">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserAppointmentList, cancelAppointment, updateAppointmentTime } from '@/api/exam/appointment'
import { getAvailableTimeSlotsForPackage, getTimeSlotDetail } from '@/api/exam/timeslot'
import Pagination from '@/components/Pagination'
import DateTimeFormat from '@/components/DateTimeFormat'

export default {
  name: 'AppointmentList',
  components: { Pagination, DateTimeFormat  },
  filters: {
    statusFilter(status) {
      const statusMap = {
        'PENDING': '待体检',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消',
        '0': '待确认',
        '1': '待体检',
        '2': '进行中',
        '3': '已完成',
        '4': '已取消'
      }
      return statusMap[status] || '未知'
    },
    statusTypeFilter(status) {
      const statusMap = {
        'PENDING': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'info',
        '0': 'info',
        '1': 'warning',
        '2': 'primary',
        '3': 'success',
        '4': 'info'
      }
      return statusMap[status] || 'info'
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
        status: undefined
      },
      statusOptions: [
        { label: '待确认', value: 0 },
        { label: '待体检', value: 1 },
        { label: '进行中', value: 2 },
        { label: '已完成', value: 3 },
        { label: '已取消', value: 4 }
      ],
      changeTimeDialog: false,
      changeTimeForm: {
        id: undefined,
        date: '',
        timeSlotId: undefined
      },
      timeSlotList: [],
      timeSlotLoading: false,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      },
      isAdmin: false
    }
  },
  beforeCreate() {
    // 确保在初始化前定义queryParams，防止在render前触发错误
    this.queryParams = {
      pageNum: 1,
      pageSize: 10,
      dateRange: [],
      status: undefined
    }
  },
  created() {
    this.getList()
    this.checkAdminPermission()
  },
  methods: {
    // 检查是否有管理员权限
    checkAdminPermission() {
      const userPermissions = this.$store.state.user.permissions || []
      // 这里检查用户是否有管理员权限，可以根据实际情况调整判断条件
      this.isAdmin = userPermissions.includes('exam:appointment')
      console.log('User has admin permission:', this.isAdmin)
    },
    // 获取预约日期信息
    getAppointmentDate(row) {
      // 优先使用从时间段详情中获取的日期
      if (row.timeSlotDetail && row.timeSlotDetail.date) {
        return row.timeSlotDetail.date
      }
      
      // 如果有appointmentDate字段，尝试格式化
      if (row.appointmentDate) {
        return Array.isArray(row.appointmentDate) 
          ? this.formatArrayDate(row.appointmentDate)
          : row.appointmentDate
      }
      
      return '未提供'
    },
    // 获取时间段信息
    getTimeSlotInfo(row) {
      // 优先使用格式化后的时间范围
      if (row.appointmentTimeRange) {
        return row.appointmentTimeRange
      }
      
      // 尝试从时间段详情格式化
      if (row.timeSlotDetail && row.timeSlotDetail.startTime && row.timeSlotDetail.endTime) {
        return this.formatTimeRange(row.timeSlotDetail.startTime, row.timeSlotDetail.endTime)
      }
      
      // 回退到显示ID
      return `ID: ${row.timeSlotId || '未知'}`
    },
    // 获取科室名称
    getDepartmentName(row) {
      // 优先使用已设置的科室名称
      if (row.departmentName) {
        return row.departmentName
      }
      
      // 尝试从时间段详情获取
      if (row.timeSlotDetail && row.timeSlotDetail.departmentName) {
        return row.timeSlotDetail.departmentName
      }
      
      return '未提供'
    },
    // 格式化数组形式的时间
    formatArrayDate(dateArray) {
      if (!dateArray || !Array.isArray(dateArray) || dateArray.length < 6) {
        return '无日期'
      }
      // 格式化为 YYYY-MM-DD HH:MM:SS
      return `${dateArray[0]}-${String(dateArray[1]).padStart(2, '0')}-${String(dateArray[2]).padStart(2, '0')} ${String(dateArray[3]).padStart(2, '0')}:${String(dateArray[4]).padStart(2, '0')}:${String(dateArray[5]).padStart(2, '0')}`
    },
    // 格式化状态码
    getStatusString(status) {
      const statusMap = {
        0: 'PENDING_CONFIRM',
        1: 'PENDING', 
        2: 'IN_PROGRESS',
        3: 'COMPLETED',
        4: 'CANCELLED'
      }
      return statusMap[status] || 'PENDING'
    },
    async getList() {
      this.loading = true
      try {
        // 处理日期范围
        let params = { ...this.queryParams }
        if (params.dateRange && params.dateRange.length === 2) {
          params.startDate = params.dateRange[0]
          params.endDate = params.dateRange[1]
        }
        delete params.dateRange
        
        console.log('查询参数:', params)
        const response = await getUserAppointmentList(params)
        console.log('API响应:', response)
        
        if (response && response.code === 200) {
          // 使用records而不是list
          const records = response.data.records || []
          
          // 处理每条记录，格式化日期和状态
          this.appointmentList = records.map(record => {
            return {
              ...record,
              createTimeFormatted: this.formatArrayDate(record.createTime),
              updateTimeFormatted: this.formatArrayDate(record.updateTime),
              // 将数字状态转为字符串，以便于过滤器处理
              statusString: this.getStatusString(record.status)
            }
          })
          
          this.total = response.data.total || 0
          console.log('处理后的数据:', this.appointmentList)
        
        } else {
          this.$message.error(response?.message || '获取预约列表失败')
        }
      } catch (error) {
        console.error('获取预约列表失败:', error)
        this.$message.error('获取预约列表失败：' + (error.message || '服务器错误'))
      } finally {
        this.loading = false
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
        status: undefined
      }
      this.getList()
    },
    async handleCancel(row) {
      try {
        await this.$confirm('确认取消该预约吗？', '提示', {
          type: 'warning'
        })
        console.log('取消预约ID:', row.id)
        await cancelAppointment(row.id)
        this.$message.success('取消预约成功')
        this.getList()
      } catch (error) {
        console.error('取消预约失败:', error)
      }
    },
    handleChangeTime(row) {
      console.log('改期预约数据:', row)
      this.changeTimeForm = {
        id: row.id,
        date: '',
        timeSlotId: row.timeSlotId
      }
      this.changeTimeDialog = true
    },
    async handleDateChange() {
      if (!this.changeTimeForm.date) return
      
      this.timeSlotLoading = true
      this.changeTimeForm.timeSlotId = undefined  // 重置时间段选择
      
      try {
        console.log('获取可用时间段, 日期:', this.changeTimeForm.date)
        
        // 获取当前编辑的预约记录
        const currentAppointment = this.appointmentList.find(item => item.id === this.changeTimeForm.id)
        if (!currentAppointment) {
          console.error('找不到当前编辑的预约记录')
          return
        }
        
        console.log('当前预约记录:', currentAppointment)
        
        const response = await getAvailableTimeSlotsForPackage({
          date: this.changeTimeForm.date,
          packageId: currentAppointment.packageId
        })
        
        if (response && response.code === 200) {
          this.timeSlotList = response.data || []
          console.log('获取到可用时间段:', this.timeSlotList)
        } else {
          this.$message.error(response?.message || '获取可用时间段失败')
        }
      } catch (error) {
        console.error('获取可用时间段失败:', error)
        this.$message.error('获取可用时间段失败: ' + (error.message || '服务器错误'))
        this.timeSlotList = []
      } finally {
        this.timeSlotLoading = false
      }
    },
    async submitChangeTime() {
      if (!this.changeTimeForm.date || !this.changeTimeForm.timeSlotId) {
        this.$message.warning('请选择预约日期和时间段')
        return
      }
      
      try {
        // 获取当前选中的预约和新时间段信息，用于显示确认信息
        const currentAppointment = this.appointmentList.find(item => item.id === this.changeTimeForm.id)
        const selectedSlot = this.timeSlotList.find(item => item.id === this.changeTimeForm.timeSlotId)
        
        // 显示确认对话框
        await this.$confirm(
          `确认将预约时间从 "${this.getAppointmentDate(currentAppointment)} ${this.getTimeSlotInfo(currentAppointment)}" 
          改为 "${this.changeTimeForm.date} ${this.formatTimeRange(selectedSlot.startTime, selectedSlot.endTime)}"?`, 
          '确认改期', 
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 显示加载中
        const loading = this.$loading({
          lock: true,
          text: '正在提交改期请求...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        
        try {
          console.log('提交改期数据:', this.changeTimeForm)
          const response = await updateAppointmentTime(this.changeTimeForm.id, this.changeTimeForm.timeSlotId)
          
          if (response && response.code === 200) {
            const newDate = this.changeTimeForm.date
            const newTime = this.formatTimeRange(selectedSlot.startTime, selectedSlot.endTime)
            this.$message.success(`预约已成功改期至 ${newDate} ${newTime}`)
            this.changeTimeDialog = false
            this.getList()
          } else {
            this.$message.error(response?.message || '修改预约时间失败')
          }
        } catch (error) {
          console.error('修改预约时间失败:', error)
          this.$message.error('修改预约时间失败: ' + (error.message || '服务器错误'))
        } finally {
          loading.close()
        }
      } catch (error) {
        // 用户取消操作
        console.log('用户取消了改期操作')
      }
    },
    handleDetail(row) {
      console.log('查看预约详情:', row.id)
      this.$router.push(`/exam/appointment/detail/${row.id}`)
    },
    handleViewResult(row) {
      if (!row.id) {
        this.$message.error('该记录缺少ID字段，无法查看详情。请联系管理员检查数据完整性。');
        return;
      }
      
      const numericId = Number(row.id);
      if (isNaN(numericId)) {
        this.$message.error('记录ID不是有效的数字，无法查看详情。');
        console.error('无效的记录ID:', row.id, typeof row.id);
        return;
      }
      
      console.log('跳转到详情页面，使用ID:', numericId);
      // 统一使用input.vue页面，设置为只读模式
      this.$router.push({ 
        path: '/exam/result/input', 
        query: { id: numericId, mode: 'readonly' } 
      });
    },
    goToManagement() {
      this.$router.push('/exam/appointment/management')
    },
    // 获取时间段详情信息
    async fetchTimeSlotDetails() {
      // 获取所有不同的timeSlotId
      const timeSlotIds = [...new Set(this.appointmentList
        .filter(item => item.timeSlotId)
        .map(item => item.timeSlotId))]
      
      console.log('获取时间段详情, IDs:', timeSlotIds)
      
      // 保存已加载的时间段详情
      const timeSlotDetails = {}
      
      // 为每个timeSlotId获取详情
      const promises = timeSlotIds.map(async (id) => {
        try {
          const response = await getTimeSlotDetail(id)
          if (response && response.code === 200) {
            timeSlotDetails[id] = response.data
            console.log(`时间段ID ${id} 详情:`, response.data)
            return true
          }
        } catch (error) {
          console.error(`获取时间段 ${id} 详情失败:`, error)
          return false
        }
      })
      
      await Promise.all(promises)
      
      // 更新每个预约记录的时间段详情
      this.appointmentList = this.appointmentList.map(item => {
        if (item.timeSlotId && timeSlotDetails[item.timeSlotId]) {
          return {
            ...item,
            timeSlotDetail: timeSlotDetails[item.timeSlotId]
          }
        }
        return item
      })
    },
    // 获取当前预约信息（用于改期对话框显示）
    getCurrentAppointmentInfo() {
      const currentAppointment = this.appointmentList.find(item => item.id === this.changeTimeForm.id)
      if (!currentAppointment) return '无法获取当前预约信息'
      
      const date = this.getAppointmentDate(currentAppointment)
      const time = this.getTimeSlotInfo(currentAppointment)
      
      return `${date} ${time}`
    },
    // 格式化时间范围
    formatTimeRange(startTime, endTime) {
      if (!startTime || !endTime) return '未提供时间'
      
      let start = startTime
      let end = endTime
      
      // 如果是数组格式，转换为字符串
      if (Array.isArray(startTime) && startTime.length >= 3) {
        start = `${String(startTime[0]).padStart(2, '0')}:${String(startTime[1]).padStart(2, '0')}`
      }
      
      if (Array.isArray(endTime) && endTime.length >= 3) {
        end = `${String(endTime[0]).padStart(2, '0')}:${String(endTime[1]).padStart(2, '0')}`
      }
      
      return `${start} - ${end}`
    },
    // 选择时间段（用于改期）
    selectTimeSlotForReschedule(row) {
      this.changeTimeForm.timeSlotId = row.id
      console.log('已选择时间段:', row)
    },
    // 获取已选择的时间段信息
    getSelectedTimeSlotInfo() {
      const selectedSlot = this.timeSlotList.find(item => item.id === this.changeTimeForm.timeSlotId)
      if (!selectedSlot) return ''
      
      const timeRange = this.formatTimeRange(selectedSlot.startTime, selectedSlot.endTime)
      const deptName = selectedSlot.deptName || '未指定科室'
      
      return `${this.changeTimeForm.date} ${timeRange} ${deptName}`
    },
    // 高亮选中的行
    highlightSelectedRow({ row }) {
      if (row.id === this.changeTimeForm.timeSlotId) {
        return 'selected-row'
      }
      return ''
    }
  }
}
</script>

<style scoped>
.current-appointment-info {
  margin-bottom: 10px;
}

.no-date-selected {
  margin-bottom: 10px;
}

.no-slots-available {
  margin-bottom: 10px;
}

.selected-slot-info {
  margin-top: 10px;
  margin-bottom: 10px;
}

.dialog-footer {
  text-align: right;
}

.selected-row {
  background-color: #f0f9eb;
}
</style>