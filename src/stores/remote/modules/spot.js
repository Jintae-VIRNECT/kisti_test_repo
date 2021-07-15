import { SET_IS_SPOT_STAND } from '../mutation-types'

const state = {
  isSpotStand: false,
}

const getters = {
  isSpotStand: state => state.isSpotStand,
}

const mutations = {
  [SET_IS_SPOT_STAND]: (state, isSpotStand) => {
    state.isSpotStand = isSpotStand
  },
}

export default {
  state,
  getters,
  mutations,
}
