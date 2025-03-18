import { login, logout, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'
import router, { asyncRoutes, constantRoutes, resetRouter } from '@/router'

const state = {
  token: getToken(),
  user: {},
  roles: [],
  permissions: [],
  routes: []
}

const getters = {
  permission_routes: state => {
    // 合并静态路由和动态路由，过滤掉隐藏的路由
    const allRoutes = constantRoutes.concat(state.routes)
    const visibleRoutes = allRoutes.filter(route => !route.hidden)
    console.log('All visible routes:', visibleRoutes)
    return visibleRoutes
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
    state.roles = user.roles || []
    state.permissions = user.permissions || []
  },
  SET_ROUTES: (state, routes) => {
    state.routes = routes
  }
}

// 根据权限过滤路由
function filterAsyncRoutes(routes, permissions) {
  const res = []
  console.log('Filtering routes:', routes)
  console.log('With permissions:', permissions)

  routes.forEach(route => {
    const tmp = { ...route }
    if (hasPermission(permissions, tmp)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, permissions)
      }
      res.push(tmp)
    }
  })

  console.log('Filtered result:', res)
  return res
}

// 检查是否有权限访问该路由
function hasPermission(permissions, route) {
  if (route.meta && route.meta.permissions) {
    // 如果路由需要的权限是数组中的任意一个，就允许访问
    return route.meta.permissions.some(permission => permissions.includes(permission))
  } else {
    return true
  }
}

const actions = {
  // 登录
  login({ commit }, userInfo) {
    console.log('Starting login with data:', userInfo)
    return new Promise((resolve, reject) => {
      login(userInfo)
        .then(response => {
          console.log('Raw login response:', response)
          // 确保响应数据存在
          if (!response || !response.data) {
            reject(new Error('登录响应数据为空'))
            return
          }

          const { data: responseData } = response
          console.log('Login response data:', responseData)

          // 检查响应状态
          if (responseData.code === 200) {
            const { token, user } = responseData.data
            // 检查必要的数据
            if (!token || !user) {
              reject(new Error('登录响应缺少必要的数据'))
              return
            }

            console.log('Setting user data:', { token, user })
            commit('SET_TOKEN', token)
            commit('SET_USER', user)
            setToken(token)
            resolve(response)
          } else {
            reject(new Error(responseData.message || '登录失败'))
          }
        })
        .catch(error => {
          console.error('Login API error:', error)
          reject(error)
        })
    })
  },

  // 生成路由
  generateRoutes({ commit, state }) {
    return new Promise((resolve, reject) => {
      try {
        const { permissions } = state
        console.log('Generating routes with permissions:', permissions)
        
        if (!Array.isArray(permissions)) {
          reject(new Error('权限数据格式错误'))
          return
        }

        let accessedRoutes = []
        if (permissions.length > 0) {
          // 过滤异步路由
          accessedRoutes = filterAsyncRoutes(asyncRoutes, permissions)
          console.log('Generated routes:', accessedRoutes)
          
          // 更新路由
          commit('SET_ROUTES', accessedRoutes)
          
          // 动态添加路由
          accessedRoutes.forEach(route => {
            console.log('Adding route:', route)
            router.addRoute(route)
          })
        } else {
          console.warn('No permissions found, menu might not show up')
        }
        
        // 返回所有路由，包括基础路由
        resolve(constantRoutes.concat(accessedRoutes))
      } catch (error) {
        console.error('Generate routes error:', error)
        reject(error)
      }
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

  // 登出
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      try {
        commit('SET_TOKEN', '')
        commit('SET_USER', {})
        removeToken()
        resetRouter()
        resolve()
      } catch (error) {
        reject(error)
      }
    })
  },

  // 重置 token
  resetToken({ commit }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '')
      commit('SET_USER', {})
      removeToken()
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