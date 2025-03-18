<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>体检记录管理</span>
      </div>
      <el-table
        v-loading="loading"
        :data="recordList"
        border
        style="width: 100%">
        <el-table-column
          prop="id"
          label="ID"
          width="80">
        </el-table-column>
        <el-table-column
          prop="patientName"
          label="患者姓名"
          width="120">
        </el-table-column>
        <el-table-column
          prop="packageName"
          label="体检套餐"
          width="150">
        </el-table-column>
        <el-table-column
          prop="examDate"
          label="体检日期"
          width="120">
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '已完成' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleInput(scope.row)">录入结果</el-button>
            <el-button
              size="mini"
              type="success"
              @click="handleReview(scope.row)">查看结果</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ExamRecord',
  data() {
    return {
      loading: false,
      recordList: [
        {
          id: 1,
          patientName: '张三',
          packageName: '基础体检套餐',
          examDate: '2023-03-10',
          status: '已完成'
        },
        {
          id: 2,
          patientName: '李四',
          packageName: '全面体检套餐',
          examDate: '2023-03-11',
          status: '进行中'
        }
      ],
      currentPage: 1,
      pageSize: 10,
      total: 2
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.loading = true
      // 模拟API请求
      setTimeout(() => {
        this.loading = false
      }, 500)
    },
    handleInput(row) {
      this.$router.push({ path: '/exam/result/input', query: { id: row.id } })
    },
    handleReview(row) {
      this.$router.push({ path: '/exam/result/review', query: { id: row.id } })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    }
  }
}
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 