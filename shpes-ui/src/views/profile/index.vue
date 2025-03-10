<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <div slot="header" class="card-header">
        <span>个人信息</span>
        <el-button
          style="float: right; padding: 3px 0"
          type="text"
          @click="handleEdit"
        >
          {{ isEditing ? '保存' : '编辑' }}
        </el-button>
      </div>
      
      <el-form :model="userInfo" :disabled="!isEditing" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="userInfo.username" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="userInfo.name" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userInfo.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userInfo.email" />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Profile',
  data() {
    return {
      isEditing: false,
      userInfo: {
        username: '',
        name: '',
        phone: '',
        email: ''
      }
    }
  },
  computed: {
    ...mapGetters([
      'username'
    ])
  },
  created() {
    // 初始化用户信息
    this.userInfo.username = this.username
  },
  methods: {
    handleEdit() {
      if (this.isEditing) {
        // 保存用户信息
        this.$message.success('保存成功')
      }
      this.isEditing = !this.isEditing
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  padding: 20px;

  .profile-card {
    max-width: 800px;
    margin: 0 auto;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>