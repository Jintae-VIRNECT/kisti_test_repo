import Vue from 'vue'
import API from '@/models/api'

export default {
  state: {
    reportDetail: {},
    issueDetail: {},
    smartToolDetail: {},
  },
  getters: {
    reportDetail(state) {
      return state.reportDetail
    },
    issueDetail(state) {
      return state.issueDetail
    },
    smartToolDetail(state) {
      return state.smartToolDetail
    },
  },
  mutations: {
    SET_REPORT_DETAIL(state, obj) {
      state.reportDetail = obj
    },
    SET_ISSUE_DETAIL(state, obj) {
      state.issueDetail = obj
    },
    SET_SMART_TOOL_DETAIL(state, obj) {
      state.smartToolDetail = obj
    },
  },
  actions: {},
}
