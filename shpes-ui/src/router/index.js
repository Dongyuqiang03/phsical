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
        meta: { title: '首页', icon: 'dashboard' }
      }
    ]
  }
]

// 需要根据权限动态加载的路由
export const asyncRoutes = [
  {
    path: '/system',
    component: Layout,
    meta: { title: '系统管理', icon: 'setting', permissions: ['system'] },
    children: [
      {
        path: 'user',
        component: () => import('../views/system/user/index.vue'),
        name: 'User',
        meta: { title: '用户管理', permissions: ['system:user'] }
      },
      {
        path: 'role',
        component: () => import('../views/system/role/index.vue'),
        name: 'Role',
        meta: { title: '角色管理', permissions: ['system:role'] }
      },
      {
        path: 'department',
        component: () => import('../views/system/department/index.vue'),
        name: 'Department',
        meta: { title: '部门管理', permissions: ['system:department'] }
      }
    ]
  },
  {
    path: '/exam',
    component: Layout,
    meta: { title: '体检管理', icon: 'medical', permissions: ['exam'] },
    children: [
      {
        path: 'item',
        component: () => import('../views/exam/item/index.vue'),
        name: 'ExamItem',
        meta: { title: '体检项目管理', permissions: ['exam:item'] }
      },
      {
        path: 'package',
        component: () => import('../views/exam/package/index.vue'),
        name: 'ExamPackage',
        meta: { title: '体检套餐管理', permissions: ['exam:package'] }
      },
      {
        path: 'appointment',
        component: () => import('../views/exam/appointment/index.vue'),
        name: 'ExamAppointment',
        meta: { title: '体检预约管理', permissions: ['exam:appointment'] }
      },
      {
        path: 'result',
        component: () => import('../views/exam/result/index.vue'),
        name: 'ExamRecord',
        meta: { title: '体检记录管理', permissions: ['exam:result'] }
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

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const hasToken = getToken()

  if (to.path === '/login') {
    // 访问登录页，直接放行
    next()
  } else if (hasToken) {
    // 已登录状态
    const hasRoles = store.state.user.roles && store.state.user.roles.length > 0
    if (hasRoles) {
      next()
    } else {
      // 有 token 但没有角色信息，可能是 token 失效
      store.dispatch('user/resetToken').then(() => {
        next('/login')
      })
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
  router.matcher = newRouter.matcher
}

export default router