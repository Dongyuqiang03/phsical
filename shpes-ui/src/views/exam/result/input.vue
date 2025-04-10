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
import { getExamReport, getPackageItems, submitExamResults, submitConclusion } from '@/api/exam/result'

export default {
  name: 'ResultInput',
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
        examNo: ''
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
        
        // 获取体检报告信息
        const { data } = await getExamReport(numericId);
        
        if (!data) {
          this.$message.error('未找到体检记录');
          return;
        }
        
        // 调试日志：查看后端返回的数据结构和字段
        console.log('体检报告原始数据:', data);
        
        // 设置体检基本信息，与后端字段保持一致
        this.examInfo = {
          id: data.id || numericId,
          examNo: data.examNo || '',            // 体检编号
          name: data.userName || '',            // 用户姓名
          gender: data.gender || '',            // 性别
          age: data.age || '',                  // 年龄
          phone: data.phone || '',              // 联系电话
          appointmentId: data.appointmentId || 0, // 预约ID
          appointmentNo: data.appointmentNo || '', // 预约编号
          packageId: data.packageId || 0,       // 套餐ID
          packageName: data.packageName || '未知套餐' // 套餐名称
        };
        
        console.log('处理后的体检信息:', this.examInfo);
        
        // 处理体检项目
        if (Array.isArray(data.examItems) && data.examItems.length > 0) {
          this.examItems = data.examItems;
        } else {
          // API未返回体检项目，尝试获取套餐标准项目
          if (this.examInfo.packageId) {
            try {
              const { data: packageItems } = await getPackageItems(this.examInfo.packageId);
              
              if (Array.isArray(packageItems) && packageItems.length > 0) {
                // 转换标准项目为可用格式，使用referenceValue作为参考范围
                this.examItems = packageItems.map(item => ({
                  id: item.id,
                  name: item.name,
                  reference: item.referenceValue || '暂无参考值', // 使用referenceValue字段
                  result: '',
                  status: 'NORMAL',
                  analysis: ''
                }));
              } else {
                this.useDefaultExamItems();
              }
            } catch (error) {
              console.error('获取套餐标准体检项目失败:', error);
              this.useDefaultExamItems();
            }
          } else {
            this.useDefaultExamItems();
          }
        }
        
        // 加载已有的检查结果
        if (Array.isArray(data.results) && data.results.length > 0) {
          // 将已有结果合并到检查项目中
          data.results.forEach(result => {
            const item = this.examItems.find(item => item.id === result.itemId);
            if (item) {
              item.result = result.result || '';
              // 确保状态值使用统一的格式
              item.status = result.status === 'normal' ? 'NORMAL' : 
                           (result.status === 'abnormal' ? 'ABNORMAL' : result.status || 'NORMAL');
              item.analysis = result.analysis || '';
            }
          });
        }
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
        // 确保ID是有效的数字
        const recordId = this.$route.query.id;
        const numericId = Number(recordId);
        if (isNaN(numericId)) {
          this.$message.error('记录ID不是有效的数字，无法保存结果。');
          console.error('无效的记录ID:', recordId, typeof recordId);
          return;
        }
        
        // 过滤出已填写结果的项目
        const filledItems = this.examItems.filter(item => item.result);
        
        if (filledItems.length === 0) {
          this.$message.warning('请至少填写一项检查结果');
          return;
        }
        
        // 构造提交数据 - 移除physical字段，因为界面上已经没有体格检查录入项
        const data = {
          id: numericId, // 使用数字ID
          appointmentId: this.examInfo.appointmentId,
          items: filledItems.map(item => {
            // 确保itemId是正确的格式（可能是数字ID或字符串ID，取决于后端接口）
            const itemId = item.id;
            return {
              itemId: itemId,
              itemName: item.name, // 添加项目名称，可能在服务端需要
              result: item.result,
              status: item.status,
              analysis: item.analysis || ''
            }
          })
        }
        
        console.log('正在保存体检结果，数据:', data);
        await submitExamResults(data)
        this.$message.success('检查结果保存成功')
        
        // 保存成功后可以刷新一下数据
        this.getExamInfo();
      } catch (error) {
        console.error('保存检查结果失败:', error)
        this.$message.error('保存失败: ' + (error.message || '未知错误'))
      }
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
        
        console.log('正在提交体检结论，使用ID:', numericId);
        await submitConclusion(data)
        this.$message.success('体检结论提交成功')
        
        // 跳转到报告页面，检查正确的路由路径
        const reportPath = `/exam/report/detail`
        this.$router.push({
          path: reportPath,
          query: { id: numericId }
        })
      } catch (error) {
        console.error('提交体检结论失败:', error)
        this.$message.error('提交失败: ' + (error.message || '未知错误'))
      }
    },
    // 使用默认体检项目
    useDefaultExamItems() {
      this.examItems = [
        {
          id: 'blood_pressure',
          name: '血压',
          reference: '90/60-140/90mmHg',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'heart_rate',
          name: '心率',
          reference: '60-100次/分',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'respiratory_rate',
          name: '呼吸频率',
          reference: '12-20次/分',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'temperature',
          name: '体温',
          reference: '36.3-37.2℃',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'blood_routine',
          name: '血常规',
          reference: '红细胞(RBC): 4.0-5.5×10^12/L; 血红蛋白(HGB): 120-160g/L; 白细胞(WBC): 4.0-10.0×10^9/L; 血小板(PLT): 100-300×10^9/L',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'urine_routine',
          name: '尿常规',
          reference: '颜色: 淡黄色或黄色透明; 尿蛋白: 阴性; 尿糖: 阴性; 尿胆原: 阴性; pH值: 5.0-8.0; 尿比重: 1.003-1.030',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'liver_function',
          name: '肝功能',
          reference: '谷丙转氨酶(ALT): 0-40U/L; 谷草转氨酶(AST): 0-40U/L; 总胆红素(TBIL): 5.1-17.1μmol/L; 直接胆红素(DBIL): 0-6.8μmol/L',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'kidney_function',
          name: '肾功能',
          reference: '尿素氮(BUN): 2.86-7.14mmol/L; 肌酐(Cr): 44-133μmol/L; 尿酸(UA): 149-416μmol/L',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'blood_lipids',
          name: '血脂',
          reference: '总胆固醇(TC): 3.1-5.7mmol/L; 甘油三酯(TG): 0.4-1.7mmol/L; 高密度脂蛋白(HDL-C): 0.9-1.8mmol/L; 低密度脂蛋白(LDL-C): 1.9-3.1mmol/L',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'blood_glucose',
          name: '血糖',
          reference: '空腹血糖: 3.9-6.1mmol/L; 餐后2小时血糖: <7.8mmol/L',
          result: '',
          status: 'NORMAL',
          analysis: ''
        },
        {
          id: 'thyroid_function',
          name: '甲状腺功能',
          reference: 'TSH: 0.27-4.2mIU/L; FT3: 3.1-6.8pmol/L; FT4: 12-22pmol/L',
          result: '',
          status: 'NORMAL',
          analysis: ''
        }
      ];
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