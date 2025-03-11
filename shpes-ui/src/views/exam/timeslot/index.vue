<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery" class="form-inline">
        <el-form-item>
          <el-date-picker
            v-model="listQuery.date"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 180px;"
          />
        </el-form-item>
        <el-form-item>
          <el-select v-model="listQuery.departmentId" placeholder="选择科室" clearable>
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleFilter">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-container">
      <el-button type="primary" icon="el-icon-plus" @click="handleCreate">新增时间段</el-button>
      <el-button type="success" icon="el-icon-copy-document" @click="handleBatchCreate">批量设置</el-button>
    </div>

    <!-- 表格区域 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      style="width: 100%">
      <el-table-column label="日期" prop="date" width="120" />
      <el-table-column label="时间段" prop="timeSlot" width="150" />
      <el-table-column label="科室" prop="departmentName" width="120" />
      <el-table-column label="容量" width="120">
        <template slot-scope="{row}">
          <el-input-number
            v-model="row.capacity"
            :min="0"
            :max="999"
            size="mini"
            @change="handleCapacityChange(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="已预约" prop="reserved" width="100" align="center" />
      <el-table-column label="剩余" width="100" align="center">
        <template slot-scope="{row}">
          {{ row.capacity - row.reserved }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template slot-scope="{row}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">编辑</el-button>
          <el-button type="danger" size="mini" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页区域 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <!-- 时间段表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="right"
        label-width="100px">
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="temp.date"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-time-picker
            v-model="temp.timeSlot"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="科室" prop="departmentId">
          <el-select v-model="temp.departmentId" placeholder="请选择科室" style="width: 100%;">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number
            v-model="temp.capacity"
            :min="0"
            :max="999"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="temp.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 批量设置对话框 -->
    <el-dialog title="批量设置时间段" :visible.sync="batchVisible" width="600px">
      <el-form
        ref="batchForm"
        :rules="batchRules"
        :model="batchTemp"
        label-position="right"
        label-width="100px">
        <el-form-item label="日期范围" prop="dateRange">
          <el-date-picker
            v-model="batchTemp.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-time-picker
            v-model="batchTemp.timeSlot"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="HH:mm:ss"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="科室" prop="departmentId">
          <el-select v-model="batchTemp.departmentId" placeholder="请选择科室" style="width: 100%;">
            <el-option
              v-for="item in departmentOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number
            v-model="batchTemp.capacity"
            :min="0"
            :max="999"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="batchTemp.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatchForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import { getTimeSlotList, createTimeSlot, updateTimeSlot, deleteTimeSlot, batchCreateTimeSlot, updateTimeSlotCapacity } from '@/api/exam/timeslot'
import { getAllDepartments } from '@/api/department'

export default {
  name: 'TimeSlot',
  components: { Pagination },
  data() {
    return {
      list: [],
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 10,
        date: undefined,
        departmentId: undefined
      },
      departmentOptions: [],
      dialogVisible: false,
      dialogTitle: '',
      batchVisible: false,
      temp: {
        id: undefined,
        date: '',
        timeSlot: [],
        departmentId: undefined,
        capacity: 0,
        status: 1
      },
      batchTemp: {
        dateRange: [],
        timeSlot: [],
        departmentId: undefined,
        capacity: 0,
        status: 1
      },
      rules: {
        date: [
          { required: true, message: '请选择日期', trigger: 'change' }
        ],
        timeSlot: [
          { required: true, message: '请选择时间段', trigger: 'change' }
        ],
        departmentId: [
          { required: true, message: '请选择科室', trigger: 'change' }
        ],
        capacity: [
          { required: true, message: '请输入容量', trigger: 'blur' }
        ]
      },
      batchRules: {
        dateRange: [
          { required: true, message: '请选择日期范围', trigger: 'change' }
        ],
        timeSlot: [
          { required: true, message: '请选择时间段', trigger: 'change' }
        ],
        departmentId: [
          { required: true, message: '请选择科室', trigger: 'change' }
        ],
        capacity: [
          { required: true, message: '请输入容量', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getDepartmentOptions()
  },
  methods: {
    async getList() {
      this.listLoading = true
      try {
        const { data } = await getTimeSlotList(this.listQuery)
        this.list = data.items
        this.total = data.total
      } catch (error) {
        console.error('获取时间段列表失败:', error)
      }
      this.listLoading = false
    },
    async getDepartmentOptions() {
      try {
        const { data } = await getAllDepartments()
        this.departmentOptions = data
      } catch (error) {
        console.error('获取部门列表失败:', error)
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetQuery() {
      this.listQuery = {
        page: 1,
        limit: 10,
        date: undefined,
        departmentId: undefined
      }
      this.getList()
    },
    handleCreate() {
      this.dialogTitle = '新增时间段'
      this.temp = {
        id: undefined,
        date: '',
        timeSlot: [],
        departmentId: undefined,
        capacity: 0,
        status: 1
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.dialogTitle = '编辑时间段'
      this.temp = Object.assign({}, row)
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    async handleDelete(row) {
      try {
        await this.$confirm('是否确认删除该时间段?', '提示', {
          type: 'warning'
        })
        await deleteTimeSlot(row.id)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        console.error('删除时间段失败:', error)
      }
    },
    submitForm() {
      this.$refs['dataForm'].validate(async (valid) => {
        if (valid) {
          try {
            if (this.temp.id) {
              await updateTimeSlot(this.temp)
            } else {
              await createTimeSlot(this.temp)
            }
            this.dialogVisible = false
            this.$message.success('保存成功')
            this.getList()
          } catch (error) {
            console.error('保存时间段失败:', error)
          }
        }
      })
    },
    handleBatchCreate() {
      this.batchTemp = {
        dateRange: [],
        timeSlot: [],
        departmentId: undefined,
        capacity: 0,
        status: 1
      }
      this.batchVisible = true
      this.$nextTick(() => {
        this.$refs['batchForm'].clearValidate()
      })
    },
    submitBatchForm() {
      this.$refs['batchForm'].validate(async (valid) => {
        if (valid) {
          try {
            await batchCreateTimeSlot(this.batchTemp)
            this.batchVisible = false
            this.$message.success('批量设置成功')
            this.getList()
          } catch (error) {
            console.error('批量设置时间段失败:', error)
          }
        }
      })
    },
    async handleCapacityChange(row) {
      try {
        await updateTimeSlotCapacity(row.id, row.capacity)
        this.$message.success('容量更新成功')
      } catch (error) {
        console.error('更新容量失败:', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
  }

  .action-container {
    margin-bottom: 20px;
  }
}
</style> 