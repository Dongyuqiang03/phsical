<template>
  <div class="exam-result-scroll-wrapper">
    <div class="app-container">
      <div class="result-container" v-loading="loading">
        <!-- 基本信息 -->
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>{{ pageTitle }}</span>
            <!-- 编辑模式下显示保存按钮 -->
            <el-button-group style="float: right" v-if="!isReadonlyMode">
              <el-button
                type="default"
                size="small"
                @click="handleBack">
                返回列表
              </el-button>
              <el-button
                type="primary"
                size="small"
                :disabled="!canSubmit"
                @click="handleSubmitResults">
                {{ isEditMode ? '保存体检信息' : '提交体检信息' }}
              </el-button>
            </el-button-group>
            <!-- 只读模式下显示返回按钮 -->
            <el-button-group style="float: right" v-else>
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
                <!-- 只读模式下显示纯文本 -->
                <span v-if="isReadonlyMode" :class="{'abnormal-text': row.status !== 'NORMAL'}">
                  {{ row.result || '未填写' }}
                </span>
                <!-- 编辑模式下显示输入框 -->
                <el-input
                  v-else
                  v-model="row.result"
                  :class="{'abnormal-input': row.status !== 'NORMAL'}"
                  :placeholder="row.inputTip || '请输入检查结果'"
                  @change="handleResultChange(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="结果分析" min-width="150">
              <template slot-scope="{row}">
                <!-- 只读模式下显示状态标签和分析文本 -->
                <template v-if="isReadonlyMode">
                  <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
                    {{ row.status === 'NORMAL' ? '正常' : '异常' }}
                  </el-tag>
                  <div v-if="row.status === 'ABNORMAL'" class="analysis-text">
                    {{ row.analysis || '无异常分析' }}
                  </div>
                </template>
                <!-- 编辑模式下显示下拉选择和输入框 -->
                <template v-else>
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
              <!-- 只读模式下显示纯文本 -->
              <div v-if="isReadonlyMode" class="conclusion-text">
                {{ conclusionForm.mainFindings || '暂无主要发现' }}
              </div>
              <!-- 编辑模式下显示输入框 -->
              <el-input
                v-else
                v-model="conclusionForm.mainFindings"
                type="textarea"
                :rows="3"
                placeholder="请输入体检主要发现"
              />
            </el-form-item>
            <el-form-item label="体检结论">
              <!-- 只读模式下显示纯文本 -->
              <div v-if="isReadonlyMode" class="conclusion-text">
                {{ conclusionForm.conclusion || '暂无体检结论' }}
              </div>
              <!-- 编辑模式下显示输入框 -->
              <el-input
                v-else
                v-model="conclusionForm.conclusion"
                type="textarea"
                :rows="3"
                placeholder="请输入体检结论"
              />
            </el-form-item>
            <el-form-item label="医生建议">
              <!-- 只读模式下显示纯文本 -->
              <div v-if="isReadonlyMode" class="conclusion-text">
                {{ conclusionForm.suggestion || '暂无医生建议' }}
              </div>
              <!-- 编辑模式下显示输入框 -->
              <el-input
                v-else
                v-model="conclusionForm.suggestion"
                type="textarea"
                :rows="3"
                placeholder="请输入医生建议"
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
import { getExamReport, getPackageItems, submitExamResults, updateExamResults, submitConclusion, getResultsByRecordId } from '@/api/exam/result'
import { getRecordDetail } from '@/api/exam/record'

export default {
  name: 'ResultInput',
  data() {
    return {
      loading: false,
      mode: 'new', // 默认为新建模式，可选值：new(新建), edit(编辑), readonly(只读)
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
      // 只读模式下不允许提交
      if (this.isReadonlyMode) return false
      // 编辑模式下只要有任意改动就允许提交
      if (this.isEditMode) {
        return this.examItems.some(item => item.result) || 
               this.conclusionForm.mainFindings || 
               this.conclusionForm.conclusion || 
               this.conclusionForm.suggestion
      }
      // 新建模式下需要同时检查结果和结论是否已填写
      return this.examItems.some(item => item.result) && 
             this.conclusionForm.mainFindings && 
             this.conclusionForm.conclusion && 
             this.conclusionForm.suggestion
    },
    canSubmitConclusion() {
      // 只读模式下不允许提交
      if (this.isReadonlyMode) return false
      return this.conclusionForm.mainFindings && this.conclusionForm.conclusion && this.conclusionForm.suggestion
    },
    // 编辑模式的计算属性
    isEditMode() {
      return this.mode === 'edit'
    },
    // 只读模式的计算属性
    isReadonlyMode() {
      return this.mode === 'readonly'
    },
    // 页面标题计算属性
    pageTitle() {
      if (this.isReadonlyMode) {
        return '查看体检结果';
      } else if (this.isEditMode || (this.examInfo && this.examInfo.status === 2)) {
        return '编辑体检结果';
      }
      // 默认显示"录入体检结果"
      return '录入体检结果';
    }
  },
  created() {
    this.initMode()
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
    // 初始化页面模式
    initMode() {
      // 获取URL参数中的mode值
      const mode = this.$route.query.mode
      if (mode === 'edit') {
        this.mode = 'edit'
        console.log('当前为编辑模式')
      } else if (mode === 'readonly') {
        this.mode = 'readonly'
        console.log('当前为只读模式')
      } else {
        this.mode = 'new'
        console.log('当前为新建模式')
      }
    },
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
      try {
        // 获取和校验ID
        const recordId = this.$route.query.id;
        this.loading = true;

        // 转换ID为数字并验证
        const numericId = Number(recordId);
        if (isNaN(numericId)) {
          this.$message.error('记录ID不是有效的数字');
          return;
        }

        // 获取体检记录详情
        const { data } = await getRecordDetail(numericId);

        if (!data) {
          this.$message.error('未找到体检记录');
          return;
        }

        // 设置体检基本信息
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

        // 如果是新建模式，先获取套餐项目列表
        if (this.mode === 'new' && this.examInfo.packageId) {
          try {
            const packageItemsResponse = await getPackageItems(this.examInfo.packageId);
            if (packageItemsResponse.data && Array.isArray(packageItemsResponse.data)) {
              this.examItems = packageItemsResponse.data.map(item => ({
                id: item.id,
                itemName: item.name || '',
                referenceValue: item.referenceValue || '暂无参考值',
                result: '',
                status: 'NORMAL',
                analysis: ''
              }));
            }
          } catch (error) {
            console.error('获取体检套餐项目失败:', error);
            this.$message.error('获取体检套餐项目失败: ' + (error.message || '未知错误'));
          }
        } else if (Array.isArray(data.results)) {
          // 编辑或只读模式下，使用已有的结果数据
          this.examItems = data.results.map(item => ({
            id: item.itemId,
            itemName: item.itemName || '',
            referenceValue: item.referenceValue || '暂无参考值',
            result: item.value || '',
            status: item.abnormal === 1 ? 'ABNORMAL' : 'NORMAL',
            analysis: item.analysis || ''
          }));
        }

        // 设置体检结论
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
        message: '确认提交体检信息吗？',
        type: 'conclusion'
      }
    },
    async confirmSubmit() {
      await this.saveResults()
      this.submitDialog.visible = false
    },
    async saveResults() {
      try {
        const recordId = this.$route.query.id;
        const numericId = Number(recordId);
        if (isNaN(numericId)) {
          this.$message.error('记录ID不是有效的数字，无法保存结果。');
          console.error('无效的记录ID:', recordId, typeof recordId);
          return;
        }

        const filledItems = this.examItems.filter(item => item.result);

        if (filledItems.length === 0) {
          this.$message.warning('请至少填写一项检查结果');
          return;
        }

        if (this.isEditMode) {
          await this.updateResults(numericId, filledItems);
        } else {
          await this.createResults(filledItems);
        }

        this.getExamInfo();
      } catch (error) {
        console.error(`${this.isEditMode ? '更新' : '保存'}检查结果失败:`, error);
        this.$message.error(`${this.isEditMode ? '更新' : '保存'}失败: ` + (error.message || '未知错误'));
      }
    },

    async createResults(filledItems) {
      const data = {
        appointmentId: this.examInfo.appointmentId,
        items: filledItems.map(item => ({
          itemId: Number(item.id),
          itemName: item.name,
          result: item.result,
          status: item.status === 'ABNORMAL' ? 'ABNORMAL' : 'NORMAL',
          analysis: item.analysis || ''
        })),
        conclusion: this.conclusionForm.conclusion || '',
        mainFindings: this.conclusionForm.mainFindings || '',
        suggestion: this.conclusionForm.suggestion || ''
      };

      console.log('正在保存体检结果，数据:', data);
      await submitExamResults(data);
      this.$message.success('检查结果保存成功');
    },

    async updateResults(numericId, filledItems) {
      const data = {
        id: numericId,
        appointmentId: this.examInfo.appointmentId,
        items: filledItems.map(item => ({
          id: Number(item.id),
          result: item.result,
          status: item.status,
          analysis: item.analysis || ''
        })),
        conclusion: this.conclusionForm.conclusion || '',
        mainFindings: this.conclusionForm.mainFindings || '',
        suggestion: this.conclusionForm.suggestion || ''
      };

      console.log('正在更新体检结果，数据:', data);
      await updateExamResults(data);
      this.$message.success('检查结果更新成功');
    },
    async saveConclusion() {
      try {
        // 确保ID是有效的数字
        const recordId = this.$route.query.id;
        const numericId = Number(recordId);
        if (isNaN(numericId)) {
          this.$message.error('记录ID不是有效的数字，无法提交结论。');
          console.error('无效的记录ID:', recordId, typeof recordId);
          return;
        }

        const data = {
          id: numericId, // 使用数字类型ID
          ...this.conclusionForm
        }

        console.log(`正在${this.isEditMode ? '更新' : '提交'}体检结论，使用ID:`, numericId);
        await submitConclusion(data)
        this.$message.success(`体检结论${this.isEditMode ? '更新' : '提交'}成功`)

        // 根据当前模式决定后续操作
        if (this.isEditMode) {
          // 编辑模式下，结论更新成功后可能返回上一页或刷新页面
          this.$confirm('结论更新成功，是否返回体检记录管理页面?', '提示', {
            confirmButtonText: '返回列表',
            cancelButtonText: '继续编辑',
            type: 'success'
          }).then(() => {
            // 返回体检记录管理页面
            this.$router.push('/exam/result');
          }).catch(() => {
            // 继续留在当前页面编辑
          });
        } else {
          // 新建模式下，跳转到报告页面
          const reportPath = `/exam/report/detail`
          this.$router.push({
            path: reportPath,
            query: {id: numericId}
          })
        }
      } catch (error) {
        console.error(`${this.isEditMode ? '更新' : '提交'}体检结论失败:`, error)
        this.$message.error(`${this.isEditMode ? '更新' : '提交'}失败: ` + (error.message || '未知错误'))
      }
    },
  
    formatGender(gender) {
      // 根据实际需求实现性别格式化逻辑
      if (gender === 1) return '男';
      if (gender === 2) return '女';
      if (gender === '1') return '男';
      if (gender === '2') return '女';
      if (gender === 'male' || gender === 'MALE') return '男';
      if (gender === 'female' || gender === 'FEMALE') return '女';
      return gender || '未知';
    },
    // 返回列表方法
    handleBack() {
      this.$router.push('/exam/result')
    },
    // 打印报告方法
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
        // 调用导出报告API
        import('@/api/exam/record').then(({exportExamReport}) => {
          exportExamReport(recordId).then(response => {
            // 处理文件下载
            this.handleBlobDownload(response, `体检报告_${this.examInfo.examNo || recordId}.pdf`);
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

.conclusion-text {
  line-height: 1.6;
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
  min-height: 60px;
}

.analysis-text {
  margin-top: 5px;
  color: #F56C6C;
  font-size: 12px;
  line-height: 1.4;
}
.abnormal-text {
  color: #F56C6C;
  font-weight: bold;
}

.abnormal-input {
  color: #F56C6C;
}
</style>