<template>
  <div class="record-container">
    <el-card class="box-card">
      <div slot="header" class="card-header">
        <span>体检记录</span>
      </div>
      <el-table :data="examRecords" style="width: 100%">
        <el-table-column prop="date" label="体检日期" width="180"></el-table-column>
        <el-table-column prop="package" label="体检套餐" width="180"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="viewReport(scope.row)"
              :disabled="!scope.row.reportAvailable">查看报告</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog title="体检报告" :visible.sync="dialogVisible" width="70%">
        <div class="report-content" v-if="currentReport">
          <h3>基本信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ currentReport.name }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ currentReport.gender }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ currentReport.age }}</el-descriptions-item>
            <el-descriptions-item label="体检日期">{{ currentReport.examDate }}</el-descriptions-item>
          </el-descriptions>

          <h3>体检结果</h3>
          <el-collapse v-model="activeNames">
            <el-collapse-item v-for="item in currentReport.items" :key="item.department" :title="item.department" :name="item.department">
              <el-descriptions :column="1" border>
                <el-descriptions-item :label="detail.name" v-for="detail in item.details" :key="detail.name">
                  {{ detail.value }}
                  <el-tag size="mini" :type="detail.status === '异常' ? 'danger' : 'success'" style="margin-left: 10px">
                    {{ detail.status }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
              <div class="department-conclusion">
                <strong>科室小结：</strong>{{ item.conclusion }}
              </div>
            </el-collapse-item>
          </el-collapse>

          <div class="report-conclusion">
            <h3>体检结论</h3>
            <p>{{ currentReport.conclusion }}</p>
          </div>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Record',
  data() {
    return {
      examRecords: [],
      dialogVisible: false,
      currentReport: null,
      activeNames: []
    }
  },
  created() {
    this.fetchExamRecords()
  },
  methods: {
    fetchExamRecords() {
      // TODO: Integrate with backend API
      // Mock data for development
      this.examRecords = [
        {
          date: '2024-01-15',
          package: '入学体检',
          status: '已完成',
          reportAvailable: true
        },
        {
          date: '2023-09-20',
          package: '常规体检',
          status: '已完成',
          reportAvailable: true
        },
        {
          date: '2024-01-20',
          package: '教职工体检',
          status: '进行中',
          reportAvailable: false
        }
      ]
    },
    getStatusType(status) {
      const statusMap = {
        '已完成': 'success',
        '进行中': 'warning',
        '未开始': 'info'
      }
      return statusMap[status] || 'info'
    },
    viewReport(record) {
      // TODO: Integrate with backend API
      // Mock report data
      this.currentReport = {
        name: '张三',
        gender: '男',
        age: '20',
        examDate: record.date,
        items: [
          {
            department: '内科',
            details: [
              { name: '血压', value: '120/80mmHg', status: '正常' },
              { name: '心率', value: '75次/分', status: '正常' }
            ],
            conclusion: '内科检查未见异常'
          },
          {
            department: '外科',
            details: [
              { name: '体重', value: '65kg', status: '正常' },
              { name: '身高', value: '175cm', status: '正常' }
            ],
            conclusion: '外科检查未见异常'
          }
        ],
        conclusion: '体检结果正常，建议定期复查。'
      }
      this.dialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.record-container {
  padding: 20px;

  .report-content {
    h3 {
      margin: 20px 0;
      color: #303133;
    }

    .department-conclusion {
      margin: 15px 0;
      color: #606266;
    }

    .report-conclusion {
      margin-top: 30px;
      padding: 20px;
      background-color: #f5f7fa;
      border-radius: 4px;

      h3 {
        margin-top: 0;
      }

      p {
        color: #606266;
        line-height: 1.6;
      }
    }
  }
}
</style>