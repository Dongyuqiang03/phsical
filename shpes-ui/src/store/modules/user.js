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
    console.log('[Menu Routes] Final visible routes:', visibleRoutes)
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
    // 从RoleVO对象中提取角色编码
    state.roles = (user.roles || []).map(role => role.roleCode)
    state.permissions = user.permissions || []
  },
  SET_ROUTES: (state, routes) => {
    state.routes = routes
  }
}

// 根据权限过滤路由
function filterAsyncRoutes(routes, permissions) {
  const res = []
  console.log('[Route Filter] Starting route filtering with permissions:', permissions)
  routes.forEach(route => {
    const tmp = { ...route }
    const hasAccess = hasPermission(permissions, tmp)
    console.log('[Route Filter] Permission check:', {
      path: tmp.path,
      hasAccess,
      requiredPermissions: tmp.meta?.permissions,
      component: tmp.component ? 'Component exists' : 'No component'
    })

    if (hasAccess) {
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
    // 如果路由需要的权限是数组中的任意一个，就允许访问
    return route.meta.permissions.some(permission => permissions.includes(permission))
  }
  return true
}

const actions = {
  // 登录
  login({ commit, dispatch }, userInfo) {
    console.log('Starting login with data:', userInfo)
    return new Promise((resolve, reject) => {
      login(userInfo)
        .then(async response => {
          // 确保响应数据存在
          if (!response || !response.data) {
            reject(new Error('登录响应数据为空'))
            return
          }

          const { data: responseData } = response

          // 检查响应状态
          if (response.code === 200) {
            const { token, user } = responseData
            console.log('[Login] Login successful, responseData:', responseData)
            // 检查必要的数据
            if (!token || !user) {
              console.error('Missing required data - token:', !token, 'user:', !user)
              reject(new Error('登录响应缺少必要的数据'))
              return
            }

            try {
              commit('SET_TOKEN', token)
              commit('SET_USER', user)
              setToken(token)
              // 保存用户信息到localStorage
              localStorage.setItem('userInfo', JSON.stringify(user))
              console.log('[Login] User permissions:', state.permissions)

              // 生成路由并添加到router实例
              await dispatch('generateRoutes')
              
              resolve({ response, redirectPath: '/' })
            } catch (error) {
              console.error('Generate routes error:', error)
              reject(error)
            }
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
        if (!Array.isArray(permissions)) {
          console.error('[Route Generation] Permissions is not an array:', permissions)
          reject(new Error('权限数据格式错误'))
          return
        }

        let accessedRoutes = []
        console.log('[Route Generation] Starting route generation with state:', {
          roles: state.roles,
          permissions: permissions,
          asyncRoutesCount: asyncRoutes.length
        })
        
        // 先重置路由，清除所有动态路由
        resetRouter()
        
        if (permissions.length > 0) {
          accessedRoutes = filterAsyncRoutes(asyncRoutes, permissions)
          commit('SET_ROUTES', accessedRoutes)
          console.log('[Route Generation] Routes generated:', accessedRoutes.length)
          
          // 只在这里添加路由到router实例
          accessedRoutes.forEach(route => {
            router.addRoute(route)
          })
          
          console.log('[Route Generation] Routes added to router:', router.getRoutes())
        } else {
          console.warn('[Route Generation] No permissions found, menu might not show up')
        }
        resolve(accessedRoutes)
      } catch (error) {
        console.error('[Route Generation] Generate routes error:', error)
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