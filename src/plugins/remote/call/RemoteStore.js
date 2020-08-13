const getDefaultState = () => {
  return {
    initing: true,
    viewForce: false,
    mainView: {},
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
      // roleType: 'LEADER' / 'EXPERT'
      // permission: 'default' / 'noAR' / false / true
      // hasArFeature
    ],
    chatList: [
      // {
      //   name: '일이삼사',
      //   status: 'create',
      //   date: new Date(),
      //   uuid: null,
      //   type: 'system',
      //   subType: 'INIT',
      // },
      // {
      //   name: '일이삼사',
      //   status: 'invite',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   name: '일이삼사',
      //   status: 'leave',
      //   date: new Date(),
      //   uuid: null,
      //   type: 'system',
      // },
      // {
      //   name: '일이삼사',
      //   status: 'stream-stop',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   name: '하하하',
      //   status: 'stream-start',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   name: '일이삼사',
      //   status: 'stream-background',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   name: '파일명.png',
      //   status: 'drawing',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   status: 'ar-deny',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   status: 'ar-unsupport',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   status: 'ar-pointing',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   status: 'ar-area',
      //   date: new Date(),
      //   type: 'system',
      // },
      // {
      //   text: '텍스트',
      //   name: '일이삼사',
      //   date: new Date(),
      //   uuid: 111,
      //   type: 'ME',
      // },
      // {
      //   text: '텟트',
      //   name: '하하',
      //   date: new Date(),
      //   uuid: 111,
      //   type: 'ME',
      // },
      // {
      //   text: '텍스트',
      //   name: '일이삼사',
      //   date: new Date(),
      //   uuid: 111,
      //   type: 'OPPONENT',
      //   profile:
      //     'https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/profile/2020-06-26_1ea7941341d4405380015fca00a58da2png',
      // },
      // {
      //   text: '텍스트',
      //   name: '일이삼사',
      //   date: new Date(),
      //   uuid: 111,
      //   type: 'OPPONENT',
      //   profile:
      //     'https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/profile/2020-06-26_1ea7941341d4405380015fca00a58da2png',
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
    cameraStatus: 'default', // 'default': 초기세팅
    flash: false, // flash 제어
    flashStatus: 'default', // 'default': 초기세팅

    // user option
    allowLocalRecord: true,
    allowPointing: true,
  }
}

const state = getDefaultState()

const mutations = {
  // stream
  setMainView(state, id) {
    const idx = state.participants.findIndex(obj => obj.id === id)
    if (idx < 0) return
    state.mainView = state.participants[idx]
  },
  setMainViewForce(state, force) {
    // if (!state.mainView || !state.mainView.id) return
    state.viewForce = force
  },
  addStream(state, payload) {
    if (payload.me) {
      state.participants.splice(0, 0, payload)
      if (payload.video) {
        state.mainView = payload
      }
      state.initing = false
    } else {
      state.participants.push(payload)
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
    if (idx >= 0) {
      // 메인뷰를 보고있으면 메인뷰 변경
      if (
        state.participants[idx].connectionId === state.mainView.connectionId
      ) {
        const pIdx = state.participants.findIndex(
          user =>
            user.connectionId !== state.mainView.connectionId &&
            user.video === true,
        )
        if (pIdx > -1) {
          state.mainView = state.participants[pIdx]
        } else {
          state.mainView = {}
          state.isBackground = false
          state.zoomLevel = 1 // zoom 레벨
          state.zoomMax = 5 // zoom 최대 레벨
          state.cameraStatus = 'default' // 'default': 초기세팅
          state.flash = false // flash 제어
          state.flashStatus = 'default' // 'default': 초기세팅
        }
      }
      let participant = state.participants.splice(idx, 1)
      state.chatList.push({
        name: participant[0].nickname,
        status: 'leave',
        date: new Date(),
        type: 'system',
      })
    }
    // resolution 데이터 제거
    const rIdx = state.resolutions.findIndex(
      obj => obj.connectionId === connectionId,
    )
    if (rIdx < 0) return
    state.resolutions.splice(rIdx, 1)
  },

  // device control
  deviceControl(state, params) {
    for (let key in params) {
      if (key in state && params[key] !== null) {
        state[key] = params[key]
      }
    }
  },
  updateParticipant(state, param) {
    const idx = state.participants.findIndex(
      user => user.connectionId === param.connectionId,
    )
    if (idx < 0) {
      return
    }
    for (let key in param) {
      if (key in state.participants[idx] && param[key] !== null) {
        if (key === 'connectionId') continue
        state.participants[idx][key] = param[key]
        if (state.participants[idx].id === state.mainView.id) {
          state.mainView[key] = param[key]
        }
      }
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

  // chat
  addChat(state, payload) {
    state.chatList.push({
      ...payload,
      date: new Date(),
    })
  },
  removeChat(state, payload) {
    const idx = state.chatList.findIndex(obj => obj.uuid === payload)
    if (idx < 0) return
    state.chatList.splice(idx, 1)
  },
  clearChat(state) {
    state.chatList = []
  },

  callClear() {
    Object.assign(state, getDefaultState())

    return true
  },
}

const actions = {
  addChat({ commit }, payload) {
    commit('addChat', payload)
  },
  setMainView({ commit }, payload) {
    if ('id' in payload) {
      commit('setMainView', payload.id)
    }
    if ('force' in payload) {
      commit('setMainViewForce', payload.force)
    } else {
      commit('setMainViewForce', false)
    }
  },
}

const getters = {
  viewForce: state => state.viewForce,
  mainView: state => state.mainView,
  participants: state => state.participants,
  chatList: state => state.chatList,
  resolutions: state => state.resolutions,
  initing: state => state.initing,
  deviceInfo: state => {
    return {
      zoomLevel: state.zoomLevel,
      zoomMax: state.zoomMax,
      cameraStatus: state.cameraStatus,
      flash: state.flash,
      flashStatus: state.flashStatus,
    }
  },
  control: state => {
    return {
      localRecord: state.allowLocalRecord,
      pointing: state.allowPointing,
    }
  },
}

export default {
  state,
  mutations,
  getters,
  actions,
}
