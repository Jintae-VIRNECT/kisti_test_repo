import { SEARCH } from '../mutation-types'

const state = {
  input: {
    text: '',
    target: ['title', 'memberList[].nickName'],
  },
  status: { status: 'ALL' },
  useDate: { useDate: false },
  date: {
    from: null,
    to: null,
  },
  sort: {
    column: 'ACTIVE_DATE',
    direction: 'DESC',
  },
}

const mutations = {
  [SEARCH.INPUT](state, input) {
    Object.assign(state.input, input)
  },
  [SEARCH.STATUS](state, status) {
    state.status = status
  },
  [SEARCH.DATE](state, date) {
    Object.assign(state.date, date)
  },
  [SEARCH.SORT](state, sort) {
    Object.assign(state.sort, sort)
  },
  [SEARCH.DATE_TOGGLE](state, sort) {
    Object.assign(state.useDate, sort)
  },
}

export default {
  state,
  mutations,
}
