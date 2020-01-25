import issue from '@/data/issue'
import currentReportedDetailProcess from '@/data/currentReportedDetailProcess'
import currentReportedProcess from '@/data/currentReportedProcess'
import currentUploadedContent from '@/data/currentUploadedContent'
import sceneGroup from '@/data/sceneGroup'

export default {
  state: {
    issue,
    currentReportedDetailProcess,
    currentReportedProcess,
    currentUploadedContent,
    sceneGroup,
  },
  getters: {
    issue(state) {
      return state.issue
    },
    currentReportedDetailProcess(state) {
      return state.currentReportedDetailProcess
    },
    currentReportedProcess(state) {
      return state.currentReportedProcess
    },
    currentUploadedContent(state) {
      return state.currentUploadedContent
    },
    sceneGroup(state) {
      return state.sceneGroup
    },
  },
  mutations: {
    set_issue(state, data) {
      state.issue = data
    },
    set_currentReportedDetailProcess(state, data) {
      state.currentReportedDetailProcess = data
    },
    set_currentReportedProcess(state, data) {
      state.currentReportedProcess = data
    },
    set_currentUploadedContent(state, data) {
      state.currentUploadedContent = data
    },
    set_sceneGroup(state, data) {
      state.set_sceneGroup = data
    },
  },
}
