import Vue from 'vue'
import API from '@/models/api'

export default {
  state: {
    lastProcess: null,
    processList: [],
  },
  getters: {
    getLastProcess(state) {
      return state.lastProcess
    },
    getProcessList(state) {
      return state.processList
    },
  },
  mutations: {
    LAST_PROCESS(state, obj) {
      state.lastProcess = obj
    },
    PROCESS_LIST(state, list) {
      state.processList = list
    },
  },
  actions: {
    async PROCESS_LIST(state, params = {}) {
      const response = await Vue.axios.get(API.PROCESS_LIST(), {
        params: {
          search: params.search || '',
          filter: params.filter || '',
          sort: params.sort || '',
          size: params.size || 20,
          page: params.page || 0,
        },
      })
      const { code, data, message } = response.data
      if (code === 200) {
        state.commit('PROCESS_LIST', data.processes)
        return response.data
      } else throw new Error(message)
    },
    async CREATE_PROCESS(state, form) {
      const response = await Vue.axios.post(API.CREATE_PROCESS(), form)
      const { code, message } = response.data
      if (code === 200) return response.data
      else throw new Error(message)
    },
  },
}
