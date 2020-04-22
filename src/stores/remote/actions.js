import * as types from './mutation-types'

export default {
  // account
  updateAccount({ commit }, account) {
    commit(types.ACCOUNT_SET, account)
  },
  clearAccount({ commit }) {
    commit(types.ACCOUNT_CLEAR)
  },

  /**
   * Change Call Mode
   * @param {String} state : stream, sharing, ar
   */
  changeCallMode({ commit }, state) {
    commit(types.CALL_MODE_SET, state)
  },
  /**
   * Change Call Action
   * @param {String} state : pointing, drawing
   */
  changeAction({ commit }, state) {
    commit(types.CALL_ACTION_SET, state)
  },

  callStream({ commit }, state) {
    commit(types.CALL_STREAM, state)
  },
  callMic({ commit }, state) {
    commit(types.CALL_MIC, state)
  },
  callSpeaker({ commit }, state) {
    commit(types.CALL_SPEAKER, state)
  },
  muteOnOff({ commit }) {
    commit(types.MUTE_ON_OFF)
  },

  /**
   * Set device type
   * @param {Object} payload
   */
  setDeviceType({ commit }, payload) {
    commit(types.SET_DEVICE, payload)
  },

  /**
   * filter
   * @param {String} payload
   */
  setFilter({ commit }, payload) {
    commit(types.SEARCH_FILTER, payload)
  },

  /**
   * member list
   * @param {Object} payload
   */
  setMemberList({ commit }, payload) {
    commit(types.SET_MEMBER_LIST, payload)
  },
}
