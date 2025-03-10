<template>
  <div class="appointment-container">
    <el-card class="box-card">
      <div slot="header" class="card-header">
        <span>体检预约</span>
      </div>
      <el-form :model="appointmentForm" :rules="rules" ref="appointmentForm" label-width="100px">
        <el-form-item label="体检套餐" prop="package">
          <el-select v-model="appointmentForm.package" placeholder="请选择体检套餐">
            <el-option
              v-for="item in packages"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="预约日期" prop="date">
          <el-date-picker
            v-model="appointmentForm.date"
            type="date"
            placeholder="选择日期"
            :picker-options="dateOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="appointmentForm.timeSlot" placeholder="请选择时间段">
            <el-option
              v-for="slot in timeSlots"
              :key="slot.value"
              :label="slot.label"
              :value="slot.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input
            type="textarea"
            v-model="appointmentForm.notes"
            placeholder="请输入备注信息（选填）">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('appointmentForm')">提交预约</el-button>
          <el-button @click="resetForm('appointmentForm')">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Appointment',
  data() {
    return {
      appointmentForm: {
        package: '',
        date: '',
        timeSlot: '',
        notes: ''
      },
      rules: {
        package: [
          { required: true, message: '请选择体检套餐', trigger: 'change' }
        ],
        date: [
          { required: true, message: '请选择预约日期', trigger: 'change' }
        ],
        timeSlot: [
          { required: true, message: '请选择时间段', trigger: 'change' }
        ]
      },
      packages: [
        { id: 1, name: '入学体检套餐' },
        { id: 2, name: '常规体检套餐' },
        { id: 3, name: '教职工体检套餐' }
      ],
      timeSlots: [
        { value: '1', label: '8:00-9:00' },
        { value: '2', label: '9:00-10:00' },
        { value: '3', label: '10:00-11:00' },
        { value: '4', label: '14:00-15:00' },
        { value: '5', label: '15:00-16:00' }
      ],
      dateOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7
        }
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // TODO: Integrate with backend API
          this.$message({
            type: 'success',
            message: '预约提交成功！'
          })
        } else {
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    }
  }
}
</script>

<style lang="scss" scoped>
.appointment-container {
  padding: 20px;

  .box-card {
    width: 600px;
    margin: 0 auto;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>