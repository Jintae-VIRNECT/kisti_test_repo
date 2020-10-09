import * as types from './mutation-types'

export default {
  // account
  updateAccount({ commit }, account) {
    commit(types.ACCOUNT_SET, account)
  },
  clearAccount({ commit }) {
    commit(types.ACCOUNT_CLEAR)
  },
  initWorkspace({ commit }, infoList) {
    commit(types.INIT_WORKSPACE, infoList)
  },
  changeWorkspace({ commit }, workspace) {
    commit(types.CHANGE_WORKSPACE, workspace)
  },
  clearWorkspace({ commit }) {
    commit(types.CLEAR_WORKSPACE)
  },

  //calendar
  setCalendar({ commit }, calendar) {
    commit(types.CALENDAR_SET, calendar)
  },
}
