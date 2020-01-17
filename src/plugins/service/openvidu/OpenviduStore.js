const getDefaultState = () => {
  return {
    call: {
      mainSession: {
        // nickName: nickName,
        // userName: name
        // stream: '',
        // nodeId: ''
      },
      sessions: [
        // {
        //   nickName: nickName,
        //   userName: name
        //   stream: '',
        //   nodeId: ''
        // }
      ]
    },
    chat: {
      chatList: [{
        text: '버넥트 리모트 팀 외 5명 원격통신 시작합니다.',
        name: 'alarm',
        date: new Date,
        chatId: null,
        type: 'system'
      }]
    }
  }
}

const state = getDefaultState()

const mutations = {
  SET_MAIN_SESSION(state, payload) {
    state.call.mainSession = payload
  },
  ADD_SESSION(state, payload) {
    if (payload.nodeId === 'main') {
      state.call.sessions.splice(0, 0, payload)
      return
    }
    state.call.sessions.push(payload)
    state.chat.chatList.push({
      text: payload.nickName + '님이 대화에 참여하셨습니다.',
      name: 'people',
      date: new Date,
      chatId: null,
      type: 'system'
    })
  },
  UPDATE_SESSION_STREAM(state, payload) {
    const idx = state.call.sessions.findIndex(obj => obj.nodeId === payload.nodeId)
    if(idx < 0) return
    state.call.sessions[idx].stream = payload.stream
  },
  REMOVE_SESSION(state, payload) {
    const idx = state.call.sessions.findIndex(obj => obj.nodeId === payload)
    if (idx < 0) return
    let nickName = state.call.sessions[idx].nickName
    state.call.sessions.splice(idx, 1)
    state.chat.chatList.push({
      text: nickName + '님이 대화에서 나가셨습니다.',
      name: 'people',
      date: new Date,
      chatId: null,
      type: 'system'
    })
  },
  CLEAR_SESSION(state, payload) {
    state.call.sessions = []
  },

  // chat
  ADD_CHAT(state, payload) {
    // let recentChat = state.chat.chatList[state.chat.chatList.length - 1]
    // if (recentChat.type === payload.type, )
    state.chat.chatList.push(payload)
  },
  REMOVE_CHAT(state, payload) {
    const idx = state.chat.chatList.findIndex(obj => obj.chatId === payload)
    if(idx < 0) return
    state.chat.chatList.splice(idx, 1)
  },
  CLEAR_CHAT(state) {
    state.chat.chatList = []
  }

  
}

const getters = {
  'mainSession': state => state.call.mainSession,
  'sessions': state => state.call.sessions,
  'chatList': state => state.chat.chatList
}

const actions = {
  /**
   * Set Main View Session
   * @param {Object} session 
   */
  setMainSession({ commit }, session) {
    commit('SET_MAIN_SESSION', session);
  },
  /**
   * Add Participants Session Object
   * @param {Object} session
   */
  addSession({ commit }, session) {
    commit('ADD_SESSION', session);
  },
  /**
   * Update Session Stream
   * @param {Object} session : { nodeId: String, stream: MediaStream } 
   */
  updateSessionStream({ commit }, session) {
    commit('UPDATE_SESSION_STREAM', session);
  },
  /**
   * Remove Participants Session Object
   * @param {String} nodeId 
   */
  removeSession({ commit }, nodeId) {
    commit('REMOVE_SESSION', nodeId);
  },
  /**
   * Clear Sessions
   */
  clearSession({ commit }) {
    commit('CLEAR_SESSION');
  },
  
  /**
   * Add Chat Object
   * @param {Object} chat 
   */
  addChat({ commit }, chat) {
    commit('ADD_CHAT', chat);
  },
  /**
   * Remove Chat Object
   * @param {String} chatId 
   */
  removeChat({ commit }, chatId) {
    commit('REMOVE_CHAT', chatId);
  },
  /**
   * Clear Chat Object
   */
  clearChat({ commit }) {
    commit('CLEAR_CHAT');
  },

  
  /**
   * toggle speaker
   */
  toggleSpeaker({ commit }) {
    commit('CALL_SPEAKER');
  },
  /**
   * toggle MIC
   */
  toggleMic({ commit }) {
    commit('CALL_MIC');
  },
}

export default {
  state,
  mutations,
  actions,
  getters
}
