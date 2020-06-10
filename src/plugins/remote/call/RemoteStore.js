const getDefaultState = () => {
  return {
    role: '', // 'LEADER' / 'EXPERT'
    mainView: {
      // nickname: nickname,
      // userName: name
      // stream: '',
      // uuid: ''
    },
    participants: [
      // id: uuid,
      // stream: MediaStream,
      // connectionId: stream.connection.connectionId,
      // nickname: nickname,
      // path: path,
      // audio: stream.audioActive,
      // video: stream.videoActive,
      // status: 'good',
      // resolution: { width, height }
      // role: 'LEADER' / 'EXPERT'
    ],
    chatList: [
      // {
      //   text: '버넥트 리모트 팀 외 5명 원격통신 시작합니다.',
      //   name: 'alarm',
      //   date: new Date(),
      //   uuid: null,
      //   type: 'system',
      // },
    ],
    resolutions: [
      // {
      //   connectionId: '',
      //   width: 300,
      //   height: 600
      // }
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
  // stream
  setMainView(state, participantId) {
    const idx = state.participants.findIndex(obj => obj.id === participantId)
    if (idx < 0) return
    state.mainView = state.participants[idx]
  },
  addStream(state, payload) {
    if (payload.me) {
      console.log(state.mainView, payload)
      state.participants.splice(0, 0, payload)
      if (payload.video) {
        state.mainView = payload
      }
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
  propertyChanged(state, payload) {
    const idx = state.participants.findIndex(
      obj => obj.connectionId === payload.connectionId,
    )
    if (idx < 0) return

    for (let key in payload) {
      if (key === 'connectionId') continue
      state.participants[idx][key] = payload[key]
    }
  },
  removeStream(state, connectionId) {
    const idx = state.participants.findIndex(
      obj => obj.connectionId === connectionId,
    )
    if (idx >= 0) {
      let participant = state.participants.splice(idx, 1)
      state.chatList.push({
        text: participant[0].nickname + '님이 대화에서 나가셨습니다.',
        name: 'people',
        date: new Date(),
        uuid: null,
        type: 'system',
      })
      // 메인뷰를 보고있으면 메인뷰 변경
      if (participant[0].connectionId === state.mainView.connectionId) {
        const pIdx = state.participants.find(user => user.video === true)
        if (pIdx > -1) {
          state.mainView = state.participants[0]
        } else {
          state.mainView = {}
        }
        console.log(state.mainView)
      }
    }
    console.log(state.resolutions)
    // resolution 데이터 제거
    const rIdx = state.resolutions.findIndex(
      obj => obj.connectionId === connectionId,
    )
    if (rIdx < 0) return
    state.resolutions.splice(rIdx, 1)
    console.log(state.resolutions)
  },
  deviceUpdate(state, object) {
    for (let key in object) {
      state[key] = object[key]
    }
  },
  updateResolution(state, payload) {
    const idx = state.resolutions.findIndex(
      resolution => resolution.connectionId === payload.connectionId,
    )
    if (idx < 0) {
      state.resolutions.push(payload)
      return
    }
    Object.assign(state.resolutions[idx], payload)
  },
  myRole(state, payload) {
    state.role = payload
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

  clear() {
    Object.assign(state, getDefaultState())

    return true
  },
}

const getters = {
  myRole: state => state.role,
  mainView: state => state.mainView,
  participants: state => state.participants,
  chatList: state => state.chatList,
  resolutions: state => state.resolutions,
}

export default {
  state,
  mutations,
  getters,
}
