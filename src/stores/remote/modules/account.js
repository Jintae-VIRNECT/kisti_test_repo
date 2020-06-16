import { ACCOUNT_SET, ACCOUNT_CLEAR } from '../mutation-types'

function getDefaultState() {
  return {
    uuid: null,
    profile: null,
    description: null,
    email: null,
    nickname: null,
    serviceInfo: null,
    userType: null,
    roleType: '', // 'LEADER' / 'EXPERT' / 'WORKER'
  }
}
const state = getDefaultState()

const mutations = {
  [ACCOUNT_SET](state, payload) {
    if (typeof payload === 'object') {
      for (let key in payload) {
        if (key in state && payload[key] != null) {
          state[key] = payload[key]
        }
      }

      return state
    } else {
      return false
    }
  },

  [ACCOUNT_CLEAR](state) {
    Object.assign(state, getDefaultState())

    return true
  },
}

export default {
  state,
  mutations,
}
