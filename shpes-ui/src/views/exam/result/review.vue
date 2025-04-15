<template>
  <div class="app-container">
    <div class="review-container" v-loading="loading">
      <!-- 操作按钮 -->
      <div class="operation-bar" v-if="!isReadonlyMode">
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

      <!-- 只读模式操作栏 -->
      <div class="operation-bar" v-if="isReadonlyMode">
        <el-button-group>
          <el-button
            type="primary"
            @click="handlePrint">
            打印报告
          </el-button>
          <el-button
            type="default"
            @click="handleBack">
            返回列表
          </el-button>
        </el-button-group>
      </div>

      <!-- 基本信息 -->
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <span>{{ pageTitle }}</span>
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
                v-if="!isReadonlyMode"
                v-model="row.reviewComment"
                type="textarea"
                :rows="2"
                placeholder="请输入审核意见（选填）"
              />
              <div v-else class="review-comment">{{ row.reviewComment || '无' }}</div>
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
          <el-form-item label="审核意见" v-if="!isReadonlyMode">
            <el-input
              v-model="conclusionForm.reviewComment"
              type="textarea"
              :rows="3"
              placeholder="请输入审核意见"
            />
          </el-form-item>
          <el-form-item label="审核意见" v-else>
            <div class="conclusion-text">{{ conclusionForm.reviewComment || '无审核意见' }}</div>
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
import { getResultsByRecordId } from '@/api/exam/result'

export default {
  name: 'ResultReview',
  data() {
    return {
      loading: false,
      mode: 'normal', // 默认为正常模式，可选值：normal(默认), readonly(只读)
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
    // 是否为只读模式
    isReadonlyMode() {
      return this.mode === 'readonly'
    },
    // 页面标题计算属性
    pageTitle() {
      return this.isReadonlyMode ? '查看体检结果' : '体检报告审核'
    },
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
    this.initMode()
    this.getReportDetail()
  },
  methods: {
    // 初始化页面模式
    initMode() {
      // 获取URL参数中的mode值
      const mode = this.$route.query.mode
      if (mode === 'readonly') {
        this.mode = 'readonly'
        console.log('当前为只读模式')
      } else {
        this.mode = 'normal'
        console.log('当前为正常模式')
      }
    },
    async getReportDetail() {
      this.loading = true
      try {
        // 1. 从查询参数获取记录ID
        const recordId = this.$route.query.id;
        if (!recordId) {
          this.$message.error('记录ID不存在，无法获取报告信息');
          return;
        }
        
        console.log('正在获取体检记录信息，ID:', recordId);
        
        // 2. 使用与input.vue相同的API获取数据
        const response = await getExamReport(recordId);
        if (!response.data || response.code !== 200) {
          this.$message.error('获取体检记录信息失败: ' + (response.message || '未知错误'));
          return;
        }
        
        const data = response.data;
        console.log('体检记录信息:', data);
        
        // 3. 设置基本信息 - 使用与input.vue相同的字段映射逻辑
        this.examInfo = {
          appointmentId: data.appointmentId || '',
          userName: data.userName || data.name || '',
          gender: data.gender || '',
          age: data.age || '',
          packageName: data.packageName || '',
          examDate: data.examDate || data.createTime || '',
          doctorName: data.doctorName || '',
          submitTime: data.updateTime || '',
          status: data.status,
          reviewStatus: data.reviewStatus || 'PENDING'
        };
        
        // 4. 设置体格检查数据
        this.physicalForm = data.physical || {
          height: '',
          weight: '',
          bmi: '',
          bloodPressure: ''
        };
        
        // 5. 处理体检项目和结果 - 使用getResultsByRecordId接口获取结果数据
        try {
          // 调用getResultsByRecordId接口获取体检结果
          const resultResponse = await getResultsByRecordId(recordId);
          console.log('从后端获取的体检结果数据:', resultResponse);
          
          if (resultResponse.data && Array.isArray(resultResponse.data)) {
            // 将获取到的结果转换为组件需要的格式
            this.examItems = resultResponse.data.map(result => ({
              id: result.id || result.itemId,
              name: result.itemName || result.name || '未知项目',
              reference: result.reference || '',
              // 统一使用后端字段名，优先使用value字段，兼容旧版本的result字段
              result: result.value || result.result || '',
              // 统一使用后端字段名，优先使用abnormal字段，兼容旧版本的status字段
              status: (result.abnormal === 1 || result.abnormal === '1') ? 'ABNORMAL' : 'NORMAL',
              // 统一使用后端字段名，优先使用suggestion字段，兼容旧版本的analysis字段
              analysis: result.suggestion || result.analysis || '',
              reviewComment: ''
            }));
          } else {
            // 如果API没有返回结果数据，尝试使用报告中的数据
            if (Array.isArray(data.examItems) && data.examItems.length > 0) {
              // 如果API直接返回了examItems，使用它们
              this.examItems = data.examItems.map(item => ({
                id: item.id || item.itemId,
                name: item.name || item.itemName || '未知项目',
                reference: item.reference || '',
                result: item.result || item.value || '',
                status: item.status || (item.abnormal === 1 ? 'ABNORMAL' : 'NORMAL'),
                analysis: item.analysis || item.suggestion || '',
                reviewComment: ''
              }));
            } else if (Array.isArray(data.results) && data.results.length > 0) {
              // 如果API返回了results数组，转换它们
              this.examItems = data.results.map(result => ({
                id: result.id || result.itemId,
                name: result.itemName || result.name || '未知项目',
                reference: result.reference || '',
                result: result.value || result.result || '',
                status: result.abnormal === 1 ? 'ABNORMAL' : 'NORMAL',
                analysis: result.suggestion || result.analysis || '',
                reviewComment: ''
              }));
            } else {
              // 如果没有结果，显示空数组
              this.examItems = [];
              this.$message.warning('该体检记录暂无体检结果数据');
            }
          }
        } catch (error) {
          console.error('获取体检结果数据失败:', error);
          // 如果获取结果失败，尝试使用报告中的数据
          if (Array.isArray(data.examItems) && data.examItems.length > 0) {
            this.examItems = data.examItems.map(item => ({
              id: item.id || item.itemId,
              name: item.name || item.itemName || '未知项目',
              reference: item.reference || '',
              result: item.result || item.value || '',
              status: item.status || (item.abnormal === 1 ? 'ABNORMAL' : 'NORMAL'),
              analysis: item.analysis || item.suggestion || '',
              reviewComment: ''
            }));
          } else if (Array.isArray(data.results) && data.results.length > 0) {
            this.examItems = data.results.map(result => ({
              id: result.id || result.itemId,
              name: result.itemName || result.name || '未知项目',
              reference: result.reference || '',
              result: result.value || result.result || '',
              status: result.abnormal === 1 ? 'ABNORMAL' : 'NORMAL',
              analysis: result.suggestion || result.analysis || '',
              reviewComment: ''
            }));
          } else {
            this.examItems = [];
            this.$message.warning('该体检记录暂无体检结果数据');
          }
        }
        
        // 6. 设置结论信息
        this.conclusionForm = {
          findings: data.findings || data.conclusion || '',
          suggestion: data.suggestion || '',
          reviewComment: ''
        };
        
      } catch (error) {
        console.error('获取体检报告失败:', error);
        this.$message.error('获取体检报告失败: ' + (error.message || '未知错误'));
      } finally {
        this.loading = false;
      }
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
    },
    // 只读模式下的打印报告方法
    handlePrint() {
      const recordId = this.$route.params.id || this.$route.query.id
      if (!recordId) {
        this.$message.error('记录ID不存在，无法打印报告');
        return;
      }
      
      this.$confirm('确定要打印该体检报告吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.loading = true;
        // 调用导出报告API
        import('@/api/exam/record').then(({ exportExamReport }) => {
          exportExamReport(recordId).then(response => {
            // 处理文件下载
            this.handleBlobDownload(response, `体检报告_${recordId}.pdf`);
            this.$message.success('报告导出成功');
          }).catch(error => {
            console.error('导出报告失败:', error);
            this.$message.error('导出报告失败: ' + (error.message || '未知错误'));
          }).finally(() => {
            this.loading = false;
          });
        });
      }).catch(() => {
        // 用户取消操作
      });
    },
    // 返回列表方法
    handleBack() {
      this.$router.go(-1); // 返回上一页
    },
    // 处理Blob文件下载
    handleBlobDownload(response, fileName) {
      if (!response) {
        this.$message.error('下载失败：响应为空');
        return;
      }
      
      // 创建Blob链接并下载
      const blob = new Blob([response], { 
        type: response.type || 'application/octet-stream' 
      });
      
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName;
      link.click();
      URL.revokeObjectURL(link.href);
    },
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