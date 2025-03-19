<template>
  <div class="sidebar-container">
    <div class="logo-container">
      <img src="@/assets/logo.png" class="sidebar-logo">
      <h1 class="sidebar-title">体检管理系统</h1>
    </div>
    <div class="sidebar-menu">
    <el-menu-item v-if="!item.hidden" :index="resolvePath(item.path)" :class="{'submenu-title-noDropdown':!isNest}">
      <i v-if="item.meta && item.meta.icon" :class="'el-icon-' + item.meta.icon"></i>
      <span slot="title">{{ item.meta.title }}</span>
    </el-menu-item>
    <el-submenu v-else-if="!item.hidden && item.children && item.children.length > 0" :index="resolvePath(item.path)">
      <template slot="title">
        <i v-if="item.meta && item.meta.icon" :class="'el-icon-' + item.meta.icon"></i>
        <span>{{ item.meta.title }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(child.path)"
        class="nest-menu"
      />
    </el-submenu>
  </div>
  </div>
</template>

<script>
import path from 'path'

export default {
  name: 'SidebarItem',
  props: {
    item: {
      type: Object,
      required: true
    },
    basePath: {
      type: String,
      default: ''
    },
    isNest: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    resolvePath(routePath) {
      if (this.isExternalLink(routePath)) {
        return routePath
      }
      return path.resolve(this.basePath, routePath)
    },
    isExternalLink(path) {
      return /^(https?:|mailto:|tel:)/.test(path)
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebar-container {
  height: 100%;
  background-color: #304156;
}

.logo-container {
  height: 60px;
  padding: 10px;
  display: flex;
  align-items: center;
  background-color: #2b2f3a;

  .sidebar-logo {
    width: 32px;
    height: 32px;
    margin-right: 12px;
  }

  .sidebar-title {
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    margin: 0;
    white-space: nowrap;
  }
}

.sidebar-menu {
  .el-menu-item, .el-submenu__title {
    height: 50px;
    line-height: 50px;
  }
  
  .el-menu-item i, .el-submenu__title i {
    margin-right: 12px;
    width: 16px;
    text-align: center;
  }
}

.nest-menu .el-menu-item {
  min-width: 180px !important;
  background-color: #1f2d3d !important;
  
  &:hover {
    background-color: #001528 !important;
  }
}
</style>