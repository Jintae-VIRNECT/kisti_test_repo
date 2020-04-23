import { SEARCH_SORT, SEARCH_FILTER } from '../mutation-types'

const state = {
  search: {
    sort: '',
    filter: '',
  },
  beforeRoute: null,
}

const mutations = {
  [SEARCH_SORT](state, payload) {
    state.search.sort = payload
  },
  [SEARCH_FILTER](state, payload) {
    state.search.filter = payload
  },
}

export default {
  state,
  mutations,
}
