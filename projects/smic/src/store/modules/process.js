import Vue from 'vue'
import API from '@/models/api'

// tmp data
import taskGroup from '@/data/taskGroup'

export default {
  state: {
    processList: [],
    processDetail: {
      subProcessList: [],
    },
    subProcessDetail: {
      jobsList: taskGroup.tableData,
    },
  },
  getters: {
    processList(state) {
      return state.processList
    },
    processDetail(state) {
      return state.processDetail
    },
    subProcessDetail(state) {
      return state.subProcessDetail
    },
  },
  mutations: {
    SET_PROCESS_LIST(state, list) {
      state.processList = list
    },
    SET_PROCESS_INFO(state, obj) {
      state.processDetail.info = obj
    },
    SET_SUB_PROCESS_LIST(state, list) {
      state.processDetail.subProcessList = list
    },
    SET_SUB_PROCESS_INFO(state, obj) {
      state.subProcessDetail.info = obj
    },
    SET_JOBS_LIST(state, list) {
      state.subProcessDetail.jobsList = list
    },
  },
  actions: {
    // 공정 조회
    async getProcessList(state, params = {}) {
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
        state.commit('SET_PROCESS_LIST', data.processes)
        return data
      } else throw new Error(message)
    },
    // 공정 생성
    async createProcess(state, form) {
      const response = await Vue.axios.post(API.PROCESS_CREATE(), form)
      const { code, data, message } = response.data
      if (code === 200) return data
      else throw new Error(message)
    },
    // 공정 상세조회
    async getProcessInfo() {},
    // 공정의 세부공정 리스트
    async getSubProcessList(state, params = {}) {
      const response = await Vue.axios.get(
        API.SUB_PROCESS_LIST(params.processId),
        {
          params: {
            sort: params.sort || '',
            size: params.size || 20,
            page: params.page || 0,
          },
        },
      )
      const { code, data, message } = response.data
      if (code === 200) {
        state.commit('SET_SUB_PROCESS_LIST', data.subProcesses)
        return data
      } else throw new Error(message)
    },
    async getSubProcessDetail() {},
    async getJobsList() {},
  },
}
