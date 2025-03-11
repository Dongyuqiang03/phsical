<template>
  <div class="app-container">
    <div class="detail-container" v-loading="loading">
      <!-- 基本信息 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>预约信息</span>
          <el-button
            v-if="detail.status === 'PENDING'"
            style="float: right; margin-left: 10px"
            size="mini"
            type="danger"
            @click="handleCancel">
            取消预约
          </el-button>
          <el-button
            v-if="detail.status === 'PENDING'"
            style="float: right"
            size="mini"
            type="primary"
            @click="handleChangeTime">
            修改时间
          </el-button>
        </div>
        <el-descriptions class="margin-top" :column="2" border>
          <el-descriptions-item label="预约编号">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="预约状态">
            <el-tag :type="detail.status | statusTypeFilter">
              {{ detail.status | statusFilter }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="预约人">{{ detail.userName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.phone }}</el-descriptions-item>
          <el-descriptions-item label="预约日期">{{ detail.appointmentDate }}</el-descriptions-item>
          <el-descriptions-item label="预约时间">{{ detail.timeSlot }}</el-descriptions-item>
          <el-descriptions-item label="体检科室">{{ detail.departmentName }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 套餐信息 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>体检套餐</span>
        </div>
        <el-descriptions class="margin-top" :column="1" border>
          <el-descriptions-item label="套餐名称">{{ detail.packageName }}</el-descriptions-item>
          <el-descriptions-item label="套餐说明">{{ detail.packageDescription }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 体检项目列表 -->
        <div class="sub-title">体检项目列表</div>
        <el-table :data="detail.examItems" border style="width: 100%">
          <el-table-column label="项目名称" prop="name" />
          <el-table-column label="检查科室" prop="departmentName" width="120" />
          <el-table-column label="项目说明" prop="description" show-overflow-tooltip />
          <el-table-column label="注意事项" prop="notice" show-overflow-tooltip width="200" />
        </el-table>
      </el-card>

      <!-- 体检记录 -->
      <el-card class="box-card" v-if="detail.status === 'COMPLETED'">
        <div slot="header" class="clearfix">
          <span>体检记录</span>
          <el-button
            style="float: right"
            size="mini"
            type="primary"
            @click="handleViewReport">
            查看报告
          </el-button>
        </div>
        <el-descriptions class="margin-top" :column="2" border>
          <el-descriptions-item label="体检日期">{{ detail.examDate }}</el-descriptions-item>
          <el-descriptions-item label="体检医生">{{ detail.doctorName }}</el-descriptions-item>
          <el-descriptions-item label="体检结论" :span="2">{{ detail.conclusion }}</el-descriptions-item>
          <el-descriptions-item label="健康建议" :span="2">{{ detail.suggestion }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>

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
import { getAppointmentDetail, cancelAppointment, updateAppointmentTime } from '@/api/exam/appointment'
import { getAvailableTimeSlots } from '@/api/exam/timeslot'

export default {
  name: 'AppointmentDetail',
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
      detail: {
        id: undefined,
        status: '',
        userName: '',
        phone: '',
        appointmentDate: '',
        timeSlot: '',
        departmentName: '',
        createTime: '',
        packageName: '',
        packageDescription: '',
        examItems: [],
        examDate: '',
        doctorName: '',
        conclusion: '',
        suggestion: ''
      },
      changeTimeDialog: false,
      changeTimeForm: {
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
    this.getDetail()
  },
  methods: {
    async getDetail() {
      this.loading = true
      try {
        const { data } = await getAppointmentDetail(this.$route.params.id)
        this.detail = data
      } catch (error) {
        console.error('获取预约详情失败:', error)
      }
      this.loading = false
    },
    async handleCancel() {
      try {
        await this.$confirm('确认取消该预约吗？', '提示', {
          type: 'warning'
        })
        await cancelAppointment(this.detail.id)
        this.$message.success('取消预约成功')
        this.getDetail()
      } catch (error) {
        console.error('取消预约失败:', error)
      }
    },
    handleChangeTime() {
      this.changeTimeForm = {
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
          packageId: this.detail.packageId
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
        await updateAppointmentTime(this.detail.id, {
          timeSlotId: this.changeTimeForm.timeSlotId
        })
        this.$message.success('修改预约时间成功')
        this.changeTimeDialog = false
        this.getDetail()
      } catch (error) {
        console.error('修改预约时间失败:', error)
      }
    },
    handleViewReport() {
      // 跳转到体检报告页面
      this.$router.push(`/exam/report/${this.detail.id}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .detail-container {
    .box-card {
      margin-bottom: 20px;
    }

    .sub-title {
      font-size: 16px;
      font-weight: bold;
      margin: 20px 0;
    }
  }
}
</style> 