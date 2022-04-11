import {
  SET_IS_CONTROL_POP_OVER_VISIBLE,
  SET_CONTROL_ACTIVE,
  SET_IS_REQUEST_MODAL_VISIBLE,
} from '../mutation-types'

const state = {
  isControlPopOverVisible: false,
  controlActive: false,
  isRequestModalVisible: false,
}

const mutations = {
  [SET_IS_CONTROL_POP_OVER_VISIBLE](state, visible) {
    state.isControlPopOverVisible = visible
  },
  [SET_CONTROL_ACTIVE](state, active) {
    state.controlActive = active
  },
  [SET_IS_REQUEST_MODAL_VISIBLE](state, visible) {
    state.isRequestModalVisible = visible
  },
}

const getters = {
  isControlPopOverVisible: state => state.isControlPopOverVisible,
  controlActive: state => state.controlActive,
  isRequestModalVisible: state => state.isRequestModalVisible,
}

export default {
  state,
  getters,
  mutations,
}
