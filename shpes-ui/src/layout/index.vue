<template>
  <div class="app-wrapper">
    <div class="sidebar-container">
      <el-menu
        :default-active="$route.path"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item index="/appointment">
          <i class="el-icon-date"></i>
          <span slot="title">体检预约</span>
        </el-menu-item>
        <el-menu-item index="/record">
          <i class="el-icon-document"></i>
          <span slot="title">体检记录</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <i class="el-icon-user"></i>
          <span slot="title">个人中心</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="main-container">
      <div class="navbar">
        <div class="right-menu">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              {{ username }}
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <div class="app-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Layout',
  computed: {
    ...mapGetters([
      'username'
    ])
  },
  methods: {
    handleLogout() {
      this.$store.dispatch('user/logout').then(() => {
        this.$router.push('/login')
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  height: 100vh;
  display: flex;

  .sidebar-container {
    width: 210px;
    height: 100%;
    background-color: #304156;

    .el-menu {
      border: none;
      height: 100%;
      width: 100%;
    }
  }

  .main-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .navbar {
      height: 50px;
      border-bottom: 1px solid #dcdfe6;
      display: flex;
      align-items: center;
      padding: 0 20px;

      .right-menu {
        margin-left: auto;

        .el-dropdown-link {
          cursor: pointer;
          color: #409EFF;
        }
      }
    }

    .app-main {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
    }
  }
}
</style>