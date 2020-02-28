import api from '@/api/gateway'

export default {
  state: {
    reportDetail: {},
    reportList: [],
    issueDetail: {},
    issueList: [],
    smartToolDetail: {},
    smartToolList: [],
  },
  getters: {
    reportDetail(state) {
      return state.reportDetail
    },
    reportList(state) {
      return state.reportList
    },
    issueDetail(state) {
      return state.issueDetail
    },
    issueList(state) {
      return state.issueList
    },
    smartToolDetail(state) {
      return state.smartToolDetail
    },
    smartToolList(state) {
      return state.smartToolList
    },
  },
  mutations: {
    SET_REPORT_DETAIL(state, obj) {
      state.reportDetail = obj
    },
    SET_REPORT_LIST(state, list) {
      state.reportList = list
    },
    SET_ISSUE_DETAIL(state, obj) {
      state.issueDetail = obj
    },
    SET_ISSUE_LIST(state, list) {
      state.issueList = list
    },
    SET_SMART_TOOL_DETAIL(state, id) {
      state.smartToolDetail = state.smartToolList.find(
        smartTool => smartTool.jobId === id,
      )
    },
    SET_SMART_TOOL_LIST(state, list) {
      state.smartToolList = list
    },
  },
  actions: {
    async getReportDetail(context) {},
    async getReportList(context, params = {}) {
      const data = await api('REPORT_LIST', { params })
      context.commit('SET_REPORT_LIST', data.reports)
      return data
    },
    async getIssueDetail(context) {},
    async getIssueList(context, params = {}) {
      const data = await api('ISSUE_LIST', { params })
      context.commit('SET_ISSUE_LIST', data.issues)
      return data
    },
    async getSmartToolList(context, params = {}) {
      const data = await api('SMART_TOOL_LIST', {
        route: { processId: params.processId },
        params,
      })
      context.commit('SET_SMART_TOOL_LIST', data.smartTools)
      return data
    },
  },
}
