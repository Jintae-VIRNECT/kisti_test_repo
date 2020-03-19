import api from '@/api/gateway'

export default {
  state: {
    processList: [],
    processTotal: 0,
    subProcessListAll: [],
    processDetail: {
      info: {},
      subProcessList: [],
    },
    subProcessDetail: {
      info: {},
      jobsList: [],
    },
    processStatistics: {},
    processDailyTotal: [],
  },
  getters: {
    processList(state) {
      return state.processList
    },
    processTotal(state) {
      return state.processTotal
    },
    processDetail(state) {
      return state.processDetail
    },
    subProcessDetail(state) {
      return state.subProcessDetail
    },
    subProcessListAll(state) {
      return state.subProcessListAll
    },
    processStatistics(state) {
      return state.processStatistics
    },
    processDailyTotal(state) {
      return state.processDailyTotal
    },
  },
  mutations: {
    SET_PROCESS_LIST(state, list) {
      state.processList = list
    },
    SET_PROCESS_TOTAL(state, num) {
      state.processTotal = num
    },
    SET_PROCESS_INFO(state, obj) {
      state.processDetail = { ...state.processDetail, info: obj }
    },
    CLOSE_PROCESS(state, { processId, conditions }) {
      const process = state.processList.find(
        process => process.id === processId,
      )
      process.conditions = conditions
      state.processList = [...state.processList]
    },
    DELETE_PROCESS(state, processId) {
      state.processList = state.processList.filter(
        process => process.id !== processId,
      )
    },
    SET_SUB_PROCESS_LIST(state, list) {
      state.processDetail = { ...state.processDetail, subProcessList: list }
    },
    SET_SUB_PROCESS_LIST_ALL(state, list) {
      state.subProcessListAll = list
    },
    SET_SUB_PROCESS_INFO(state, obj) {
      state.subProcessDetail = { ...state.subProcessDetail, info: obj }
    },
    SET_JOBS_LIST(state, list) {
      state.subProcessDetail = { ...state.subProcessDetail, jobsList: list }
    },
    SET_PROCESS_STATISTICS(state, obj) {
      state.processStatistics = obj
    },
    SET_PROCESS_DAILY_TOTAL(state, list) {
      state.processDailyTotal = list
    },
  },
  actions: {
    // 공정 조회
    async getProcessList(context, params) {
      const data = await api('PROCESS_LIST', {
        params: {
          ...params,
        },
      })
      context.commit('SET_PROCESS_LIST', data.processes)
      context.commit('SET_PROCESS_TOTAL', data.pageMeta.totalElements)
      return data
    },
    // 공정 상세조회
    async getProcessInfo(context, processId) {
      const data = await api('PROCESS_DETAIL', {
        route: { processId },
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
        route: { processId: params.processId },
        params,
      })
      return data
    },
    // 공정 종료
    async closeProcess(context, processId) {
      const data = await api('PROCESS_CLOSE', {
        route: { processId },
      })
      context.commit('CLOSE_PROCESS', {
        processId,
        conditions: data.conditions,
      })
      return data
    },
    // 공정 삭제
    async deleteProcess(context, processId) {
      const data = await api('PROCESS_DELETE', {
        route: { processId },
      })
      context.commit('DELETE_PROCESS', processId)
      return data
    },
    // 전체 세부공정 리스트
    async getSubProcessListAll(context, params) {
      const data = await api('SUB_PROCESS_ALL', {
        params,
      })
      context.commit('SET_SUB_PROCESS_LIST_ALL', data.subProcesses)
      return data
    },
    // 공정의 세부공정 리스트
    async getSubProcessList(context, params) {
      const data = await api('SUB_PROCESS_LIST', {
        route: { processId: params.processId },
        params: {
          size: 50,
          sort: 'priority,asc',
          ...params,
        },
      })
      context.commit('SET_SUB_PROCESS_LIST', data.subProcesses)
      return data
    },
    // 세부공정 상세조회
    async getSubProcessDetail(context, subProcessId) {
      const data = await api('SUB_PROCESS_DETAIL', {
        route: { subProcessId },
      })
      context.commit('SET_SUB_PROCESS_INFO', data)
      return data
    },
    // 세부공정 편집
    async updateSubProcess(context, params) {
      const data = await api('SUB_PROCESS_UPDATE', {
        route: { subProcessId: params.subProcessId },
        params,
      })
      return data
    },
    // 작업 리스트
    async getJobsList(context, params) {
      const data = await api('JOBS_LIST', {
        route: { subProcessId: params.subProcessId },
        params: {
          size: 50,
          sort: 'priority,asc',
          ...params,
        },
      })
      context.commit('SET_JOBS_LIST', data.jobs)
      return data
    },
    // 공정 통계
    async getProcessStatistics(context) {
      const data = await api('PROCESS_STATISTICS')
      context.commit('SET_PROCESS_STATISTICS', data)
    },
    // 일자별 공정 진행률
    async getProcessDailyTotal(context, month) {
      const data = await api('PROCESS_DAILY_TOTAL', {
        params: { month },
      })
      context.commit('SET_PROCESS_DAILY_TOTAL', data.dailyTotal)
    },
  },
}
