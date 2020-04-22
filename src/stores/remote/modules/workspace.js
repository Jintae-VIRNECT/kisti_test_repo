
import {
  SEARCH_SORT,
  SEARCH_FILTER,
  SET_HISTORY_LIST,
  DELETE_ALL_HISTORY_LIST,
  DELETE_SINGLE_HISTORY_LIST,
} from '../mutation-types'

import { SEARCH_SORT, SEARCH_FILTER, SET_MEMBER_LIST } from '../mutation-types'


const state = {
  search: {
    sort: '',
    filter: '',
  },
  beforeRoute: null,

  historyList: [],

  memberList: [],

}

const mutations = {
  [SEARCH_SORT](state, payload) {
    state.search.sort = payload
  },
  [SEARCH_FILTER](state, payload) {
    state.search.filter = payload
  },

  [SET_HISTORY_LIST](state, payload) {
    state.historyList = payload
  },
  [DELETE_SINGLE_HISTORY_LIST](state, payload) {
    const roomId = payload
    console.log(roomId)
    const pos = state.historyList.findIndex(room => {
      return room.roomId === roomId
    })
    console.log(pos)

    state.historyList.splice(pos, 1)
  },
  [DELETE_ALL_HISTORY_LIST](state) {
    state.historyList = []

  [SET_MEMBER_LIST](state, payload) {
    state.memberList = payload

  },
}

export default {
  state,
  mutations,
}
