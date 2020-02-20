import Vue from 'vue'
import API from '@/models/api'

export default {
  state: {
    lastProcess: null,
  },
  getters: {
    getLastProcess(state) {
      return state.lastProcess
    },
  },
  mutations: {
    LAST_PROCESS(state, obj) {
      state.lastProcess = obj
    },
  },
  actions: {
    async CREATE_PROCESS(state, form) {
      const response = await Vue.axios.post(API.CREATE_PROCESS(), form)
      const { code, message } = response.data
      if (code === 200) return response.data
      else throw new Error(message)
    },
  },
}
