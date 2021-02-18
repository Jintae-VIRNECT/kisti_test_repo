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
    commit(types.INIT_WORKSPACE, infoList)
    commit(types.CLEAR_COMPANY_INFO)
  },
  changeWorkspace({ commit }, workspace) {
    commit(types.CHANGE_WORKSPACE, workspace)
    commit(types.CLEAR_COMPANY_INFO)
  },
  setCompanyInfo({ commit }, info) {
    commit(types.SET_COMPANY_INFO, info)
  },
  clearWorkspace({ commit }, uuid) {
    commit(types.CLEAR_WORKSPACE, uuid)
  },

  callReset({ commit }) {
    commit(types.CALL_RESET)
    commit(types.FILE_RESET)
    commit(types.ALLOW_RESET)
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
    if ('video' in payload) {
      commit(types.SETTINGS.SET_VIDEO_DEVICE, payload['video'])
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
   * set server Record
   * @param {*} param0
   * @param {Object} payload
   */
  setServerRecord({ commit }, payload) {
    commit(types.SETTINGS.SET_SERVER_RECORD, payload)
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
   * set Allow
   * @param {*} param0
   * @param {Object} payload
   */
  setTranslate({ commit }, payload) {
    if ('flag' in payload) {
      commit(types.SETTINGS.SET_TRANSLATE_FLAG, payload['flag'])
    }
    if ('code' in payload) {
      commit(types.SETTINGS.SET_TRANSLATE_CODE, payload['code'])
    }
    if ('multiple' in payload) {
      commit(types.SETTINGS.SET_TRANSLATE_MULTIPLE, payload['multiple'])
    }
    if ('sttSync' in payload) {
      commit(types.SETTINGS.SET_STT_SYNC, payload['sttSync'])
    }
    if ('ttsAllow' in payload) {
      commit(types.SETTINGS.SET_TTS_ALLOW, payload['ttsAllow'])
    }
  },
  setScreenStrict({ commit }, payload) {
    commit(types.SETTINGS.SET_SCREEN_STRICT, payload)
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
   * update local record status
   */
  setLocalRecordStatus({ commit }, status) {
    commit(types.SETTINGS.SET_LOCAL_RECORD_STATUS, status)
  },

  /**
   * update server record status
   */
  setServerRecordStatus({ commit }, status) {
    commit(types.SETTINGS.SET_SERVER_RECORD_STATUS, status)
  },
}
