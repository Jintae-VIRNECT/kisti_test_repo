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
   * set devices
   * @param {*} param0
   * @param {Object} payload
   */
  setDevices({ commit }, payload) {
    if ('mic' in payload) {
      commit(types.SETTINGS.SET_MIC_DEVICE, payload['mic'])
    }
    if ('speaker' in payload) {
      commit(types.SETTINGS.SET_SPEAKER_DEVICE, payload['speaker'])
    }
  },
  /**
   * set Record
   * @param {*} param0
   * @param {Object} payload
   */
  setRecord({ commit }, payload) {
    commit(types.SETTINGS.SET_RECORD, payload)
  },
  /**
   * set Allow
   * @param {*} param0
   * @param {Object} payload
   */
  setAllow({ commit }, payload) {
    commit(types.SETTINGS.SET_ALLOW, payload)
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
   * remove pdf pages
   * @param {Object} pdfId
   */
  removePdfPage({ commit }, pdfId) {
    commit(types.REMOVE_PDF_PAGE, pdfId)
  },
  /**
   * set capture image
   * @param {Object} fileInfo
   */
  setCapture({ commit }, fileInfo) {
    commit(types.SET_CAPTURE, fileInfo)
  },
  /**
   * remove capture image
   */
  clearCapture({ commit }) {
    commit(types.CLEAR_CAPTURE)
  },

  /**license */
  setLicense({ commit }, status) {
    commit(types)
  },
}
