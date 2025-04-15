/**
 * 权限工具函数
 */

/**
 * 检查用户是否拥有指定权限
 * @param {Array} userPermissions 用户拥有的权限列表
 * @param {Array|String} requiredPermissions 需要的权限
 * @returns {Boolean} 是否有权限
 */
export function hasPermission(userPermissions, requiredPermissions) {
  if (!requiredPermissions || requiredPermissions.length === 0) {
    return true;
  }

  if (!userPermissions || userPermissions.length === 0) {
    return false;
  }

  // 转换为数组方便处理
  const required = Array.isArray(requiredPermissions) ? requiredPermissions : [requiredPermissions];

  // 检查是否有交集（任一所需权限满足即可）
  return required.some(permission => userPermissions.includes(permission));
}

/**
 * 检查路由访问权限
 * @param {Object} route 路由对象
 * @param {Array} permissions 权限列表
 * @returns {Boolean} 是否有权限访问
 */
export function checkRoutePermission(route, permissions) {
  console.log(`[权限检查] 路由: ${route.path}`, route.meta?.permissions);
  console.log(`[权限检查] 用户权限: ${permissions}`);

  if (route.meta && route.meta.permissions) {
    const hasAccess = hasPermission(permissions, route.meta.permissions);
    console.log(`[权限检查] 访问结果: ${hasAccess ? '允许' : '拒绝'}`);
    return hasAccess;
  }
  
  console.log('[权限检查] 路由无需权限，允许访问');
  return true;
}

/**
 * 调试打印所有路由及其权限要求
 * @param {Array} routes 路由列表
 * @param {String} indent 缩进（用于递归打印）
 */
export function printRoutePermissions(routes, indent = '') {
  routes.forEach(route => {
    console.log(`${indent}路由: ${route.path}, 权限要求: ${route.meta?.permissions || '无'}`);
    if (route.children && route.children.length) {
      printRoutePermissions(route.children, indent + '  ');
    }
  });
}

/**
 * 初始化权限调试
 * @param {Object} store Vuex Store
 * @param {Object} router Vue Router
 */
export function initPermissionDebug(store, router) {
  // 打印当前用户信息和权限
  const permissions = store.state.user?.permissions || [];
  const roles = store.state.user?.roles || [];
  
  console.log('=== 权限调试信息 ===');
  console.log('当前用户权限:', permissions);
  console.log('当前用户角色:', roles);
  
  // 打印所有路由及其权限要求
  console.log('所有路由权限要求:');
  printRoutePermissions(router.options.routes);
  
  // 打印权限验证结果
  console.log('路由访问权限验证结果:');
  router.options.routes.forEach(route => {
    checkRoutePermission(route, permissions);
  });
} 