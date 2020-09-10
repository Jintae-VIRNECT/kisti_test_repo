import {
  ROOM_SET,
  ADD_ROOM_MEMBER,
  REMOVE_ROOM_MEMBER,
  ROOM_CLEAR,
} from '../mutation-types'

function getDefaultRoomInfo() {
  return {
    sessionId: null,
    title: null,
    description: null,
    profile: null,
    leaderId: null,
    maxUserCount: 0,
    memberList: [],
  }
}
const state = getDefaultRoomInfo()

const mutations = {
  [ROOM_SET](state, payload) {
    state.sessionId = payload.sessionId
    state.title = payload.title
    state.description = payload.description
    state.profile = payload.profile
    state.leaderId = payload.leaderId
    state.maxUserCount = payload.maxUserCount
    state.memberList = payload.memberList
  },

  [ADD_ROOM_MEMBER](state, payload) {
    if (payload.length < 1) return
    for (let part of payload) {
      const idx = state.memberList.findIndex(
        member => member.uuid === part.uuid,
      )
      if (idx > -1) break
      state.memberList.push(part)
    }
  },

  [REMOVE_ROOM_MEMBER](state, participantId) {
    const idx = state.memberList.findIndex(
      member => member.uuid === participantId,
    )
    if (idx < 0) return

    state.memberList.splice(idx, 1)
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
   * add room member
   * @param {Array[Object]} payload // member info
   */
  addMember({ commit }, payload) {
    commit(ADD_ROOM_MEMBER, payload)
  },
  /**
   * add room member
   * @param {Array[Object]} payload // member info
   */
  removeMember({ commit }, participantId) {
    commit(REMOVE_ROOM_MEMBER, participantId)
  },

  /**
   * clear room info
   * @param {*} payload
   */
  roomClear({ commit }) {
    commit(ROOM_CLEAR)
  },
}

const getters = {
  roomInfo: state => state,
}

export default {
  state,
  actions,
  getters,
  mutations,
}
