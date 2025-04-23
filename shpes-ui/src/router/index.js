import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '../layout/index.vue'
import { getToken } from '@/utils/auth' // 确保这个工具函数存在
import store from '@/store'

Vue.use(VueRouter)

// 白名单列表，不需要登录就可以访问的路由
const whiteList = ['/login']

// 基础路由，不需要权限
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('../views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('../views/dashboard/index.vue'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 's-home' }
      }
    ]
  }
]

// 需要根据权限动态加载的路由
export const asyncRoutes = [
  {
    path: '/system',
    component: Layout,
    meta: { title: '系统管理', icon: 's-tools', permissions: ['system'] },
    children: [
      {
        path: '/system/user',
        component: () => import('../views/system/user/index.vue'),
        name: 'User',
        meta: { title: '用户管理', icon: 'user', permissions: ['system:user'] }
      },
      {
        path: '/system/role',
        component: () => import('../views/system/role/index.vue'),
        name: 'Role',
        meta: { title: '角色管理', icon: 's-custom', permissions: ['system:role'] }
      },
      {
        path: '/system/department',
        component: () => import('../views/system/department/index.vue'),
        name: 'Department',
        meta: { title: '部门管理', icon: 'office-building', permissions: ['system:department'] }
      }
    ]
  },
  {
    path: '/exam',
    component: Layout,
    meta: { title: '体检管理', icon: 'first-aid-kit', permissions: ['exam'] },
    children: [
      {
        path: 'category',
        component: () => import('../views/exam/category/index.vue'),
        name: 'ExamCategory',
        meta: { title: '体检项目分类管理', icon: 'folder', permissions: ['exam:category'] }
      },
      {
        path: 'item',
        component: () => import('../views/exam/item/index.vue'),
        name: 'ExamItem',
        meta: { title: '体检项目管理', icon: 'document', permissions: ['exam:item'] }
      },
      {
        path: 'package',
        component: () => import('../views/exam/package/index.vue'),
        name: 'ExamPackage',
        meta: { title: '体检套餐管理', icon: 'goods', permissions: ['exam:package'] }
      },
      {
        path: 'timeslot',
        component: () => import('../views/exam/timeslot/index.vue'),
        name: 'ExamTimeSlot',
        meta: { title: '预约时间段管理', icon: 'date', permissions: ['exam:timeslot'] }
      },
      {
        path: 'appointment',
        component: () => import('../views/exam/appointment/index.vue'),
        name: 'ExamAppointment',
        meta: { title: '在线预约', icon: 'date', permissions: ['exam:appointment:online'] }
      },
      {
        path: 'appointment/list',
        component: () => import('../views/exam/appointment/list.vue'),
        name: 'ExamAppointmentList',
        meta: { title: '我的预约', icon: 'notebook-1', permissions: ['exam:appointment:my'] }
      },
      {
        path: 'appointment/management',
        component: () => import('../views/exam/appointment/management.vue'),
        name: 'ExamAppointmentManagement',
        meta: { title: '预约管理', icon: 'tickets', permissions: ['exam:appointment'] }
      },
      {
        path: 'appointment/detail/:id',
        component: () => import('../views/exam/appointment/detail.vue'),
        name: 'ExamAppointmentDetail',
        meta: { title: '预约详情', icon: 'document', permissions: ['exam:appointment'] },
        hidden: true
      },
      {
        path: 'result',
        component: () => import('../views/exam/record/index.vue'),
        name: 'ExamRecord',
        meta: { title: '体检记录管理', icon: 'notebook-2', permissions: ['exam:record'] }
      },
      {
        path: 'result/input',
        component: () => import('../views/exam/result/input.vue'),
        name: 'ExamResultInput',
        meta: { title: '录入体检结果', permissions: ['exam:result'] },
        hidden: true
      },
      {
        path: 'result/review',
        component: () => import('../views/exam/result/review.vue'),
        name: 'ExamResultReview',
        meta: { title: '查看体检结果', permissions: ['exam:result'] },
        hidden: true
      }
    ]
  }
]

const createRouter = () => new VueRouter({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// 动态添加路由的方法
export function addRoutes(routes) {
  // 检查是否已存在相同路由
  const existingRoutes = router.getRoutes()
  routes.forEach(route => {
    // 如果路由已存在，先删除
    if (route.name && existingRoutes.some(existing => existing.name === route.name)) {
      router.removeRoute(route.name)
    }
    router.addRoute(route)
  })
}

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const hasToken = getToken()
  console.log('[Router Guard] Current route:', to.path)
  console.log('[Router Guard] Has token:', hasToken)
  console.log('[Router Guard] Route meta:', to.meta)
  console.log('[Router Guard] Route matched:', to.matched)
  console.log('[Router Guard] Current router routes:', router.getRoutes().map(route => route.path))

  if (to.path === '/login') {
    next()
  } else if (hasToken) {
    const hasRoles = store.state.user.roles && store.state.user.roles.length > 0
    console.log('[Router Guard] Has roles:', hasRoles)
    
    if (hasRoles) {
      console.log('[Router Guard] Roles exist, proceeding...')
      next()
    } else {
      console.log('[Router Guard] No roles found, checking localStorage...')
      // 有 token 但没有角色信息，说明可能是页面刷新
      // 尝试从localStorage获取用户信息
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      
      if (userInfo && userInfo.roles) {
        // 如果本地存储有用户信息，更新到store
        store.commit('user/SET_USER', userInfo)
        console.log('[Router Guard] User info from localStorage:', userInfo)
        store.dispatch('user/generateRoutes').then(() => {
          console.log('[Router Guard] Routes generated successfully')
          // 路由已在 generateRoutes 中添加，这里不需要重复添加
          next({ ...to, replace: true })
        }).catch((error) => {
          console.error('[Router Guard] Failed to generate routes:', error)
          store.dispatch('user/resetToken').then(() => {
            next('/login')
          })
        })
      } else {
        console.log('[Router Guard] No valid userInfo found, redirecting to login...')
        // 如果本地也没有用户信息，重置token并跳转到登录页
        store.dispatch('user/resetToken').then(() => {
          next('/login')
        })
      }
    }
  } else {
    // 未登录状态
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单中，直接进入
      next()
    } else {
      // 其他所有页面重定向到登录页
      next(`/login?redirect=${to.path}`)
    }
  }
})

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // 重置路由器的matcher
}

export default router