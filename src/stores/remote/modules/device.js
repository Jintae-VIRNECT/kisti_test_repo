import {
  SET_DEVICE,
  SET_IS_MOBILE_SIZE,
  SET_IS_TABLET_SIZE,
} from '../mutation-types'

const state = {
  type: '', // desktop, tablet, mobile
  isMobileSize: false,
  isTabletSize: false,
}

const getters = {
  isMobileSize: state => state.isMobileSize,
  isTabletSize: state => state.isTabletSize,
}

const mutations = {
  [SET_DEVICE](state, type) {
    state.type = type
  },
  [SET_IS_MOBILE_SIZE](state, isMobileSize) {
    state.isMobileSize = isMobileSize
  },
  [SET_IS_TABLET_SIZE](state, isTabletSize) {
    state.isTabletSize = isTabletSize
  },
}

export default {
  state,
  getters,
  mutations,
}
