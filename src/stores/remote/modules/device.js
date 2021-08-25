import { SET_DEVICE, SET_IS_MOBILE } from '../mutation-types'

const state = {
  type: '', // desktop, tablet, mobile
  isMobile: false,
}

const getters = {
  isMobile: state => state.isMobile,
}

const mutations = {
  [SET_DEVICE](state, type) {
    state.type = type
  },
  [SET_IS_MOBILE](state, isMobile) {
    state.isMobile = isMobile
  },
}

export default {
  state,
  getters,
  mutations,
}
