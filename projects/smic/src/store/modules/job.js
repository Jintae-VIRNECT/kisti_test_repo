import Vue from 'vue'
import API from '@/models/api'

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
    SET_SMART_TOOL_DETAIL(state, obj) {
      state.smartToolDetail = obj
    },
    SET_SMART_TOOL_LIST(state, list) {
      state.smartToolList = list
    },
  },
  actions: {
    async getReportDetail(context) {},
    async getReportList(context) {},
    async getIssueDetail(context) {},
    async getIssueList(context) {},
    async getSmartToolDetail(context) {},
    async getSmartToolList(context) {},
  },
}
