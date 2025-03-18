import { login, logout, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router, { asyncRoutes, constantRoutes, resetRouter } from '@/router'

const state = {
  token: getToken(),
  user: null,
  roles: [],
  permissions: [],
  routes: []
}

const getters = {
  permission_routes: state => {
    return constantRoutes.concat(state.routes).filter(route => !route.hidden)
  }
}

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USER: (state, user) => {
    state.user = user
    if (user) {
      state.roles = user.roles || []
      state.permissions = user.permissions || []
    } else {
      state.roles = []
      state.permissions = []
    }
  },
  SET_ROUTES: (state, routes) => {
    state.routes = routes
  }
}

// 根据权限过滤路由
function filterAsyncRoutes(routes, permissions) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(permissions, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, permissions)
      }
      res.push(tmp)
    }
  })

  return res
}

// 检查是否有权限访问该路由
function hasPermission(permissions, route) {
  if (route.meta && route.meta.permissions) {
    return permissions.some(permission => route.meta.permissions.includes(permission))
  } else {
    return true
  }
}

const actions = {
  // 用户登录
  login({ commit, dispatch }, loginData) {
    return new Promise((resolve, reject) => {
      login(loginData)
        .then(async response => {
          const { token, user } = response.data
          setToken(token)
          commit('SET_TOKEN', token)
          commit('SET_USER', user)
          // 等待路由生成完成
          await dispatch('generateRoutes')
          resolve(response)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 生成路由
  generateRoutes({ commit, state }) {
    return new Promise(resolve => {
      const { permissions } = state
      // 过滤异步路由
      const accessedRoutes = filterAsyncRoutes(asyncRoutes, permissions)
      // 更新路由
      commit('SET_ROUTES', accessedRoutes)
      // 动态添加路由
      router.addRoutes(accessedRoutes)
      resolve(accessedRoutes)
    })
  },

  // 获取用户信息
  getInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getUserInfo()
        .then(response => {
          const { data } = response
          if (!data) {
            reject('验证失败，请重新登录。')
            return
          }

          commit('SET_USER', data)
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 用户登出
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      logout()
        .then(() => {
          commit('SET_TOKEN', '')
          commit('SET_USER', null)
          commit('SET_ROUTES', [])
          removeToken()
          resetRouter()
          resolve()
          // 登出后跳转到登录页
          window.location.href = '/login'
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 重置token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_USER', null)
      commit('SET_ROUTES', [])
      removeToken()
      resetRouter()
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  getters,
  actions
}