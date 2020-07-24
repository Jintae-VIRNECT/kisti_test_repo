import { ROOM_SET, ROOM_CLEAR } from '../mutation-types'

function getDefaultRoomInfo() {
  return {
    roomId: null,
    sessionId: null,
    title: null,
    description: null,
    participantsCount: null,
    path: null,
    leaderId: null,
    maxParticipantCount: 0,
  }
}
const state = getDefaultRoomInfo()

const mutations = {
  [ROOM_SET](state, payload) {
    state.roomId = payload.roomId
    state.sessionId = payload.sessionId
    state.title = payload.title
    state.description = payload.description
    state.participantsCount = payload.participantsCount
    state.path = payload.path
    state.leaderId = payload.leaderId
    state.maxParticipantCount = payload.maxParticipantCount
  },

  [ROOM_CLEAR](state) {
    Object.assign(state, getDefaultRoomInfo())

    return true
  },
}

const actions = {
  /**
   * set room info
   * @param {Object} payload // room info
   */
  setRoomInfo({ commit }, payload) {
    commit(ROOM_SET, payload)
  },

  /**
   * clear room info
   * @param {*} payload
   */
  roomClear({ commit }) {
    commit(ROOM_CLEAR)
  },
}

export default {
  state,
  actions,
  mutations,
}
