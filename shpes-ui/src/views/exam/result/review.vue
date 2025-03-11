<template>
  <div class="app-container">
    <div class="review-container" v-loading="loading">
      <!-- 操作按钮 -->
      <div class="operation-bar">
        <el-button-group>
          <el-button
            type="success"
            :disabled="!canApprove"
            @click="handleApprove">
            通过审核
          </el-button>
          <el-button
            type="danger"
            :disabled="!canReject"
            @click="handleReject">
            退回修改
          </el-button>
        </el-button-group>
      </div>

      <!-- 基本信息 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>体检信息</span>
          <el-tag style="float: right" :type="statusType">{{ statusText }}</el-tag>
        </div>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="预约编号">{{ examInfo.appointmentId }}</el-descriptions-item>
          <el-descriptions-item label="体检人">{{ examInfo.userName }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ examInfo.gender }}</el-descriptions-item>
          <el-descriptions-item label="年龄">{{ examInfo.age }}岁</el-descriptions-item>
          <el-descriptions-item label="体检套餐">{{ examInfo.packageName }}</el-descriptions-item>
          <el-descriptions-item label="体检日期">{{ examInfo.examDate }}</el-descriptions-item>
          <el-descriptions-item label="检查医生">{{ examInfo.doctorName }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ examInfo.submitTime }}</el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="reviewStatusType">{{ reviewStatusText }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 体格检查 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>体格检查</span>
        </div>
        <el-descriptions :column="4" border>
          <el-descriptions-item label="身高">{{ physicalForm.height }}cm</el-descriptions-item>
          <el-descriptions-item label="体重">{{ physicalForm.weight }}kg</el-descriptions-item>
          <el-descriptions-item label="BMI">{{ physicalForm.bmi }}</el-descriptions-item>
          <el-descriptions-item label="血压">{{ physicalForm.bloodPressure }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 检查结果 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>检查结果</span>
          <span style="float: right; color: #666; font-size: 14px">
            异常项：{{ abnormalCount }} 项
          </span>
        </div>
        <el-table :data="examItems" border>
          <el-table-column label="项目名称" prop="name" min-width="150" />
          <el-table-column label="参考范围" prop="reference" min-width="150" show-overflow-tooltip />
          <el-table-column label="检查结果" prop="result" min-width="150" />
          <el-table-column label="结果分析" min-width="200">
            <template slot-scope="{row}">
              <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
                {{ row.status === 'NORMAL' ? '正常' : '异常' }}
              </el-tag>
              <div v-if="row.status === 'ABNORMAL'" class="analysis">
                {{ row.analysis }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="审核意见" min-width="200">
            <template slot-scope="{row}">
              <el-input
                v-model="row.reviewComment"
                type="textarea"
                :rows="2"
                placeholder="请输入审核意见（选填）"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 体检结论 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>体检结论</span>
        </div>
        <el-form :model="conclusionForm" label-width="100px">
          <el-form-item label="主要发现">
            <div class="conclusion-text">{{ conclusionForm.findings }}</div>
          </el-form-item>
          <el-form-item label="结论建议">
            <div class="conclusion-text">{{ conclusionForm.suggestion }}</div>
          </el-form-item>
          <el-form-item label="审核意见">
            <el-input
              v-model="conclusionForm.reviewComment"
              type="textarea"
              :rows="3"
              placeholder="请输入审核意见"
            />
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 退回原因对话框 -->
    <el-dialog
      title="退回原因"
      :visible.sync="rejectDialog.visible"
      width="500px">
      <el-form :model="rejectDialog.form" label-width="80px">
        <el-form-item label="退回原因" required>
          <el-input
            v-model="rejectDialog.form.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入退回原因"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rejectDialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="confirmReject">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 审核确认对话框 -->
    <el-dialog
      title="审核确认"
      :visible.sync="approveDialog.visible"
      width="400px">
      <div class="dialog-content">
        <p>确认通过此份体检报告的审核吗？</p>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="approveDialog.visible = false">取 消</el-button>
        <el-button type="primary" @click="confirmApprove">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getExamReport, reviewReport } from '@/api/exam/report'

export default {
  name: 'ResultReview',
  data() {
    return {
      loading: false,
      examInfo: {
        appointmentId: '',
        userName: '',
        gender: '',
        age: '',
        packageName: '',
        examDate: '',
        doctorName: '',
        submitTime: '',
        status: '',
        reviewStatus: ''
      },
      physicalForm: {
        height: '',
        weight: '',
        bmi: '',
        bloodPressure: ''
      },
      examItems: [],
      conclusionForm: {
        findings: '',
        suggestion: '',
        reviewComment: ''
      },
      rejectDialog: {
        visible: false,
        form: {
          reason: ''
        }
      },
      approveDialog: {
        visible: false
      }
    }
  },
  computed: {
    statusType() {
      const typeMap = {
        PENDING: 'warning',
        COMPLETED: 'success',
        CANCELLED: 'info'
      }
      return typeMap[this.examInfo.status] || 'info'
    },
    statusText() {
      const textMap = {
        PENDING: '待体检',
        COMPLETED: '已完成',
        CANCELLED: '已取消'
      }
      return textMap[this.examInfo.status] || '未知'
    },
    reviewStatusType() {
      const typeMap = {
        PENDING: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger'
      }
      return typeMap[this.examInfo.reviewStatus] || 'info'
    },
    reviewStatusText() {
      const textMap = {
        PENDING: '待审核',
        APPROVED: '已通过',
        REJECTED: '已退回'
      }
      return textMap[this.examInfo.reviewStatus] || '未知'
    },
    abnormalCount() {
      return this.examItems.filter(item => item.status === 'ABNORMAL').length
    },
    canApprove() {
      return this.examInfo.reviewStatus === 'PENDING'
    },
    canReject() {
      return this.examInfo.reviewStatus === 'PENDING'
    }
  },
  created() {
    this.getReportDetail()
  },
  methods: {
    async getReportDetail() {
      this.loading = true
      try {
        const { data } = await getExamReport(this.$route.params.id)
        this.examInfo = {
          appointmentId: data.appointmentId,
          userName: data.userName,
          gender: data.gender,
          age: data.age,
          packageName: data.packageName,
          examDate: data.examDate,
          doctorName: data.doctorName,
          submitTime: data.submitTime,
          status: data.status,
          reviewStatus: data.reviewStatus
        }
        this.physicalForm = data.physical
        this.examItems = data.examItems.map(item => ({
          ...item,
          reviewComment: ''
        }))
        this.conclusionForm = {
          findings: data.conclusion.findings,
          suggestion: data.conclusion.suggestion,
          reviewComment: ''
        }
      } catch (error) {
        console.error('获取体检报告失败:', error)
      }
      this.loading = false
    },
    handleApprove() {
      this.approveDialog.visible = true
    },
    handleReject() {
      this.rejectDialog.visible = true
    },
    async confirmApprove() {
      try {
        const data = {
          action: 'APPROVE',
          itemComments: this.examItems
            .filter(item => item.reviewComment)
            .map(item => ({
              itemId: item.id,
              comment: item.reviewComment
            })),
          conclusionComment: this.conclusionForm.reviewComment
        }
        await reviewReport(this.$route.params.id, data)
        this.$message.success('审核通过成功')
        this.approveDialog.visible = false
        this.getReportDetail()
      } catch (error) {
        console.error('审核通过失败:', error)
      }
    },
    async confirmReject() {
      if (!this.rejectDialog.form.reason) {
        this.$message.warning('请输入退回原因')
        return
      }
      try {
        const data = {
          action: 'REJECT',
          reason: this.rejectDialog.form.reason,
          itemComments: this.examItems
            .filter(item => item.reviewComment)
            .map(item => ({
              itemId: item.id,
              comment: item.reviewComment
            })),
          conclusionComment: this.conclusionForm.reviewComment
        }
        await reviewReport(this.$route.params.id, data)
        this.$message.success('退回成功')
        this.rejectDialog.visible = false
        this.rejectDialog.form.reason = ''
        this.getReportDetail()
      } catch (error) {
        console.error('退回失败:', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .review-container {
    .operation-bar {
      margin-bottom: 20px;
      text-align: right;
    }

    .box-card {
      margin-bottom: 20px;
    }

    .conclusion-text {
      line-height: 1.6;
      padding: 10px;
      background-color: #f8f8f8;
      border-radius: 4px;
    }
  }
}

.analysis {
  margin-top: 5px;
  color: #F56C6C;
  font-size: 12px;
}

.dialog-content {
  padding: 20px 0;
  text-align: center;
}
</style> 