<template>
  <div class="sidebar-menu">
    <!-- 带子菜单的菜单项 -->
    <el-submenu v-if="item.children && item.children.length > 0 && !item.hidden" :index="resolvePath(item.path)">
      <template slot="title">
        <i v-if="item.meta && item.meta.icon" :class="'el-icon-' + item.meta.icon"></i>
        <span>{{ item.meta.title }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(item.path)"
        class="nest-menu"
      />
    </el-submenu>
    <!-- 普通菜单项 -->
    <el-menu-item v-else-if="!item.hidden" :index="resolvePath(item.path)" :class="{'submenu-title-noDropdown':!isNest}">
      <i v-if="item.meta && item.meta.icon" :class="'el-icon-' + item.meta.icon"></i>
      <span slot="title">{{ item.meta.title }}</span>
    </el-menu-item>
  </div>
</template>

<script>
import { isExternal } from '@/utils/validate'
import Item from './Item.vue'
import AppLink from './Link.vue'

export default {
  name: 'SidebarItem',
  components: { Item, AppLink },
  props: {
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  data() {
    this.onlyOneChild = null
    return {}
  },
  methods: {
    hasOneShowingChild(children = [], parent) {
      const showingChildren = children.filter(item => {
        if (item.hidden) {
          return false
        } else {
          this.onlyOneChild = item
          return true
        }
      })

      if (showingChildren.length === 1) {
        return true
      }

      if (showingChildren.length === 0) {
        this.onlyOneChild = { ...parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    },
    resolvePath(routePath) {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(this.basePath)) {
        return this.basePath
      }
      return this.basePath.replace(/\/?$/, '/') + routePath.replace(/^\//, '')
    }
  }
}
</script>