import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '../layout/index.vue'

Vue.use(VueRouter)

// 基础路由，不需要权限
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('../views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
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

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router