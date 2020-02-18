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
  actions: {},
}
