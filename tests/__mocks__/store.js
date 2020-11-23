import Vuex from 'vuex'

//unit-test에 사용될 mock store를 정의

let state
let getters
let actions
let mutations
let store

state = {
  participants: [
    {
      id: '4705cf50e6d02c59b0eef9591666e2a3',
      stream: '[object MediaStream]',
      connectionId: 'con_GEYK6UjuWs',
      nickname: '왕밤빵17',
      path:
        'https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/profile/2020-09-21_ffb0ab57f49f4954b2254ec18a2ed696jpg',
      video: true,
      audio: true,
      hasVideo: true,
      speaker: true,
      mute: false,
      status: 'good',
      roleType: 'LEADER',
      deviceType: 'DESKTOP',
      permission: 'default',
      hasArFeature: false,
      cameraStatus: 1,
      zoomLevel: 1,
      zoomMax: 1,
      flash: 'default',
      me: true,
    },
    {
      id: '4d127135f699616fb88e6bc4fa6d784a',
      stream: '[object MediaStream]',
      connectionId: 'con_Pp0eyKlTLo',
      nickname: '왕밤빵16',
      path:
        'https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/profile/2020-09-09_7164645dca644ebe83e3c9e6236e311ajpg',
      video: true,
      audio: true,
      hasVideo: true,
      speaker: true,
      mute: false,
      status: 'good',
      roleType: 'EXPERT',
      deviceType: 'MOBILE',
      permission: 'default',
      hasArFeature: true,
      cameraStatus: 1,
      zoomLevel: 1,
      zoomMax: 4,
      flash: 0,
    },
  ],
}
getters = {
  mainView: () => {
    return {
      id: '4705cf50e6d02c59b0eef9591666e2a3',
      stream: {},
      connectionId: 'testConnId',
      nickname: '왕밤빵17',
      path: '',
      video: true,
      audio: true,
      hasVideo: true,
      speaker: true,
      mute: false,
      status: 'good',
      roleType: 'LEADER',
      deviceType: 'DESKTOP',
      permission: 'default',
      hasArFeature: false,
      cameraStatus: 1,
      zoomLevel: 1,
      zoomMax: 1,
      flash: 'default',
      me: true,
    }
  },
  participants: () => state.participants,
  view: () => 'stream',
  shareFile: () => {},
  viewForce: () => true,
  deviceType: () => 'desktop',
}
actions = {
  setView: jest.fn(),
  addChat: jest.fn(),
}
mutations = {
  updateParticipant: jest.fn(),
  clearParticipants: () => {
    state.participants = [{}]
  },
}
store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations,
})

export default store
