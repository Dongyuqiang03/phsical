<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'is-collapse': !sidebar.opened }">
      <div class="logo">
        <img src="@/assets/logo.png" alt="logo">
        <span v-show="sidebar.opened">校医院体检系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="!sidebar.opened"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF">
        <sidebar-item v-for="route in permission_routes" :key="route.path" :item="route" :base-path="route.path"/>
      </el-menu>
    </div>
    
    <!-- 主容器 -->
    <div class="main-container">
      <!-- 头部导航 -->
      <div class="navbar">
        <div class="left">
          <hamburger :is-active="sidebar.opened" @toggle-click="toggleSideBar"/>
          <breadcrumb/>
        </div>
        <div class="right">
          <el-dropdown trigger="click">
            <div class="avatar-wrapper">
              <img :src="avatar" class="user-avatar">
              <i class="el-icon-caret-bottom"/>
            </div>
            <el-dropdown-menu slot="dropdown">
              <router-link to="/profile">
                <el-dropdown-item>个人中心</el-dropdown-item>
              </router-link>
              <el-dropdown-item divided @click.native="logout">
                <span style="display:block;">退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 主要内容区 -->
      <app-main/>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Hamburger from './components/Hamburger/index.vue'
import Breadcrumb from './components/Breadcrumb/index.vue'
import SidebarItem from './components/SidebarItem/index.vue'
import AppMain from './components/AppMain/index.vue'

export default {
  name: 'Layout',
  components: {
    Hamburger,
    Breadcrumb,
    SidebarItem,
    AppMain
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'permission_routes'
    ]),
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      try {
        await this.$store.dispatch('user/logout')
        this.$router.push(`/login?redirect=${this.$route.fullPath}`)
      } catch (error) {
        console.error('退出失败:', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar-container {
  width: 210px;
  height: 100%;
  background: #304156;
  transition: width 0.3s;
  overflow: hidden;

  &.is-collapse {
    width: 54px;
  }

  .logo {
    height: 50px;
    display: flex;
    align-items: center;
    padding: 10px;
    background: #2b2f3a;
    
    img {
      width: 32px;
      height: 32px;
      margin-right: 12px;
    }
    
    span {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      white-space: nowrap;
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f0f2f5;
}

.navbar {
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px;

  .left {
    display: flex;
    align-items: center;
  }

  .right {
    .avatar-wrapper {
      cursor: pointer;
      display: flex;
      align-items: center;

      .user-avatar {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        margin-right: 5px;
      }
    }
  }
}
</style>