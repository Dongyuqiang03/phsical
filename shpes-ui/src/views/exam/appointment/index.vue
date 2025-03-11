<template>
  <div class="app-container">
    <el-steps :active="active" finish-status="success" align-center>
      <el-step title="选择套餐" />
      <el-step title="选择时间" />
      <el-step title="确认预约" />
    </el-steps>

    <!-- 套餐选择 -->
    <div v-show="active === 0" class="step-container">
      <el-card v-for="item in packageList" :key="item.id" class="package-card" :class="{ 'is-selected': selectedPackage && selectedPackage.id === item.id }">
        <div slot="header" class="package-header">
          <span class="package-name">{{ item.name }}</span>
          <el-tag size="small">{{ item.itemCount }}个项目</el-tag>
        </div>
        <div class="package-content">
          <div class="package-description">{{ item.description }}</div>
          <div class="package-items">
            <el-tag v-for="examItem in item.items" :key="examItem.id" size="small" class="item-tag">
              {{ examItem.name }}
            </el-tag>
          </div>
        </div>
        <div class="package-footer">
          <el-button type="primary" size="small" @click="selectPackage(item)">选择此套餐</el-button>
        </div>
      </el-card>
    </div>

    <!-- 时间选择 -->
    <div v-show="active === 1" class="step-container">
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
        <el-table-column label="时间段" prop="timeSlot" width="150" />
        <el-table-column label="科室" prop="departmentName" width="120" />
        <el-table-column label="剩余名额" width="100" align="center">
          <template slot-scope="{row}">
            {{ row.capacity - row.reserved }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="120">
          <template slot-scope="{row}">
            <el-button
              type="primary"
              size="mini"
              :disabled="row.capacity <= row.reserved"
              @click="selectTimeSlot(row)">
              选择
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 预约确认 -->
    <div v-show="active === 2" class="step-container">
      <el-form ref="form" :model="form" label-width="100px" class="confirm-form">
        <el-form-item label="体检套餐">
          <span>{{ selectedPackage.name }}</span>
        </el-form-item>
        <el-form-item label="预约日期">
          <span>{{ selectedDate }}</span>
        </el-form-item>
        <el-form-item label="预约时间">
          <span>{{ selectedTimeSlot.timeSlot }}</span>
        </el-form-item>
        <el-form-item label="预约科室">
          <span>{{ selectedTimeSlot.departmentName }}</span>
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

    <!-- 底部按钮 -->
    <div class="bottom-container">
      <el-button v-if="active > 0" @click="prev">上一步</el-button>
      <el-button v-if="active < 2" type="primary" :disabled="!canNext" @click="next">下一步</el-button>
      <el-button v-else type="primary" @click="submitAppointment">确认预约</el-button>
    </div>
  </div>
</template>

<script>
import { getAllExamPackages } from '@/api/exam/package'
import { getAvailableTimeSlots } from '@/api/exam/timeslot'
import { createAppointment } from '@/api/exam/appointment'

export default {
  name: 'Appointment',
  data() {
    return {
      active: 0,
      packageList: [],
      selectedPackage: null,
      selectedDate: '',
      selectedTimeSlot: null,
      timeSlotList: [],
      timeSlotLoading: false,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      },
      form: {}
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
      try {
        const { data } = await getAllExamPackages()
        this.packageList = data
      } catch (error) {
        console.error('获取体检套餐列表失败:', error)
      }
    },
    selectPackage(item) {
      this.selectedPackage = item
      this.next()
    },
    async handleDateChange() {
      if (!this.selectedDate) return
      this.timeSlotLoading = true
      try {
        const { data } = await getAvailableTimeSlots({
          date: this.selectedDate,
          packageId: this.selectedPackage.id
        })
        this.timeSlotList = data
      } catch (error) {
        console.error('获取可预约时间段失败:', error)
      }
      this.timeSlotLoading = false
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
      try {
        await createAppointment({
          packageId: this.selectedPackage.id,
          timeSlotId: this.selectedTimeSlot.id
        })
        this.$message.success('预约成功')
        this.$router.push('/exam/appointment/list')
      } catch (error) {
        console.error('预约失败:', error)
      }
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

  .confirm-form {
    width: 500px;
    margin: 0 auto;

    .notice-content {
      color: #666;
      line-height: 1.8;
    }
  }

  .bottom-container {
    text-align: center;
    margin-top: 30px;
  }
}
</style> 