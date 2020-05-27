import { ROOM_SET, ROOM_CLEAR } from '../mutation-types'

function Participant(info) {
  this.uuid = info.id
  this.email = info.email
  this.name = info.name
  this.nickname = info.nickname
  this.device = info.device
  this.role = info.role
  this.profile = info.profile
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
      for (let participant of payload.participants) {
        state.participants.push(new Participant(participant))
      }
      console.log(state.participants)
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
