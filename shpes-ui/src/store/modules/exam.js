import axios from 'axios'

const state = {
  appointments: [],
  examRecords: [],
  currentRecord: null,
  packages: [],
  timeSlots: [],
  loading: false
}

const mutations = {
  SET_APPOINTMENTS: (state, appointments) => {
    state.appointments = appointments
  },
  SET_EXAM_RECORDS: (state, records) => {
    state.examRecords = records
  },
  SET_CURRENT_RECORD: (state, record) => {
    state.currentRecord = record
  },
  SET_PACKAGES: (state, packages) => {
    state.packages = packages
  },
  SET_TIME_SLOTS: (state, slots) => {
    state.timeSlots = slots
  },
  SET_LOADING: (state, status) => {
    state.loading = status
  }
}

const actions = {
  fetchAppointments({ commit }) {
    commit('SET_LOADING', true)
    return axios.get('/api/appointments')
      .then(response => {
        commit('SET_APPOINTMENTS', response.data)
        return response.data
      })
      .finally(() => {
        commit('SET_LOADING', false)
      })
  },

  createAppointment({ commit }, appointmentData) {
    commit('SET_LOADING', true)
    return axios.post('/api/appointments', appointmentData)
      .then(response => {
        return response.data
      })
      .finally(() => {
        commit('SET_LOADING', false)
      })
  },

  fetchExamRecords({ commit }) {
    commit('SET_LOADING', true)
    return axios.get('/api/records')
      .then(response => {
        commit('SET_EXAM_RECORDS', response.data)
        return response.data
      })
      .finally(() => {
        commit('SET_LOADING', false)
      })
  },

  fetchPackages({ commit }) {
    return axios.get('/api/packages')
      .then(response => {
        commit('SET_PACKAGES', response.data)
        return response.data
      })
  },

  fetchTimeSlots({ commit }, date) {
    return axios.get(`/api/timeslots/available?date=${date}`)
      .then(response => {
        commit('SET_TIME_SLOTS', response.data)
        return response.data
      })
  },

  updateExamRecord({ commit }, { recordId, data }) {
    commit('SET_LOADING', true)
    return axios.put(`/api/records/${recordId}`, data)
      .then(response => {
        commit('SET_CURRENT_RECORD', response.data)
        return response.data
      })
      .finally(() => {
        commit('SET_LOADING', false)
      })
  }
}

const getters = {
  appointmentCount: state => state.appointments.length,
  completedExams: state => state.examRecords.filter(record => record.status === 'completed'),
  pendingExams: state => state.examRecords.filter(record => record.status === 'pending'),
  isLoading: state => state.loading
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}