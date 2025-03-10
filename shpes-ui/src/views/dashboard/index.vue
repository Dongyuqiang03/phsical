<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="card-header">
            <span>待体检预约</span>
          </div>
          <div class="card-content">
            <h2>{{ pendingAppointments }}</h2>
            <p>个预约待处理</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="card-header">
            <span>今日体检人数</span>
          </div>
          <div class="card-content">
            <h2>{{ todayExams }}</h2>
            <p>人</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="card-header">
            <span>本月体检人数</span>
          </div>
          <div class="card-content">
            <h2>{{ monthExams }}</h2>
            <p>人</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="card-header">
            <span>体检报告待出具</span>
          </div>
          <div class="card-content">
            <h2>{{ pendingReports }}</h2>
            <p>份</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="card-header">
            <span>最近预约</span>
          </div>
          <el-table :data="recentAppointments" style="width: 100%">
            <el-table-column prop="name" label="姓名" width="120"></el-table-column>
            <el-table-column prop="date" label="预约日期" width="180"></el-table-column>
            <el-table-column prop="package" label="体检套餐"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === '已确认' ? 'success' : 'warning'">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="card-header">
            <span>待处理报告</span>
          </div>
          <el-table :data="pendingReportList" style="width: 100%">
            <el-table-column prop="name" label="姓名" width="120"></el-table-column>
            <el-table-column prop="examDate" label="体检日期" width="180"></el-table-column>
            <el-table-column prop="department" label="科室"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template slot-scope="scope">
                <el-tag type="danger">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      pendingAppointments: 0,
      todayExams: 0,
      monthExams: 0,
      pendingReports: 0,
      recentAppointments: [],
      pendingReportList: []
    }
  },
  created() {
    this.fetchDashboardData()
  },
  methods: {
    fetchDashboardData() {
      // TODO: Integrate with backend API
      // Mock data for development
      this.pendingAppointments = 15
      this.todayExams = 28
      this.monthExams = 342
      this.pendingReports = 8
      this.recentAppointments = [
        {
          name: '张三',
          date: '2024-01-20',
          package: '入学体检',
          status: '已确认'
        },
        {
          name: '李四',
          date: '2024-01-21',
          package: '常规体检',
          status: '待确认'
        }
      ]
      this.pendingReportList = [
        {
          name: '王五',
          examDate: '2024-01-19',
          department: '内科',
          status: '待审核'
        },
        {
          name: '赵六',
          examDate: '2024-01-19',
          department: '外科',
          status: '待审核'
        }
      ]
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;

  .box-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .card-content {
      text-align: center;
      padding: 20px 0;

      h2 {
        font-size: 28px;
        color: #409EFF;
        margin: 0;
      }

      p {
        margin: 10px 0 0;
        color: #909399;
      }
    }
  }
}
</style>