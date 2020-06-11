import { ROOM_SET, ROOM_CLEAR } from '../mutation-types'

function newParticipant(info) {
  return {
    uuid: info.id,
    email: info.email,
    name: info.name,
    nickname: info.nickname,
    device: info.device,
    role: info.role,
    path: info.path,
  }
}

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
    participants: [],
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
    if (payload.participants.length > 0) {
      state.participants = []
      for (let participant of payload.participants) {
        state.participants.push(newParticipant(participant))
      }
    }
  },

  [ROOM_CLEAR](state) {
    Object.assign(state, getDefaultRoomInfo())

    return true
  },
}

export default {
  state,
  mutations,
}
