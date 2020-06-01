const getDefaultState = () => {
  return {
    call: {
      mainView: {
        // nickname: nickname,
        // userName: name
        // stream: '',
        // uuid: ''
      },
      participants: [
        // {
        //   uuid: null,
        //   stream: null,
        //   nickname: '이름',
        //   sessionName: '세션이름',
        //   path: 'default',
        //   status: 'good',
        // }
      ],
    },
    chatList: [
      {
        text: '버넥트 리모트 팀 외 5명 원격통신 시작합니다.',
        name: 'alarm',
        date: new Date(),
        uuid: null,
        type: 'system',
      },
    ],
    isBackground: false,
    zoomLevel: 1, // zoom 레벨
    zoomMax: 5, // zoom 최대 레벨
    zoomStatus: 'default', // 'default': 초기세팅, utils/deviceinfo.js
    flash: false, // flash 제어
    flashStatus: 'default', // 'default': 초기세팅, utils/deviceinfo.js
  }
}

const state = getDefaultState()

const mutations = {
  setMainView(state, payload) {
    state.call.mainView = payload
    state.call.participants.push(payload)
  },
  addStream(state, payload) {
    if (payload.uuid === 'main') {
      state.call.participants.splice(0, 0, payload)
      return
    }
    state.call.participants.push(payload)
    state.chatList.push({
      text: payload.nickname + '님이 대화에 참여하셨습니다.',
      name: 'people',
      date: new Date(),
      uuid: null,
      type: 'system',
    })
  },
  setStream(state, payload) {
    const idx = state.call.participants.findIndex(
      obj => obj.uuid === payload.uuid,
    )
    if (idx < 0) return
    state.call.participants[idx].stream = payload.stream
  },
  updateStreamInfo(state, payload) {
    const idx = state.call.participants.findIndex(
      obj => obj.uuid === payload.uuid,
    )
    if (idx < 0) return

    let updateSession = state.call.participants[idx]

    for (let key in payload) {
      if (
        key in updateSession &&
        payload[key] !== null &&
        payload[key] !== 'uuid'
      ) {
        updateSession[key] = payload[key]
      }
    }
    state.call.participants.splice(idx, 1, updateSession)
  },
  removeStream(state, connectionId) {
    const idx = state.call.participants.findIndex(
      obj => obj.connectionId === connectionId,
    )
    if (idx < 0) return
    let nickname = state.call.participants[idx].nickname
    state.call.participants.splice(idx, 1)
    state.chatList.push({
      text: nickname + '님이 대화에서 나가셨습니다.',
      name: 'people',
      date: new Date(),
      uuid: null,
      type: 'system',
    })
  },
  clearStreams(state) {
    state.call.participants = []
    state.call.mainView = null
  },

  // chat
  addChat(state, payload) {
    state.chatList.push(payload)
  },
  removeChat(state, payload) {
    const idx = state.chatList.findIndex(obj => obj.uuid === payload)
    if (idx < 0) return
    state.chatList.splice(idx, 1)
  },
  clearChat(state) {
    state.chatList = []
  },
  deviceUpdate(state, object) {
    for (let key in object) {
      state[key] = object[key]
    }
  },
}

const getters = {
  mainView: state => state.call.mainView,
  participants: state => state.call.participants,
  chatList: state => state.chatList,
}

export default {
  state,
  mutations,
  getters,
}
