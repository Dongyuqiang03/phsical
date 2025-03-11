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
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="appointmentList"
      border
      style="width: 100%">
      <el-table-column label="预约编号" prop="id" width="120" />
      <el-table-column label="预约人" prop="userName" width="120" />
      <el-table-column label="体检套餐" prop="packageName" min-width="150" />
      <el-table-column label="预约日期" prop="appointmentDate" width="120" />
      <el-table-column label="预约时间" prop="timeSlot" width="120" />
      <el-table-column label="科室" prop="departmentName" width="120" />
      <el-table-column label="状态" width="100">
        <template slot-scope="{row}">
          <el-tag :type="row.status | statusTypeFilter">
            {{ row.status | statusFilter }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="{row}">
          <el-button
            v-if="row.status === 'PENDING'"
            size="mini"
            type="primary"
            @click="handleChangeTime(row)">
            改期
          </el-button>
          <el-button
            v-if="row.status === 'PENDING'"
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
    <el-dialog title="预约改期" :visible.sync="changeTimeDialog" width="500px">
      <el-form :model="changeTimeForm" label-width="80px">
        <el-form-item label="预约日期">
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
        <el-form-item label="时间段">
          <el-select v-model="changeTimeForm.timeSlotId" placeholder="请选择时间段" style="width: 100%">
            <el-option
              v-for="item in timeSlotList"
              :key="item.id"
              :label="item.timeSlot"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="changeTimeDialog = false">取 消</el-button>
        <el-button type="primary" @click="submitChangeTime">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAppointmentList, cancelAppointment, updateAppointmentTime } from '@/api/exam/appointment'
import { getAvailableTimeSlots } from '@/api/exam/timeslot'
import Pagination from '@/components/Pagination'

export default {
  name: 'AppointmentList',
  components: { Pagination },
  filters: {
    statusFilter(status) {
      const statusMap = {
        PENDING: '待体检',
        COMPLETED: '已完成',
        CANCELLED: '已取消'
      }
      return statusMap[status]
    },
    statusTypeFilter(status) {
      const statusMap = {
        PENDING: 'warning',
        COMPLETED: 'success',
        CANCELLED: 'info'
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
        status: undefined
      },
      statusOptions: [
        { label: '待体检', value: 'PENDING' },
        { label: '已完成', value: 'COMPLETED' },
        { label: '已取消', value: 'CANCELLED' }
      ],
      changeTimeDialog: false,
      changeTimeForm: {
        id: undefined,
        date: '',
        timeSlotId: undefined
      },
      timeSlotList: [],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const { data } = await getAppointmentList(this.queryParams)
        this.appointmentList = data.list
        this.total = data.total
      } catch (error) {
        console.error('获取预约列表失败:', error)
      }
      this.loading = false
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
        await cancelAppointment(row.id)
        this.$message.success('取消预约成功')
        this.getList()
      } catch (error) {
        console.error('取消预约失败:', error)
      }
    },
    handleChangeTime(row) {
      this.changeTimeForm = {
        id: row.id,
        date: '',
        timeSlotId: undefined
      }
      this.changeTimeDialog = true
    },
    async handleDateChange() {
      if (!this.changeTimeForm.date) return
      try {
        const { data } = await getAvailableTimeSlots({
          date: this.changeTimeForm.date,
          packageId: this.appointmentList.find(item => item.id === this.changeTimeForm.id).packageId
        })
        this.timeSlotList = data
      } catch (error) {
        console.error('获取可用时间段失败:', error)
      }
    },
    async submitChangeTime() {
      if (!this.changeTimeForm.date || !this.changeTimeForm.timeSlotId) {
        this.$message.warning('请选择预约日期和时间段')
        return
      }
      try {
        await updateAppointmentTime(this.changeTimeForm.id, {
          timeSlotId: this.changeTimeForm.timeSlotId
        })
        this.$message.success('修改预约时间成功')
        this.changeTimeDialog = false
        this.getList()
      } catch (error) {
        console.error('修改预约时间失败:', error)
      }
    },
    handleDetail(row) {
      this.$router.push(`/exam/appointment/detail/${row.id}`)
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
}
</style> 