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
    participantsCount: null,
    path: null,
    leaderId: null,
    maxParticipantCount: 0,
    memberList: [],
    token: null,
    coturn: [],
    wss: null,
  }
}
const state = getDefaultRoomInfo()

const mutations = {
  [ROOM_SET](state, payload) {
    state.sessionId = payload.sessionId
    state.title = payload.title
    state.description = payload.description
    state.participantsCount = payload.participantsCount
    state.path = payload.path
    state.leaderId = payload.leaderId
    state.maxParticipantCount = payload.maxParticipantCount
    state.memberList = payload.memberList
    state.token = payload.token
    state.coturn = payload.coturn
    state.wss = payload.wss
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

  [REMOVE_ROOM_MEMBER](state, payload) {
    const idx = state.memberList.findIndex(
      member => member.uuid === payload.uuid,
    )
    if (idx < 0) return

    state.memberList.splice(idx, 1)
  },

  [ROOM_CLEAR](state) {
    Object.assign(state, getDefaultRoomInfo())

    return true
  },
}

// const actions = {
//   /**
//    * set room info
//    * @param {Object} payload // room info
//    */
//   // setRoomInfo({ commit }, payload) {
//   //   commit(ROOM_SET, payload)
//   // },
//   /**
//    * add room member
//    * @param {Array[Object]} payload // member info
//    */
//   addMember({ commit }, payload) {
//     commit(ADD_ROOM_MEMBER, payload)
//   },

//   /**
//    * clear room info
//    * @param {*} payload
//    */
//   roomClear({ commit }) {
//     commit(ROOM_CLEAR)
//   },
// }

const getters = {
  roomInfo: state => state,
  roomMember: state => state.memberList,
}

export default {
  state,
  // actions,
  getters,
  mutations,
}
