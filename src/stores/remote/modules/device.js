import { SET_DEVICE } from '../mutation-types'

const state = {
  type: '', // desktop, tablet, mobile
}

const mutations = {
  [SET_DEVICE](state, type) {
    state.type = type
  },
}

export default {
  state,
  mutations,
}
