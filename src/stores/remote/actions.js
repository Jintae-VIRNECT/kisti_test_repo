import * as types from './mutation-types'

export default {
  // account
  updateAccount({ commit }, account) {
    commit(types.ACCOUNT_SET, account)
  },
  clearAccount({ commit }) {
    commit(types.ACCOUNT_CLEAR)
  },
  initWorkspace({ commit }, infoList) {
    console.log(infoList)
    commit(types.INIT_WORKSPACE, infoList)
  },
  changeWorkspace({ commit }, id) {
    commit(types.CHANGE_WORKSPACE, id)
  },

  /**
   * Change Call Mode
   * @param {String} state : stream, sharing, ar
   */
  setView({ commit }, state) {
    commit(types.CALL_MODE_SET, state)
  },
  /**
   * Change Tool Status
   * @param {Object} state { key: value }
   */
  setTool({ commit }, payload) {
    if (payload && payload.target) {
      let type = ''

      switch (payload.target) {
        case 'drawColor':
          type = types.TOOL_DRAWING_COLOR
          break
        case 'drawOpacity':
          type = types.TOOL_DRAWING_OPACITY
          break
        case 'textSize':
          type = types.TOOL_TEXT_SIZE
          break
        case 'lineWidth':
          type = types.TOOL_LINE_WIDTH
      }

      commit(type, payload.value)
    }
  },
  /**
   * Change Call Action
   * @param {String} state : pointing, drawing
   */
  setAction({ commit }, state) {
    commit(types.CALL_ACTION_SET, state)
  },

  callStream({ commit }, state) {
    commit(types.CALL_STREAM, state)
  },
  callMic({ commit }, state) {
    commit(types.CALL_MIC, state)
  },
  callSpeaker({ commit }, state) {
    commit(types.CALL_SPEAKER, state)
  },
  muteOnOff({ commit }) {
    commit(types.MUTE_ON_OFF)
  },

  /**
   * Set device type
   * @param {Object} payload
   */
  setDeviceType({ commit }, payload) {
    commit(types.SET_DEVICE, payload)
  },

  /**
   * filter
   * @param {String} payload
   */
  setFilter({ commit }, payload) {
    commit(types.SEARCH_FILTER, payload)
  },

  /** workspace-settings **/

  /**
   *
   * @param {*} param0
   * @param {String} payload deviceId
   */
  setMic({ commit }, payload) {
    localStorage.setItem('mic', payload)
    commit(types.SETTINGS.SET_MIC, payload)
  },

  /**
   *
   * @param {*} param0
   * @param {String} payload deviceId
   */
  setSpeaker({ commit }, payload) {
    localStorage.setItem('speaker', payload)
    commit(types.SETTINGS.SET_SPEAKER, payload)
  },

  /**
   *
   * @param {*} param0
   * @param {Number} payload record length
   */
  setLocalRecordLength({ commit }, payload) {
    localStorage.setItem('recordingTime', payload)
    commit(types.SETTINGS.SET_LOCAL_RECORD_LENGTH, payload)
  },

  /**
   *
   * @param {*} param0
   * @param {String} payload record resolution
   */
  setRecordResolution({ commit }, payload) {
    localStorage.setItem('recordingResolution', payload)
    commit(types.SETTINGS.SET_RECORD_RESOLUTION, payload)
  },

  /**
   *
   * @param {*} param0
   * @param {*} payload language
   */
  setLanguage({ commit }, payload) {
    localStorage.setItem('language', payload)
    commit(types.SETTINGS.SET_LANGUAGE, payload)
  },

  /**
   * set room info
   * @param {Object} payload // room info
   */
  setRoomInfo({ commit }, payload) {
    commit(types.ROOM_SET, payload)
  },

  /**
   * clear room info
   * @param {*} payload
   */
  roomClear({ commit }) {
    commit(types.ROOM_CLEAR)
  },

  /**
   * add file history
   * @param {Object} fileInfo
   */
  addHistory({ commit }, fileInfo) {
    commit(types.ADD_HISTORY, fileInfo)
  },
  /**
   * remove file history
   * @param {String} fileId
   */
  removeHistory({ commit }, fileId) {
    commit(types.REMOVE_HISTORY, fileId)
  },
  /**
   * add file
   * @param {Object} fileInfo
   */
  addFile({ commit }, fileInfo) {
    commit(types.ADD_FILE, fileInfo)
  },
  /**
   * add file history
   * @param {String} fileId
   */
  removeFile({ commit }, fileId) {
    commit(types.REMOVE_PDF_PAGE, fileId)
    commit(types.REMOVE_FILE, fileId)
  },
  /**
   * add file history
   * @param {Object} pdfImageInfo
   */
  addPdfPage({ commit }, pdfImageInfo) {
    commit(types.ADD_PDF_PAGE, pdfImageInfo)
  },
}
