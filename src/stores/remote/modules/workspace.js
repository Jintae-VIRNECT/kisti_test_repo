import { SEARCH_SORT, SEARCH_FILTER, SET_MEMBER_LIST } from '../mutation-types'

const state = {
  search: {
    sort: '',
    filter: '',
  },
  beforeRoute: null,
  memberList: [],
}

const mutations = {
  [SEARCH_SORT](state, payload) {
    state.search.sort = payload
  },
  [SEARCH_FILTER](state, payload) {
    state.search.filter = payload
  },
  [SET_MEMBER_LIST](state, payload) {
    state.memberList = payload
  },
}

export default {
  state,
  mutations,
}
