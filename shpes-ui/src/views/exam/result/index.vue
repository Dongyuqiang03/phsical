<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>体检记录管理</span>
      </div>
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" class="form-inline">
          <el-form-item label="用户">
            <el-input
              v-model="queryParams.userName"
              placeholder="请输入用户名"
              clearable
              style="width: 200px"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 130px">
              <el-option
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="体检日期">
            <el-date-picker
              v-model="queryParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="yyyy-MM-dd"
              style="width: 240px"
            />
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
        border
        style="width: 100%">
        <el-table-column
          prop="examNo"
          label="体检编号"
          width="180">
          <template slot-scope="scope">
            {{ scope.row.examNo || '未指定' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="userName"
          label="用户姓名"
          width="120">
          <template slot-scope="scope">
            {{ scope.row.userName || '未知用户' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="packageName"
          label="体检套餐"
          width="150">
          <template slot-scope="scope">
            {{ scope.row.packageName || '未指定套餐' }}
          </template>
        </el-table-column>
        <el-table-column
          label="体检日期"
          width="120">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="250">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleInput(scope.row)"
              :disabled="scope.row.status === 2">录入结果</el-button>
            <el-button
              size="mini"
              type="success"
              @click="handleReview(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.pageNum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getRecordPage } from '@/api/exam/record'

export default {
  name: 'ExamRecord',
  data() {
    return {
      loading: false,
      recordList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        status: undefined,
        dateRange: []
      },
      statusOptions: [
        { label: '待体检', value: 0 },
        { label: '进行中', value: 1 },
        { label: '已完成', value: 2 }
      ]
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      let params = { ...this.queryParams }
      
      // 处理日期范围
      if (params.dateRange && params.dateRange.length === 2) {
        params.startDate = params.dateRange[0]
        params.endDate = params.dateRange[1]
      }
      delete params.dateRange
      
      getRecordPage(params).then(response => {
        if (response.data && response.code === 200) {
          // 处理响应数据，补充缺失的信息
          const records = response.data.list || response.data.records || [];
          
          // 数据补全处理
          this.recordList = records.map(record => {
            // 确保每条记录都有id字段
            if (!record.id && record.examNo) {
              console.warn(`记录缺少id字段，使用examNo: ${record.examNo}，但请确保后端API返回正确的id字段`);
            }
            
            return {
              ...record,
              // 处理可能为空的字段
              userName: record.userName || '未知用户',
              packageName: record.packageName || '未指定套餐',
              examNo: record.examNo || '未指定'
            };
          });
          
          this.total = response.data.total || 0
        } else {
          this.$message.error(response.message || '获取体检记录失败')
          this.recordList = []
          this.total = 0
        }
      }).catch(error => {
        console.error('获取体检记录失败:', error)
        this.$message.error('获取体检记录失败: ' + (error.message || '服务器错误'))
        this.recordList = []
        this.total = 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.fetchData()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        status: undefined,
        dateRange: []
      }
      this.fetchData()
    },
    handleInput(row) {
      // 必须使用数字类型的主键ID进行API调用，而不是examNo
      if (!row.id) {
        this.$message.error('该记录缺少ID字段，无法进行结果录入。请联系管理员检查数据完整性。');
        return;
      }
      
      // 检查ID是否为有效数字
      const numericId = Number(row.id);
      if (isNaN(numericId)) {
        this.$message.error('记录ID不是有效的数字，无法进行结果录入。');
        console.error('无效的记录ID:', row.id, typeof row.id);
        return;
      }
      
      console.log('跳转到结果录入页面，使用ID:', numericId);
      this.$router.push({ 
        path: '/exam/result/input', 
        query: { id: numericId } 
      });
    },
    handleReview(row) {
      // 必须使用数字类型的主键ID进行API调用，而不是examNo
      if (!row.id) {
        this.$message.error('该记录缺少ID字段，无法查看详情。请联系管理员检查数据完整性。');
        return;
      }
      
      // 检查ID是否为有效数字
      const numericId = Number(row.id);
      if (isNaN(numericId)) {
        this.$message.error('记录ID不是有效的数字，无法查看详情。');
        console.error('无效的记录ID:', row.id, typeof row.id);
        return;
      }
      
      console.log('跳转到详情页面，使用ID:', numericId);
      this.$router.push({ 
        path: '/exam/result/review', 
        query: { id: numericId } 
      });
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.fetchData()
    },
    formatDate(date) {
      if (!date) return ''
      
      if (typeof date === 'string') {
        return date.split('T')[0]
      }
      
      if (Array.isArray(date) && date.length >= 3) {
        // 处理后端返回的日期数组格式 [year, month, day, hour, minute, second]
        const year = date[0]
        const month = String(date[1]).padStart(2, '0')
        const day = String(date[2]).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      
      return date
    },
    getStatusType(status) {
      const statusMap = {
        0: 'info',
        1: 'warning',
        2: 'success'
      }
      return statusMap[status] || 'info'
    },
    getStatusLabel(status) {
      const statusMap = {
        0: '待体检',
        1: '进行中',
        2: '已完成'
      }
      return statusMap[status] || '未知'
    }
  }
}
</script>

<style scoped>
.filter-container {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 