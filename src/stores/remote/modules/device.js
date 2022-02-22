import {
  SET_DEVICE,
  SET_IS_MOBILE_SIZE,
  SET_IS_TABLET_SIZE,
  SET_IS_BROWSER_BACKGROUND,
} from '../mutation-types'

const state = {
  type: '', // desktop, tablet, mobile
  isMobileSize: false,
  isTabletSize: false,
  isBrowserBackground: false,
}

const getters = {
  isMobileSize: state => state.isMobileSize,
  isTabletSize: state => state.isTabletSize,
  isBrowserBackground: state => state.isBrowserBackground,
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
  [SET_IS_BROWSER_BACKGROUND](state, isBrowserBackground) {
    state.isBrowserBackground = isBrowserBackground
  },
}

export default {
  state,
  getters,
  mutations,
}
