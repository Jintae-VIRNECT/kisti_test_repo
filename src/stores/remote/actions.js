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
  setCompanyInfo({ commit }, info) {
    commit(types.SET_COMPANY_INFO, info)
  },
  //calendar
  setCalendar({ commit }, calendar) {
    commit(types.CALENDAR_SET, calendar)
  },

  setSearch({ commit }, payload) {
    if ('keyword' in payload) {
      commit(types.SEARCH.KEYWORD, payload['keyword'])
    }

    if ('status' in payload) {
      commit(types.SEARCH.STATUS, payload['status'])
    }

    if ('date' in payload) {
      commit(types.SEARCH.DATE, payload['date'])
    }

    if ('sort' in payload) {
      commit(types.SEARCH.SORT, payload['sort'])
    }

    if ('useDate' in payload) {
      commit(types.SEARCH.DATE_TOGGLE, payload['useDate'])
    }
  },
}
