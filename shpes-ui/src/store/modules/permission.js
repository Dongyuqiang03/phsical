import { asyncRoutes, constantRoutes } from '@/router'

/**
 * 使用 meta.permissions 判断当前用户是否有权限
 * @param permissions
 * @param route
 */
function hasPermission(permissions, route) {
  if (route.meta && route.meta.permissions) {
    const hasAccess = route.meta.permissions.some(permission => permissions.includes(permission))
    return hasAccess
  } else {
    return true
  }
}

/**
 * 通过递归过滤异步路由表
 * @param routes asyncRoutes
 * @param permissions
 */
export function filterAsyncRoutes(routes, permissions) {
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

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }, { permissions }) {
    return new Promise(resolve => {
      console.log('Generating routes with permissions:', permissions)
      let accessedRoutes = []
      
      if (permissions && permissions.length > 0) {
        accessedRoutes = filterAsyncRoutes(asyncRoutes, permissions)
      }
      
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}