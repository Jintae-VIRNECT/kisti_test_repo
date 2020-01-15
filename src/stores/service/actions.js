import * as types from './mutation-types'

export default {
  // account
  updateAccount({ commit }, account) {
    commit(types.ACCOUNT_SET, account);
  },
  clearAccount({ commit }) {
    commit(types.ACCOUNT_CLEAR);
  },

  /**
   * Change Call Mode
   * @param {String} state : stream, sharing, ar
   */
  changeCallMode({ commit }, state) {
    commit(types.CALL_MODE_SET, state);
  },
  /**
   * Change Call Action
   * @param {String} state : pointing, drawing
   */
  changeAction({ commit }, state) {
    commit(types.CALL_ACTION_SET, state);
  },
}
