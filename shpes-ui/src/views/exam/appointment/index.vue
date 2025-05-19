<template>
  <div class="app-container">
    <el-steps :active="active" finish-status="success" align-center>
      <el-step title="选择套餐" />
      <el-step title="选择时间" />
      <el-step title="确认预约" />
      <el-step title="预约成功" />
    </el-steps>

    <!-- 套餐选择 -->
    <div v-show="active === 0" class="step-container">
      <div v-if="packageLoading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      <div v-else-if="serverError" class="error-container">
        <el-result
          icon="error"
          title="服务器异常"
          :sub-title="serverErrorMessage"
        >
          <template #extra>
            <el-button type="primary" @click="retryGetPackageList">重试</el-button>
            <el-button @click="goBack">返回</el-button>
          </template>
        </el-result>
      </div>
      <div v-else-if="packageList.length === 0" class="empty-container">
        <el-empty description="暂无套餐"></el-empty>
      </div>
      <el-card v-else v-for="item in packageList" :key="item.id" class="package-card" :class="{ 'is-selected': selectedPackage && selectedPackage.id === item.id }">
        <div slot="header" class="package-header">
          <span class="package-name">{{ item.name }}</span>
          <el-tag size="small">{{ item.itemCount || 0 }}个项目</el-tag>
        </div>
        <div class="package-content">
          <div class="package-description">{{ item.description || '暂无描述' }}</div>
          <div class="package-items">
            <el-tag v-if="item.items && item.items.length > 0" v-for="examItem in item.items" :key="examItem.id" size="small" class="item-tag">
              {{ examItem.name }}
            </el-tag>
            <span v-else>暂无项目</span>
          </div>
        </div>
        <div class="package-footer">
          <el-button type="primary" size="small" @click="selectPackage(item)">选择此套餐</el-button>
        </div>
      </el-card>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </div>

    <!-- 时间选择 -->
    <div v-show="active === 1" class="step-container">
      <div v-if="timeSlotServerError" class="error-container">
        <el-result
          icon="error"
          title="获取时间段失败"
          :sub-title="timeSlotErrorMessage"
        >
          <template #extra>
            <el-button type="primary" @click="handleDateChange">重试</el-button>
            <el-button @click="prev">返回</el-button>
          </template>
        </el-result>
      </div>
      <div v-else>
        <div class="filter-container">
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            :picker-options="pickerOptions"
            value-format="yyyy-MM-dd"
            style="width: 200px;"
            @change="handleDateChange"
          />
        </div>
        <el-table
          v-loading="timeSlotLoading"
          :data="timeSlotList"
          border
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
                @click="selectTimeSlot(row)">
                选择
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 预约确认 -->
    <div v-show="active === 2" class="step-container">
      <el-form ref="form" :model="form" label-width="100px" class="confirm-form">
        <el-form-item label="体检套餐">
          <span>{{ selectedPackage ? selectedPackage.name : '' }}</span>
        </el-form-item>
        <el-form-item label="预约日期">
          <span>
            <date-time-format :value="selectedDate" type="date" />
          </span>
        </el-form-item>
        <el-form-item label="预约时间">
          <span v-if="selectedTimeSlot">
            <date-time-format :value="selectedTimeSlot.startTime" type="time" /> - 
            <date-time-format :value="selectedTimeSlot.endTime" type="time" />
          </span>
          <span v-else>未选择时间</span>
        </el-form-item>
        <el-form-item label="预约科室">
          <span>{{ getDepartmentName() }}</span>
        </el-form-item>
        <el-form-item label="注意事项">
          <div class="notice-content">
            <p>1. 请至少提前30分钟到达体检中心</p>
            <p>2. 请空腹进行体检</p>
            <p>3. 体检当天请携带有效证件</p>
            <p>4. 如需取消预约，请提前24小时操作</p>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <!-- 预约成功 -->
    <div v-show="active === 3" class="step-container">
      <el-result
        icon="success"
        title="预约成功"
        sub-title="您的体检预约已成功提交，请按时到达体检中心"
      >
        <template #extra>
          <el-card class="success-info">
            <el-descriptions title="预约信息" :column="1" border>
              <el-descriptions-item label="预约编号">{{ appointmentInfo.appointmentNo || '生成中...' }}</el-descriptions-item>
              <el-descriptions-item label="体检套餐">{{ selectedPackage ? selectedPackage.name : '' }}</el-descriptions-item>
              <el-descriptions-item label="预约日期">{{ selectedDate }}</el-descriptions-item>
              <el-descriptions-item label="预约时间">
                <date-time-format :value="selectedTimeSlot.startTime" type="time" /> - 
                <date-time-format :value="selectedTimeSlot.endTime" type="time" />
              </el-descriptions-item>
              <el-descriptions-item label="预约科室">{{ getDepartmentName() }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag type="success">待体检</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <div style="margin-top: 20px; text-align: center;">
              <el-button type="primary" @click="viewMyAppointments">查看我的预约</el-button>
              <el-button @click="backToHome">返回首页</el-button>
            </div>
          </el-card>
        </template>
      </el-result>
    </div>

    <!-- 底部按钮 -->
    <div class="bottom-container" v-if="active < 3">
      <el-button v-if="active > 0" @click="prev">上一步</el-button>
      <el-button v-if="active < 2" type="primary" :disabled="!canNext" @click="next">下一步</el-button>
      <el-button v-else-if="active === 2" type="primary" :loading="submitLoading" @click="submitAppointment">确认预约</el-button>
    </div>
  </div>
</template>

<script>
import { getExamPackageList } from '@/api/exam/package'
import { getAvailableTimeSlots, getAvailableTimeSlotsForPackage } from '@/api/exam/timeslot'
import { createAppointment } from '@/api/exam/appointment'
import DateTimeFormat from '@/components/DateTimeFormat'

export default {
  name: 'Appointment',
  components: {
    DateTimeFormat
  },
  data() {
    return {
      active: 0,
      packageList: [],
      packageLoading: false,
      serverError: false,
      serverErrorMessage: '',
      timeSlotServerError: false,
      timeSlotErrorMessage: '',
      selectedPackage: null,
      selectedDate: '',
      selectedTimeSlot: null,
      timeSlotList: [],
      timeSlotLoading: false,
      pageNum: 1,
      pageSize: 10,
      total: 0,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      },
      form: {},
      submitLoading: false,
      appointmentInfo: {} // 存储预约成功后的信息
    }
  },
  computed: {
    canNext() {
      if (this.active === 0) {
        return this.selectedPackage !== null
      } else if (this.active === 1) {
        return this.selectedTimeSlot !== null
      }
      return true
    }
  },
  created() {
    this.getPackageList()
  },
  methods: {
    async getPackageList() {
      this.packageLoading = true
      this.serverError = false
      this.serverErrorMessage = ''
      
      // 从用户信息中获取性别，如果没有则默认不限
      const userInfo = this.$store.getters.user || {}
      const gender = userInfo.gender || 0
      
      try {
        const response = await getExamPackageList({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          gender,
          status: 1 // 只查询启用的套餐
        })
        
        if (response && response.code === 200 && response.data) {
          this.packageList = response.data.records || []
          this.total = response.data.total || 0
          
          if (this.packageList.length === 0) {
            this.$message.warning('暂无可用体检套餐')
          }
        } else {
          this.packageList = []
          this.serverError = true
          this.serverErrorMessage = response?.message || '获取体检套餐失败'
          console.error('获取体检套餐列表失败:', response)
        }
      } catch (error) {
        console.error('获取体检套餐列表失败:', error)
        this.packageList = []
        this.serverError = true
        this.serverErrorMessage = error?.message || '系统异常，请联系管理员'
      } finally {
        this.packageLoading = false
      }
    },
    handleCurrentChange(val) {
      this.pageNum = val
      this.getPackageList()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.pageNum = 1
      this.getPackageList()
    },
    retryGetPackageList() {
      this.getPackageList()
    },
    goBack() {
      this.$router.push('/dashboard')
    },
    selectPackage(item) {
      this.selectedPackage = item
      this.next()
    },
    async handleDateChange() {
      if (!this.selectedDate || !this.selectedPackage) return
      
      this.timeSlotLoading = true
      this.timeSlotServerError = false
      this.timeSlotErrorMessage = ''
      
      try {
        const response = await getAvailableTimeSlotsForPackage({
          date: this.selectedDate,
          packageId: this.selectedPackage.id
        })
        if (response && response.code === 200 && response.data) {
          this.timeSlotList = response.data || []
        } else {
          this.timeSlotList = []
          this.timeSlotServerError = true
          this.timeSlotErrorMessage = response?.message || '获取可预约时间段失败'
          console.error('获取可预约时间段失败:', response)
        }
      } catch (error) {
        console.error('获取可预约时间段失败:', error)
        this.timeSlotList = []
        this.timeSlotServerError = true
        this.timeSlotErrorMessage = '系统异常，请联系管理员'
      } finally {
        this.timeSlotLoading = false
      }
    },
    selectTimeSlot(item) {
      this.selectedTimeSlot = item
      this.next()
    },
    prev() {
      if (this.active > 0) {
        this.active--
      }
    },
    next() {
      if (this.active < 2 && this.canNext) {
        this.active++
      }
    },
    async submitAppointment() {
      if (!this.selectedPackage || !this.selectedTimeSlot) {
        this.$message.warning('请选择体检套餐和预约时间')
        return
      }
      
      this.submitLoading = true
      
      try {
        const response = await createAppointment({
          packageId: this.selectedPackage.id,
          timeSlotId: this.selectedTimeSlot.id
        })
        
        if (response && response.code === 200) {
          // 存储预约信息
          this.appointmentInfo = response.data || {}
          // 显示成功页面
          this.active = 3
          this.$message.success('预约成功')
        } else {
          this.$message.error(response?.message || '预约失败')
        }
      } catch (error) {
        console.error('预约失败:', error)
        this.$message.error('预约失败: ' + (error.message || '系统异常'))
      } finally {
        this.submitLoading = false
      }
    },
    viewMyAppointments() {
      this.$router.push('/exam/appointment/list')
    },
    backToHome() {
      this.$router.push('/dashboard')
    },
    // 在确认预约页面展示科室信息的方法
    getDepartmentName() {
      if (!this.selectedTimeSlot) return '';
      return this.selectedTimeSlot.deptName || '未指定科室';
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .step-container {
    margin: 30px 0;
    min-height: 400px;
  }
  
  .loading-container,
  .empty-container,
  .error-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 300px;
  }

  .package-card {
    margin-bottom: 20px;
    cursor: pointer;

    &.is-selected {
      border: 2px solid #409EFF;
    }

    .package-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .package-name {
        font-size: 16px;
        font-weight: bold;
      }
    }

    .package-content {
      margin: 15px 0;

      .package-description {
        color: #666;
        margin-bottom: 10px;
      }

      .package-items {
        .item-tag {
          margin-right: 5px;
          margin-bottom: 5px;
        }
      }
    }

    .package-footer {
      text-align: right;
    }
  }

  .filter-container {
    margin-bottom: 20px;
  }

  .bottom-container {
    margin-top: 20px;
    text-align: center;
  }
  
  .confirm-form {
    max-width: 600px;
    margin: 0 auto;
    
    .notice-content {
      padding: 10px;
      background-color: #f8f8f8;
      border-radius: 4px;
    }
  }

  .pagination-container {
    margin-top: 20px;
    text-align: center;
  }

  .success-info {
    margin: 20px 0;
    width: 100%;
    max-width: 600px;
  }
}
</style>