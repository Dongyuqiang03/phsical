import Vue from 'vue'
import Vuex from 'vuex'
import user from './modules/user'
import app from './modules/app'
import exam from './modules/exam'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    user,
    app,
    exam
  },
  state: {
    // Global state
  },
  mutations: {
    // Global mutations
  },
  actions: {
    // Global actions
  },
  getters: {
    // Global getters
  }
})