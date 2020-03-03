const getDefaultState = () => {
  return {
    initialized: false,
    duplicated: false,
    groupId: null,
    roomId: null,
    opponentId: null,
    clientType: null,
    opponentStream: null,
    opponentScreenWidth: undefined,
    opponentScreenHeight: undefined,
    opponentScreenVerticalPad: 0,
    //접속자 목록
    users: [],
    notify: {},
    channelOnUse: false, // true: 사용불가, false: 사용가능
    messageData: '', // 공유 이미지 데이터
    messageListeners: [],
    currentGroup: {
      groupNickname: null,
      connected: false,
    },
    deviceReady: {
      readyState: true,
      reason: null,
    }, // 디바이스 연결 상태
    callBitrate: '1.7',
  }
}
const state = getDefaultState()

const mutations = {
  remoteClear(state) {
    const DefaultState = getDefaultState()

    DefaultState.currentGroup = state.currentGroup
    DefaultState.users = state.users.slice(0)
    DefaultState.initialized = true

    Object.assign(state, DefaultState)
  },
  remoteReady(state, value) {
    state.initialized = value
  },
  remoteUpdate(state, object) {
    for (let key in object) {
      state[key] = object[key]
    }
  },
  messageData(state, object) {
    state.messageData += object
  },
  remoteConnected(state, value) {
    state.currentGroup.groupNickname = value
    state.currentGroup.connected = true
  },
  remoteDisconnected(state, value) {
    state.currentGroup.groupNickname = value
    state.currentGroup.connected = false
  },
  remoteConnectTimeout(state, value) {
    state.connectTryTimeOut = value
  },
  remoteConnectError(state, value) {
    state.currentGroup.connected = false
    state.connectTry = value
  },
  remoteDeviceReady(state, value) {
    state.deviceReady = value
  },
}

const getters = {
  remote: state => state,
  canUseChannel: state => !state.channelOnUse,
  messageData: state => state.messageData,
  remoteNotify: state => state.notify,
  remoteOpponent: state => {
    return {
      sId: state.opponentId,
      stream: state.opponentStream,
      screenWidth: state.opponentScreenWidth,
      screenHeight: state.opponentScreenHeight,
      screenVPad: state.opponentScreenVerticalPad,
    }
  },
  callBitrate: state => state.callBitrate,
}

export default {
  state,
  mutations,
  getters,
}
