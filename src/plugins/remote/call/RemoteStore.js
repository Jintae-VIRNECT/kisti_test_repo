import { ROLE } from 'configs/remote.config'

const getDefaultState = () => {
  return {
    initing: true,
    viewForce: false,
    restrictedMode: false,
    mainView: {},
    participants: [
      // id: uuid,
      // stream: MediaStream,
      // connectionId: stream.connection.connectionId,
      // nickname: nickname,
      // path: path,
      // audio: stream.audioActive,
      // video: stream.videoActive,
      // hasVideo: hasVideo,
      // status: 'good',
      // roleType: 'LEADER' / 'EXPERT'
      // deviceType: configs/device.config.DEVICE
      // permission: 'default' / 'noAR' / false / true
      // hasArFeature
      // cameraStatus: 'default', // 'default': 초기세팅
      // zoomLevel: 1, // zoom 레벨
      // zoomMax: 5, // zoom 최대 레벨
      // cameraStatus: 'default', // 'default': 초기세팅
      // flash: 'default', // flash 제어
      // screenShare: true, false //PC 화면공유 여부
      // currentWatching: uuid //현재 보고있는 참가자의 uuid
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
    //화면 공유시 카메라 스트림을 보관하기 위함
    myTempStream: null, //MediaStream
    screenSharing: false,
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

  clearMainView(state, connectionId) {
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
          state.viewForce = false
        } else {
          state.viewForce = false
          //mainView 객체를 참조하는 동작을 위함
          setTimeout(() => {
            state.mainView = {}
          })
        }
      }
    }
    // resolution 데이터 제거
    const rIdx = state.resolutions.findIndex(
      obj => obj.connectionId === connectionId,
    )
    if (rIdx < 0) return
    state.resolutions.splice(rIdx, 1)
  },
  addStream(state, payload) {
    if (payload.me) {
      state.participants.splice(0, 0, payload)
    } else {
      state.participants.push(payload)
    }
  },
  setStream(state, payload) {
    const idx = state.participants.findIndex(obj => obj.id === payload.id)
    if (idx < 0) return
    state.participants[idx].stream = payload.stream
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
        if (state.participants[0].hasVideo) {
          state.mainView = state.participants[0]
          state.viewForce = false
        } else {
          const pIdx = state.participants.findIndex(
            user =>
              user.connectionId !== state.mainView.connectionId &&
              user.video === true,
          )
          if (pIdx > -1) {
            state.mainView = state.participants[pIdx]
            state.viewForce = false
          } else {
            state.mainView = {}
            state.viewForce = false
          }
        }
      }
      let participant = state.participants.splice(idx, 1)
      let id
      if (state.chatList.length > 0) {
        id = state.chatList[state.chatList.length - 1].id + 1
      } else {
        id = 1
      }
      state.chatList.push({
        id,
        name: participant[0].nickname,
        status: 'leave',
        date: new Date(),
        type: 'system',
      })
      if (participant[0].roleType === ROLE.LEADER) {
        state.viewForce = false
      }
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
    const idx = state.participants.findIndex(
      user => user.connectionId === params.connectionId,
    )
    if (idx < 0) return
    for (let key in params) {
      if (key === 'connectionId') continue
      state.participants[idx][key] = params[key]
      if (state.participants[idx].id === state.mainView.id) {
        state.mainView[key] = params[key]
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
    if (state.participants[idx].me === true) {
      setTimeout(() => {
        state.initing = false

        const hasVideo = param['hasVideo'] === true
        const noMainView = !state.mainView || !state.mainView.id

        if (hasVideo && noMainView) {
          state.mainView = state.participants[idx]
        }
      }, 100)
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
  // video, audio 제한모드 설정
  setRestrictedMode(state, payload) {
    state.restrictedMode = payload
  },

  // chat
  addChat(state, payload) {
    let id
    if (state.chatList.length > 0) {
      id = state.chatList[state.chatList.length - 1].id + 1
    } else {
      id = 1
    }
    state.chatList.push({
      id: id,
      ...payload,
      date: new Date(),
    })
  },
  updateChat(state, payload) {
    const idx = state.chatList.findIndex(chat => chat.id === payload.id)
    if (idx < 0) return
    const chat = state.chatList[idx]
    for (let key in payload) {
      if (key === 'id') continue
      chat[key] = payload[key]
    }
    state.chatList.splice(idx, 1, chat)
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
  setMyTempStream(state, payload) {
    state.myTempStream = payload
  },
  setScreenSharing(state, payload) {
    state.screenSharing = payload
  },
}

const actions = {
  addChat({ commit }, payload) {
    commit('addChat', payload)
  },
  updateChat({ commit }, payload) {
    commit('updateChat', payload)
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
  setMyTempStream({ commit }, payload) {
    commit('setMyTempStream', payload)
  },
  setScreenSharing({ commit }, payload) {
    commit('setScreenSharing', payload)
  },
}

const getters = {
  viewForce: state => state.viewForce,
  mainView: state => state.mainView,
  allowCameraControl: state => !state.restrictedMode,
  participants: state => state.participants,
  myInfo: state => {
    if (state.participants.length > 0) {
      return state.participants[0]
    } else {
      return {}
    }
  },
  chatList: state => state.chatList,
  resolutions: state => state.resolutions,
  initing: state => state.initing,
  myTempStream: state => state.myTempStream,
  screenSharing: state => state.screenSharing,
}

export default {
  state,
  mutations,
  getters,
  actions,
}
