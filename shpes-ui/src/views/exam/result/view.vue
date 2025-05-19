<template>
  <div class="exam-result-scroll-wrapper">
    <div class="app-container">
      <div class="result-container" v-loading="loading">
        <!-- 基本信息 -->
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>查看体检结果</span>
            <el-button-group style="float: right">
              <el-button
                type="default"
                size="small"
                @click="handleBack">
                返回列表
              </el-button>
              <el-button
                type="primary"
                size="small"
                @click="handlePrint">
                打印报告
              </el-button>
            </el-button-group>
          </div>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="体检编号">{{ examInfo.examNo || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="预约编号">{{ examInfo.appointmentNo || '暂无' }}</el-descriptions-item>
            <el-descriptions-item label="体检人">{{ examInfo.name || '未知' }}</el-descriptions-item>
            <el-descriptions-item label="性别">{{ formatGender(examInfo.gender) }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ examInfo.age ? examInfo.age + '岁' : '未知' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ examInfo.phone || '未知' }}</el-descriptions-item>
            <el-descriptions-item label="体检套餐">{{ examInfo.packageName || '未知套餐' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 检查结果 -->
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>检查结果</span>
          </div>
          <el-table :data="examItems" border>
            <el-table-column label="项目名称" prop="itemName" min-width="150" />
            <el-table-column label="参考范围" prop="referenceValue" min-width="150" show-overflow-tooltip />
            <el-table-column label="检查结果" min-width="200">
              <template slot-scope="{row}">
                <span :class="{'abnormal-text': row.status !== 'NORMAL'}">
                  {{ row.result || '未填写' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="结果分析" min-width="150">
              <template slot-scope="{row}">
                <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
                  {{ row.status === 'NORMAL' ? '正常' : '异常' }}
                </el-tag>
                <div v-if="row.status === 'ABNORMAL'" class="analysis-text">
                  {{ row.analysis || '无异常分析' }}
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 体检结论 -->
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>体检结论</span>
          </div>
          <el-form label-width="100px">
            <el-form-item label="主要发现">
              <div class="conclusion-text">
                {{ conclusionForm.mainFindings || '暂无主要发现' }}
              </div>
            </el-form-item>
            <el-form-item label="体检结论">
              <div class="conclusion-text">
                {{ conclusionForm.conclusion || '暂无体检结论' }}
              </div>
            </el-form-item>
            <el-form-item label="医生建议">
              <div class="conclusion-text">
                {{ conclusionForm.suggestion || '暂无医生建议' }}
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import { getRecordDetail } from '@/api/exam/record'

export default {
  name: 'ResultView',
  data() {
    return {
      loading: false,
      examInfo: {
        appointmentId: '',
        appointmentNo: '',
        name: '',
        gender: '',
        age: '',
        phone: '',
        packageName: '',
        packageId: '',
        examDate: '',
        examNo: '',
        status: ''
      },
      examItems: [],
      conclusionForm: {
        mainFindings: '',
        conclusion: '',
        suggestion: ''
      }
    }
  },
  created() {
    this.getExamInfo()
  },
  mounted() {
    this.fixScrollIssue()
    setTimeout(() => {
      this.fixScrollIssue()
    }, 1000)
  },
  updated() {
    this.fixScrollIssue()
  },
  beforeDestroy() {
    document.getElementById('app-main-scroll-fix')?.remove()
  },
  methods: {
    fixScrollIssue() {
      let styleEl = document.getElementById('app-main-scroll-fix')
      if (!styleEl) {
        styleEl = document.createElement('style')
        styleEl.id = 'app-main-scroll-fix'
        document.head.appendChild(styleEl)
      }
      styleEl.textContent = `
        .app-main, .main-container, .exam-result-scroll-wrapper {
          overflow: auto !important;
          height: auto !important;
        }
        .app-main {
          min-height: calc(100vh - 50px) !important;
        }
        .exam-result-scroll-wrapper {
          min-height: calc(100vh - 90px) !important;
          padding-bottom: 100px !important;
        }
        body, html {
          overflow: auto !important;
        }
      `
    },
    async getExamInfo() {
      try {
        const recordId = this.$route.query.id;
        this.loading = true;

        const numericId = Number(recordId);
        const { data } = await getRecordDetail(numericId);

        if (!data) {
          this.$message.error('未找到体检记录');
          return;
        }

        this.examInfo = {
          id: data.id || numericId,
          examNo: data.examNo || '',
          name: data.userName || '',
          gender: data.gender || '',
          age: data.age || '',
          phone: data.phone || '',
          appointmentId: data.appointmentId || 0,
          appointmentNo: data.appointmentNo || '',
          packageId: data.packageId || 0,
          packageName: data.packageName || '未知套餐'
        };

        if (Array.isArray(data.results)) {
          this.examItems = data.results.map(item => ({
            resultId: item.id,
            itemName: item.itemName || '',
            referenceValue: item.referenceValue || '暂无参考值',
            result: item.value || '',
            status: item.abnormal === 1 ? 'ABNORMAL' : 'NORMAL',
            analysis: item.analysis || ''
          }));
        }

        this.conclusionForm = {
          mainFindings: data.mainFindings || '',
          conclusion: data.conclusion || '',
          suggestion: data.suggestion || ''
        };

      } catch (error) {
        console.error('获取体检信息失败:', error);
        this.$message.error('获取体检信息失败: ' + (error.message || '未知错误'));
      } finally {
        this.loading = false;
      }
    },
    formatGender(gender) {
      if (gender === 1) return '男';
      if (gender === 2) return '女';
      if (gender === '1') return '男';
      if (gender === '2') return '女';
      if (gender === 'male' || gender === 'MALE') return '男';
      if (gender === 'female' || gender === 'FEMALE') return '女';
      return gender || '未知';
    },
    handleBack() {
      this.$router.back()
    },
    handlePrint() {
      const recordId = this.$route.query.id;
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
        import('@/api/exam/record').then(({exportExamReport}) => {
          exportExamReport(recordId).then(response => {
            this.handleBlobDownload(response, `体检报告_${this.examInfo.examNo || recordId}.pdf`);
            this.$message.success('报告导出成功');
          }).catch(error => {
            console.error('导出报告失败:', error);
            this.$message.error('导出报告失败: ' + (error.message || '未知错误'));
          }).finally(() => {
            this.loading = false;
          });
        });
      }).catch(() => {});
    },
    handleBlobDownload(response, fileName) {
      if (!response) {
        this.$message.error('下载失败：响应为空');
        return;
      }

      const blob = new Blob([response], {
        type: response.type || 'application/octet-stream'
      });

      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = fileName;
      link.click();
      URL.revokeObjectURL(link.href);
    }
  }
}
</script>

<style lang="scss">
body .app-main {
  overflow: auto !important;
  height: auto !important;
}

body .main-container {
  overflow: auto !important;
}

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

.conclusion-text {
  line-height: 1.6;
  white-space: pre-wrap;
}

.abnormal-text {
  color: #f56c6c;
}

.analysis-text {
  margin-top: 5px;
  color: #f56c6c;
  font-size: 12px;
}
</style>