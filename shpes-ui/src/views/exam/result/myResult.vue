<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="体检日期">
          <el-date-picker
            v-model="queryParams.examDateRange"
            type="daterange"
            align="right"
            unlink-panels
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="套餐名称">
          <el-input v-model="queryParams.packageName" placeholder="请输入套餐名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="未检查" value="0"></el-option>
            <el-option label="部分完成" value="1"></el-option>
            <el-option label="已完成" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="loading"
      :data="recordList"
      style="width: 100%">
      <el-table-column
        prop="examNo"
        label="体检编号"
        width="180">
      </el-table-column>
      <el-table-column
        prop="examDate"
        label="体检日期"
        width="180">
      </el-table-column>
      <el-table-column
        prop="packageName"
        label="体检套餐">
      </el-table-column>
      <el-table-column
        prop="status"
        label="状态"
        width="120">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="conclusion"
        label="体检总结"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        label="操作"
        width="180">
        <template slot-scope="scope">
          <el-button 
            size="mini" 
            type="primary" 
            @click="handleViewDetail(scope.row)"
            :disabled="!canViewDetail(scope.row)">
            查看详情
          </el-button>
          <el-button 
            size="mini" 
            type="success" 
            @click="handleExportPdf(scope.row)"
            :disabled="!canExport(scope.row)">
            导出PDF
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { getUserExamRecords, exportExamReport } from '@/api/exam/record'
import Pagination from '@/components/Pagination'

export default {
  name: 'MyExamResult',
  components: { Pagination },
  data() {
    return {
      loading: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        examDateRange: [],
        packageName: '',
        status: ''
      },
      // 日期选择器配置
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      // 体检记录列表
      recordList: [],
      // 总记录数
      total: 0
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 获取体检记录列表
    async getList() {
      this.loading = true
      try {
        // 处理日期范围
        const params = { ...this.queryParams }
        if (params.examDateRange && params.examDateRange.length === 2) {
          params.beginDate = params.examDateRange[0]
          params.endDate = params.examDateRange[1]
        }
        delete params.examDateRange

        const { data } = await getUserExamRecords(params)
        this.recordList = data.records || []
        this.total = data.total
      } catch (error) {
        console.error('获取体检记录失败:', error)
        this.$message.error('获取体检记录失败')
      } finally {
        this.loading = false
      }
    },
    // 查询按钮点击事件
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置查询条件
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        examDateRange: [],
        packageName: '',
        status: ''
      }
      this.getList()
    },
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        '0': '待体检',
        '1': '进行中(未录入结果)',
        '2': '进行中(已录入结果)',
        '3': '已完成'
      }
      return statusMap[status] || '未知状态'
    },
    // 获取状态标签类型
    getStatusType(status) {
      const statusTypeMap = {
        '0': 'info',
        '1': 'warning',
        '2': 'warning',
        '3': 'success'
      }
      return statusTypeMap[status] || 'info'
    },
    // 判断是否可以查看详情
    canViewDetail(row) {
      // 只有进行中(已录入结果)或已完成的体检才可以查看详情
      return ['2', '3'].includes(row.status)
    },
    // 判断是否可以导出PDF
    canExport(row) {
      // 只有已完成的体检才可以导出PDF
      return row.status === '3'
    },
    // 查看详情按钮点击事件
    handleViewDetail(row) {
      // 跳转到体检详情页面
      this.$router.push({
        path: '/exam/result/review',
        query: { id: row.id }
      })
    },
    // 导出PDF按钮点击事件
    async handleExportPdf(row) {
      try {
        this.loading = true
        await exportExamReport(row.id)
        this.$message.success('体检报告导出成功')
      } catch (error) {
        console.error('导出体检报告失败:', error)
        this.$message.error('导出体检报告失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}
.pagination-container {
  margin-top: 15px;
}
</style> 