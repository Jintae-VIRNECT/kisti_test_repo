const getDefaultState = () => {
  return {
    call: {
      mainView: {
        // nickName: nickName,
        // userName: name
        // stream: '',
        // uuid: ''
      },
      participants: [
        // {
        //   uuid: null,
        //   stream: null,
        //   nickName: '이름',
        //   sessionName: '세션이름',
        //   path: 'default',
        //   status: 'good',
        // }
      ],
    },
    chat: {
      chatList: [
        {
          text: '버넥트 리모트 팀 외 5명 원격통신 시작합니다.',
          name: 'alarm',
          date: new Date(),
          uuid: null,
          type: 'system',
        },
      ],
    },
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
    state.chat.chatList.push({
      text: payload.nickName + '님이 대화에 참여하셨습니다.',
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
    console.log('>>setstream', payload.stream)
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
  removeStream(state, payload) {
    const idx = state.call.participants.findIndex(obj => obj.uuid === payload)
    if (idx < 0) return
    let nickName = state.call.participants[idx].nickName
    state.call.participants.splice(idx, 1)
    state.chat.chatList.push({
      text: nickName + '님이 대화에서 나가셨습니다.',
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
    state.chat.chatList.push(payload)
  },
  removeChat(state, payload) {
    const idx = state.chat.chatList.findIndex(obj => obj.uuid === payload)
    if (idx < 0) return
    state.chat.chatList.splice(idx, 1)
  },
  clearChat(state) {
    state.chat.chatList = []
  },
}

const getters = {
  mainView: state => state.call.mainView,
  participants: state => state.call.participants,
  chatList: state => state.chat.chatList,
}

export default {
  state,
  mutations,
  getters,
}
