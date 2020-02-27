import api from '@/api/gateway'

export default {
  state: {
    processList: [],
    processDetail: {
      info: {},
      subProcessList: [],
    },
    subProcessDetail: {
      info: {},
      jobsList: [],
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
    DELETE_PROCESS(state, processId) {
      state.processList = state.processList.filter(
        process => process.id !== processId,
      )
    },
    SET_SUB_PROCESS_LIST(state, list) {
      state.processDetail = { ...state.processDetail, subProcessList: list }
    },
    SET_SUB_PROCESS_INFO(state, obj) {
      state.subProcessDetail = { ...state.subProcessDetail, info: obj }
    },
    SET_JOBS_LIST(state, list) {
      state.subProcessDetail = { ...state.subProcessDetail, jobsList: list }
    },
  },
  actions: {
    // 공정 조회
    async getProcessList(context, params) {
      const data = await api('PROCESS_LIST', {
        params: {
          sort: 'created_at,desc',
          size: 20,
          ...params,
        },
      })
      context.commit('SET_PROCESS_LIST', data.processes)
      return data
    },
    // 공정 상세조회
    async getProcessInfo(context, processId) {
      const data = await api('PROCESS_DETAIL', {
        query: { processId },
      })
      context.commit('SET_PROCESS_INFO', data)
      return data
    },
    // 공정 생성
    async createProcess(context, params) {
      const data = await api('PROCESS_CREATE', { params })
      return data
    },
    // 공정 편집
    async updateProcess(context, params) {
      const data = await api('PROCESS_UPDATE', {
        query: { processId: params.processId },
        params,
      })
      return data
    },
    // 공정 삭제
    async deleteProcess(context, processId) {
      const data = await api('PROCESS_DELETE', {
        query: { processId },
      })
      context.commit('DELETE_PROCESS', processId)
      return data
    },
    // 공정의 세부공정 리스트
    async getSubProcessList(context, params) {
      const data = await api('SUB_PROCESS_LIST', {
        query: { processId: params.processId },
        params,
      })
      context.commit('SET_SUB_PROCESS_LIST', data.subProcesses)
      return data
    },
    // 세부공정 편집
    async updateSubProcess(context, params) {
      const data = await api('SUB_PROCESS_DETAIL', {
        query: { subProcessId: params.subProcessId },
        params,
      })
      return data
    },
    // 작업 리스트
    async getJobsList(context, params) {
      const data = await api('JOBS_LIST', {
        query: { subProcessId: params.subProcessId },
        params,
      })
      context.commit('SET_JOBS_LIST', data.jobs)
      return data
    },
  },
}
