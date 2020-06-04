const getDefaultState = () => {
  return {
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
    chatList: [],
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
  setMainView(state, participantId) {
    const idx = state.participants.findIndex(obj => obj.id === participantId)
    if (idx < 0) return
    state.mainView = state.participants[idx]
  },
  addStream(state, payload) {
    if (state.me) {
      state.participants.splice(0, 0, payload)
      state.mainView = payload
      return
    }
    state.participants.push(payload)
    state.chatList.push({
      text: payload.nickname + '님이 대화에 참여하셨습니다.',
      name: 'people',
      date: new Date(),
      uuid: null,
      type: 'system',
    })
    if (!state.mainView || !state.mainView.stream) {
      state.mainView = payload
    }
  },
  setStream(state, payload) {
    const idx = state.participants.findIndex(obj => obj.id === payload.id)
    if (idx < 0) return
    state.participants[idx].stream = payload.stream
  },
  updateStreamInfo(state, payload) {
    const idx = state.participants.findIndex(obj => obj.id === payload.id)
    if (idx < 0) return

    let updateSession = state.participants[idx]

    for (let key in payload) {
      if (
        key in updateSession &&
        payload[key] !== null &&
        payload[key] !== 'id'
      ) {
        updateSession[key] = payload[key]
      }
    }
    state.participants.splice(idx, 1, updateSession)
  },
  removeStream(state, connectionId) {
    const idx = state.participants.findIndex(
      obj => obj.connectionId === connectionId,
    )
    if (idx < 0) return
    let nickname = state.participants[idx].nickname
    state.participants.splice(idx, 1)
    state.chatList.push({
      text: nickname + '님이 대화에서 나가셨습니다.',
      name: 'people',
      date: new Date(),
      uuid: null,
      type: 'system',
    })
  },
  clearStreams(state) {
    state.participants = []
    state.mainView = null
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
  mainView: state => state.mainView,
  participants: state => state.participants,
  chatList: state => state.chatList,
}

export default {
  state,
  mutations,
  getters,
}
