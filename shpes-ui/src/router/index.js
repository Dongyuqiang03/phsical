import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout'

Vue.use(VueRouter)

export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 'dashboard' }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

export const asyncRoutes = [
  {
    path: '/system',
    component: Layout,
    meta: { title: '系统管理', icon: 'setting', roles: ['admin'] },
    children: [
      {
        path: 'user',
        component: () => import('@/views/system/user/index'),
        name: 'User',
        meta: { title: '用户管理' }
      },
      {
        path: 'role',
        component: () => import('@/views/system/role/index'),
        name: 'Role',
        meta: { title: '角色管理' }
      },
      {
        path: 'department',
        component: () => import('@/views/system/department/index'),
        name: 'Department',
        meta: { title: '部门管理' }
      }
    ]
  },
  {
    path: '/exam',
    component: Layout,
    meta: { title: '体检管理', icon: 'medical', roles: ['admin', 'doctor', 'nurse'] },
    children: [
      {
        path: 'item',
        component: () => import('@/views/exam/item/index'),
        name: 'ExamItem',
        meta: { title: '体检项目管理' }
      },
      {
        path: 'package',
        component: () => import('@/views/exam/package/index'),
        name: 'ExamPackage',
        meta: { title: '体检套餐管理' }
      },
      {
        path: 'appointment',
        component: () => import('@/views/exam/appointment/index'),
        name: 'ExamAppointment',
        meta: { title: '体检预约管理' }
      },
      {
        path: 'record',
        component: () => import('@/views/exam/record/index'),
        name: 'ExamRecord',
        meta: { title: '体检记录管理' }
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

// Navigation guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else if (!token) {
    next('/login')
  } else {
    next()
  }
})

export default router