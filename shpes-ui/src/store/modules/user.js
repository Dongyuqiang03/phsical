import axios from 'axios'

const state = {
  token: localStorage.getItem('token'),
  userInfo: null,
  roles: []
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
    if (token) {
      localStorage.setItem('token', token)
    } else {
      localStorage.removeItem('token')
    }
  },
  SET_USER_INFO: (state, userInfo) => {
    state.userInfo = userInfo
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  }
}

const actions = {
  login({ commit }, loginForm) {
    return new Promise((resolve, reject) => {
      axios.post('/api/auth/login', loginForm)
        .then(response => {
          const { token } = response.data
          commit('SET_TOKEN', token)
          resolve(response)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  getUserInfo({ commit }) {
    return new Promise((resolve, reject) => {
      axios.get('/api/users/info')
        .then(response => {
          const { data } = response
          commit('SET_USER_INFO', data)
          commit('SET_ROLES', data.roles)
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  logout({ commit }) {
    return new Promise((resolve) => {
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', null)
      commit('SET_ROLES', [])
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}