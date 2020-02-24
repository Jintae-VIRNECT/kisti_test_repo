import Vue from 'vue'
import API from '@/models/api'

import dayjs from '@/plugins/dayjs'

// tmp data
import taskGroup from '@/data/taskGroup'

export default {
  state: {
    processList: [],
    processDetail: {
      info: {},
      subProcessList: [],
    },
    subProcessDetail: {
      info: {},
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
      state.processDetail = { ...state.processDetail, info: obj }
    },
    SET_SUB_PROCESS_LIST(state, list) {
      state.processDetail = { ...state.processDetail, subProcessList: list }
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
    async getProcessList(context, params = {}) {
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
        context.commit('SET_PROCESS_LIST', data.processes)
        return data
      } else throw new Error(message)
    },
    // 공정 생성
    async createProcess(context, form) {
      const response = await Vue.axios.post(API.PROCESS_CREATE(), form)
      const { code, data, message } = response.data
      if (code === 200) return data
      else throw new Error(message)
    },
    // 공정 편집
    async updateProcess(context, form) {
      const response = await Vue.axios.post(
        API.PROCESS_DETAIL(form.processId),
        form,
      )
      const { code, data, message } = response.data
      if (code === 200) return data
      else throw new Error(message)
    },
    // 공정 삭제
    async deleteProcess(context, processId) {
      const response = await Vue.axios.delete(API.PROCESS_DETAIL(processId))
      const { code, data, message } = response.data
      if (code === 200) return data
      else throw new Error(message)
    },
    // 공정의 세부공정 리스트
    async getSubProcessList(context, params = {}) {
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
        context.commit('SET_SUB_PROCESS_LIST', data.subProcesses)
        return data
      } else throw new Error(message)
    },
    // 세부공정 편집
    async updateSubProcess(context, form) {
      const response = await Vue.axios.post(
        API.SUB_PROCESS_DETAIL(form.subProcessId),
        form,
      )
      const { code, data, message } = response.data
      if (code === 200) return data
      else throw new Error(message)
    },
    async getSubProcessDetail() {},
    async getJobsList() {},
  },
}
