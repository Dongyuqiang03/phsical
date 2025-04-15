import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import { initPermissionDebug } from '@/utils/permission'

Vue.use(ElementUI)
Vue.prototype.$http = axios

// 添加权限调试初始化
const originalPush = router.push
router.push = function push(location, onResolve, onReject) {
  console.log('[路由跳转]', location);
  return originalPush.call(this, location, onResolve, onReject);
}

// 应用启动后进行权限调试
window.debugPermissions = () => {
  initPermissionDebug(store, router);
}

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')