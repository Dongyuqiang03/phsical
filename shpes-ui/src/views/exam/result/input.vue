<template>
  <div class="exam-result-scroll-wrapper">
    <div class="app-container">
      <div class="result-container" v-loading="loading">
        <!-- 基本信息 -->
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>体检信息</span>
            <el-button-group style="float: right">
              <el-button
                type="primary"
                size="small"
                :disabled="!canSubmit"
                @click="handleSubmitResults">
                保存结果
              </el-button>
              <el-button
                type="success"
                size="small"
                :disabled="!canSubmitConclusion"
                @click="handleSubmitConclusion">
                提交结论
              </el-button>
            </el-button-group>
          </div>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="预约编号">{{ examInfo.appointmentId }}</el-descriptions-item>
            <el-descriptions-item label="体检人">{{ examInfo.userName }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ examInfo.gender }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ examInfo.age }}岁</el-descriptions-item>
            <el-descriptions-item label="体检套餐">{{ examInfo.packageName }}</el-descriptions-item>
            <el-descriptions-item label="体检日期">{{ examInfo.examDate }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 体格检查 -->
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>体格检查</span>
          </div>
          <el-form :model="physicalForm" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="身高(cm)">
                  <el-input-number
                    v-model="physicalForm.height"
                    :min="0"
                    :max="300"
                    :precision="1"
                    @change="calculateBMI"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="体重(kg)">
                  <el-input-number
                    v-model="physicalForm.weight"
                    :min="0"
                    :max="500"
                    :precision="1"
                    @change="calculateBMI"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="BMI">
                  <el-input v-model="physicalForm.bmi" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="血压(mmHg)">
                  <el-input v-model="physicalForm.bloodPressure" placeholder="如：120/80" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>

        <!-- 检查结果 -->
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>检查结果</span>
          </div>
          <el-table :data="examItems" border>
            <el-table-column label="项目名称" prop="name" min-width="150" />
            <el-table-column label="参考范围" prop="reference" min-width="150" show-overflow-tooltip />
            <el-table-column label="检查结果" min-width="200">
              <template slot-scope="{row}">
                <el-input
                  v-model="row.result"
                  :placeholder="row.inputTip || '请输入检查结果'"
                  @change="handleResultChange(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="结果分析" min-width="150">
              <template slot-scope="{row}">
                <el-select v-model="row.status" style="width: 100px">
                  <el-option label="正常" value="NORMAL" />
                  <el-option label="异常" value="ABNORMAL" />
                </el-select>
                <el-input
                  v-if="row.status === 'ABNORMAL'"
                  v-model="row.analysis"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入异常分析"
                  style="margin-top: 5px"
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
              <el-input
                v-model="conclusionForm.findings"
                type="textarea"
                :rows="3"
                placeholder="请输入体检主要发现"
              />
            </el-form-item>
            <el-form-item label="结论建议">
              <el-input
                v-model="conclusionForm.suggestion"
                type="textarea"
                :rows="3"
                placeholder="请输入结论建议"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- 提交确认对话框 -->
      <el-dialog
        title="提交确认"
        :visible.sync="submitDialog.visible"
        width="400px">
        <div class="dialog-content">
          <p>{{ submitDialog.message }}</p>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="submitDialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="confirmSubmit">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getExamReport, submitExamResults, submitConclusion } from '@/api/exam/report'

export default {
  name: 'ResultInput',
  data() {
    return {
      loading: false,
      examInfo: {
        appointmentId: '',
        userName: '',
        gender: '',
        age: '',
        packageName: '',
        examDate: ''
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
        suggestion: ''
      },
      submitDialog: {
        visible: false,
        message: '',
        type: '' // 'results' or 'conclusion'
      }
    }
  },
  computed: {
    canSubmit() {
      return this.examItems.some(item => item.result)
    },
    canSubmitConclusion() {
      return this.conclusionForm.findings && this.conclusionForm.suggestion
    }
  },
  created() {
    this.getExamInfo()
  },
  // 在组件被挂载后，修复滚动问题
  mounted() {
    this.fixScrollIssue()
    // 页面加载后延迟一会儿再次修复滚动，以防布局渲染完成后再次被限制
    setTimeout(() => {
      this.fixScrollIssue()
    }, 1000)
  },
  // 在组件更新后，也要确保滚动问题被修复
  updated() {
    this.fixScrollIssue()
  },
  // 组件销毁前还原滚动设置，避免影响其他页面
  beforeDestroy() {
    // 移除内联样式
    document.getElementById('app-main-scroll-fix')?.remove()
  },
  methods: {
    // 修复滚动问题的方法
    fixScrollIssue() {
      // 1. 直接给app-main添加样式覆盖
      let styleEl = document.getElementById('app-main-scroll-fix')
      if (!styleEl) {
        styleEl = document.createElement('style')
        styleEl.id = 'app-main-scroll-fix'
        document.head.appendChild(styleEl)
      }
      styleEl.textContent = `
        .app-main {
          overflow: auto !important;
          height: auto !important;
          min-height: calc(100vh - 50px) !important;
        }
        .main-container {
          overflow: auto !important;
        }
        .exam-result-scroll-wrapper {
          overflow-y: auto !important;
          height: auto !important;
          min-height: calc(100vh - 90px) !important;
          padding-bottom: 100px !important;
        }
      `

      // 2. 直接修改DOM元素的样式
      const appMain = document.querySelector('.app-main')
      if (appMain) {
        appMain.style.overflow = 'auto'
        appMain.style.height = 'auto'
      }

      const mainContainer = document.querySelector('.main-container')
      if (mainContainer) {
        mainContainer.style.overflow = 'auto'
      }

      // 3. 防止页面滚动锁定
      document.body.style.overflow = 'auto'
      document.documentElement.style.overflow = 'auto'
    },
    
    async getExamInfo() {
      this.loading = true
      try {
        // 获取路由参数中的记录ID
        const recordId = this.$route.query.id
        if (!recordId) {
          this.$message.error('未能获取到记录ID，请返回列表重试')
          this.$router.push('/exam/result')
          return
        }
        
        try {
          // 直接使用数字ID进行查询
          const { data } = await getExamReport(recordId)
          this.examInfo = {
            appointmentId: data.appointmentId,
            userName: data.userName,
            gender: data.gender,
            age: data.age,
            packageName: data.packageName,
            examDate: data.examDate
          }
          this.examItems = data.examItems.map(item => ({
            ...item,
            result: '',
            status: 'NORMAL',
            analysis: ''
          }))
          // 如果已有结果，加载已有数据
          if (data.results) {
            this.loadExistingResults(data.results)
          }
        } catch (apiError) {
          console.error('API请求失败:', apiError)
          
          if (apiError.response && apiError.response.status === 400 && 
              apiError.response.data && apiError.response.data.message && 
              apiError.response.data.message.includes('NumberFormatException')) {
            // 处理数字格式错误，可能是使用了examNo而不是ID
            this.$message.error('系统错误：请求的ID格式不正确。请使用正确的数字ID而不是体检编号。')
          } else {
            this.$message.error('获取体检信息失败: ' + (apiError.message || '未知错误'))
          }
          
          // 无论哪种错误，都返回列表页
          setTimeout(() => {
            this.$router.push('/exam/result')
          }, 1500)
          return
        }
      } catch (error) {
        console.error('获取体检信息失败:', error)
        this.$message.error('获取体检信息失败: ' + (error.message || '未知错误'))
      }
      this.loading = false
      
      // 数据加载完成后再次检查滚动
      this.$nextTick(this.fixScrollIssue)
    },
    loadExistingResults(results) {
      this.physicalForm = {
        height: results.height,
        weight: results.weight,
        bmi: results.bmi,
        bloodPressure: results.bloodPressure
      }
      this.examItems = this.examItems.map(item => {
        const result = results.items.find(r => r.itemId === item.id)
        if (result) {
          return {
            ...item,
            result: result.result,
            status: result.status,
            analysis: result.analysis
          }
        }
        return item
      })
      if (results.conclusion) {
        this.conclusionForm = {
          findings: results.conclusion.findings,
          suggestion: results.conclusion.suggestion
        }
      }
    },
    calculateBMI() {
      if (this.physicalForm.height && this.physicalForm.weight) {
        const heightInMeters = this.physicalForm.height / 100
        this.physicalForm.bmi = (this.physicalForm.weight / (heightInMeters * heightInMeters)).toFixed(1)
      }
    },
    handleResultChange(row) {
      // 可以在这里添加结果验证逻辑
      if (row.reference && row.result) {
        // 简单的范围判断示例
        const isNormal = this.checkResultInRange(row.result, row.reference)
        row.status = isNormal ? 'NORMAL' : 'ABNORMAL'
      }
    },
    checkResultInRange(result, reference) {
      // 根据实际需求实现范围判断逻辑
      return true
    },
    handleSubmitResults() {
      this.submitDialog = {
        visible: true,
        message: '确认保存当前录入的检查结果吗？',
        type: 'results'
      }
    },
    handleSubmitConclusion() {
      this.submitDialog = {
        visible: true,
        message: '确认提交体检结论吗？提交后将生成正式体检报告。',
        type: 'conclusion'
      }
    },
    async confirmSubmit() {
      if (this.submitDialog.type === 'results') {
        await this.saveResults()
      } else {
        await this.saveConclusion()
      }
      this.submitDialog.visible = false
    },
    async saveResults() {
      try {
        const data = {
          appointmentId: this.examInfo.appointmentId,
          physical: this.physicalForm,
          items: this.examItems.map(item => ({
            itemId: item.id,
            result: item.result,
            status: item.status,
            analysis: item.analysis
          }))
        }
        await submitExamResults(data)
        this.$message.success('检查结果保存成功')
      } catch (error) {
        console.error('保存检查结果失败:', error)
        this.$message.error('保存失败: ' + (error.message || '未知错误'))
      }
    },
    async saveConclusion() {
      try {
        const data = {
          id: this.$route.query.id, // 使用query中的id，确保它是数字类型ID
          ...this.conclusionForm
        }
        await submitConclusion(data)
        this.$message.success('体检结论提交成功')
        
        // 跳转到报告页面，检查正确的路由路径
        const reportPath = `/exam/report/detail`
        this.$router.push({
          path: reportPath,
          query: { id: this.$route.query.id }
        })
      } catch (error) {
        console.error('提交体检结论失败:', error)
        this.$message.error('提交失败: ' + (error.message || '未知错误'))
      }
    }
  }
}
</script>

<style lang="scss">
/* 全局样式修复 */
body .app-main {
  overflow: auto !important;
  height: auto !important;
}

body .main-container {
  overflow: auto !important;
}

/* 修复滚动问题，优先级最高 */
.exam-result-scroll-wrapper {
  overflow-y: auto !important;
  height: auto !important;
  min-height: calc(100vh - 90px) !important;
  padding-bottom: 100px !important;
}
</style>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  width: 100%;

  .result-container {
    .box-card {
      margin-bottom: 20px;
    }
  }
}

.dialog-content {
  padding: 20px 0;
  text-align: center;
}
</style> 