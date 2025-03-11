<template>
  <div class="app-container">
    <div class="report-container" v-loading="loading">
      <!-- 操作按钮 -->
      <div class="operation-bar">
        <el-button type="primary" icon="el-icon-printer" @click="handlePrint">打印报告</el-button>
        <el-button type="success" icon="el-icon-download" @click="handleExport">导出PDF</el-button>
      </div>

      <!-- 报告内容 -->
      <div class="report-content" id="reportContent">
        <!-- 报告头部 -->
        <div class="report-header">
          <h1>体检报告单</h1>
          <div class="report-info">
            <div class="info-item">
              <span class="label">报告编号：</span>
              <span class="value">{{ report.id }}</span>
            </div>
            <div class="info-item">
              <span class="label">体检日期：</span>
              <span class="value">{{ report.examDate }}</span>
            </div>
          </div>
        </div>

        <!-- 基本信息 -->
        <div class="section">
          <h2>基本信息</h2>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="姓名">{{ report.userName }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ report.gender }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ report.age }}岁</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ report.idCard }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ report.phone }}</el-descriptions-item>
            <el-descriptions-item label="体检套餐">{{ report.packageName }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 体格检查 -->
        <div class="section">
          <h2>体格检查</h2>
          <el-descriptions :column="4" border>
            <el-descriptions-item label="身高">{{ report.height }}cm</el-descriptions-item>
            <el-descriptions-item label="体重">{{ report.weight }}kg</el-descriptions-item>
            <el-descriptions-item label="BMI">{{ report.bmi }}</el-descriptions-item>
            <el-descriptions-item label="血压">{{ report.bloodPressure }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 检查结果 -->
        <div class="section">
          <h2>检查结果</h2>
          <el-table :data="report.examResults" border style="width: 100%">
            <el-table-column label="检查项目" prop="itemName" min-width="150" />
            <el-table-column label="检查结果" prop="result" min-width="150" />
            <el-table-column label="参考范围" prop="reference" min-width="150" />
            <el-table-column label="结果分析" min-width="150">
              <template slot-scope="{row}">
                <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
                  {{ row.status === 'NORMAL' ? '正常' : '异常' }}
                </el-tag>
                <div v-if="row.status === 'ABNORMAL'" class="analysis">
                  {{ row.analysis }}
                </div>
              </template>
            </el-table-column>
            <el-table-column label="检查科室" prop="departmentName" width="120" />
            <el-table-column label="检查医生" prop="doctorName" width="120" />
          </el-table>
        </div>

        <!-- 检查结论 -->
        <div class="section">
          <h2>体检结论</h2>
          <div class="conclusion-box">
            <div class="conclusion-item">
              <div class="label">主要发现：</div>
              <div class="content">{{ report.findings }}</div>
            </div>
            <div class="conclusion-item">
              <div class="label">结论建议：</div>
              <div class="content">{{ report.suggestion }}</div>
            </div>
          </div>
          <div class="signature">
            <div class="sign-item">
              <span>体检医生：</span>
              <span>{{ report.doctorName }}</span>
            </div>
            <div class="sign-item">
              <span>报告日期：</span>
              <span>{{ report.reportDate }}</span>
            </div>
          </div>
        </div>

        <!-- 报告底部 -->
        <div class="report-footer">
          <div class="hospital-info">
            <p>校医院体检中心</p>
            <p>联系电话：xxx-xxxxxxxx</p>
            <p>地址：xxx校区校医院</p>
          </div>
          <div class="qrcode">
            <!-- 预留二维码位置 -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getExamReport, exportReport } from '@/api/exam/report'

export default {
  name: 'ExamReport',
  data() {
    return {
      loading: false,
      report: {
        id: '',
        examDate: '',
        userName: '',
        gender: '',
        age: '',
        idCard: '',
        phone: '',
        packageName: '',
        height: '',
        weight: '',
        bmi: '',
        bloodPressure: '',
        examResults: [],
        findings: '',
        suggestion: '',
        doctorName: '',
        reportDate: ''
      }
    }
  },
  created() {
    this.getReport()
  },
  methods: {
    async getReport() {
      this.loading = true
      try {
        const { data } = await getExamReport(this.$route.params.id)
        this.report = data
      } catch (error) {
        console.error('获取体检报告失败:', error)
      }
      this.loading = false
    },
    handlePrint() {
      const content = document.getElementById('reportContent')
      const printWindow = window.open('', '_blank')
      printWindow.document.write(`
        <html>
          <head>
            <title>体检报告单</title>
            <style>
              ${this.getPrintStyles()}
            </style>
          </head>
          <body>
            ${content.innerHTML}
          </body>
        </html>
      `)
      printWindow.document.close()
      printWindow.print()
    },
    async handleExport() {
      try {
        await exportReport(this.$route.params.id)
        this.$message.success('报告导出成功')
      } catch (error) {
        console.error('报告导出失败:', error)
      }
    },
    getPrintStyles() {
      return `
        body {
          padding: 20px;
          font-family: Arial, sans-serif;
        }
        .operation-bar {
          display: none;
        }
        .report-header h1 {
          text-align: center;
          margin-bottom: 20px;
        }
        .section {
          margin-bottom: 20px;
        }
        .section h2 {
          margin-bottom: 10px;
        }
        table {
          width: 100%;
          border-collapse: collapse;
        }
        table, th, td {
          border: 1px solid #ddd;
          padding: 8px;
        }
        .conclusion-box {
          border: 1px solid #ddd;
          padding: 15px;
        }
        .signature {
          margin-top: 30px;
          display: flex;
          justify-content: space-between;
        }
        .report-footer {
          margin-top: 50px;
          text-align: center;
        }
      `
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .report-container {
    background: #fff;
    padding: 20px;

    .operation-bar {
      margin-bottom: 20px;
      text-align: right;
    }

    .report-content {
      padding: 20px;
      border: 1px solid #ddd;

      .report-header {
        text-align: center;
        margin-bottom: 30px;

        h1 {
          margin-bottom: 20px;
        }

        .report-info {
          display: flex;
          justify-content: space-between;
          margin: 0 50px;

          .info-item {
            .label {
              font-weight: bold;
            }
          }
        }
      }

      .section {
        margin-bottom: 30px;

        h2 {
          margin-bottom: 15px;
          padding-left: 10px;
          border-left: 4px solid #409EFF;
        }

        .conclusion-box {
          border: 1px solid #ddd;
          padding: 20px;

          .conclusion-item {
            margin-bottom: 15px;

            .label {
              font-weight: bold;
              margin-bottom: 5px;
            }

            .content {
              line-height: 1.6;
            }
          }
        }

        .signature {
          margin-top: 30px;
          display: flex;
          justify-content: flex-end;

          .sign-item {
            margin-left: 50px;
          }
        }
      }

      .report-footer {
        margin-top: 50px;
        text-align: center;
        color: #666;

        .hospital-info {
          p {
            margin: 5px 0;
          }
        }
      }
    }
  }
}

.analysis {
  margin-top: 5px;
  color: #F56C6C;
  font-size: 12px;
}
</style> 