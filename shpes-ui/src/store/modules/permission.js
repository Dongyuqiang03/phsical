import { asyncRoutes, constantRoutes } from '@/router'

/**
 * 使用 meta.permissions 判断当前用户是否有权限
 * @param permissions 权限列表（包含权限编码和角色编码）
 * @param route 路由对象
 */
function hasPermission(permissions, route) {
  if (route.meta && route.meta.permissions) {
    // 如果路由需要的权限是数组中的任意一个，就允许访问
    return route.meta.permissions.some(permission => permissions.includes(permission))
  }
  return true
}

/**
 * 通过递归过滤异步路由表
 * @param routes asyncRoutes
 * @param permissions 权限列表（包含权限编码和角色编码）
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