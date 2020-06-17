import * as types from './mutation-types'
import { VIEW, ACTION } from 'configs/view.config'

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

  callReset({ commit }) {
    commit(types.CALL_RESET)
    commit(types.FILE_RESET)
  },
  /**
   * Change Call Mode
   * @param {String} state : stream, sharing, ar
   */
  setView({ commit }, state) {
    if (state === VIEW.STREAM) {
      commit(types.CALL_ACTION_SET, ACTION.STREAM_DEFAULT)
    } else if (state === VIEW.DRAWING) {
      commit(types.CALL_ACTION_SET, ACTION.DRAWING_LINE)
    } else if (state === VIEW.AR) {
      commit(types.CALL_ACTION_SET, ACTION.AR_POINTING)
    }
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

  callMic({ commit }, state) {
    localStorage.setItem('mic', state)
    commit(types.CALL_MIC, state)
  },
  callSpeaker({ commit }, state) {
    localStorage.setItem('speaker', state)
    commit(types.CALL_SPEAKER, state)
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
   * set mic device
   * @param {*} param0
   * @param {String} payload deviceId
   */
  setMicDevice({ commit }, payload) {
    localStorage.setItem('micDevice', payload)
    commit(types.SETTINGS.SET_MIC_DEVICE, payload)
  },

  /**
   * set speaker device
   * @param {*} param0
   * @param {String} payload deviceId
   */
  setSpeakerDevice({ commit }, payload) {
    localStorage.setItem('speakerDevice', payload)
    commit(types.SETTINGS.SET_SPEAKER_DEVICE, payload)
  },

  /**
   * set local recording length
   * @param {*} param0
   * @param {Number} payload record length
   */
  setLocalRecordLength({ commit }, payload) {
    localStorage.setItem('recordingTime', payload)
    commit(types.SETTINGS.SET_LOCAL_RECORD_LENGTH, payload)
  },

  /**
   * set record resolution
   * @param {*} param0
   * @param {String} payload record resolution
   */
  setRecordResolution({ commit }, payload) {
    localStorage.setItem('recordingResolution', payload)
    commit(types.SETTINGS.SET_RECORD_RESOLUTION, payload)
  },

  /**
   * set language
   * @param {*} param0
   * @param {*} payload language
   */
  setLanguage({ commit }, payload) {
    localStorage.setItem('language', payload)
    commit(types.SETTINGS.SET_LANGUAGE, payload)
  },

  /**
   * set local record interval
   * @param {*} param0
   * @param {*} payload local record interval
   */
  setLocalRecordInterval({ commit }, payload) {
    localStorage.setItem('recordingInterval', payload)
    commit(types.SETTINGS.SET_LOCAL_RECORD_INTERVAL, payload)
  },

  /**
   * set allow pointing
   * @param {*} param0
   * @param {*} payload
   */
  setAllowPointing({ commit }, payload) {
    localStorage.setItem('allowPointing', payload)
    commit(types.SETTINGS.SET_ALLOW_POINTING, payload)
  },

  /**
   * set allow local recording
   * @param {*} param0
   * @param {*} payload
   */
  setAllowLocalRecording({ commit }, payload) {
    localStorage.setItem('allowLocalRecording', payload)
    commit(types.SETTINGS.SET_ALLOW_LOCAL_RECORDING, payload)
  },

  /**
   * set screen stream for local recording
   * @param {*} param0
   * @param {*} payload
   */
  setScreenStream({ commit }, payload) {
    commit(types.SETTINGS.SET_SCREEN_STREAM, payload)
  },

  /**
   * set local record target
   * @param {*} param0
   * @param {*} payload
   */
  setLocalRecordTarget({ commit }, payload) {
    commit(types.SETTINGS.SET_LOCAL_RECORD_TARGET, payload)
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
   * show image
   * @param {Object} fileInfo
   */
  showImage({ commit }, fileInfo) {
    commit(types.SHOW_IMAGE, fileInfo)
  },
  /**
   * show image
   * @param {Object} fileInfo
   */
  showArImage({ commit }, fileInfo) {
    commit(types.SHOW_AR_IMAGE, fileInfo)
    if (fileInfo && fileInfo.id) {
      commit(types.CALL_ACTION_SET, ACTION.AR_DRAWING)
    }
  },
  /**
   * add file history
   * @param {Object} fileInfo
   */
  addHistory({ commit }, fileInfo) {
    commit(types.ADD_HISTORY, fileInfo)
  },
  /**
   * add file history
   * @param {Object} fileInfo
   */
  updateHistory({ commit }, fileInfo) {
    commit(types.UPDATE_HISTORY, fileInfo)
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
   * add pdf pages
   * @param {Object} pdfImageInfo
   */
  addPdfPage({ commit }, pdfImageInfo) {
    commit(types.ADD_PDF_PAGE, pdfImageInfo)
  },
  /**
   * add remove pdf pages
   * @param {Object} pdfId
   */
  removePdfPage({ commit }, pdfId) {
    commit(types.REMOVE_PDF_PAGE, pdfId)
  },
}
