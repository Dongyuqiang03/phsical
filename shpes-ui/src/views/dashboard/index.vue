<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="card-header">
            <div class="title">今日预约</div>
            <i class="el-icon-date icon"></i>
          </div>
          <div class="card-body">
            <div class="number">{{ stats.todayAppointments }}</div>
            <div class="compare">
              较昨日
              <span :class="stats.appointmentTrend >= 0 ? 'up' : 'down'">
                {{ Math.abs(stats.appointmentTrend) }}%
                <i :class="stats.appointmentTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              </span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="card-header">
            <div class="title">待体检</div>
            <i class="el-icon-user icon"></i>
          </div>
          <div class="card-body">
            <div class="number">{{ stats.pendingExams }}</div>
            <div class="compare">
              较昨日
              <span :class="stats.pendingTrend >= 0 ? 'up' : 'down'">
                {{ Math.abs(stats.pendingTrend) }}%
                <i :class="stats.pendingTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              </span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="card-header">
            <div class="title">已完成</div>
            <i class="el-icon-check icon"></i>
          </div>
          <div class="card-body">
            <div class="number">{{ stats.completedExams }}</div>
            <div class="compare">
              较昨日
              <span :class="stats.completedTrend >= 0 ? 'up' : 'down'">
                {{ Math.abs(stats.completedTrend) }}%
                <i :class="stats.completedTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              </span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="card-header">
            <div class="title">异常率</div>
            <i class="el-icon-warning icon"></i>
          </div>
          <div class="card-body">
            <div class="number">{{ stats.abnormalRate }}%</div>
            <div class="compare">
              较昨日
              <span :class="stats.abnormalTrend >= 0 ? 'up' : 'down'">
                {{ Math.abs(stats.abnormalTrend) }}%
                <i :class="stats.abnormalTrend >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              </span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <div class="chart-card">
          <div class="card-header">
            <div class="title">体检预约趋势</div>
            <div class="actions">
              <el-radio-group v-model="appointmentChartType" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="card-body">
            <div ref="appointmentChart" style="height: 300px;"></div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <div class="card-header">
            <div class="title">科室工作量</div>
            <div class="actions">
              <el-select v-model="departmentChartDate" size="small" placeholder="选择日期">
                <el-option label="今日" value="today"></el-option>
                <el-option label="本周" value="week"></el-option>
                <el-option label="本月" value="month"></el-option>
              </el-select>
            </div>
          </div>
          <div class="card-body">
            <div ref="departmentChart" style="height: 300px;"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 体检完成情况 -->
    <el-row class="chart-row">
      <el-col :span="24">
        <div class="chart-card">
          <div class="card-header">
            <div class="title">体检完成情况</div>
            <div class="actions">
              <el-date-picker
                v-model="completionDate"
                type="daterange"
                size="small"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期">
              </el-date-picker>
            </div>
          </div>
          <div class="card-body">
            <el-table :data="completionData" style="width: 100%" border>
              <el-table-column prop="date" label="日期" width="180"></el-table-column>
              <el-table-column prop="total" label="预约总数"></el-table-column>
              <el-table-column prop="completed" label="已完成"></el-table-column>
              <el-table-column prop="normal" label="正常"></el-table-column>
              <el-table-column prop="abnormal" label="异常"></el-table-column>
              <el-table-column prop="rate" label="完成率">
                <template slot-scope="scope">
                  <el-progress :percentage="scope.row.rate" :color="getProgressColor(scope.row.rate)"></el-progress>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      stats: {
        todayAppointments: 0,
        appointmentTrend: 0,
        pendingExams: 0,
        pendingTrend: 0,
        completedExams: 0,
        completedTrend: 0,
        abnormalRate: 0,
        abnormalTrend: 0
      },
      appointmentChartType: 'week',
      departmentChartDate: 'today',
      completionDate: [],
      completionData: [],
      appointmentChart: null,
      departmentChart: null
    }
  },
  mounted() {
    this.initCharts()
    this.fetchDashboardData()
  },
  beforeDestroy() {
    if (this.appointmentChart) {
      this.appointmentChart.dispose()
    }
    if (this.departmentChart) {
      this.departmentChart.dispose()
    }
  },
  methods: {
    initCharts() {
      // 初始化预约趋势图
      this.appointmentChart = echarts.init(this.$refs.appointmentChart)
      this.appointmentChart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['预约人数', '实到人数']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '预约人数',
            type: 'line',
            data: [120, 132, 101, 134, 90, 230, 210],
            smooth: true,
            areaStyle: {
              opacity: 0.3
            }
          },
          {
            name: '实到人数',
            type: 'line',
            data: [110, 122, 91, 124, 85, 220, 200],
            smooth: true,
            areaStyle: {
              opacity: 0.3
            }
          }
        ]
      })

      // 初始化科室工作量图
      this.departmentChart = echarts.init(this.$refs.departmentChart)
      this.departmentChart.setOption({
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '科室工作量',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 235, name: '内科' },
              { value: 274, name: '外科' },
              { value: 310, name: '检验科' },
              { value: 335, name: '影像科' },
              { value: 400, name: '体检科' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    },
    async fetchDashboardData() {
      try {
        // 模拟数据，实际项目中应该从API获取
        this.stats = {
          todayAppointments: 156,
          appointmentTrend: 12.5,
          pendingExams: 89,
          pendingTrend: -5.2,
          completedExams: 67,
          completedTrend: 8.3,
          abnormalRate: 15.6,
          abnormalTrend: 2.1
        }

        this.completionData = [
          { date: '2024-03-11', total: 100, completed: 85, normal: 75, abnormal: 10, rate: 85 },
          { date: '2024-03-12', total: 120, completed: 98, normal: 88, abnormal: 10, rate: 82 },
          { date: '2024-03-13', total: 95, completed: 80, normal: 72, abnormal: 8, rate: 84 }
        ]
      } catch (error) {
        console.error('获取数据失败:', error)
      }
    },
    getProgressColor(rate) {
      if (rate >= 90) return '#67C23A'
      if (rate >= 70) return '#409EFF'
      if (rate >= 50) return '#E6A23C'
      return '#F56C6C'
    }
  },
  watch: {
    appointmentChartType() {
      // 根据选择的时间范围更新预约趋势图数据
      this.fetchDashboardData()
    },
    departmentChartDate() {
      // 根据选择的日期更新科室工作量图数据
      this.fetchDashboardData()
    },
    completionDate() {
      // 根据选择的日期范围更新体检完成情况数据
      this.fetchDashboardData()
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;

  .stat-card {
    background: #fff;
    border-radius: 4px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 1px 4px rgba(0,21,41,.08);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .title {
        font-size: 16px;
        color: #606266;
      }

      .icon {
        font-size: 24px;
        color: #409EFF;
      }
    }

    .card-body {
      .number {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 10px;
      }

      .compare {
        font-size: 14px;
        color: #909399;

        .up {
          color: #67C23A;
        }

        .down {
          color: #F56C6C;
        }
      }
    }
  }

  .chart-row {
    margin-bottom: 20px;
  }

  .chart-card {
    background: #fff;
    border-radius: 4px;
    padding: 20px;
    box-shadow: 0 1px 4px rgba(0,21,41,.08);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .title {
        font-size: 16px;
        color: #606266;
      }
    }
  }
}
</style>